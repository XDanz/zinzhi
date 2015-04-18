#include <stdio.h>
#include <stdlib.h>
#include "mm.h"
#include "memlib.h"

#define MAX_EXP 25
#define EXP_BASE 2
#define MIN_BLOCK_SIZE 16
#define MAX_FIXED 512


int 
main(int args, char **argv)
{
        int exp, num;
        exp = 0;
        
        fprintf(stdout, "%-5s, %5s\n", "exp" , "num");
        for (; exp < MAX_EXP+1; exp++) {
                num = EXP_BASE << exp;
                fprintf(stdout, "%-5d %5d\n", exp , num);
                if (num >= MIN_BLOCK_SIZE && num > MAX_FIXED)
                        break;
        }
        exit(0);
}
