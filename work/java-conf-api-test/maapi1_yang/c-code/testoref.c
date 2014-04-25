#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/poll.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <stdarg.h>


#include <stdio.h>

#include "confd.h"
#include "confd_maapi.h"
#include "mtest.h"

#define OK(E) assert((E) == CONFD_OK)

static int sock,tid;
static char *user = "admin";
static char *context = "maapi";
static const char *groups[] = {"admin"};
static enum confd_dbname dbname = CONFD_RUNNING;
static int debuglevel = CONFD_SILENT;

static int cnct() {

  struct sockaddr_in addr;
  struct confd_ip ip;

  addr.sin_addr.s_addr = inet_addr("127.0.0.1");
  addr.sin_family = AF_INET;
  addr.sin_port = htons(4565);

  if ( (sock = socket(PF_INET, SOCK_STREAM, 0)) < 0 )
    confd_fatal("Failed to open socket\n");

  if (maapi_connect(sock, (struct sockaddr*)&addr,
                    sizeof (struct sockaddr_in)) < 0)
      confd_fatal("Failed to confd_connect() to confd \n");

  ip.af = AF_INET;
  inet_pton(AF_INET, "127.0.0.1", &ip.ip.v4);

  //OK(maapi_load_schemas(sock));

  OK(maapi_start_user_session(sock, user, context, groups, 1,
                              &ip, CONFD_PROTO_TCP));

  if ((tid = maapi_start_trans(sock, dbname, CONFD_READ_WRITE)) < 0)
    confd_fatal("failed to start trans \n");
  return CONFD_OK;
}

int main(int argc, char **argv){
  int c;
  //char *key_www = "www";
  //char cwd[256];
  //  confd_value_t vkey_100;
  //  confd_value_t vkey_www;
  //char *path = NULL;
  //char *valstr = NULL;
  //char *path2 = NULL;

  //char *expr = NULL;

  //char buf[BUFSIZ];
  //enum mop op = M_NONE;


  while ((c = getopt(argc, argv, "tdPSrc")) != -1) {
    switch(c) {
    case 'r':
      dbname = CONFD_RUNNING;
      break;
    case 't':
      debuglevel = CONFD_TRACE;
      break;
    case 'd':
      debuglevel = CONFD_DEBUG;
      break;
    case 'P':
      debuglevel = CONFD_PROTO_TRACE;
      break;
    case 'S':
      debuglevel = CONFD_SILENT;
      break;
    case 'c':
      dbname = CONFD_CANDIDATE;
      break;
    default:
      debuglevel = CONFD_SILENT;
    }
  }

  confd_init("maapi", stderr, debuglevel);

  OK(cnct());
  fprintf(stderr,"Connection done.\n");

  OK(maapi_set_namespace(sock,tid,mtest__ns));

  confd_value_t v;
  confd_hkeypath_t hkp;
  int n;
  char buf2[400];
  hkp.v[4][0].type = mtest_mtest;
  hkp.v[4][0].type = C_XMLTAG;
  hkp.v[4][0].val.xmltag.tag = mtest_mtest;
  hkp.v[4][0].val.xmltag.ns = mtest__ns;
  hkp.v[4][1].type = C_NOEXISTS;



  hkp.v[3][0].type = C_XMLTAG;
  hkp.v[3][0].val.xmltag.tag = mtest_servers;
  hkp.v[3][0].val.xmltag.ns = mtest__ns;
  hkp.v[3][1].type = C_NOEXISTS;

  hkp.v[2][0].type = C_XMLTAG;
  hkp.v[2][0].val.xmltag.tag = mtest_server;
  hkp.v[2][0].val.xmltag.ns = mtest__ns;
  hkp.v[2][1].type = C_NOEXISTS;

  hkp.v[1][0].type = C_BUF;
  hkp.v[1][0].val.buf.size = 4;
  hkp.v[1][0].val.buf.ptr = (unsigned char*)"smtp";
  hkp.v[1][1].type = C_NOEXISTS;

  hkp.v[0][0].type = C_XMLTAG;
  hkp.v[0][0].val.xmltag.tag = mtest_ip;
  hkp.v[0][0].val.xmltag.ns = mtest__ns;
  hkp.v[0][1].type = C_NOEXISTS;



  /* end marker */
  hkp.v[6][0].type = C_NOEXISTS;
  hkp.len = 5;

  n = confd_pp_kpath(buf2, BUFSIZ, &hkp);
  fprintf(stderr,"n = %d len=%zu buf=\"%s\"\n", n, strlen(buf2), buf2);



  v.type = C_OBJECTREF;
  v.val.hkp = &hkp;

  OK(maapi_set_elem(sock,tid,&v,"/mtest/types/objectref"));

  return 0;

}
