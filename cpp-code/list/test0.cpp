#include <iostream>
#include "List.h"

using std::cout;

// Prints the content of the list
template <typename V>
void print ( List<V>&);

int main ( int argc, char** argv ) {

  List<int> l;
  l.push_back(1);
  l.push_back(2);
  l.push_back(3);

  cout << " print the first l " << "\n";

  print<int>(l);
  List<int> lc = l;

  cout << " print the first copy lc " << "\n";
  print<int>(lc);


}
template <typename V>
void print ( List<V>& l ) {
  typename List<V>::Iterator it = l.begin();

  for ( ; it != l.end(); it++ ) {
    cout << " elem ="  <<  *it << " \n";
  }

}
