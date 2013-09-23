#include <stdexcept>
#include <algorithm>

#define CAPACITY 50

using namespace std;
// Template Vector which handles re-sizing of its capacity.
//
// The following depicts the protected state variables:
//
// capacity - Is the count of how many elements the Vector
//            is capable to store before it needs to be resized.
//            it does not specifiy thecount how many stored elements there
//            are.
//
// size     - Is the count of how many object the Vector stores.
//
// INVARIANT:  0 <= size <= capacity
//
// When size reaches capacity then re sizing occurres
// with CAPACITY amount of elements but the size is left uncanged.
template <typename T>
class Vector
{
public:
  // ---- Constructor defs start
  // Default. set capacity to CAPACITY ,
  // its size and alloc the array with CAPACITY.
  Vector<T>();
  // Size Ctor , avoid implicit conversion with explicit
  // set capacity the size to the arg and alloc the array with capacity.
  explicit Vector<T>(const std::size_t sz);

  // initialize sz elements with default value e
  Vector<T>(const std::size_t sz, const T& e);
  // Copy Ctor.
  Vector<T>(const Vector<T>& v);

  // initializer_list constructor
  Vector<T>(const std::initializer_list<T>& c);
  // move constructor initialize a Vector whith a rvalue or with the
  // explicit std::move()
  Vector<T>(Vector<T>&& rhs) noexcept;
  // --- Constructor defs end

  // Ops. assignment
  Vector<T>& operator=(const Vector<T>& v);

  // Initalize the Vector through initializer list
  Vector<T>& operator=(const std::initializer_list<T> c ) {
    _size = 0;
    for ( T  x : c ) push_back(x);

    return *this;
  }

  // Index ops.
  T& operator[](std::size_t i)
  {  if ( i < _size )
      return arr[i];
    throw std::out_of_range("Index out of Vector range");
  }

  const T& operator[] (std::size_t i) const
  {  if ( i < _size)
      return arr[i];
    else
      throw std::out_of_range("Index out of Vector range");
  }

  // add element e at the end of the vector
  void push_back ( const T& e );
  // delete an element at position p
  void erase(const size_t p);

  // insert element e at position p, if p == size()
  // the insert will behave as push_back
  void insert(const size_t p,const T& e);

  // return the number of element in the Vector
  std::size_t size() const { return _size; }

  // clear the contained element from the Vector
  void clear();

  // retrives the current capacity of the Vector
  // used for convenience
  std::size_t capacity() const { return _capacity; }

  // Sort elements of this Vector
  void sort(bool ascending = true);

  // Same as sort() but in addition it removes duplicates
  void unique_sort(bool ascending = true);

  // returns true wether the element elem is contains in the Vector
  bool exists(const T& elem);

  // Dtor.
  ~Vector() { delete [] arr; }

protected:
  // size of the array is the capacity
  size_t _capacity;
  // amount filled
  size_t _size;
  // our array
  T* arr;

  // Using  merge sort
  void m_sort (T* a,size_t n, bool asc);

  // merge a b with size na, nb into c
  void merge (const T* a,size_t na,const T* b, size_t nb, T* c, bool asc);

  void resize_if_not_fit () {
    if ( _size == _capacity )
      resize();
  }

  // Re size the capacity of the vector with CAPACITY amount of
  // elements.
  // update the capacity
  // size should be left alone.
  void resize()
  { T *tmp = new T[_capacity + CAPACITY];
    for ( int i = 0 ; i < _size; i++ )
      tmp[i] = arr[i];

    _capacity += CAPACITY;

    delete [] arr;
    arr = tmp;
  }
};

// -- Definition start
template<typename T>
Vector<T>::Vector(): _capacity(CAPACITY), _size(0), arr(new T[CAPACITY])
{ }

template<typename T>
Vector<T>::Vector(std::size_t sz) : _size(sz)

