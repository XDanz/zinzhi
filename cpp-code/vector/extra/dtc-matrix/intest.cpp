#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>

using namespace std;
int main ( ) {
  
  vector<vector<int>> m;
  // char bracket;
  // cin >> bracket;
  // cout << "bracket:" << bracket << endl;
  int e;
  
  // while ( cin.peek() != ']' || cin.eofbit ) {
  char c;
  while ( cin >> c ) {
    if ( c == ']') break;
    vector<int> row;
    while ( cin >> e ) {
      cout << ",e:" << e << endl;
      row.push_back ( e );
    }
    m.push_back( row );
    cin.clear();
  }
  
  cout << "m.size() :" << m.size() << endl;


}
