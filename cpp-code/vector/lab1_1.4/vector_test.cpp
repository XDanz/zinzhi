#include <iostream>

int main  ( int argc , char** argv) 
{
  Vector v ( 10 );

  Vector v2 ( 11 );
  cout << "v: " <<  v << "\n";

  cout << "  == Assigning values == " << "\n";
  for (size_t i = 0; i < v.size(); i++ ) {
    v[i] = i;
  }

  for (size_t i = 0; i < v2.size(); i++ ) {
    v2[i] = i;
  }

  cout << "v:" << v << "\n";

  cout << " == Assignment operator == " << "\n";
  v = v2;

  cout << "v:" << v << "\n";

  cout << " == Copy constructor == " << "\n";
  Vector v3(v);
  cout << "v3: " << v3 << "\n";
  cout << " == assigning v3[1] the value of 2 == " << "\n";
  int x = 2;
  v3[1] = x;

  cout << "v :" << v << "\n";
  cout << "v3:" << v3 << "\n";
  assert ( v3[1] != v[1] );

  cout << " == Createing zero vector v4 == " << "\n";
  Vector v4(0);
  cout << "v4: " << v4 << "\n";

  cout << " == Creating default vector v5 == " << "\n";
  Vector v5;

  v5 = {1,2,3};
  cout << "v5:" << v5 << "\n";

  cout << " == Creating vector a == " << "\n";
  Vector a = v5;
  // Vector v6 = a;
  // v5[0] = 6;
  // assert ( a[0] != v5[0] );
  // cout << "a: " << a << "\n";
  // cout << "v5: " << v5 << "\n";
  
  return 0;

}

