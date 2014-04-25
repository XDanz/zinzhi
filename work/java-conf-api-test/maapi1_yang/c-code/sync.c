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
#include <confd.h>
#include <confd_lib.h>
#include <confd_maapi.h>
#include "ncs.h"
//confdc --emit-h ncs.h  $NCS_DIR/etc/ncs/tailf-ncs.fxs

#define OK(E) assert((E) == CONFD_OK)


int main(int argc, char **argv)
{
    int sock, tid;
    char *user = "admin";
    const char *groups[] = {"admin"};
    int debuglevel = CONFD_SILENT;
    char *context = "maapi";
    enum confd_dbname dbname = CONFD_RUNNING;
    struct sockaddr_in addr;
    struct confd_ip ip;
    confd_tag_value_t input[2];
    confd_tag_value_t *output;
    confd_tag_value_t *check_sync_output;
    int noutput = 0 ,noutput2 = 0;

    confd_init("MAAPI", stderr, debuglevel);

    addr.sin_addr.s_addr = inet_addr("127.0.0.1");
    addr.sin_family = AF_INET;
    addr.sin_port = htons(4569);

    if ((sock = socket(PF_INET, SOCK_STREAM, 0)) < 0 )
        confd_fatal("Failed to open socket\n");

    if ( maapi_connect(sock,(struct sockaddr*)&addr,sizeof(struct sockaddr_in))
         != CONFD_OK  )  {
        close ( sock );
        confd_fatal("Failed to connect() to NCS \n");
    }

    ip.af = AF_INET;
    inet_pton(AF_INET, "127.0.0.1", &ip.ip.v4);

    OK(maapi_load_schemas(sock));

    OK(maapi_start_user_session(sock, user, context, groups, 1,
                                &ip, CONFD_PROTO_TCP));

    if ((tid = maapi_start_trans(sock, dbname, CONFD_READ_WRITE)) < 0)
        confd_fatal("failed to start trans \n");


    OK( maapi_request_action_th (sock,tid,NULL,0,
                                 &check_sync_output,
                                 &noutput2,
                                 "/devices/device/{device0}/check-sync"));
    confd_value_t *val;
    int i_val = 0;

    val = CONFD_GET_TAG_VALUE (&check_sync_output[0]);
    switch ( CONFD_GET_ENUM_VALUE( val ) )
        {
        case ncs_in_sync_result:
            printf(" in sync! \n");
            break;
        case ncs_locked:
            printf(" locked! \n");
            break;
        case ncs_unknown:
            printf(" unknown! \n");
            break;

        case ncs_out_of_sync:
            printf( " out of sync ..");
            OK(maapi_request_action_th(sock,tid,NULL,0,&output,&noutput,
                                       "/devices/device{device0}/sync-from",
                                       ncs__ns));
            break;
        case ncs_unsupported:
            printf(" unsupported! \n");
            break;
        case ncs_back_logged:
            printf(" back logged! \n");
            break;
        case ncs_in_commit_queue:
            printf(" in commit queue! \n");
            break;
        }

    return 0;
}


