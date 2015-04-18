#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <errno.h>
#include "mm.h"


/* Misc manifest constants */
#define MAXLINE    1024   /* max line size */
#define MAXARGS     128   /* max args on a command line */
#define MAXJOBS      16   /* max jobs at any point in time */
#define MAXBLOCKS  4000   /* max jobs at any point in time */

#define MAXJID    1<<16   /* max job ID */

/* Job states */
//define UNDEF 0 /* undefined */
//define FG 1    /* running in foreground */
//define BG 2    /* running in background */
//define ST 3    /* stopped */

#define NEG_ERR(MSG, C) if ((C) < 0) unix_error(MSG)

/* Global variables */
extern char **environ;      /* defined in libc */
char prompt[] = "mmalloc> ";    /* command line prompt (DO NOT CHANGE) */
int verbose = 0;            /* if true, print additional output */


int nextblockid = 0;

char sbuf[MAXLINE];         /* for composing sprintf messages */

//struct job_t {              /* The job struct */
//pid_t pid;              /* job PID */
//int jid;                /* job ID [1, 2, ...] */
//int state;              /* UNDEF, BG, FG, or ST */
//char cmdline[MAXLINE];  /* command line */
//};

//struct job_t jobs[MAXJOBS];

/* End global variables */
struct block_t {
        unsigned int blockid;
        void *blockptr;
};

struct block_t blocks[MAXBLOCKS];

/* Function prototypes */

/* Here are the functions that you will implement */
void eval(char *cmdline);
int builtin_cmd(char **argv);
//void do_bgfg(char **argv);
//void waitfg(pid_t pid);

//void sigchld_handler(int sig);
//void sigtstp_handler(int sig);
//void sigint_handler(int sig);

/* Here are helper routines that we've provided for you */
int parseline(const char *cmdline, char **argv); 

void clearblock(struct block_t *block);
void initblock(struct block_t *block);
int maxblockid(struct block_t *blocks);
int addblock(struct block_t *blocks,unsigned int blockid,void *blockptr);
int deleteblock(struct block_t *blocks, unsigned blockid); 
void *blockptr(unsigned blockid);
struct block_t *getbyid(struct block_t *blocks,unsigned blockid);

void listblocks(struct block_t *jobs);

void usage(void);
void unix_error(char *msg);
void app_error(char *msg);
//typedef void handler_t(int);
//handler_t *Signal(int signum, handler_t *handler);

/*
 * main - main routine 
 */
int main(int argc, char **argv) {

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


        /* Initialize the job list */
        //initjobs(jobs);
        initblock(blocks);
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
        int bytes = 0;
        void *bp;
        int index;
  
        if ((first = strstr(cmdline,"malloc")) != 0) {
                pch = strtok(cmdline," ");
                pch = strtok(NULL," ");
    
                bytes = atoi(mellanslag);

                bp = mm_malloc(bytes);
    
                if ((char *)bp != NULL) {
                        addblock(blocks, nextblockid++, bp);
                } else {
                        printf("blockpointer is null\n");
                }
    
    
        } else if ((first = strstr(cmdline,"free")) != 0) {
                pch = strtok(cmdline," ");
                pch = strtok(NULL," ");
                if (!pch) {
                        scanf ("%p",&bp);
                } else {
                        sscanf ("%p",&bp);
                }

                index = atoi(mellanslag);

                printf("numblocks=%d\n", index);

                bp = blockptr(index);
    
                if (bp != NULL) {
                    mm_free(bp);
                    deleteblock(blocks,index);
                }
    
        } else if ((first = strncmp(cmdline,"sb2",3)) == 0) {
                checkheap2();
        } else if (strncmp(cmdline,"sb",2) == 0 ) {
                checkheap(1);
        } else if ((first = strstr(cmdline,"ls")) != 0) {
                listblocks(blocks);
        } else if((first = strstr(cmdline,"quit")) != 0) {
                exit(0);
        }
        return;
}

/* 
 * parseline - Parse the command line and build the argv array.
 * 
 * Characters enclosed in single quotes are treated as a single
 * argument.  Return true if the user has requested a BG job, false if
 * the user has requested a FG job.  
 */
int parseline(const char *cmdline, char **argv) {
        static char array[MAXLINE]; /* holds local copy of command line */
        char *buf = array;          /* ptr that traverses command line */
        char *delim;                /* points to first space delimiter */
        int argc;                   /* number of args */
        int bg;                     /* background job? */
  
        strcpy(buf, cmdline);
        buf[strlen(buf)-1] = ' ';  /* replace trailing '\n' with space */
        while (*buf && (*buf == ' ')) /* ignore leading spaces */
                buf++;

        /* Build the argv list */
        argc = 0;
        if (*buf == '\'') {
                buf++;
                delim = strchr(buf, '\'');
        }
        else {
                delim = strchr(buf, ' ');
        }

        while (delim) {
                argv[argc++] = buf;
                *delim = '\0';
                buf = delim + 1;
                while (*buf && (*buf == ' ')) /* ignore spaces */
                        buf++;

                if (*buf == '\'') {
                        buf++;
                        delim = strchr(buf, '\'');
                }
                else {
                        delim = strchr(buf, ' ');
                }
        }
        argv[argc] = NULL;
    
        if (argc == 0)  /* ignore blank line */
                return 1;

        /* should the job run in the background? */
        if ((bg = (*argv[argc-1] == '&')) != 0) {
                argv[--argc] = NULL;
        }
        return bg;
}

