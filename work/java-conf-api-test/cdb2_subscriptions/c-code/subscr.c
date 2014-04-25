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

enum cdb_iter_ret iter ( confd_hkeypath_t *kp,
                         enum cdb_iter_op op,
                         confd_value_t *oldvalue,
                         confd_value_t *newvalue,
                         void *state ) ;

char * enum_to_str ( enum cdb_iter_op op );
int appendflag ( int *, char *);

int main(int argc, char *argv[])
{

    struct sockaddr_in addr;
    int point, status, subsock, c , debuglevel = CONFD_SILENT, flags;
    char *confd_addr = "127.0.0.1";
    int confd_port = CONFD_PORT;
    char test[100], path[BUFSIZ];
    char *sep = ",";
    char *word;

    addr.sin_addr.s_addr = inet_addr ( confd_addr );
    addr.sin_family = AF_INET;
    addr.sin_port = htons(confd_port);

    while (( c = getopt ( argc, argv, "Ptdf:p:" )) != -1 ) {
        switch (c) {
        case 'd':
            debuglevel = CONFD_DEBUG;
            break;
        case 't':
            debuglevel = CONFD_TRACE;
            break;
        case 'P':
            debuglevel = CONFD_PROTO_TRACE;
            break;
        case 'f':
            strcpy(test, optarg );

            word = strtok( test, sep );
            if ( word != NULL ) {
                appendflag ( &flags, word ) ;
                fprintf(stderr, " tok : %s \n", word);

                while( (word = strtok ( NULL, sep)) != NULL ) {
                    fprintf(stderr, " tok : %s \n" , word);
                    appendflag ( &flags, word);
                }

            }
            break;
        case 'p':
            strcpy ( path, optarg);
            break;
        }
    }


    confd_init ( argv[0],stderr,debuglevel );

    if ( confd_load_schemas ((struct sockaddr*)&addr,
                             sizeof ( struct sockaddr_in ) ) != CONFD_OK )
        confd_fatal("%s: Failed to load schemas from confd\n", argv[0]);

    if ((subsock = socket ( PF_INET,SOCK_STREAM, 0 )) < 0 )
        confd_fatal("Failed to cdb_connect() to confd \n");

    if (cdb_connect (subsock,CDB_SUBSCRIPTION_SOCKET, (struct sockaddr*)&addr,
                     sizeof ( struct sockaddr_in)) < 0 )
        confd_fatal("Failed to cdb_connect() to confd \n");

    if ((status = cdb_subscribe(subsock , 1, zb__ns ,
                                &point,
                                path)) != CONFD_OK)

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

            if ((status = cdb_diff_iterate ( subsock,
                                             sub_points[0],
                                             iter,
                                             ITER_WANT_PREV |
                                             ITER_WANT_SCHEMA_ORDER,NULL))
                != CONFD_OK ) {
                confd_fatal("terminate sub_read: %d\n", status);
            }

            if ((status = cdb_sync_subscription_socket(subsock,
                                                       CDB_DONE_PRIORITY))
                != CONFD_OK) {
                confd_fatal("failed to sync subscription: %d\n", status);
            }
        }
    }
}
enum cdb_iter_ret iter ( confd_hkeypath_t *kp,
                         enum cdb_iter_op op,
                         confd_value_t *oldvalue,
                         confd_value_t *newvalue,
                         void *state ) {
    fprintf(stderr, " ## iteration --> \n");
    char buf[BUFSIZ] , buf2[BUFSIZ], buf3[BUFSIZ];
    char app[BUFSIZ];

    memset ( buf , 0 , BUFSIZ);
    memset ( buf2, 0 , BUFSIZ);
    memset ( buf3, 0 , BUFSIZ);

    confd_pp_kpath(buf, BUFSIZ,kp );
    fprintf(stderr, "%s --> %s \n", buf, enum_to_str(op) );
    if ( op == MOP_VALUE_SET || op == MOP_MOVED_AFTER ) {
        if ( oldvalue != NULL)
            confd_pp_value( buf2, BUFSIZ, oldvalue );

        if ( newvalue != NULL) {
            while ( newvalue->type != C_NOEXISTS)  {
                confd_pp_value ( buf2, BUFSIZ, newvalue++);
                strcat(app, buf2);
                strcat(app, " ");
            }
        }
        fprintf (stderr, "%s --> from \"%s\" to \"%s\" \n", buf, app, buf3);
    }
    fprintf(stderr, " ## iteration -->  DONE \n");
    return ITER_RECURSE;
}

int appendflag ( int * flags, char * word ) {
    int ret = 0;
    if ( strcmp( word, "PREV"))
        *flags |= ITER_WANT_PREV;  ret++;
    if ( strcmp ( word, "ANCESTOR_DELETE"))
        *flags |= ITER_WANT_ANCESTOR_DELETE; ret++;
    if ( strcmp ( word, "SCHEMA_ORDER"))
        *flags |= ITER_WANT_SCHEMA_ORDER; ret++;
    if ( strcmp ( word, "LEAF_FIRST_ORDER"))
        *flags |= ITER_WANT_LEAF_FIRST_ORDER; ret++;
    if ( strcmp ( word, "LEAF_LAST_ORDER"))
        *flags |= ITER_WANT_LEAF_LAST_ORDER; ret++;
    if ( strcmp ( word, "ITER_WANT_REVERSE"))
        *flags |= ITER_WANT_REVERSE; ret++;
    return ret;
}

char * enum_to_str ( enum cdb_iter_op op ) {
    char * retstr = "NON";
    switch ( op ) {
    case MOP_CREATED:
        retstr = "MOP_CREATED";
        break;
    case MOP_DELETED:
        retstr = "MOP_DELETED";
        break;
    case MOP_MODIFIED:
        retstr = "MOP_MODIFIED";
        break;
    case MOP_VALUE_SET:
        retstr = "MOP_VALUE_SET";
        break;
    case MOP_MOVED_AFTER:
        retstr = "MOP_MOVED_AFTER";
        break;
    }
    return retstr;
}

