#ifndef MATRIX_H
#define MATRIX_H
#include <vector>
#include <iostream>
#include <stdexcept>

template <class T>
class Vector : public std::vector< T > {
public:
    explicit Vector <T> (std::size_t size  = 0, T data = T()) : 
    std::vector<T>(size, data) {}
    const T& operator[](unsigned int i) const throw(std::out_of_range) {
        return this->at( i );
    }
    T& operator[](unsigned int i) throw(std::out_of_range) {
        return this->at( i );
    }    
};

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
    private:
        friend std::istream& operator>>( std::istream&, Matrix& );
    };
    
    Matrix ( ): m_vectors(matrix_row(1)) , m_rows(1), m_cols(1) { }

  Matrix( std::size_t rows, std::size_t cols): m_vectors(matrix_row(rows)),
        m_rows(rows), m_cols(cols) { }
    
  Matrix( const Matrix& m): m_vectors(matrix_row(m.rows())), 
        m_rows(m.rows()), m_cols(m.cols()) {
        copy_elems (m,m.rows(),m.cols());
    }
  Matrix(int size): m_vectors(matrix_row(size)), m_rows(size), m_cols(1) 
        {

        }
    ~Matrix( );
    
    Matrix& operator= ( const Matrix& m ) {
        if ( m == this ) 
            return this;

        int minCols = (m.cols() < this->cols())? m.cols() : this->cols();
        int minRows = (m.rows() < this->rows())? m.rows() : this->rows();
       
        m_vectors.clear();
        for ( int i = 0; i < minRows; i++ ) {
            m_vectors.push_back(matrix_row(minCols));
        }

        copy_elems (m, minRows, minCols );
        this->m_rows = minRows;
        htis->m_cols = m_cols;
        
        return this;
    }

    Matrix operator+ ( const Matrix& m) const {
        if ( this->cols() != m.cols() || this->rows() != m.rows()  ) 
            throw std::out_of_range("order does not match");
        
        Matrix sum (this->row(), this->cols() );
        for (int i = 0; i < this->rows(); i++ ) {
            for ( int j = 0; j < this->cols(); j++) {
                sum[i][j] = this->[i][j] + m[i][j];
            }
        }
        return sum;
    }

    Matrix operator* ( const Matrix& m) const {
        if ( this->cols() != m.rows() ) 
            throw std::out_of_range("cols does not match row");

        int k = 0;
        int elemSum = 0;

        Matrix sum (this->row(),m.cols());

        for ( int i = 0; i < sum.rows(); i++ ) {

            for (int j = 0; j < sum.cols(); i++ ) {
                
                for (; k < sum.row(); k++ ) elemSum += (*this)[i][k] * m[k][j];
                
                sum[i][j] = elemSum;
                elemSum = k = 0;
            }
        }
        return sum;
    }

    Matrix operator* ( int ) const;
    Matrix operator-( const Matrix& ) const;
    Matrix operator-( ) const;
    
    Matrix& transpose( );
    
    matrix_row& operator[]( index i );
    const matrix_row& operator[]( index i ) const;
    
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
                this->[i][j] = m[i][j];
        }
    }
 private:
    Vector< matrix_row >        m_vectors;
    std::size_t                 m_rows;
    std::size_t                 m_cols;
    
    void add_row( );            // Non mandatory help function
    friend std::istream& operator>> ( std::istream&, Matrix& );
};

std::istream& operator>> ( std::istream&, Matrix& );
std::ostream& operator<< ( std::ostream&, Matrix& );
Matrix operator* ( int, const Matrix& );

#endif // MATRIX_H
