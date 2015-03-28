#include <stdio.h>
#include <stdlib.h>

int 
main (int argc, char** argv)
{
  char *name; // array of characters
  int  len;   // length of name
  int i;      // loop counter

  scanf("%d", &len );
  while ( getchar() != '\n')  // throw away rest of line
    ; 

  name = malloc(len + 1);
  
  if (name == 0) {
    puts("malloc failed");
    exit(1);
  } else {
    for ( i = 0; i < len; i++ )
      name[i] = getchar();
    name[len] = '\0';

    printf ("Goodbye , %s\n", name);

  }
  return 0;
}
