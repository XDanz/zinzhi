#include <stdio.h>
#include <string.h>

int 
main (int argc, char **argv) 
{
    char *buf = NULL;
    char *pch ;
    size_t len = 0;
    void *ptr;
    //0x7f3ad2951298

    ssize_t n = getline (&buf, &len, stdin);
    printf ( "read %zu \n",n);
    printf ("Splitting string \"%s\" into tokens:\n", buf);
    pch = strtok (buf, " ");
    if ( pch == NULL ) {
        printf(" pch is null !\n");
        return (1);
    }
    printf ("%s \n", pch);
    
    pch = strtok (NULL, " ");

    if ( pch == NULL ) {
        printf(" pch is null !\n");
        scanf("%p", &ptr);
        printf ("ptr = %p \n", ptr);
        return (1);
    } else {
        printf ("pch \"%s\" \n", pch);
        sscanf(pch, "%p", &ptr); 
        printf ( "ptr =  %p \n", ptr);
        return 0;
    }
}
