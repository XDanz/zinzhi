#include <list>
#include <string>

using namespace std;

class Tree {
    struct Node {
        string value;
        list<Node*> children;
        Node* parent;
        Node(string = string(), Node* = 0);
    };
    list<Node*> nodes;
public:
    class Iterator;
    Tree();
    Tree(const Tree&);
    Tree(const string&);
    Tree(const string&, const list<Tree*>&);
    ~Tree();
    Tree& operator=(const Tree&);
    bool operator==(const Tree&) const;
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
    static int numChildren(Iterator it);

    class Iterator {
        Tree* tree;
        list<Node*>::iterator lit;
    public:
        Iterator();
        Iterator(const Iterator&);
        Iterator(Tree*, Node* = 0);
        Iterator(Tree*, list<Node*>::iterator it);
        void operator=(const Iterator& it);
        bool operator==(const Iterator& it);
        bool operator!=(const Iterator& it);
        Iterator& operator++();  // prefix operator
        Iterator  operator++(int); // postfix increment
        std::string& operator*() const {
            return (*lit)->value;
        }
        bool operator!();
        friend class Tree;
    };
protected:
    int internal_height(Node* p) const;
    int internal_leaves(Node* p) const;
    int internal_level(Node* p, Iterator it) const;
    Node* tree_clone(Node* p, list<Node*>& nodes, Node* parent);
    list<Node*> level(int n);
    list<Node*>::iterator litn(Node*);
    list<Node*>::iterator litp(Node*);
    list<Node*>::iterator nextSibling(list<Node*>::iterator);
    void print(int,string);
};
