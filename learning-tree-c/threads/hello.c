//
// Created by danter on 2016-09-12.
//

#include "hello.h"
/******************************************************************************
* FILE: hello.c
* DESCRIPTION:
*   A "hello world" Pthreads program.  Demonstrates thread creation and
*   termination.
* AUTHOR: Blaise Barney
* LAST REVISED: 08/09/11
******************************************************************************/
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>
#include <syscall.h>
#define NUM_THREADS	5

struct pthread_info {
    long n;
    pthread_t t;
};

void *PrintHello(void *pthread_info)
{
  struct  pthread_info *t_info;
  t_info = (struct pthread_info*)pthread_info;
  printf("Hello World! It's me, thread (#%ld!),pthread_t: %li, tid: %li pid: %u\n", t_info->n, t_info->t, syscall(SYS_gettid), getpid ());
  sleep (1000*60*60);
  pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
  struct pthread_info threads[NUM_THREADS];
  int rc;
  //long t;
  for(long i=0;i<NUM_THREADS;i++) {
      printf("In main: creating thread #%ld \n", i);
      threads[i].n = i;
      rc = pthread_create(&threads[i].t, NULL, PrintHello, (void *)&threads[i]);
      if (rc) {
          printf("ERROR; return code from pthread_create() is %d\n", rc);
          exit(-1);
        }
    }

  long rval;
  long *prval = &rval;
  for (long t=0;t<NUM_THREADS;t++) {
      pthread_join (threads[t].t, (void *)&prval);

      printf("thread #%ld died return val: %ld \n", threads[t], rval);
    }
  /* Last thing that main() should do */
  pthread_exit(NULL);
}