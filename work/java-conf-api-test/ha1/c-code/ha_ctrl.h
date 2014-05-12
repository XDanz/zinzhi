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


#define ERR(s, doexit) do {                             \
        fatal(0, doexit, "%d: %s failed: %s (%d): %s",  \
              __LINE__, (s),                            \
              confd_strerror(confd_errno),              \
              confd_errno, confd_lasterr());            \
    } while (0)

#define OK(E) do {                              \
        if ((E) != CONFD_OK) {                  \
            ERR(#E, 1);                         \
        }                                       \
    } while (0)

#define IFNAMESIZE 2048

struct ha_common_t {
    struct sockaddr_in * vrtl_addr; // vips
    char secret_token [BUFSIZ];
    
};

struct ha_nodes_t {
    char name [BUFSIZ];
    int pref_master;
    struct sockaddr_in ip_addr;
    uint16_t port;
};




int cdb_read (struct sockaddr_in* addr , struct ha_nodes_t* nodes ,
    struct ha_common_t* common);

int maapi_read_ha_state ( struct sockaddr_in* addr ,int32_t *state );
