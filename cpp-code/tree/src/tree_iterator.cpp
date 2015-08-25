#include <list>
#include "Tree.h"
// Iterator

Tree::Iterator::Iterator() {
}

Tree::Iterator::Iterator(const Iterator& it) :
    tree(it.tree), lit(it.lit) {
}

Tree::Iterator::Iterator(Tree* tree, Node* p) : tree(tree) {
    list<Node*> nodes = tree->nodes;
    lit = std::find(nodes.begin(), nodes.end(), p);
}

Tree::Iterator::Iterator(Tree* tree, list<Tree*> lit): tree(tree), lit(lit) {
}

Tree::void Iterator::operator=(const Iterator& it) {
    tree = it.tree;
    lit = it.lit;
}

bool Tree::Iterator::operator==(const Iterator& it) {
    return tree == it.tree && lit == it.lit;
}

bool Tree::Iterator::operator!=(const Iterator& it) {
    return tree != it.tree || it.lit != lit;
}

TreeIterator& Tree::Iterator::operator++() {
    ++lit;
    return *this;
}

Iterator Iterator::operator++(int) {
    Iterator it (*this);
    operator++();
    return it;
}

string& Iterator::operator*() const {
    return (*lit)->value;
}
