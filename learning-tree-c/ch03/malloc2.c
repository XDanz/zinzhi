#include <stdio.h>
#include <stdlib.h>

void PrintVariant1 (int dim, int *pInt);
void PrintVariant2 (int dim, int *pInt);
void PrintVariant3 (int dim, int *pInt);
void
ReadInts(int num, int a[]) {
  int i;
  for (i = 0; i < num; i++) {
      printf ("Enter int %d: ", i );
      scanf("%d", a + i);
    }
}

int
main (int argc, char** argv)
{
  int *ia;  /* array of ints */
  int dim;
  int i;

  printf ("How many intems? ");
  scanf ("%d", &dim);

  while (getchar() != '\n')
    ;

  ia = malloc (dim * sizeof (int) );

  if (ia == 0) {
      puts("heap failure!");
      exit (1);
    } else {

      ReadInts (dim, ia);

      printf("Printing forward variant 1.\n");
      PrintVariant1(dim, ia);

      printf("Printing forward variant 2.\n");
      PrintVariant2(dim , ia);

      printf ("Print backwards variant 3.! \n");
      PrintVariant3 (dim, ia);

      free(ia);
    }
  return 0;
}

/// Print forward variant 3.
void PrintVariant3 (int dim, int *pInt)
{
  /// print backwards
  while(dim--)
    printf (" int %d: %d\n", dim , pInt[dim]);
}

/// Print forward variant 2.
void PrintVariant2 (int dim, int *pInt)
{
  int t = dim , i = 0;
  while(dim--)
    {
      i = t - dim;
      printf (" int %d: %d\n", i, pInt[i]);
    }

}

/// Print forward variant 1.
void PrintVariant1 (int dim, int *pInt)
{
  int i;
  for (i = 0; i < dim ; i++ )
    printf (" int %d: %d\n", i , pInt[i]);
}
