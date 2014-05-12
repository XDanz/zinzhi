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
#include <netdb.h>

#include "confd.h"
#include "confd_maapi.h"

void pval(confd_value_t *v);

#define OK(E) assert((E) == CONFD_OK)

int main(int argc, char** argv)
{
    int sock,th,eval;
    struct maapi_cursor mc;
    struct confd_ip ip;
    struct sockaddr_in addr;

    int debuglevel = CONFD_PROTO_TRACE;

    const char *groups[] = {"admin"};

    addr.sin_addr.s_addr = inet_addr("127.0.0.1");
    addr.sin_family = AF_INET;
    addr.sin_port = htons(4565);

    confd_init("MYNAME", stderr, (enum confd_debug_level)debuglevel);
    if((sock = socket(PF_INET,SOCK_STREAM,0)) < 0)
        confd_fatal("Could not open socket \n");

    if(maapi_connect(sock,(struct sockaddr*)&addr,
                     sizeof(struct sockaddr_in)) < 0)
        confd_fatal("Failed to connect to NCS \n");

    ip.af = AF_INET;
    inet_pton(AF_INET,"66.55.44.33", &ip.ip.v4);

    if ( maapi_load_schemas(sock) != CONFD_OK) {
        printf (" load schemas ok \n");
        return 1;
    }
    
    if (maapi_start_user_session(
            sock,"admin","maapi",
            groups,1,&ip,CONFD_PROTO_TCP) != CONFD_OK)
        confd_fatal("Failed new usess\n");
    
    if (( th = maapi_start_trans(sock,CONFD_RUNNING,CONFD_READ)) < 0)
        confd_fatal("Failed to set ns\n");


    maapi_init_cursor(sock, th, &mc,
                      "/navu-test/config/long-key/long-key-list");
    ///keylezz/keyless/keyless-list");
        while (1) {
            eval = maapi_get_next(&mc);
            if (mc.n == 0)
                break;
            if (eval == CONFD_ERR)
                break;
        /* fprintf(stderr, "XXX getnext: eval = %d, Got next value ",eval); */
        /* confd_value_t *keys = &(mc.keys); */
            maapi_exists ( sock, th , 
                           "/bulk:special/bklist/{%x}", 
                           &mc.keys[0]);
            
        /* pval(&(mc.keys[0])); */
        }
    return 0;
}

void pval(confd_value_t *v)
{
    char buf[BUFSIZ];
    confd_pp_value(buf, BUFSIZ, v);
    fprintf(stderr, "%s\n", buf);
}

