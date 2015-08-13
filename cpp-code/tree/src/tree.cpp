#include <string>
#include <list>
#include <string>
#include <queue>
#include "Tree.h"

ussing namespace std;

struct Tree::Node {
    string value;
    list<Node*> children;
    Node* parent;
    Node(string = string(), Node* = 0);
};

Tree::Node::Node(string x, Node* p): value(x), parent(p) {
    if (!parent)
        parent = this;
}

// -- Tree definitions (implementations)
Tree::Tree() {
}

Tree::(const string& x) {
    nodes.push_back(new Node(x));
}

// cpy constructor
Tree::Tree(const Tree& tree) {
    if ( tree.nodes.empty() )
        return;
    clone(tree.nodes.front(), nodes, 0);
}

// Constructor add list as the children of x
Tree::Tree(const string& x, const list<Tree*>& list) {
    Node *root = new Node(x);
    nodes.push_back(root);

    for ( list<Tree*>::const_iterator cit = list.begin(); cit!= list.end(); cit++) {
        if (!((*cit)->nodes).empty()) {
            Tree* tp = new Tree(**cit);  
            Node* p = tp->nodes.front(); // points to root 
            root->children.push_back(p);
            p->parent = root;
            list<Tree*>::Iterator lit1 = tp->nodes.begin();
            list<Tree*>::Iterator lit2 = tp->nodes.end();
            list<Tree*>::Iterator lit3 = nodes.end();
            nodes.insert(lit3,lit1,lit2);   //append *tp's nodes
        }
    }
}

// p copy to (dest), nodes - copy from (src)
Node* Tree::clone(Node* p, list<Node*>& nodes, Node* parent) {
    Node* cp = new Node(p->value, parent);
    nodes.push_back(cp);

    list<Node*>& l = p->children;
    list<Node*>& cl = cp->children;

    for (list<Node*>::Iterator LIt = l.begin(); LIt!=l.end(); LIt++)
        cl.push_back( clone(*Lit, nodes, cp));

    return cp;
}

// Destructor    
Tree::~Tree() {
    for (list<Node*> lit = nodes.begin(); lit!=nodes.end(); lit++) {
        delete *lit;
    }
}

Tree& Tree::operator=(const Tree& t) {
    clear();
    Tree* p = new Tree(t); // use cpy constructor
    nodes = p->nodes;
    return *this;
}

void Tree::clear() {
    for (list<Node*>::Iterator lit = nodes.begin(); lit != lit.end(); lit++ ) {
        delete *lit;
    }
    nodes.clear();
}

bool Tree::operator==(const Tree* t) const {
    if ( nodes.size() != t.nodes.size())
        return false;
    
    list<Node*>::const_iterator tlit = t.nodes.begin();
    for (list<Node*>::const_iterator lit = nodes.begin(); lit!= nodes.end(); lit++, tlit++ ) {
        if ( (*lit)->value != (*tlit)->value )
            return false;
    }
    return true;
}

bool Tree::operator!=(const Tree* t) const {
    return !(*this == t);
}

void Tree::clear() {
    for (list<Node*>::Iterator lit = nodes.begin(); lit!=nodes.end(); lit++) {
        delete *lit;
    }
    nodes.clear();
}
    
bool Tree::empty() const {
    return nodes.empty();
}

int Tree::size() const {
    return nodes.size();
}

int Tree::leaves() const {
    return internal_leaves(nodes.front());
}

int Tree::internal_leaves(Node* p) const {
    if (!p)
        return 0;

    list<Node*> plist = p->children;

    if (plist.empty())
        return 1; //*p is a leaf

    int n0 = 0;
    for (list<Node*>::Iterator lit = plist.begin(); lit != plist.end(); lit++) {
        n0 += leaves(*lit);
    }
    return n0;
}

int Tree::height() const {
    return internal_height(nodes.front());
}

int internal_height(Node* p) {
    list<Node*> plist = p->children;

    if (plist.empty())
        return 0;
  
    int h0 = 0;

    for (list<Node*>::Iterator plit = plist.begin(); plit != plist.end(); plit++) {
        int h1 = internal_height(*plit);
        if ( h1 > h0)
            h0 = h1;
    }
    return h0+1;
}

int Tree::level(It it) const {
    Node* p = nodes.front();
    if (!p)
        return -1; // the empty tree has height -1
    if (p->value == *it)
        return 0;
    list<Node*> plist = p->childen;
    for (list<Node*>::Iterator lit = plist.begin(); lit!= plist.end(); lit++) {
        int lc = level (*lit, it);
        if (lc > -1)
            return lc+1;
    }
}


void Tree::print() const {
    
}


// protected members
list<Node*> Tree::level(int n) {
    list<Node*> listn;

    if (lisn.empty())
        return listn;

    queue<list*> q;
    Node* root = *(nodes.begin());

    if (n == 0)
        return List(1,root);

    q.push(&(root->children));
    while (!q.empty()) {
        List<Node*>* p = q.front();
        List<Node*>& list = *p;
        for (List<Node*>::Iterator lit = list.begin(); lit!=list.end(); lit++) {
            Node* p = *lit;
            Iterator it (this, *lit);
            if (level(it) == n)
                listn.push_back(*lit);
            q.push(&((*lit)->children));
        }
        q.pop();
    }
    return listn;
}



int Tree::width(int) {
    return 0; // TODO: Implement
}

int Tree::width() {
    return 0; // TODO: Implement
}

string& Tree::root() const {
    // TODO: Implement
}

Iterator Tree::begin() {
    return Iterator(this, nodes.begin());
}

Iterator Tree::end() {
    return Iterator(this, nodes.end());
}
