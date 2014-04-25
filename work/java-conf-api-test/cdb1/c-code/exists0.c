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
#include "optest.h"


int
main(int argc, char **argv)
{
  struct sockaddr_in addr;
  int ret,c, sock;

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

  if (cdb_connect(sock, CDB_DATA_SOCKET, (struct sockaddr *)&addr,
                  sizeof(struct sockaddr_in)) != CONFD_OK) {
      confd_fatal("%s: Failed to connect to ConfD", argv[0]);
  }

  if ((ret = cdb_start_session(sock, CDB_RUNNING)) != CONFD_OK) {
      confd_fatal("Cannot start session\n");
  }

  int num = 0;

  num = cdb_num_instances ( sock , "/optest/shirts/shirt");
  fprintf(stderr, " num instances: %d \n", num );


  int exists = -1;
  if ((exists =  cdb_exists ( sock, "/optest/shirts/shirt[0]" )) == CONFD_ERR)
      confd_fatal(" error:  %s \n", confd_lasterr());

  if ( exists )
      fprintf (stderr, " exists!\n");
  else
      fprintf (stderr, " no exists! \n");

  return 0;

}

