#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <signal.h>
#include <sys/poll.h>

#include <sys/types.h>
#include <sys/socket.h>

#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/un.h>

#include <assert.h>

#include <confd.h>
#include <confd_cdb.h>
#include <confd_maapi.h>
#include "zub.h"


int main(int argc, char *argv[])
{

    struct sockaddr_in addr;
    int point, status, subsock;
    char *confd_addr = "127.0.0.1";
    int confd_port = CONFD_PORT;

    addr.sin_addr.s_addr = inet_addr ( confd_addr );
    addr.sin_family = AF_INET;
    addr.sin_port = htons(confd_port);

    confd_init ( argv[0],stderr,CONFD_DEBUG);

    if ( confd_load_schemas ((struct sockaddr*)&addr,
                             sizeof ( struct sockaddr_in ) ) != CONFD_OK )
        confd_fatal("%s: Failed to load schemas from confd\n", argv[0]);

    if ((subsock = socket ( PF_INET,SOCK_STREAM, 0 )) < 0 )
        confd_fatal("Failed to cdb_connect() to confd \n");

    if (cdb_connect (subsock,CDB_SUBSCRIPTION_SOCKET, (struct sockaddr*)&addr,
                     sizeof ( struct sockaddr_in)) < 0 )
        confd_fatal("Failed to cdb_connect() to confd \n");

    if ((status = cdb_subscribe(subsock , 1, zb__ns , &point,
              "/zconfigzub/buzz-interfaces/buzz/servers/server")) != CONFD_OK)
        confd_fatal("Terminate: subscribe %d\n", status);

    if (cdb_subscribe_done(subsock) != CONFD_OK)
        confd_fatal("cdb_subscribe_done() failed");

    while ( 1 ) {
        int status;
        struct pollfd set[1];

        set[0].fd = subsock;
        set[0].events = POLLIN;
        set[0].revents = 0;

        if (poll(&set[0], sizeof(set)/sizeof(*set), -1) < 0) {
            if (errno != EINTR) {
                perror("Poll failed:");
                continue;
            }
        }

        if ( set[0].revents & POLLIN ) {
            int sub_points[1];
            int res_len;
            if ((status = cdb_read_subscription_socket(subsock,
                                                       &sub_points[0],
                                                       &res_len)) != CONFD_OK) {
                confd_fatal("terminate sub_read: %d\n", status);
            }

            int j, nvals;

            confd_tag_value_t *val;

            if (cdb_get_modifications(subsock, sub_points[0],
                                     CDB_GET_MODS_INCLUDE_LISTS,
                                     &val, &nvals,
                    "/zconfigzub/buzz-interfaces/buzz/servers/server{www}")
                != CONFD_OK )
            confd_fatal("Error cdb_get_modifications \n");

            for (j=0; j<nvals; j++) {
                char tmpbuf[BUFSIZ] = { 0 };
                confd_pp_value(tmpbuf, BUFSIZ, CONFD_GET_TAG_VALUE(&val[j]));
                printf("%s %s\n", confd_hash2str(CONFD_GET_TAG_TAG(&val[j])),
                       tmpbuf);
            }
            free(val);


            if ((status = cdb_sync_subscription_socket(subsock,
                                                       CDB_DONE_PRIORITY))
                != CONFD_OK) {
                confd_fatal("failed to sync subscription: %d\n", status);
            }
        }
    }
}
