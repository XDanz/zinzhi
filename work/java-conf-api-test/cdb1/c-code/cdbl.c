/*
 * Copyright 2007 Tail-F Systems AB
 */

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/poll.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <stdio.h>
#include <signal.h>
#include <errno.h>


#include <confd.h>
#include <confd_cdb.h>
#include "mtest.h"


static int signaled = 0;

static void sighdlr(int sig){
    signaled++;
}


/* my internal db */

#define MAXH 3
#define MAXC 2

struct child {
    int64_t dn;
    char childattr[255];
    int inuse;
};

struct rfhead {
    int64_t dn;
    char sector_id[255];
    struct child children[MAXC];
    int inuse;
};
struct rfhead rfheads[MAXH];


/* read a given head structure from cdb and write the data into */
/* our array */

static void read_head(int cdbsock, confd_value_t *headkey)
{

    int i = 0;
    int pos = -1;
    /* which position should we overwrite */
    for (i=0; i< MAXH; i++) {
        if (CONFD_GET_INT64(headkey) == rfheads[i].dn) {
            pos = i;
            break;
        }
    }
    if (pos == -1) { /* pick first */
        for (i=0; i< MAXH; i++) {
            if (!rfheads[i].inuse) {
                pos = i;
                break;
            }
        }
    }
    fprintf(stderr, "Picking %d\n", pos);
    struct rfhead *hp = &rfheads[pos];
    if (cdb_cd(cdbsock, "/root/NodeB/RFHead{%x}", headkey) != CONFD_OK)
        confd_fatal("Failed to cd");
    hp->dn = CONFD_GET_INT64(headkey);
    hp->inuse = 1;
    if (cdb_get_str(cdbsock, hp->sector_id, 244, "SECTORID_ID") !=
        CONFD_OK)
        confd_fatal("Failed to get val");
    int n = cdb_num_instances(cdbsock, "Child");
    for(i=0; i<MAXC; i++) hp->children[i].inuse = 0;
    for(i=0; i<n; i++) {
        if (cdb_get_int64(cdbsock, &hp->children[i].dn,
                          "Child[%d]/cdn", i) != CONFD_OK)
            confd_fatal("Failed to get val");
        if (cdb_get_str(cdbsock, hp->children[i].childattr, 255,
                        "Child[%d]/childAttr", i) != CONFD_OK)
            confd_fatal("Failed to get val");
        hp->children[i].inuse = 1;
    }
}


/* read the entire db */

static void read_db(int cdbsock){
  int ret, i;
  confd_value_t key;

  if ((ret = cdb_start_session(cdbsock, CDB_RUNNING)) != CONFD_OK)
    confd_fatal("Cannot start session\n");
  if ((ret = cdb_set_namespace(cdbsock, mtest__ns)) != CONFD_OK)
    confd_fatal("Cnnot set namespace\n");

  int n = cdb_num_instances(cdbsock, "/root/NodeB/RFHead");
  for (i=0; i<MAXH; i++) rfheads[i].inuse = 0;
  for(i=0; i<n; i++) {
    if (cdb_get(cdbsock, &key, "/root/NodeB/RFHead[%d]/dn", i) !=
        CONFD_OK) confd_fatal("Can't get key");
    read_head(cdbsock, &key);
  }
  cdb_end_session(cdbsock);

}


static void dump_db(){
    int i, j;
    fprintf(stderr, "\nDumping \n");
    for (i=0; i< MAXH; i++) {
        if (!rfheads[i].inuse) continue;
        fprintf(stderr, "HEAD %d  <%s>\n", (int)rfheads[i].dn,
                rfheads[i].sector_id);
        for (j=0; j<MAXC; j++) {
            if (!rfheads[i].children[j].inuse)
                continue;
            fprintf(stderr, "   Child %d  <<%s>>\n",
                    (int)rfheads[i].children[j].dn,
                    rfheads[i].children[j].childattr
                );
        }
    }
    fprintf(stderr, "---------- \n");
}



int main(int argc, char **argv){

  struct sockaddr_in addr;
  int ret,c, cdbsock, sock;

  enum confd_debug_level dbgl = CONFD_SILENT;
  char *confd_addr = "127.0.0.1";
  int confd_port = CONFD_PORT;

  while ((c = getopt(argc, argv, "dtPa:p:")) != EOF) {
    switch (c) {
    case 'd':
      dbgl = CONFD_DEBUG;
      fprintf(stdout,"Change traceing to CONFD_DEBUG \n");
      break;
    case 't':
      dbgl = CONFD_TRACE;
      break;
    case 'P':
      dbgl = CONFD_PROTO_TRACE;
      fprintf(stdout,"Change traceing to CONFD_PROTO_TRACE \n");
      break;
    case 'a':
      confd_addr = optarg;
      break;
    case 'p':
      confd_port = atoi(optarg);
      break;
    }
  }

  addr.sin_addr.s_addr = inet_addr(confd_addr);
  addr.sin_family = AF_INET;
  addr.sin_port = htons(confd_port);

  confd_init(argv[0], stderr, dbgl);

  if ((sock = socket(PF_INET, SOCK_STREAM, 0)) < 0)
    confd_fatal("%s: Failed to create socket", argv[0]);


  /* if (confd_load_schemas((struct sockaddr*)&addr, */
  /*                        sizeof (struct sockaddr_in)) != CONFD_OK) */
  /*   confd_fatal("%s: Failed to load schemas from confd\n", argv[0]); */

  if (cdb_connect(sock, CDB_DATA_SOCKET, (struct sockaddr *)&addr,
                  sizeof(struct sockaddr_in)) != CONFD_OK){
    confd_fatal("%s: Failed to connect to ConfD", argv[0]);

  }

  if ((cdbsock = socket(PF_INET, SOCK_STREAM, 0)) < 0 ) {
    confd_fatal("Failed to open socket\n");
  }

  if (cdb_connect(cdbsock, CDB_READ_SOCKET, (struct sockaddr*)&addr,
                  sizeof (struct sockaddr_in)) < 0) {
    confd_fatal("Failed to cdb_connect() to confd \n");
  }

  if ((ret = cdb_start_session(cdbsock, CDB_RUNNING)) != CONFD_OK){
    confd_fatal("Cannot start session\n");
  }

  if ((ret = cdb_set_namespace(cdbsock, mtest__ns)) != CONFD_OK){
    confd_fatal("Cannot set namespace\n");
  }

  if(cdb_cd(cdbsock,"/mtest/a_number") != CONFD_OK){
    confd_fatal("Failed to cd");
  }

  return 0;

}

