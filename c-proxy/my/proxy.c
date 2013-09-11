/*
 * proxy.c - CS:APP Web proxy
 *
 * TEAM MEMBERS:
 *     Tommy Hagberg, thagberg@kth.se
 *     Daniel Terranova dtc@kth.se
 */

// A small concurrent web-proxy that logs the
// requests between browser and end server in
// proxy.log
// Works quite fine with Firefox but strange errors
// with other browsers
#include "csapp.h"
#include <stdarg.h>

typedef struct {
    pthread_t thread_tid;
    long      thread_count;
    FILE      *dbgfp;
} Thread;

Thread *tptr;
void thread_make(int i);

#define MAXNCLI 32
#define NTHREADS 10

int clifd[MAXNCLI];
int iget, iput;

static int proxy_log_fd = -1;
static const char *file ="proxy.log";




pthread_mutex_t clifd_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t clifd_cond = PTHREAD_COND_INITIALIZER;

/*
 * Function prototypes
 */
int parse_uri(char *uri, char *target_addr, char *path, int  *port);
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr,
		      char *uri, int size);

// Wrappers for rio package
ssize_t Rio_readn_w(int fd, void *ptr, size_t nbytes);
ssize_t Rio_readlineb_w(rio_t *rp, void *usrbuf, size_t maxlen);
void Rio_writen_w(int fd, void *usrbuf, size_t n);

int server(int fd);
int client(char *uri, char* method, char* hostname, char* pathname, int port,
	   char* version, int fd_first, char *header, int *size);

struct sockaddr_in clientaddr;


// main - Main routine for the proxy program.
// Which creates threads and assigns connections
// to the client.
// A busy loop is used to accepts connection from
// the client also extract client source info.
int main(int argc, char **argv) {

    int port, listenfd, connfd, i;
    char *haddrp;
    int clientlen;
    struct hostent *hp;


    /* Check arguments */
    if (argc != 2) {
        fprintf(stderr, "Usage: %s <port number>\n", argv[0]);
        exit(0);
    }


    port = atoi(argv[1]);
    signal(SIGPIPE,SIG_IGN);
    listenfd = Open_listenfd(port);

    tptr = Calloc(NTHREADS, sizeof(Thread) );
    iget = 0;
    iput = 0;

    for(i = 0; i < NTHREADS; i++)
        thread_make(i);

    if ((proxy_log_fd = Open (file,O_APPEND | O_WRONLY |
                              O_CREAT,
                              S_IRWXU)) < 0) {
        fprintf(stderr, "Error in opening the log file \n");
        exit(1);
    }


    // Server that listens for HTML requests
    while (1)  {
        clientlen = (signed int)sizeof(clientaddr);
        connfd = Accept(listenfd, (struct sockaddr*)&clientaddr,
                        (socklen_t *)&clientlen);

        /* Determine the domain name and ip address of the client */
        hp = Gethostbyaddr((const char *)&clientaddr.sin_addr.s_addr,
                           sizeof(clientaddr.sin_addr.s_addr), AF_INET);
        haddrp = inet_ntoa(clientaddr.sin_addr);

        pthread_mutex_lock(&clifd_mutex);
        clifd[iput] = connfd;
        if(++iput == MAXNCLI)
            iput = 0;

        pthread_cond_signal(&clifd_cond);
        pthread_mutex_unlock(&clifd_mutex);
    }
    exit(0);
}

