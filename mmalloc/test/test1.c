#include <stdio.h>
#include <stdlib.h>
#include <mm.h>
#include <memlib.h>

#define ALIGNMENT 8
#define ALIGN(size) (((size) + (ALIGNMENT-1)) & ~0x7)


int 
main(int argc , char **argv)
{
        char *bp;
        printf("*****Test start\n");
        mem_init();
        mm_init();
        printf("*****Test ing malloc start ****\n");
        bp = (char *)mm_malloc(5);
        printf("*****Test ing malloc end ****\n");
        /* heapcheck(); */

        mm_free(bp);
        /* heapcheck(); */
        //int i = (1<<12);
        //int j=1;
  
        //for( j=0; j < 10;j++)
        //printf("align %d bytes , align=%d \n",j,ALIGN(j));

        printf("****Test end \n");
        //printf("i=%d \n",i);
        exit (0);

}
