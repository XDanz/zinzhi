/*
 * Copyright 2005-2013 Tail-f Systems AB
 *
 * Permission to use this code as a starting point hereby granted
 *
 * Command line interface daemon High Availability Lite
 */
#include <stdio.h>
#include <stdlib.h>

#include <errno.h>
#include <string.h>
#include <ctype.h>

#include <unistd.h>
#include <sys/poll.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>


#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/un.h>
#include <net/if.h>
#include <assert.h>

#include <math.h>
#include <confd.h>
#include <confd_lib.h>
#include <confd_cdb.h>
#include <confd_maapi.h>
#include <confd_ha.h>
#include <confd_events.h>
#include "ha-controller-lite.h"
#include "ha_ctrl.h"


static const char *groups[] = {"admin"};
static char* progname;
static int debuglevel = CONFD_SILENT;


static void fatal(int err, int doexit, char *fmt, ...)
{
    va_list ap;

    fprintf(stderr, "%s: ", progname);
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    if (err != 0)
        fprintf(stderr, ": %s", strerror(err));
    fprintf(stderr, "\n");
    if (doexit)
        exit(doexit);
}

int main ( int argc, char *argv[])
{
    fprintf(stderr," Running had daemon \n");
    struct sockaddr_in in_addr, local_ip , other_ip;
    char if_name[IFNAMESIZE];
    struct ha_nodes_t ha_nodes[2];
    struct ha_common_t ha_common;
    int sockfd;
    char *ptr, buf[2048];

    struct ifconf ifc;
    struct ifreq *ifr;
    struct sockaddr_in *sinptr;


    char *addr = "127.0.0.1";

    int port = NCS_PORT;
    int ret_time = 500; // mesured in millis
    int c = -1;


    while (  (c = getopt ( argc, argv, "dp:t")) != EOF ) {
        switch (c) {
        case 'd':
            debuglevel++;
            break;
        case 'p':
            port = atoi(optarg);
            break;
        case 't':
            ret_time = atoi(optarg);
            break;
        default:
            fprintf( stderr ," Usage: <hafw> -d  -p <port> " );
            return 1;
        }
    }

    if ((progname = strrchr(argv[0], (int)'/')) == NULL)
        progname = argv[0];
    else
        progname++;

    confd_init (progname, stderr, debuglevel);

    memset(&in_addr,0,sizeof (struct sockaddr_in));
    in_addr.sin_addr.s_addr = inet_addr(addr);
    in_addr.sin_family = AF_INET;
    in_addr.sin_port = htons (port);
    fprintf(stderr, " Connecting to port: %d  \n", port);


    while (cdb_read (&in_addr,ha_nodes,&ha_common ) != CONFD_OK)
        usleep(ret_time);


    memset(&other_ip, 0 , sizeof (struct sockaddr_in));
    memset(&local_ip, 0 , sizeof (struct sockaddr_in));
    // determine my other ip
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    ifc.ifc_len = sizeof(buf);
    ifc.ifc_req = (struct ifreq *) buf;
    ioctl(sockfd, SIOCGIFCONF, &ifc);
    printf (" rec: %d bytes \n", ifc.ifc_len);
    int match = 0;
    for (ptr = buf; ptr < buf + ifc.ifc_len; ) {
        ifr = (struct ifreq *) ptr;
        //len = sizeof(struct sockaddr);

        // handle only ipv4 address
        if ( ifr->ifr_addr.sa_family == AF_INET ) {
            printf (" ifname: %s \n", ifr->ifr_name);
            sinptr = (struct sockaddr_in *)&ifr->ifr_addr;



            char lc_buf[100];
            char re_buf[100];
            inet_ntop(AF_INET, &sinptr->sin_addr, lc_buf, sizeof (lc_buf));
            inet_ntop(AF_INET, &ha_nodes[0].ip_addr.sin_addr, re_buf,
                      sizeof(re_buf));

            printf(" local %d (%s) , cmp: %d (%s)\n",
                   sinptr->sin_addr.s_addr,
                   lc_buf,
                   ha_nodes[0].ip_addr.sin_addr.s_addr,
                   re_buf
                );

            
            if (sinptr->sin_addr.s_addr == ha_nodes[0].ip_addr.sin_addr.s_addr ) 
                {
                    other_ip = ha_nodes[1].ip_addr;
                other_ip.sin_family = AF_INET;
                //other_ip.sin_port = ha_nodes.n2_port;
                other_ip.sin_port = ha_nodes[1].port;

                local_ip = ha_nodes[0].ip_addr; //.n1_ip;
                other_ip.sin_family = AF_INET;
                other_ip.sin_port = ha_nodes[0].port;

                strcpy(if_name , ifr->ifr_name);
                fprintf(stderr," using interface %s \n", ifr->ifr_name);
                match = 1;
                break;
                } 
             else if (sinptr->sin_addr.s_addr == 
                      ha_nodes[1].ip_addr.sin_addr.s_addr)
                 {
                     printf(" n2_ip is my ip \n");
                     other_ip = ha_nodes[0].ip_addr;
                     other_ip.sin_family = AF_INET;
                     other_ip.sin_port = ha_nodes[0].port;
                     
                     local_ip = ha_nodes[1].ip_addr; //n1_ip;
                     other_ip.sin_family = AF_INET;
                     other_ip.sin_port = ha_nodes[1].port;//n1_port;
                     
                     strcpy(if_name , ifr->ifr_name);
                     match = 1;
                     break;
                 }
        }

        ptr += sizeof ( struct ifreq );
    }

    if (!match)
        confd_fatal("could not determine local/other or if name \n");

    int32_t state;
    if ( maapi_read_ha_state (&other_ip, &state) == CONFD_OK ) {
        // we have read the state of /ncs:ncs-state/ha/state
    } else {
        // the other node is not responing
    }
    
    return 0;
}

