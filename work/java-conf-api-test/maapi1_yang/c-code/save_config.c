#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#include <err.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#include <confd_lib.h>
#include <confd_maapi.h>
#include <confd_cdb.h>


static int get_maapisock(struct addrinfo *addr);

int
main ( int argc , char ** argv )
{
    char confd_port[16];
    struct addrinfo hints;
    struct addrinfo *addr = NULL;
    const char *groups[] = {"admin"};
    struct confd_ip ip;
    int c, debuglevel, sock, tid, id, format = 0;

    int  streamsock;
    FILE * fpout = NULL;
    char *p;
    char path[80];
    char file[100];
    int error;

    memset(&hints, 0, sizeof(hints));
    snprintf(confd_port, sizeof(confd_port), "%d", CONFD_PORT);

    hints.ai_family = PF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;

    while ((c = getopt(argc, argv, "rdtc:f:p:o:x")) != -1) {
        switch (c) {
        case 'd':
            debuglevel = CONFD_DEBUG;
            break;
        case 't':
            debuglevel = CONFD_TRACE;
            break;
        case 'r':
            debuglevel = CONFD_PROTO_TRACE;
            break;
        case 'f':
            if (strcmp(optarg,"XML") == 0)
                format |= MAAPI_CONFIG_XML_PRETTY;
            if (strcmp(optarg,"J") == 0)
                format |= MAAPI_CONFIG_J;
            if (strcmp(optarg,"IOS") == 0)
                format |= MAAPI_CONFIG_C_IOS;
            if (strcmp(optarg,"XR") == 0)
                format |= MAAPI_CONFIG_C;
            break;
        case 'x':
            format |= MAAPI_CONFIG_XPATH;
            break;
        case 'p':
            if ( strcpy ( path  , optarg) == NULL) {
                fprintf(stderr, "argument %s could not be determine \n",optarg);
                exit (1);
            }
            break;

        case 'o':
            if ( strcpy ( file , optarg ) == NULL ) {
                fprintf(stderr, "argument %s could not be determine \n",optarg);
                exit (1);
            }
            break;
        case 'c':
            if ((p = strchr(optarg, '/')) != NULL)
                *p++ = '\0';
            else
                p = confd_port;

            error = getaddrinfo(optarg,p ,&hints,&addr);

            if (error != 0) {
                fprintf(stderr,"err :  %s \n" , gai_strerror(error));
                if (p != confd_port) {
                    *--p = '/';
                    p = "/port";
                } else {
                    p = "";
                }
                fprintf(stderr, "%s: Invalid address%s: %s\n",
                        argv[0], p, optarg);
                errx(1, "%s \n", gai_strerror(error));

                exit(1);
            }
            break;
        default:
            fprintf(stderr,
                    "Usage: %s [-dtpr] [-c address[/port]]\n",
                    argv[0]);
            exit(1);
        }
    }

    if ( addr == NULL) {
        fprintf(stderr, "addr not filled \n");
        exit (1);
    } else
        fprintf(stderr," OK \n");


    confd_init ( "maapi", stderr, debuglevel);

    if ( (sock = get_maapisock ( addr ))  < 0)
        confd_fatal(" Failed to confd_connect() to confd \n");

    ip.af = AF_INET;
    inet_pton(AF_INET,"127.0.0.1", &ip.ip.v4);
    if ( maapi_start_user_session ( sock , "admin", "maapi", groups,
                                    1 , &ip, CONFD_PROTO_TCP)  != CONFD_OK )
        confd_fatal( "Failed to start user session \n");


    if ((tid = maapi_start_trans( sock, CONFD_RUNNING,  CONFD_READ_WRITE)) < 0 )
        confd_fatal (" failed to start trans \n");

    if ( path == NULL ) {
        fprintf(stderr," No path given to save_config \n");
        exit (1);
    }

    id = maapi_save_config ( sock , tid, format , path );

    if ( (streamsock  = socket ( PF_INET,SOCK_STREAM,0)) < 0)
        confd_fatal("failed to open streamsock \n");

    if (  confd_stream_connect ( streamsock, (struct sockaddr*)addr->ai_addr,
                                 sizeof ( struct sockaddr_in ) ,id, 0) < 0 )
        confd_fatal ( "failed to stream_connect() to confd \n");

    char buf[BUFSIZ];

    assert ( (fpout = fopen (file, "w")) != NULL);
    int ack = 0;

    int reading = 1;
    while ( reading ) {
        int rval;
        rval = read ( streamsock , buf, BUFSIZ);

        ack += rval;

        if ( rval == 0 ) {
            close ( streamsock );
            if ( fpout != NULL )
                fclose ( fpout );
            if ( maapi_save_config_result( sock , id ) != CONFD_OK )
                confd_fatal (" failed to save configuration \n");

            reading = 0;

        }
        buf[rval] = 0;
        if (fpout != NULL)
            fprintf(fpout, "%s", buf);
    }
    fprintf (stderr," Wrote %d bytes to file : %s \n", ack, file );
    exit (0);
}

static int get_maapisock(struct addrinfo *addr)
{
    int sock ;
    if ((sock = socket(addr->ai_family,addr->ai_socktype,addr->ai_protocol))
        < 0)
        return -1;

    if (maapi_connect(sock, addr->ai_addr,addr->ai_addrlen) != CONFD_OK) {
        close(sock);
        return -1;
    }
    return sock;
}
