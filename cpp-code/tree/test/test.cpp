#include "Tree.h"

using namespace std;

int main() {

  list<Tree*> children;

  list<Tree*> c1_children;
  c1_children.push_back(new Tree("C3"));
  c1_children.push_back(new Tree("C4"));
  Tree c1("C1", c1_children);
  c1.print();

  children.push_back(&c1);

  list<Tree*> c2_children;
  c2_children.push_back(new Tree("C5"));
  c2_children.push_back(new Tree("C6"));
  Tree c2("C2", c2_children);

  c2.print();

  children.push_back(&c2);
  Tree t("C0", children);
  t.print();
      
}


