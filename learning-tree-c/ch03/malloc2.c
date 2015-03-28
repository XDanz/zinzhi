#include <stdio.h>
#include <stdlib.h>

int 
main (int argc, char** argv) 
{
        int *ia;  /* array of ints */
        int dim;
        int i;
        
        printf ("How many intems? ");
        scanf ("%d",&dim);
        
        while (getchar() != '\n') 
                ;
        
        ia = malloc (dim * sizeof (int) );
        
        if ( ia == 0 ) {
                puts("heap failure!");
                exit (1);
        } else {
        
                for ( i = 0 ; i < dim ; i++ ) {
                        printf ("Enter int %d: ", i );
                        scanf("%d", ia + i );
                }

                for (i = 0; i < dim ; i++ )
                        printf (" int %d: %d\n", i , ia[i]);
                
                free(ia);
        }
        return 0;
}
