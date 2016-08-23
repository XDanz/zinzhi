#include <stdio.h>
#include <stdlib.h>

int
main (int argc, char** argv)
{
  char *name; // array of characters
  size_t  len;   // length of name
  int i;      // loop counter

  printf ("enter number:");
  scanf("%zu", &len);
  while ( getchar() != '\n')  // throw away rest of line
    ;

  name = (char*)malloc(len + 1);

  if (name == 0) {
      puts("malloc failed!!");
      exit(1);
    } else
    {
      printf("Enter %zu chars:", len);
      for (i = 0; i < len; i++)
        {
          name[i] = (char) getchar ();
        }
      name[len] = '\0';

      printf ("Goodbye , %s\n", name);
    }
  return 0;
}
