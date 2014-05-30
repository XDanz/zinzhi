#ifndef MATRIX_H
#define MATRIX_H
#include "vector.h"
#include <iostream>
#include <stdexcept>

/* template <class T> */
/* class Vector : public std::vector< T > { */
/* public: */
/*     explicit Vector <T> (std::size_t size  = 0, T data = T()) :  */
/*     std::vector<T>(size, data) {} */
/*     const T& operator[](unsigned int i) const throw(std::out_of_range) { */
/*         return this->at( i ); */
/*     } */
/*     T& operator[](unsigned int i) throw(std::out_of_range) { */
/*         return this->at( i ); */
/*     }     */
/* }; */

//using namespace std;

class Matrix
{
 public:
    typedef unsigned int index;
    
  class matrix_row : private Vector< int >
  {
  public:
    matrix_row( std::size_t s = 0) : Vector< int >( s ) {}
    using Vector<int>::operator [];


    friend std::ostream& operator<<(std::ostream& os,const matrix_row& row) {
      return os;
    }

  private:
    friend std::istream& operator>>( std::istream& is, matrix_row& row ) {
      int e;
      while ( is >> e ) {
        row.push_back ( e );
      }
      return is;
    }
  };

  // matrix impl --
  Matrix (): m_rows(1), m_cols(1) { 
    m_vectors.push_back(matrix_row(m_cols)); 
  }
        
  Matrix( std::size_t rows, std::size_t cols): m_rows(rows), m_cols(cols) { 
    for ( std::size_t i = 0 ; i < m_rows; i++ ) 
      m_vectors.push_back(matrix_row(m_cols));
  }
    
  Matrix( const Matrix& m):  m_rows(m.rows()), m_cols(m.cols()) {
    for ( std::size_t i = 0; i < m_rows; i++ ) 
      m_vectors.push_back(matrix_row(m_cols));

      copy_elems (m,m.rows(),m.cols());
  }
  
  Matrix(std::size_t size): m_rows( size) , m_cols(1) {
    for ( std::size_t i = 0; i < m_rows; i++ ) 
      m_vectors.push_back(matrix_row(m_cols));
  }

  ~Matrix( ) {}
    
  Matrix& operator= ( const Matrix& m ) {
    if ( &m == this ) 
      return *this;
    
    m_vectors.clear();
    for ( std::size_t i = 0; i < m.rows(); i++ ) {
      m_vectors.push_back(matrix_row(m.cols()));
    }

    this->m_rows = m.rows();
    this->m_cols = m.cols();

    copy_elems (m, m.rows(), m.cols());
    return *this;
  }

  Matrix operator+ ( const Matrix& m) const {
    if ( this->cols() != m.cols() || this->rows() != m.rows()  ) 
      throw std::out_of_range("order does not match");
        
    Matrix sum (this->rows(), this->cols() );
    for (std::size_t i = 0; i < this->rows(); i++ ) {
      for (std::size_t j = 0; j < this->cols(); j++) {
        sum[i][j] = (*this)[i][j] + m[i][j];
      }
    }
    return sum;
  }

  Matrix operator* ( const Matrix& m) const {
    if ( this->cols() != m.rows() ) 
      throw std::out_of_range("cols does not match row");

    int elemSum = 0;

    Matrix sum (this->rows(),m.cols());

    for (std::size_t i = 0; i < sum.rows(); i++ ) {
      for (std::size_t j = 0; j < sum.cols(); i++ ) {
        for (std::size_t k = 0; k < sum.rows(); k++ ) 
          elemSum += (*this)[i][k] * m[k][j];
                
        sum[i][j] = elemSum;
        elemSum = 0;
      }
    }
        return sum;
  }

    bool operator== (const Matrix& m ) const {
        if ( m.cols() != this->cols() || m.rows() != this->rows()) 
            return false;
        
        for ( std::size_t i = 0; i < this->rows(); i++ ) 
          for ( std::size_t j = 0; j < this->cols(); j++) 
              if ( (*this)[i][j] != m[i][j] ) 
                    return false;
        
        return true;
    }
    
    Matrix operator* ( int ) const;
    Matrix operator-( const Matrix& ) const;
    Matrix operator-( ) const;
    
    Matrix& transpose( );
    
    matrix_row& operator[]( index i ) {
        return m_vectors[i];
        
    }
    const matrix_row& operator[]( index i ) const {
        return m_vectors[i];
    }
    
    std::size_t rows() const {
        return m_rows;
    }
    std::size_t cols() const {
        return m_cols;
    }
    
 protected:
    void copy_elems ( const Matrix& m,int minRows, int minCols ) {
        for ( int i = 0; i < minRows; i++ ) {
            for ( int j = 0; j < minCols; j++ )
              (*this)[i][j] = m[i][j];
        }
    }
 private:
    Vector< matrix_row >        m_vectors;
    std::size_t                 m_rows;
    std::size_t                 m_cols;
    
  void add_row( );            // Non mandatory help function
  
  friend std::istream& operator>> ( std::istream& in, Matrix& m) 
  {
    Vector<Vector<int> > vec;
    int e;
    std::size_t cols;
    char c;
    while ( in >> c ) {
      if ( c == ']') break;
      Vector<int> row;
      while ( in >> e ) {
        row.push_back ( e );
      }
      vec.push_back(row);
      cols = row.size();
      in.clear();
    }
    m = Matrix{vec.size(),cols};

    for ( std::size_t i = 0; i < vec.size(); i++ )
      for ( std::size_t j = 0; j < cols; j++ )
        m[i][j] = vec[i][j];
    
      return in;
  }

  friend std::ostream& operator<< ( std::ostream& os, Matrix& m) {
    os << "[" ;
    for ( std::size_t i = 0; i < m.rows() ; i++ ) {
      matrix_row row = m[i];
      os << " ";
      for ( std::size_t j = 0; j < m.cols(); j++ ) {
        os << row[j] << " ";

      }
      if ( i+1 != m.rows() ) os << "\n;";
    }
    os << "]";
    return os;
  }


};

std::istream& operator>> ( std::istream&, Matrix& );
std::ostream& operator<< ( std::ostream&, Matrix& );
Matrix operator* ( int, const Matrix& m);

         

  

#endif // MATRIX_H
