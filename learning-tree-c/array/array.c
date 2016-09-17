//
// Created by danter on 2016-08-28.
//

#include <stdio.h>

void print_row (const int *ptr, int cols);

int sum(const int *ptr, int size);
int sum2(const int *ptr, int size);

int main(int argc, int *argv[])
{
  int m_array[3][5] = {
      {0,   1,  2,  3,  4},
      {10, 11, 12, 13, 14},
      {20, 21, 22, 23, 24}
  };
  int i;

  for (i = 0; i < 3; i++)
    {
      printf ("row %d \n", i);
      print_row (m_array[i], 5);
    }
}

void print_row (const int *ptr, int cols)
{
  int i;

  for (i=0; i < cols; i++)
    printf("\t column(%d): %d\n", i, ptr[i]);

  printf (" \t sum: %d \n", sum (ptr, cols));
}

int sum (const int *ptr, int size)
{
   int sum = 0;

    while (size--)
      sum += *ptr++;

  return sum;
}

int sum2 (const int *ptr, int size)
{
  int i, sum = 0;

  for (i=0; i < size; i++)
    sum += ptr[i];

  return sum;
}


