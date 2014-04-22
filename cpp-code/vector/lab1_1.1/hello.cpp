#include <iostream>

///////////////////
// Uppgift 1.1 
//
// Example of usage:
// > hello
// Hello world!
//
// datan> hello C++
// Hello C++!
//
// datan> hello 3 C++
// Hello C++ C++ C++!
//
// datan> hello 2
// Hello world world!
// datan>
///////////////////

int main (int argc, char *argv[]) 
{
  
  printf("Hello ");

  if ( argc > 1 ) {

    char *ptr = '\0';
    long n = strtol (*++argv, &ptr,10);
    
    // Ok! Valid string
    if (*ptr == '\0') {
      const char *ptr_n = (argc > 2)? *++argv : "world";

      for (long i = 0; i < n; i++ ) {
        printf ("%s",ptr_n);
        if (i == n-1) 
          printf("!");
        else
          printf(" ");
      }
    } else {
      // Not a valid string
      printf ("%s!",*argv);
    }

  } else {
    printf("world!");
  }

  printf ("\n");
  return 0;

}