int maapi_read_ha_state ( struct sockaddr_in* addr ,int32_t *state ) {
    char tmp[20];
    inet_ntop(AF_INET, &addr->sin_addr.s_addr,tmp, sizeof(tmp));
    fprintf(stderr, " connect to %s at port %d \n ", tmp,
            addr->sin_port);
    int s, tid;
    struct confd_ip ip;

    if ((s = socket(AF_INET, SOCK_STREAM, 0)) < 0)
        return -1;


    if (maapi_connect(s, (struct sockaddr*)addr,
                      sizeof(struct sockaddr_in)) != CONFD_OK) {
        close(s);
        return -1;
    }
    ip.af = AF_INET;
    inet_pton(AF_INET,"127.0.0.1", &ip.ip.v4);

    if ( maapi_start_user_session (s,"admin","maapi", groups,
                                   1 , &ip, CONFD_PROTO_TCP)  != CONFD_OK )
        confd_fatal( "Failed to start user session \n");


    if ((tid = maapi_start_trans( s, CONFD_RUNNING,  CONFD_READ)) < 0 )
        confd_fatal (" failed to start trans \n");

    OK( maapi_get_enum_value_elem(s, tid, state, "/ncs:ncs-state/ha/mode"));

    return 0;

}




int cdb_read (struct  sockaddr_in* in_addr ,struct ha_nodes_t* ha_nodes,
    struct ha_common_t * ha_common ) {
    int rsock;

    if ((rsock = socket ( PF_INET, SOCK_STREAM,0)) < 0 )
        return -1;

    OK(cdb_connect (rsock,CDB_DATA_SOCKET ,
                    (const struct sockaddr*)in_addr ,
                    sizeof (struct sockaddr)));

    while (1) {
        if (cdb_start_session(rsock, CDB_RUNNING) == CONFD_OK)
            break;
        if (confd_errno == CONFD_ERR_LOCKED) {
            sleep(1);
            continue;
        }
        return -1;
    }

    cdb_set_namespace(rsock, hcl__ns);
    confd_value_t *values;
    int i, n;

    OK(cdb_get_list(rsock, &values, &n,
                    "/hcl:ha-controller-lite/virtual-address/address"));
    if ( n > 0 ) {
        ha_common->vrtl_addr = malloc ( sizeof(struct sockaddr_in) * n );
        for ( i = 0; i < n; i++ ) {
            // todo fix ipv6
            ha_common->vrtl_addr[i].sin_addr = CONFD_GET_IPV4(&values[i]);
        }
    } else { 
        printf (" No virtual address found ! \n");
    }
    

    /* OK(cdb_get_ipv4(rsock, &ha_nodes->vrtl_addr.sin_addr, */
    /*                 "/hafw:ha-fw-lite/virtual-address/ipv4-address")); */

    /* OK(cdb_get_ipv4(rsock, &ha_nodes->vrtl_mask.sin_addr, */
    /*                 "/hafw:ha-fw-lite/virtual-address/ipv4-mask")); */

    OK(cdb_get_str(rsock,ha_common->secret_token,BUFSIZ,
                   "/hcl:ha-controller-lite/secret-token"));

    char *path = "/hcl:ha-controller-lite/ha-nodes/ha-node";
    n = cdb_num_instances(rsock, path);

    if ( n > 0 ) {
        ha_nodes = malloc( sizeof ( struct ha_nodes_t) * n );
        
        confd_value_t v[n*4];
        cdb_get_objects ( rsock, v, 4, 0 , n , path );
        for ( i = 0; i < n; ++i) {
            confd_pp_value (ha_nodes[i].name, 
                            sizeof ( ha_nodes[i].name),
                            &v[i]);
            confd_free_value(&v[i]);

            /* strcpy(ha_nodes[i].name , &v[i+1]); */
            /* &ha_nodes[i]->pref_master  &v[i+2]*/
            ha_nodes[i].ip_addr.sin_addr =  CONFD_GET_IPV4(&v[i+3]);
            ha_nodes[i].port = CONFD_GET_UINT16(&v[i+4]);

        }
    }
            
            // read n1 values
      /*       OK(cdb_get_str(rsock, */
    /*                        ha_nodes[i]->nname, BUFSIZ, */
    /*                        "/hcl:ha-controller-lite/nodes/node-n1/host-name")); */

    /* OK(cdb_get_ipv4(rsock,&ha_nodes->n1_ip.sin_addr, */
    /*                 "/hcl:ha-controller-lite/nodes/node-n1/ipv4-address")); */
    /* char tmp[20]; */
    /* inet_ntop(AF_INET, &ha_nodes->n1_ip.sin_addr, tmp, sizeof (tmp)); */
    /* fprintf(stderr, " node_n1ip is %s \n", tmp); */

    /* OK(cdb_get_u_int16(rsock ,&ha_nodes->n1_port, */
    /*                    "/hafw:ha-fw-lite/nodes/node-n1/port")); */

    /* // read n2 values */
    /* OK(cdb_get_str(rsock,ha_nodes->n2_name, BUFSIZ, */
    /*                "/hafw:ha-fw-lite/nodes/node-n2/host-name")); */

    /* OK(cdb_get_ipv4(rsock ,&ha_nodes->n2_ip.sin_addr , */
    /*                 "/hafw:ha-fw-lite/nodes/node-n2/ipv4-address")); */

    /* OK(cdb_get_u_int16(rsock ,&ha_nodes->n2_port, */
    /*                    "/hafw:ha-fw-lite/nodes/node-n2/port")); */

    cdb_end_session (rsock);
    close(rsock);
    return CONFD_OK;
}


