#include "Tree.h"
#include <iostream>
#include <list>

using namespace std;

int main() {

  list<Tree*> children;
  Tree c1("child1");
  children.push_back(&c1);
  Tree c2("child2");
  children.push_back(&c2);
  Tree t("parent1", children);

      
}
