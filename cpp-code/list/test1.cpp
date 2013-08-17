#include <iostream>
#include "List.h"
#include <cassert>

using std::cout;

template <typename T>
void print (  List<T>& );


int main ( int argc, char ** argv ) {



#define SZ(L,S)                                 \
  { assert ( (L).size() == (S));   }            \


  List<int> l(10);
  SZ(l,10);
  cout << " ## initial List content ## " << "\n";
  print<int>(l);

  l.push_back ( 11 );

  int eleven = l.back();
  assert ( eleven == 11 );

  for ( int i = 5; i >= 1 ; i-- ) {
    l.push_front ( i );
  }
  cout << " After push_front( 1 - 5 )" << " \n";
  print<int>( l );
  SZ(l,16);


  l.pop_back();
  cout << " After pop_back() " << " \n";
  print<int>( l );
  assert ( l.size() == 15 );

  l.pop_front();
  cout << " After pop_front() " << " \n";
  print<int>( l );
  SZ(l,14);

}

template <typename T>
void print (  List<T>& l) {

  typename List<T>::Iterator it = l.begin();

  cout << "(";
  for (int i = 0 ; it != l.end(); it++ , i++) {
    if ( i > 0 ) cout << ",";
    cout <<  *it;
  }
  cout << ") size:" << l.size() << ", front: " << l.front() << ", back: " <<
    l.back() << "\n";

}
