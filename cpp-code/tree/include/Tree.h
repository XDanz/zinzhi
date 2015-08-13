#include <list>
#include <string>

using namespace std;

class Tree {
    struct Node;
    list<Node*> nodes;
public:
    class Iterator;
    Tree();
    Tree(const Tree&);
    Tree(const string&);
    Tree(const string&, const list<Tree*>&);
    ~Tree();
    Tree& operator=(const Tree& t);
    bool operator=(const Tree&t) const;
    bool operator!=(const Tree&) const;
    void clear();
    bool empty() const;
    int size()  const;
    int leaves() const;
    int height() const;
    int level(Iterator it) const;
    int width(int);
    int width();
    void print();
    string& root() const;
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
        Tree* tree;
        list<Node*>::Iterator lit;
    public:
        Iterator();
        Iterator(const Iterator&);
        Iterator(Tree*, Node* = 0);
        Iterator(Tree*, lit);
        void operator=(const Iterator& it);
        bool operator==(const Iterator& it);
        bool operator!=(const Iterator& it);
        Iterator& operator++();  // prefix operator
        Iterator  operator++(); // postfix increment
        std::sring& operator*() const {
            return (*lit)->value;
        }
        bool operator!();
        friend class Tree;
    };
protected:
    int inernal_leaves() const;
    list<Node*> level(int n);
    list<Node*>::Iterator litn(Node*);
    list<Node*>::Iterator litp(Node*);
    list<Node*>::Iterator nextSibling(list<Node*>::Iterator);
};
