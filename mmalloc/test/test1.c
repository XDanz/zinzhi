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
        mem_init();
        mm_init();
        fprintf(stdout, " allocating 5 bytes \n");
        bp = (char *)mm_malloc(5);
        fprintf(stdout, " -- checkheap -- \n");
        checkheap(1); 
        fprintf(stdout, " -- mm_free -- \n");
        mm_free(bp);
        fprintf(stdout, " -- checkheap -- \n");
        checkheap(); 
        exit (0);

}
