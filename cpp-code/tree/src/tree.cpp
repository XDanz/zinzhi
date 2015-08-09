#include <string>
#include "List.h"
#include "Tree.h"

struct Tree::Node {
    std::sring value;
    List<Node*> children;
    Node* parent;
    Node(string = string(), Node* = 0);
};

Node::Node(string x, Node* p): value(x), parent(p) {
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
Tree::Tree(const string& x, const List<Tree*>& list) {
    Node *root = new Node(x);
    nodes.push_back(root);

    for ( List<Tree*>::const_iterator cit = list.begin(); cit!= list.end(); cit++) {
        if (!((*cit)->nodes).empty()) {
            Tree* tp = new Tree(**cit);  
            Node* p = tp->nodes.front(); // points to root 
            root->children.push_back(p);
            p->parent = root;
            List<Tree*>::Iterator lit1 = tp->nodes.begin();
            List<Tree*>::Iterator lit2 = tp->nodes.end();
            List<Tree*>::Iterator lit3 = nodes.end();
            nodes.insert(lit3,lit1,lit2);   //append *tp's nodes
        }
    }
}

// p copy to (dest), nodes - copy from (src)
Node* clone(Node* p, List<Node*>& nodes, Node* parent) {
    Node* cp = new Node(p->value, parent);
    nodes.push_back(cp);

    List<Node*>& l = p->children;
    List<Node*>& cl = cp->children;

    for (Tree::List<Node*>::Iterator LIt = l.begin(); LIt!=l.end(); LIt++)
        cl.push_back( clone(*Lit, nodes, cp));

    return cp;
}

// Destructor    
Tree::~Tree() {
    for (LIt lit =_nodes.begin(); lit!=_nodes.end(); lit++) {
        delete *lit;
    }
}

Tree& Tree::operator=(const Tree& t) {
    clear();
    Tree* p = new Tree(t); // use cpy constructor
    nodes = p->_nodes();
    return *this;
}

void Tree::clear() {
    for (List<Node*>::Iterator lit = nodes.begin(); lit != lit.end(); lit++ ) {
        delete *lit;
    }
    nodes.clear();
}

bool Tree::operator==(const Tree* t ) const {
    if ( nodes.size() != t.nodes.size())
        return false;
    
    List<Node*>::const_iterator tlit = t.nodes.begin();
    for (List<Node*>::const_iterator lit = nodes.begin(); lit!= nodes.end(); lit++, tlit++ ) {
        if ( (*lit)->value != (*tlit)->value )
            return false;
    }
    return true;
}

bool Tree::operator!=(const Tree* t) const {
    return !(*this == t);
}

void Tree::clear() {
    for (List<Node*> lit =nodes.begin(); lit!=nodes.end(); lit++) {
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

int Tree::internal_leaves() const {
    return internal_leaves(nodes.front());
}

int Tree::internal_leaves(Node* p) const {
    if (!p)
        return 0;

    List<Node*> plist = p->children;

    if (plist.empty())
        return 1; //*p is a leaf

    int n0 = 0;
    for (List<Node*>::Iterator lit = plist.begin(); lit != plist.end(); lit++) {
        n0 += leaves(*lit);
    }
    return n0;
}

int Tree::height() const {
    Node* p = nodes.front();
    List<Node*> plist = p->children;
    if (plist.empty())
        return 0;

    int h0=0;

    for (List<Node*> plit=plist.begin(); plit!=plist.end(); plit++) {
        int h1 = height(*plit);
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
    List<Node*> plist = p->childen;
    for (List<Node*>::Iterator lit = plist.begin(); lit!= plist.end(); lit++) {
        int lc = level (*lit, it);
        if (lc > -1) return lc+1;
    }
}

// public function of the Tree::Iterator

void Tree::print() const {

}
  int leaves() const;
  int height() const;
  int level(Iterator it) const;
  int pathLenght();
  int width(int);
  int width();
  void print();
  Type& root() const;
  void reflect();
  void defoliate();
  Iterator insert(Iterator, const Type& = Type());
  void erase(Iterator);
  Iterator grow(Iterator, const Type&);
  void prune(Iterator);
  Iterator attach(Iterator, Tree&);
  int generations(Iterator, Iterator);
  Iterator begin();
  Iterator end();
  static bool isRoot(Iterator it);
  static bool isLeaf(Iterator it);
  static bool isOldestChild(Iterator it);
  static bool isYoungestChild(Iterator it);
  static Iterator youngestChild(Iterator it);
  static Iterator parent (Iterator it);
  static num numChildren(Iterator it);

  friend class Iterator {
    Tree* _tree;
    LIt _lit;
  public:
      Iterator();
      Iterator(const Iterator&);
      Iterator(Tree*, Node* = 0);
      Iterator(Tree*, LIt);
      void operator=(const Iterator& it);
      bool operator==(const Iterator& it);
      bool operator!=(const Iterator& it);
      Iterator& operator++();  // prefix operator
      Iterator  operator++(); // postfix increment
      Type& operator*() const;
      bool operator!();
      friend class Tree;
  };
protected:
    List level(int n );
    LIt litn(Node*);
    LIt litp(Node*);
    LIt nextSibling(LIt);
};