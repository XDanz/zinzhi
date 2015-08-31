#include "Tree.h"
#include <iostream>
#include <list>

using namespace std;

int main() {

  list<Tree*> children;

  list<Tree*> c1_children;
  c1_children.push_back(new Tree("C3"));
  c1_children.push_back(new Tree("C4"));
  Tree c1("C1", c1_children);
  cout << " Tree C1: -->" << endl;
  c1.print();
  cout << " Tree C1: --> ok" << endl;
  children.push_back(&c1);

  list<Tree*> c2_children;
  c2_children.push_back(new Tree("C5"));
  c2_children.push_back(new Tree("C6"));
  Tree c2("C2", c2_children);
  cout << " Tree C2: -->" << endl;
  c2.print();
  cout << " Tree C2: --> ok" << endl;

  children.push_back(&c2);
  cout << " ------ size = "  << children.size() << endl;

  Tree t("C0", children);

  cout << " ------ print C0 = " << endl;
  t.print();
      
}
