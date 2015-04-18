#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>
#include <sys/types.h>
#include <errno.h>
#include "mm.h"
#include "memlib.h"

#define MAXLINE    1024   /* max line size */

char prompt[] = "mmalloc> ";    /* command line prompt (DO NOT CHANGE) */
int verbose = 0;            /* if true, print additional output */

char sbuf[MAXLINE];         /* for composing sprintf messages */

void eval(char *cmdline);

void app_error(char *msg);

void usage(void);

/**
 * main() - main routine
 */
int
main(int argc, char **argv)
{
        char c;
        char cmdline[MAXLINE];
        int emit_prompt = 1; /* emit prompt (default) */

        /* Parse the command line */
        while ((c = getopt(argc, argv, "hvp")) != EOF) {
                switch (c) {
                case 'h':                 /* Print help message */
                        usage();
                        break;
                case 'v':                 /* emit additional diagnostic info */
                        verbose = 1;
                        break;
                case 'p':                 /* don't print a prompt */
                        emit_prompt = 0;  /* handy for automatic testing */
                        break;
                default:
                        usage();
                }
        }

        mem_init();
        mm_init();

        /* Execute the shell's read/eval loop */
        while (1) {
                /* Read command line */
                if (emit_prompt) {
                        printf("%s", prompt);
                        fflush(stdout);
                }

                if ((fgets(cmdline, MAXLINE, stdin) == NULL) && ferror(stdin))
                        app_error("fgets error");

                if (feof(stdin)) { /* End of file (ctrl-d) */
                        fflush(stdout);
                        exit(0);
                }

                /* Evaluate the command line */
                eval(cmdline);
                fflush(stdout);
                fflush(stdout);
        }
        exit(0); /* control never reaches here */
}

/**
 * eval() - Evaluate the command line that the user has just typed in
 */
void eval(char *cmdline)
{
        char *first;
        char *pch;
        int sz = 0;
        void *bp;

        if ((strstr(cmdline,"malloc")) != 0) {
                pch = strtok(cmdline," ");
                pch = strtok(NULL," ");

                sz = atoi(pch);

                bp = mm_malloc(sz);
                fprintf(stdout," malloc bp %p \n", bp);

        } else if ((strstr(cmdline,"free")) != 0) {
                pch = strtok(cmdline," ");
                pch = strtok(NULL," ");
                if (!pch) {
                        scanf ("%p", &bp);
                } else {
                        sscanf (pch,"%p", &bp);
                }

                if (bp != NULL) {
                        fprintf(stdout, " free %p \n", bp);
                        mm_free(bp);
                }

        } else if (strncmp(cmdline,"sb", 2) == 0 ) {
                checkheap(1);
        } else if ( (first = strstr(cmdline,"quit")) != 0) {
                exit(0);
        } else {
                fprintf (stderr, "Unknown command");
        }
        return;
}

/**
 * usage() - print a help message
 */
void usage(void)
{
        printf("Usage: shell [-hvp]\n");
        printf("   -h   print this message\n");
        printf("   -v   print additional diagnostic information\n");
        printf("   -p   do not emit a command prompt\n");
        exit(1);
}

void
app_error(char *msg)
{
        fprintf(stdout, "%s\n", msg);
        exit(1);
}