// Server part of the program that reads and parse/modify
// the request (from the browser) to be forwarded to the end server, through
// the client() function.
// Also have the responsible of logging the browser
// requests.
int server(int fd) {
    rio_t rio;
    char buf[MAXLINE], uri[MAXLINE], method[MAXLINE], version[MAXLINE];
    char hostname[MAXLINE], pathname[MAXLINE] , request_header_in[MAXLINE],
        lstring[MAXLINE];
    /* char *lstrptr = lstring;  */
    int is_static, responseSize;
    int *port;
    char header[5000];
    char rm_str[] = "Connection: keep-alive";
    char add_str[] = "Connection: close\n";
    char rm_hst[] = "Host:";
    char new_line[] = "\n";
    int strl = 0;
    /* FILE *log = NULL; */
    /* const char *file ="proxy.log"; */

    /* if ((log = fopen(file, "a")) == NULL) { */
    /*     fprintf(stderr, "Error in opening a file.. log-file"); */
    /*     exit(1); */
    /* } */

    port = malloc(sizeof(int));

    Rio_readinitb(&rio, fd);
    Rio_readlineb_w(&rio, buf, MAXLINE);
    sscanf(buf, "%s %s %s", method, uri, version);
    is_static = parse_uri(uri, hostname, pathname, port);

    // Read Header and parse it to remove any Connection: Keep-Alive
    // and add Connection: close.
    while (strcmp(request_header_in, "\r\n")) {

        Rio_readlineb_w(&rio, request_header_in, MAXLINE);

        if (strstr(request_header_in, rm_str) == NULL) {
            if (strstr(request_header_in, rm_hst) != NULL) {
                strcat(header, rm_hst);
                strcat(header, hostname);
                strcat(header, new_line);
            } else {
                strncat(header, request_header_in, strlen(request_header_in));
                strl += strlen(request_header_in);
            }
        } else {
            strcat(header, add_str);
        }
    }

    client(uri, method, hostname, pathname, *port,
	   version, fd, header, &responseSize);

    if (responseSize > 0 ) {
        pthread_mutex_lock(&clifd_mutex);
        //format_log_entry(lstring, &clientaddr, uri, responseSize);
        format_log_entry(lstring, &clientaddr, uri, responseSize);
        strcat(lstring, "\n");
        Write(proxy_log_fd,lstring,sizeof(lstring));
        pthread_mutex_unlock(&clifd_mutex);
    }
    //fclose(log);

    free(port);

    return 0;
}


//   Client part of the proxy that sends the modified request
//   to the end server, then receives the response and froward
//   it to the browser
int client(char *uri, char* method, char* hostname,
	   char* pathname, int  port, char* version,
           int fd, char *header, int *rsize)
{

    int lines = 0,status,readsize = 0,clientfd;
    rio_t  rio , response_rio;
    char response_body[MAXLINE];
    char request[MAXLINE];

    clientfd = Open_clientfd(hostname, port);
    Rio_readinitb(&rio, clientfd);

    /* Sending the HTTP request to the end server,
       and read the response */

    //pthread_mutex_lock(&clifd_mutex);
    sprintf(request,"GET %s HTTP/1.0\r\n",uri);
    Rio_writen_w(clientfd,request,strlen(request));

    sprintf(request,"Connection: close\r\n");
    Rio_writen_w(clientfd,request,strlen(request));

    sprintf(request,"Host: %s \r\n\r\n",hostname);
    Rio_writen_w(clientfd,request,strlen(request));

    //pthread_mutex_unlock(&clifd_mutex);
    Rio_readinitb(&response_rio, clientfd);
    while ((lines = Rio_readlineb_w( &response_rio,
                                     response_body, 2048 )) != 0 ) {
        readsize += lines;
        status = rio_writen(fd, response_body, lines);
        if(status == -1) {
            break;
        }
    }
    *rsize=readsize;
    Close(clientfd);

    return 0;
}


/*
 * parse_uri - URI parser
 *
 * Given a URI from an HTTP proxy GET request (i.e., a URL), extract
 * the host name, path name, and port.  The memory for hostname and
 * pathname must already be allocated and should be at least MAXLINE
 * bytes. Return -1 if there are any problems.
 */
