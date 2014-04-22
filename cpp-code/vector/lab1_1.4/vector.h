#include <iostream>
#include <ostream>
#include <cassert>
#include <stdexcept>

using std::cout;
// Lab 1.4 Vector with fixed length of elements of type unsigned int.
// The _size specifies the size of the Vector and retrieved by
// the member function size(). Initialize the vector with
// constructor passing size parameter.
class Vector
{
public:
  // Default constructor
  Vector ();

  // Size constructor. Initialize the Vector with size elements
  // i.e make room for size elements and initialize each with
  // default values.
  explicit Vector(size_t size);

  // Copy constructor. Copy the argument Vector for creation of
  // the Vector.
  Vector (const Vector&);

  // Move constructor. Copy a RValue for initialization of
  // the Vector.
  Vector (Vector&&) noexcept;

  // Assignment operator.
  Vector& operator=(const Vector& );

  // Move assignment.
  Vector& operator=(Vector&&);

  // Assignment operator. Assigns initialization list
  // to this Vector.
  Vector& operator=(const std::initializer_list<unsigned int> c) ;

  // Destructor release mem used by this Vector.
  ~Vector ();

  // Index operator for set/get elements in this Vector.
  unsigned int& operator[] (size_t);

  // Index operator for get elements in this Vector.
  const unsigned int& operator[] (size_t) const;

  // Returns the size of this Vector.
  size_t size() const;
protected:
  // -- Internals
  // The size of this Vector, i.e number of elements
  size_t _size;
  // Ptr to heap alloc mem where elems is stored.
  unsigned int* arr;
};
