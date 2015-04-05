#include <stdio.h>
#include <stdlib.h>

#define DBG(...) fprintf(stdout,__VA_ARGS__)

int
main (int args, char **argv)
{
        int a,*ptra;
        ptra = &a;

        DBG("ptra = %p\n",ptra);
}
