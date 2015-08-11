
class Tree {
    struct Node;
    List<Node*> nodes;
public:
    class Iterator;
    Tree();
    Tree(const Tree&);
    Tree(const Type&);
    Tree(const Type&, const List<Tree*>&);
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
    int pathLenght();
    int width(int);
    int width();
    void print();
    string& root() const;
    void reflect();
    void defoliate();
    Iterator insert(Iterator, const string& = string());
    void erase(Iterator);
    Iterator grow(Iterator, const string&);
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
        Tree* tree;
        List<Node*>::Iterator lit;
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
    List<Node*> level(int n);
    List<Node*>::Iterator litn(Node*);
    List<Node*>::Iterator litp(Node*);
    List<Node*>Iterator nextSibling(List::Iterator);
};

bint n(Node*);
int h(Node*);
//int l(Node*,It);
//Node* clone(Node*, List&, Node*);




