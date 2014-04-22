#include <iostream>
#include <ostream>
#include <cassert>
#include <stdexcept>
#include "vector.h"

// ----------------------------------------
// Definition of the Vector class
// -- Vector constructor defs start
Vector::Vector():_size(0), arr(new unsigned[_size])
{ }

// Initialize the vector with size elements assign each initialize
// each with the default value.
Vector::Vector(size_t size): _size(size), arr(new unsigned[_size])
  // Initialize the Vector setting all elements to zero as
{   for (size_t i = 0; i < _size; i++ ) arr[i] = 0; }

// Copy constructor
Vector::Vector (const Vector& v): _size(v._size), arr(new unsigned[_size])
  // Allocates heap mem , make room for
  // v._size elements. cpy all the elements
  // from arg Vector v to the heap mem
{  for (size_t i = 0; i < _size; i++ ) arr[i] = v[i]; }


// Move constructor
Vector::Vector(Vector&& v) noexcept: _size(v._size) , arr(v.arr)
{  v.arr = nullptr; }
// -- constructor definition end

// Assignment operator
Vector& Vector::operator=(const Vector& v)
// check for self-assignment
// Alloc temporary array with v.size() elements
// cpy elements from arg Vector to the alloc mem
// delete old mem , cpy tmp ptr to arr
// return ref to this
{
  if ( &v != this ) {
    unsigned * tmp = new unsigned [v._size];
    // copy elements from v to arr
    for ( size_t i = 0; i < v._size; i++ ) tmp[i] = v[i];

    delete [] arr;
    arr = tmp;
    _size = v._size;
  }
  return *this;
}
// Move Assignment operator
Vector& Vector::operator=(Vector&& v)
{
  if ( &v != this ) {
    // check for self-assignment
    // free mem used by this
    // cpy pointer from v.arr ,steal elements from rhs
    delete [] arr; arr = v.arr;
    _size = v._size;
    v.arr = nullptr;
  }
  return *this;
}

// index operator
unsigned int& Vector::operator[] (size_t i)
{
  if (i < _size)
    return arr[i];

  throw std::out_of_range("Index out of Vector range");

}
// const index operator for for get
const unsigned int& Vector::operator[] (size_t i) const
{
  if (i < _size)
    return arr[i];

  throw std::out_of_range("Index out of Vector range");

}

// Assignment operator with initializer_list
Vector& Vector::operator=(const std::initializer_list<unsigned int> c)
// Alloc temporary array with c.size() elements
// cpy elements from initialization list to the alloc mem
// delete old mem , cpy tmp ptr to arr ptr
// return ref to this
{  unsigned int* tmp = new unsigned int[c.size()];
  int i = 0;
  for (auto x : c )  tmp[i++] = x;
  delete [] arr;
  arr = tmp;
  _size = c.size();
  return *this;
}


// member function return the size of the Vector.
size_t Vector::size () const
{  return _size; }

// Destructor
Vector::~Vector()
{  delete [] arr; }