int parse_uri(char *uri, char *hostname, char *pathname, int *port)
{
    char *hostbegin;
    char *hostend;
    char *pathbegin;
    int len;

    if (strncasecmp(uri, "http://", 7) != 0) {
        hostname[0] = '\0';
        return -1;
    }
    /* Extract the host name */
    hostbegin = uri + 7;
    hostend = strpbrk(hostbegin, " :/\r\n\0");
    len = hostend - hostbegin;
    strncpy(hostname, hostbegin, len);
    hostname[len] = '\0';

    /* Extract the port number */
    *port = 80; /* default */
    if (*hostend == ':')
        *port = atoi(hostend + 1);

    /* Extract the path */
    pathbegin = strchr(hostbegin, '/');
    if (pathbegin == NULL) {
        pathname[0] = '\0';
    }
    else {
        pathbegin++;
        strcpy(pathname, pathbegin);
    }

    return 0;
}

/*
 * format_log_entry - Create a formatted log entry in logstring.
 *
 * The inputs are the socket address of the requesting client
 * (sockaddr), the URI from the request (uri), and the size in bytes
 * of the response from the server (size).
 */
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr,
                      char *uri, int size)
{
    time_t now;
    char time_str[MAXLINE];
    unsigned long host;
    unsigned char a, b, c, d;

    /* Get a formatted time string */
    now = time(NULL);
    strftime(time_str, MAXLINE, "%a %d %b %Y %H:%M:%S %Z",
             localtime(&now));

    /*
     * Convert the IP address in network byte order to dotted decimal
     * form. Note that we could have used inet_ntoa, but chose not to
     * because inet_ntoa is a Class 3 thread unsafe function that
     * returns a pointer to a static variable (Ch 13, CS:APP).
     */
    host = ntohl(sockaddr->sin_addr.s_addr);
    a = host >> 24;
    b = (host >> 16) & 0xff;
    c = (host >> 8) & 0xff;
    d = host & 0xff;


    /* Return the formatted log entry string */
    sprintf(logstring, "%s: %d.%d.%d.%d %s %d", time_str, a, b, c, d, uri,
            size);
}

/******************************************************/
/* Rio-writen & read WRAPPERS*/
/******************************************************/
// Read wrapper that prints warning
ssize_t Rio_readn_w(int fd, void *ptr, size_t nbytes) {
    ssize_t n;

    if ((n = rio_readn(fd, ptr, nbytes)) < 0) {
        fprintf(stderr, "Warning: Rio_readn_w < 0 \n");
        return 0;
    }
    return n;
}
// Read wrapper that prints warning
ssize_t Rio_readlineb_w(rio_t *rp, void *usrbuf, size_t maxlen) {
    ssize_t rc;

    if ((rc = rio_readlineb(rp, usrbuf, maxlen)) < 0) {
        fprintf(stderr,"Warning: Rio_readlineb_w < 0 \n");
        return 0;
    }
    return rc;
}
// Write wrapper that prints warning
void Rio_writen_w (int fd, void *usrbuf, size_t n) {
    ssize_t rc = -1;
    if ((rc = rio_writen(fd, usrbuf, n)) != n) {
        fprintf(stderr, "Warning: Rio_writen_w != n (%zd) \n",rc);
    }
}

// Responsible for creating the threads
void thread_make(int i) {
    void *thread_main(void * );
    Pthread_create(&tptr[i].thread_tid,
                   NULL, &thread_main,(void *)i);
    return;
}


// For each thread invoke the server with the
// file descriptor of the accepted socket
void *thread_main(void *arg) {
    int connfd;

    for( ; ; ) {

        pthread_mutex_lock(&clifd_mutex);

        while (iget == iput) {
            pthread_cond_wait(&clifd_cond, &clifd_mutex);
        }
        connfd = clifd[iget];

        if(++iget == MAXNCLI)
            iget = 0;

        pthread_mutex_unlock(&clifd_mutex);
        tptr[(int)arg].thread_count++;
        server(connfd);
        Close(connfd);
    }
}




