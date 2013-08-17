
template <typename T>
class List
{

protected:
  // inner class Node which holds information
  // on prev,next Node and data
  class Node
  { public:
    Node ( const T& data = T() , Node* prev=0, Node* next=0 )
      : _data ( data ) , _prev ( prev ), _next ( next )
    { if (_prev == 0 ) _prev = this;
      if (_next == 0 ) _next = this;
    }
    T _data;
    Node* _prev;
    Node* _next;
  };
  // -- Node class end
  Node * _;  // the dummy node
  int _size; // the size of this List

  // Public declerations of the List template members
public:
  // inner class Iterator which is used to iterate through
  // the nodes
  class Iterator
  { friend class List;
  public:
    Iterator(Node* p) : _(p) { }
    T& operator*() { return _->_data; }
    void operator=(const Iterator& it) { _ = it._; }
    bool operator==(const Iterator& it) { return _ == it._; }
    bool operator!=(const Iterator& it) { return _ != it._; }
    Iterator operator++(int) //postfix
    { Iterator it(_);
      _ = _->_next;
      return it;
    }
    Iterator& operator++()
    {  _ = _->_next;
      return *this;
    }
    Iterator operator--(int) //postfix
    { Iterator it(_);
      _ = _-> _prev;
      return it;
    }
    Iterator& operator--()
    { _ = _->_prev;
      return *this;
    }
  protected:
    Node * _;
  };
  // -- Iterator class END

  List();
  List(const List&);
  List(int);
  List(int,const T&);
  List(Iterator& , Iterator& );
  ~List();
  int size() const;
  bool empty() const;
  T& front() const;
  T& back() const;
  Iterator begin();
  Iterator end();
  void push_front(const T&);
  void push_back(const T&);
  void pop_front();
  void pop_back();
  Iterator insert(Iterator&, const T&);
  Iterator insert(Iterator&, int,const T&);
  // void erase(Iterator&);
  // void erase(Iterator&,Iterator&);
  // void clear();
  // void splice(Iterator&,List&,Iterator);

}; // -- template <class T> class List

// Implementations start
// Creates an empty List object
template <typename T>
List<T>::List () : _size(0)
{ _ = new Node();
}

// Creates an List from the reference supplied i.e copy constructor
template <typename T>
List<T>::List( const List& l ) : _size(l._size)
{ _ = new Node();
  Node* pp = _;
  for ( Node* p = l._->_next; p != l._; p = p->_next, pp = pp->_next )
    pp->_next = pp->_next->_prev = new Node ( p->_data , pp, _ );
}

template <typename T>
List<T>::List(int n) : _size(n)
{ _ = new Node ();
  Node* p = _;
  for (int i = 0; i < n ; i++)
    p = p->_prev = new Node (T(),_,p);
  _->_next = p;
}

template <typename T>
List<T>::List(int n, const T& t) : _size(n)
{ _ = new Node(); //dummy node
  Node* p = _;
  for (int i=0; i<n; i++)
    p = p->_prev = new Node(T(),_,p);
  _->_next = p;
}

template <typename T>
List<T>::List(Iterator& it1,Iterator& it2) : _size(0)
{ _ = new Node(); //dummy node
  Node * pp = _;
  for (Node* p = it1._; p != it2._; p = p->_next,pp = pp->_next)
    { pp->_next = new Node(p->_data,pp,_);
      ++_size;
    }
  _->_prev = pp;
}

template <typename T>
List<T>::~List()
{ Node* p = _->_next;
  while ( p != _ )
    { Node* pp = p->_next;
      delete p;
      p = pp;
    }
  delete _;
}

template <typename T>
int List<T>::size() const
{ return _size;
}

template <typename T>
bool List<T>::empty() const
{ return _size == 0;
}

template <typename T>
T& List<T>::front() const
{ return _->_next->_data;
}

template <typename T>
T& List<T>::back() const
{ return _->_prev->_data;
}

template <typename T>
typename List<T>::Iterator List<T>::begin() {
  return Iterator(_->_next);
}
template <typename T>
typename List<T>::Iterator List<T>::end () {
  return Iterator(_);
}

template <typename T>
void List<T>::push_front(const T& x) {

}
template <typename T>
void List<T>::push_back (const T& x )
{ _->_prev = _->_prev->_next = new Node (x,_->_prev,_);
  ++_size;
}

template <typename T>
void List<T>::pop_front()
{ Node* p = _->_prev;
  _->prev = p->_prev;
  p->_prev->_next = _;
  delete p;
  --_size;
}

template <typename T>
typename List<T>::Iterator List<T>::insert(Iterator& it, const T& x)
{ it._->_prev = it._->_prev->_next = new Node (x,it._->_prev,it._);
  it._ = it._->_prev;
  ++_size;
}