/* 
 * builtin_cmd - If the user has typed a built-in command then execute
 *    it immediately.  
 */
int builtin_cmd(char **argv) {
        return 0;     /* not a builtin command */
}





/***********************************************
 * Helper routines that manipulate the job list
 **********************************************/

/*
  clearjob - Clear the entries in a job struct 
  void clearjob(struct job_t *job) {
  job->pid = 0;
  job->jid = 0;
  job->state = UNDEF;
  job->cmdline[0] = '\0';
  }
*/

void clearblock(struct block_t *block){
        block->blockid = -1;
        block->blockptr = NULL;
}

/* initjobs - Initialize the job list 
   void initjobs(struct job_t *jobs) {
   int i;

   for (i = 0; i < MAXJOBS; i++)
   clearjob(&jobs[i]);
   }*/

void initblock(struct block_t *blocks){
        int i;

        for(i = 0; i < MAXBLOCKS; i++)
                clearblock(&blocks[i]);

}

/* maxjid - Returns largest allocated job ID 
   int maxjid(struct job_t *jobs) 
   {
   int i, max=0;

   for (i = 0; i < MAXJOBS; i++)
   if (jobs[i].jid > max)
   max = jobs[i].jid;
   return max;
   }
*/

int maxblockid(struct block_t *blocks){
        int i , max = 0;
  
        for(i = 0; i < MAXBLOCKS; i++){
                if(blocks[i].blockid > max)
                        max = blocks[i].blockid;
        }
        return max;

}

/* addjob - Add a job to the job list 
   int addjob(struct job_t *jobs, pid_t pid, int state, char *cmdline) 
   {
   int i;
    
   if (pid < 1)
   return 0;

   for (i = 0; i < MAXJOBS; i++) {
   if (jobs[i].pid == 0) {
   jobs[i].pid = pid;
   jobs[i].state = state;
   jobs[i].jid = nextjid++;


   if (nextjid > MAXJOBS)
   nextjid = 1;
   strcpy(jobs[i].cmdline, cmdline);

   if(verbose){
   printf("Added job [%d] %d %s\n", jobs[i].jid,
   (int) jobs[i].pid, jobs[i].cmdline);
   }
   return 1;
   }
   }
   printf("Tried to create too many jobs\n");
   return 0;
   }
**/


int addblock(struct block_t *blocks, unsigned  blockid, void *blockptr) {
        int i;

        printf("adding block..\n");
        for (i = 0; i < MAXBLOCKS; i++) {
                if (blocks[i].blockid == -1) {
                        blocks[i].blockid = blockid;
                        blocks[i].blockptr = blockptr;
                        printf("added block with id: %d with blockptr: 0x%x\n",
                               blockid,(char *) blockptr);
                        return 1;
                }
        }
}

/* deleteblock - Delete a block whose blockid block list */
int deleteblock(struct block_t *blocks, unsigned blockid) {
        int i;

        for (i = 0; i < MAXBLOCKS; i++) {

                if (blocks[i].blockid == blockid) {
                        clearblock(&blocks[i]);
    
                        return 1;
                }
        }
        return 0;
}




struct block_t *getbyid(struct block_t *blocks,unsigned blockid) {

        int i ; 
        if(blockid < 1)
                return NULL;

        for(i = 0; i < MAXBLOCKS; i++){
                if(blocks[i].blockid == blockid){
                        return &blocks[i];
                }
        }
}

void *blockptr(unsigned blockid) {
  
        int i ;
        if(blockid < 0)
                return NULL;

        for( i = 0; i < MAXBLOCKS; i++){
                if(blocks[i].blockid == blockid){
                        return blocks[i].blockptr;
                }
        }
        return NULL;
}

/* listblock - Print the block list */
void listblocks(struct block_t *jobs) {
        int i;
    
        for (i = 0; i < MAXBLOCKS; i++) {
                if (blocks[i].blockid != -1) {
                        printf("[%d] (0x%x) \n", blocks[i].blockid, (char *)blocks[i].blockptr);
                }
        }
}


/******************************
 * end job list helper routines
 ******************************/


/***********************
 * Other helper routines
 ***********************/

/*
 * usage - print a help message
 */
void usage(void) 
{
        printf("Usage: shell [-hvp]\n");
        printf("   -h   print this message\n");
        printf("   -v   print additional diagnostic information\n");
        printf("   -p   do not emit a command prompt\n");
        exit(1);
}

/*
 * unix_error - unix-style error routine
 */
void unix_error(char *msg){
        fprintf(stdout, "%s: %s\n", msg, strerror(errno));
        exit(1);
}

/*
 * app_error - application-style error routine
 */
void app_error(char *msg){
        fprintf(stdout, "%s\n", msg);
        exit(1);
}

