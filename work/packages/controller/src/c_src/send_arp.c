/*
 * I have turned this program from the hacking tool into simple utility
 * that allows you to send gratuitous arp requests for the interface.
 * It will pick up the MAC and IP from the given interface and advertise
 * it to the network. It works with alias interfaces as well.
 * This is useful in all sorts of failover situations where
 * - One interface completely takes over the MAC of another
 * - Alias IP is assigned to a different interface and has to be
 *   advertised as such.
 * 
 * I didn't do much - just added a couple of lines of code. It only
 * works verifiably on Linux.
 * Questions probably should go to original author, but if you really
 * want you can talk to me as well. Ths program comes as a free software
 * to be distributed in any form, binary or source without warranty
 * of any kind. Use at your own risk.
 *
 * To compile:
 * 	gcc -o send_arp send_arp.c
 *
 * 2001 (c) Ugen ugen@xonix.com
 */ 

/* send_arp.c

   This program sends out one ARP packet with source/target IP and Ethernet
   hardware addresses suuplied by the user.  It compiles and works on Linux
   and will probably work on any Unix that has SOCK_PACKET.

   The idea behind this program is a proof of a concept, nothing more.  It
   comes as is, no warranty.  However, you're allowed to use it under one
   condition: you must use your brain simultaneously.  If this condition is
   not met, you shall forget about this program and go RTFM immediately.

   yuri volobuev'97
   volobuev@t1.chem.umn.edu

*/

#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/types.h>
#if 0
#	include <linux/in.h>
#endif
#include <netinet/in.h>
#include <net/if.h>
#include <netinet/if_ether.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <linux/sockios.h>

#ifdef linux
#	define	NEWSOCKET()  socket(AF_INET, SOCK_PACKET, htons(ETH_P_RARP))
#else
#	define	NEWSOCKET()  socket(SOL_SOCKET, SOCK_RAW, ETHERTYPE_REVARP)
#endif

#define MAC_ADDR_LEN 6
#define IP_ADDR_LEN 4
#define ARP_FRAME_TYPE 0x0806
#define ETHER_HW_TYPE 1
#define IP_PROTO_TYPE 0x0800
#define OP_ARP_REQUEST	1
#define OP_ARP_REPLY	2


char usage[] = {
    "Usage: send_arp <ifname>\n\tExample:\tsend_arp eth0\n\t\t\tsend_arp eth1:1"};

struct arp_packet {
        u_char targ_hw_addr[MAC_ADDR_LEN];
        u_char src_hw_addr[MAC_ADDR_LEN];
        u_short frame_type;
        u_short hw_type;
        u_short prot_type;
        u_char hw_addr_size;
        u_char prot_addr_size;
        u_short op;
        u_char sndr_hw_addr[MAC_ADDR_LEN];
        u_char sndr_ip_addr[IP_ADDR_LEN];
        u_char rcpt_hw_addr[MAC_ADDR_LEN];
        u_char rcpt_ip_addr[IP_ADDR_LEN];
        u_char padding[18];
};

void die(const char *);
void get_ip_addr(struct in_addr*,char*);
void get_hw_addr(u_char*,char*);

int	ioctl_sock;

int opt_d = 0;

