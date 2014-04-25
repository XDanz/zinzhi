
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


/* global variables */
static int sock, tid;
static char *user = "admin";
static const char *groups[] = {"admin"};
static int debuglevel = CONFD_SILENT;
static char *context = "maapi";
static enum confd_dbname dbname = CONFD_RUNNING;


void pval(confd_value_t *v){
  printf("in pval\n");

  if(v == NULL)
    printf("value was null.\n");

    char buf[BUFSIZ];
    confd_pp_value(buf, BUFSIZ, v);
    fprintf(stderr, "%s\n", buf);
}

int result(confd_hkeypath_t *kp, confd_value_t *v, void *state){

  printf("Inside result \n");

  return ITER_CONTINUE;

}

int trace(const char* str){
  printf("Inside result \n");
  return ITER_CONTINUE;
}


static int cnct(){

  struct sockaddr_in addr;
  struct confd_ip ip;

  addr.sin_addr.s_addr = inet_addr("127.0.0.1");
  addr.sin_family = AF_INET;
  addr.sin_port = htons(4565);

  if ((sock = socket(PF_INET, SOCK_STREAM, 0)) < 0 )
    confd_fatal("Failed to open socket\n");

  if (maapi_connect(sock, (struct sockaddr*)&addr,
                    sizeof (struct sockaddr_in)) < 0)
    confd_fatal("Failed to confd_connect() to confd \n");

  ip.af = AF_INET;
  inet_pton(AF_INET, "127.0.0.1", &ip.ip.v4);

  OK(maapi_load_schemas(sock));

  OK(maapi_start_user_session(sock, user, context, groups, 1,
                              &ip, CONFD_PROTO_TCP));

  if ((tid = maapi_start_trans(sock, dbname, CONFD_READ_WRITE)) < 0)
    confd_fatal("failed to start trans \n");
  return CONFD_OK;
}

static int err(){
  fprintf(stderr, "ERROR: %d %s\n", confd_errno,
          confd_strerror(confd_errno));
  exit(1);
}


/* paths must be ns prefixed as in     */
/* ./maapi -D /aaa:aaa/authentication  */

static int usage(){
  fprintf(stderr, "Usage: maapi <-e -path> |  <-p <path> [-v <strval>]\n");
  exit(1);
}


enum mop {
  M_NONE = 0,
  M_GET = 1,
  M_SET = 2,
  M_EXISTS = 3,
  M_DELETE = 4,
  M_APPLYTRANS = 5,
  X_EVAL = 6,
  GET_CASE = 7,
  SET_VALUES = 8
};


