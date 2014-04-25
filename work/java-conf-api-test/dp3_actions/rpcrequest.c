
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <sys/poll.h>

#include <confd.h>
#include <confd_maapi.h>

#include "mathrpc.h"



static int get_maapisock(struct addrinfo *addr){
  int sock;

  if ((sock =
       socket(addr->ai_family, addr->ai_socktype, addr->ai_protocol)) < 0)
    return -1;
  if (maapi_connect(sock,
                    addr->ai_addr, addr->ai_addrlen) != CONFD_OK) {
    close(sock);
    return -1;
  }
  return sock;
}



static void request_action(struct addrinfo *addr){
  int sock;
  const char *groups[1];
  struct confd_ip ip;
  confd_tag_value_t *values;
  int nvalues = 0, i,j;
  int th;

  assert((sock = get_maapisock(addr)) >= 0);
  ip.af = AF_INET;
  inet_pton(AF_INET, "10.0.0.33", &ip.ip.v4);
  assert(maapi_start_user_session(sock, "admin", "cli", groups, 0,
                                  &ip, CONFD_PROTO_SSH) == CONFD_OK);

  j = 0;
  confd_value_t ops[2];
  CONFD_SET_INT32(&ops[j++],10);
  CONFD_SET_INT32(&ops[j++],20);


  i = 0;
  confd_tag_value_t params[3];
  CONFD_SET_TAG_XMLBEGIN(&params[i++],math_add,math__ns);
  CONFD_SET_TAG_LIST(&params[i++],math_operand,&ops[0],2);
  CONFD_SET_TAG_XMLEND(&params[i++],math_add,math__ns);


  assert((th = maapi_start_trans(sock, CONFD_RUNNING, CONFD_READ)) >= 0);

  assert(maapi_set_namespace(sock, th, math__ns) == CONFD_OK);


  assert(maapi_request_action_th(sock, th, params, 3,
                                 &values, &nvalues, "/math") == CONFD_OK);
  assert(maapi_finish_trans(sock, th) == CONFD_OK);


}


int main(int argc, char **argv){
  printf("starting .. \n");
  char confd_port[16];
  struct addrinfo hints;
  struct addrinfo *addr = NULL;
  int debuglevel = CONFD_SILENT;
  debuglevel = CONFD_PROTO_TRACE;
  confd_init("maapi",stderr,debuglevel);
  snprintf(confd_port, sizeof(confd_port), "%d", CONFD_PORT);
  memset(&hints, 0, sizeof(hints));
  hints.ai_family = PF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;


  if (addr == NULL &&
      getaddrinfo("127.0.0.1", confd_port, &hints, &addr) != 0)

    printf("request action.. \n");
    request_action(addr);
    printf("request action..OK \n");
  exit(0);


}