int
main(int argc, char** argv)
{

    struct in_addr my_in_addr;
    struct arp_packet pkt;
    struct sockaddr sa;
    int	optypes [] = {OP_ARP_REQUEST, OP_ARP_REPLY, 0}; 
    int sock;
    u_char hwaddr[MAC_ADDR_LEN];
    int	j, i;
    char *ifname, *ifsend, *cindex;
    char ch;


    if (argc < 2) {
        die(usage);
    }

    while ((ch = getopt(argc, argv, "d")) != -1)
        switch(ch) {
        case 'd':               /* undocumented: POSIX compatibility */
            opt_d = 1;
            break;
        case '?':
        default:
            goto args;
        }

  args:   argc -= optind;
    argv += optind;


    /* 
     * Linux is finicky this way - once you run an ioctl to get
     * mac address using the socket - you can't use this socket to
     * send anything. Also, when i try to repeat the ioctl - i can't
     * send through the other socket either. BIZARRE!!
     * So I make two sockets and use them as little as possible :)))
     */
    sock=NEWSOCKET();
    ioctl_sock = NEWSOCKET();

    if ((sock < 0) || (ioctl_sock < 0)) {
        perror("Unable to create socket");
        exit(1);
    }

    /* Most switches/routers respond to the ARP reply, a few only
     * to an ARP request.  RFCs say they should respond
     * to either.  Oh well... We'll try and work with all...
     * So, we broadcast both an ARP request and a reply...
     * See RFCs 2002 and 826.
     *
     * The observation about some only responding to ARP requests
     * came from Masaki Hasegawa <masaki-h@pp.iij4u.or.jp>.
     * So, this fix is due largely to him.
     */
	
    if (!(ifname = strdup(*argv)) || !(ifsend = strdup(*argv)))
        die("Cannot duplicate interface name\n");

    /*
     * For an alias interface we use its data but we send
     * the actual packet on corresponding real interface
     */
    if (cindex = strchr(ifsend, ':')) 
        *cindex = '\0';

    if (opt_d) {
        printf("Interface: %s\n", ifname);
    }

    get_hw_addr(hwaddr, ifname);
    get_ip_addr(&my_in_addr, ifname);
	

    for (j=0; optypes[j] != 0 ; j++) {
        pkt.frame_type = htons(ARP_FRAME_TYPE);
        pkt.hw_type = htons(ETHER_HW_TYPE);
        pkt.prot_type = htons(IP_PROTO_TYPE);
        pkt.hw_addr_size = MAC_ADDR_LEN;
        pkt.prot_addr_size = IP_ADDR_LEN;
        pkt.op=htons(optypes[j]);

        for (i = 0; i < MAC_ADDR_LEN; i++)
            pkt.targ_hw_addr[i] = 0xff;

        for (i = 0; i < MAC_ADDR_LEN; i++)
            pkt.rcpt_hw_addr[i] = 0xff;

        for (i = 0; i < MAC_ADDR_LEN; i++)
            pkt.src_hw_addr[i] = hwaddr[i];

        for (i = 0; i < MAC_ADDR_LEN; i++)
            pkt.sndr_hw_addr[i] = hwaddr[i];

        memcpy(pkt.sndr_ip_addr,&my_in_addr,IP_ADDR_LEN);
        memcpy(pkt.rcpt_ip_addr,&my_in_addr,IP_ADDR_LEN);

        memset(pkt.padding,0, 18);

        strcpy(sa.sa_data, ifsend);
        if (sendto(sock, (const void *)&pkt, sizeof(pkt), 0,
                   &sa,sizeof(sa)) < 0) {
            perror("Unable to send");
            exit(1);
        }
    }
    exit(0);
}

void die(const char* str)
{
    fprintf(stderr,"Error: %s\n",str);
    exit(1);
}


void get_ip_addr(struct in_addr* in_addr,char* str)
{
    struct ifreq ifr;
    struct sockaddr_in sin;

    strcpy(ifr.ifr_name, str);
    ifr.ifr_addr.sa_family = AF_INET;
    if (ioctl(ioctl_sock, SIOCGIFADDR, &ifr))
        die("Failed to get IP address for the interface");


    memcpy(&sin, &ifr.ifr_addr, sizeof(struct sockaddr_in));
    in_addr->s_addr = sin.sin_addr.s_addr;
    if (opt_d)
        printf("IP address: %s\n", inet_ntoa(*in_addr));
}

void get_hw_addr(u_char* buf,char* str)
{

    int i;
    struct ifreq ifr;
    char c,val = 0;

    strcpy(ifr.ifr_name, str);
    if (ioctl(ioctl_sock, SIOCGIFHWADDR, &ifr))
        die("Failed to get MAC address for the interface");

    memcpy(buf, ifr.ifr_hwaddr.sa_data, 8);
    if (opt_d)
        printf("MAC address: %0.2X:%0.2X:%0.2X:%0.2X:%0.2X:%0.2X\n", *(buf), *(buf+1), *(buf+2), *(buf+3),*(buf+4), *(buf+5));
}