{
  _capacity = std::max(sz,static_cast<size_t>(CAPACITY));
  arr = new T[_capacity];

  // initialize size elements to default values.
  for ( int i = 0; i < _size; i++ )
    arr[i] = T();
}
template<typename T>
Vector<T>::Vector(size_t sz,const T& elem ) :_size(sz)
{
  _capacity = std::max(sz,static_cast<size_t>(CAPACITY));
  arr = new T[_capacity];

  // initialize size elements to default values.
  for ( int i = 0; i < _size; i++ )
    arr[i] = elem;

}

template<typename T>
Vector<T>::Vector(const Vector<T>& v) :_capacity(v._capacity),
                                       _size(v._size),
                                       arr(new T[v._capacity])
{
  // copy each element in the arg vector
  // to the
  for (int i = 0; i < _size; i++ )
    arr[i] = v[i];

}
template<typename T>
Vector<T>::Vector(const std::initializer_list<T>& c):
  _capacity(CAPACITY),
  _size(0), arr(new T[CAPACITY])
{
  for ( T  x : c ) push_back(x);
}

template <typename T>
Vector<T>::Vector(Vector<T>&& v) noexcept : _capacity(v._capacity),
  _size(v._size),
  arr(v._arr)
{  v._arr = nullptr; }


template <typename T>
Vector<T>& Vector<T>::operator=(const Vector<T>& v)
{
  if ( &v != this )
    {
      T* tmp = new T[v._capacity];
      _size = v._size;
      _capacity = v._capacity;

      // copy each elements in the argument vector
      // to the temp array
      for (int i = 0; i < v._size; i++ )
        tmp[i] = v[i];

      // Delete free space
      delete [] arr;
      // assign address of the temp array
      // to ptr arr
      arr = tmp;
    }
  return *this;
}


template <typename T>
void Vector<T>::erase(const size_t p)
{  if (p < _size ) {
    for (int i = p; i < _size-1; i++)
      arr[i] = arr[i+1];

    _size--;
  } else {
    throw std::out_of_range("Index out of Vector range");
  }
}

template <typename T>
void Vector<T>::insert (const size_t p,const T& e)
{
  if ( p < _size ) {
    resize_if_not_fit();

    for ( int i = this->_size; i > p; i-- )
      arr[i] = arr[i-1];

    arr[p] = e;
    _size++;
  } else if ( p == this->_size ) {
    push_back(e);
  } else {
    throw std::out_of_range("Index out of Vector range");
  }
}

template <typename T>
void Vector<T>::push_back ( const T& e )
{
  resize_if_not_fit();

  arr[_size++] = e;
}
template <typename T>
void Vector<T>::clear()
{
  _size = 0;
}

template <typename T>
void Vector<T>::sort(bool ascending)
{
  m_sort(arr, _size, ascending);
}

template <typename T>
void Vector<T>::m_sort(T* a, size_t n, bool asc)
{
  if (n < 2) return;

  size_t  nleft = n/2; size_t nright = n - nleft;

  m_sort(a, nleft, asc);
  m_sort(a+nleft, nright, asc);

  T* p = new T[n];
  merge(a, nleft,  a+nleft, nright, p, asc);
  for (int i = 0; i < n; i++) a[i] = p[i];
  delete[] p;
}

template<typename T>
void Vector<T>::merge(const T* a, size_t na, const T* b, size_t nb,
                      T* c, bool asc)
{ size_t ia = 0, ib = 0, ic  = 0;

  while (ia < na && ib < nb) {
    if (asc)
      c[ic++] = (a[ia] < b[ib] ? a[ia++] : b[ib++]);
    else
      c[ic++] = (a[ia] < b[ib] ? b[ib++] : a[ia++]);
  }
  while (ia < na) c[ic++] = a[ia++];
  while (ib < nb) c[ic++] = b[ib++];
}

template<typename T>
void Vector<T>::unique_sort(bool ascending)
{
  sort(ascending);
  T* end = std::unique(arr, arr+_size);
  _size = end - arr;

}

template<typename T>
bool Vector<T>::exists(const T& elem )
{
  return (std::find(arr,arr+_size, elem) == (arr+_size)) ? false : true;
}