int main(int argc, char **argv){
  //printf("Inside main.\n");

  int c;
  char *path = NULL;
  char *valstr = NULL;
  //char *path2 = NULL;

  //char *expr = NULL;
  confd_value_t v;
  char buf[BUFSIZ];
  enum mop op = M_NONE;


  while ((c = getopt(argc, argv, "tdPSrcp:v:e:D:A:X:x:C:V:")) != -1) {
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
    case 'D':
      op = M_DELETE;
      path = optarg;
      break;
    case 'p':
      op = M_GET;
      path = optarg;
      break;
    case 'v':
      op = M_SET;
      valstr = optarg;
      break;
    case 'A':
      op = M_APPLYTRANS;
      //valstr = optarg;
      break;
    case 'e':
      op = M_EXISTS;
      path = optarg;
      printf("path:%s\n", path);
      break;
    case 'X':
      op = X_EVAL;
      path = optarg;
      printf("The path:%s\n", path);
      break;
    case 'x':
      valstr = optarg;
      printf("The path:%s\n", path);
      break;
    case 'C':
      op = GET_CASE;
      path = optarg;
      printf("(case C)The path:'%s'\n", path);
      break;
    case 'V':
      op = SET_VALUES;
      path = optarg;
      break;
    default:
      printf("Printing usage()\n");
      usage();
    }
  }

  if (path == NULL){
    printf("the path was null \n");
    usage();
  }
  confd_init("maapi", stderr, debuglevel);

  OK(cnct());
  printf("Connection done.\n");

  if (op == M_GET) {
    if (maapi_get_elem(sock, tid, &v, path) != CONFD_OK)
      err();
    confd_pp_value(buf, BUFSIZ, &v);
    printf("%s\n", buf);
  }
  else if (op == M_SET) {
    if (valstr == NULL)
      usage();
    if (maapi_set_elem2(sock, tid, valstr, path) != CONFD_OK)
      err();
    if (maapi_apply_trans(sock, tid, 0) != CONFD_OK)
      err();
    printf("OK\n");
  }
  else if (op == M_EXISTS) {
    int eval;
    eval = maapi_exists(sock, tid, path) == 1;
    if(eval == 1)
      printf("The path %s exists! (%d)\n",path,eval);
    else
      printf("The path %s does not exists! (%d)\n",path, eval);
  }
  else if (op == M_DELETE) {
    int eval;
    eval = maapi_delete(sock, tid, path);
    printf("%d\n", eval);
    eval = maapi_apply_trans(sock, tid, 0);
    printf("%d\n", eval);
  }else if(op == M_APPLYTRANS){
    int eval;
    eval = maapi_apply_trans(sock, tid, 0);
    eval = maapi_apply_trans(sock, tid, 0);
    printf("%d \n",eval);
  }else if(op == X_EVAL){
    char* initstate = "To some value\n";

    printf("path:%s  \n", path);
    printf("valstr:%s  \n", valstr);
    OK(maapi_xpath_eval(sock,tid,valstr,result,trace,initstate,path));
    printf("X_EVAL: %d \n", 1);

  }else if(op == GET_CASE){
    //char* path2 = "/mtest/food/case1/pretzel";
    //char* choice = "snack";
    //char buff[20];
    //    int len = 20;
    confd_value_t *rcase;

    //printf("cd..\n");
    //OK(maapi_cd(sock,tid,path2));
    //printf("cd..--> OK\n");
    //maapi_getcwd(sock,tid,len,buff);
    //printf("pwd:'%s'\n",buff);
    //OK(maapi_get_case(sock,tid,"snack",&rcase,"/mtest:mtest/));
    OK(maapi_set_elem2(sock,tid,"heineken","/mtest/food/case1/beer"));
    OK(maapi_get_case(sock, tid, "snack", &rcase, "/mtest:mtest/food"));
    pval(&rcase);
    //CONFD_GET_XMLTAG_NS(&rcase);
    //CONFD_GET_XMLTAG(&rcase);

    //printf("case: %s\n", rcase);

    //char buf[100];
    //confd_pp_value(buf, BUFSIZ, rcase);
    //fprintf(stderr, "case: %s\n", buf);

  }else if(op == SET_VALUES){
    confd_tag_value_t vals[20];//, vals2[20], vals3[20], vals4[20];
    //confd_value_t v;
    struct in_addr ip;

    int i, n;//, n2, n3, n4;
    //char *path0 = "/mtest/servers/server";
     char *path = "/mtest/servers/server{nisse}";
     i = 0;
     inet_pton(AF_INET,"127.0.0.1",&ip);
     CONFD_SET_TAG_IPV4(&vals[i], mtest_ip, ip);                    i++;
     CONFD_SET_TAG_UINT16(&vals[i], mtest_port, 22);                i++;
     CONFD_SET_TAG_XMLBEGIN(&vals[i], mtest_foo, mtest__ns);        i++;
     CONFD_SET_TAG_INT64(&vals[i], mtest_bar, 33);                  i++;
     CONFD_SET_TAG_INT64(&vals[i], mtest_baz, 44);                  i++;
     CONFD_SET_TAG_XMLEND(&vals[i], mtest_foo, mtest__ns);          i++;
     CONFD_SET_TAG_XMLBEGIN(&vals[i], mtest_interface, mtest__ns);  i++;
     CONFD_SET_TAG_STR(&vals[i], mtest_if_name, "eth0");               i++;
     CONFD_SET_TAG_INT64(&vals[i], mtest_mtu, 1492);                i++;
     CONFD_SET_TAG_XMLEND(&vals[i], mtest_interface, mtest__ns);    i++;
     CONFD_SET_TAG_XMLBEGIN(&vals[i], mtest_interface, mtest__ns);  i++;
     CONFD_SET_TAG_STR(&vals[i], mtest_if_name, "eth1");               i++;
     CONFD_SET_TAG_XMLEND(&vals[i], mtest_interface, mtest__ns);    i++;
    n = i;

    OK(maapi_set_values(sock, tid, vals, n, path));

  }
  return 0;
}


