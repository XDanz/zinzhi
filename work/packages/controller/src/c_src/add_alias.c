#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <errno.h>
#include <stdlib.h>

int skfd;
int set_ip_using(const char *name, int c, unsigned long ip)
{
    struct ifreq ifr;
    struct sockaddr_in sin;

    strncpy(ifr.ifr_name, name, IFNAMSIZ);
    memset(&sin, 0, sizeof(struct sockaddr));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = ip;
    memcpy(&ifr.ifr_addr, &sin, sizeof(struct sockaddr));
    if (ioctl(skfd, c, &ifr) < 0)
        {
            printf("failed to set ip-address\n");
            return 1;
        }
    return 0;
}
int main ( int argc, char **argv )
{
    char *ip_address=NULL;
    char *interface;
    if ( argc==3 )
        {
            struct sockaddr_in sin;
            unsigned long ip;
            ip_address=argv[2];
            interface = argv[1];
            skfd = socket( AF_INET, SOCK_DGRAM, 0);
            if ( skfd == -1 )
                {
                    printf("Couldn't open socket\n");
                    exit(0);
                }
            inet_pton( AF_INET, ip_address, &sin );
            memcpy(&ip, &sin.sin_addr.s_addr, sizeof(unsigned long));  
            set_ip_using( interface,SIOCSIFADDR,ip );
            close( skfd );
        }
    else
        printf("usage: setip <interface-name> <ip-address>\n");
}
