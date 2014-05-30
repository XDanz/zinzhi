#ifndef MATRIX_TEST_H_INCLUDED
#define MATRIX_TEST_H_INCLUDED

#include "Matrix.h"
#include "TestSuite.h"
#include <fstream>
#include <sstream>

class MatrixTestSuite : public CxxTest::TestSuite
{

    Matrix a_matrix_3by2() {    // [ 1 3 5 ]
        Matrix m;               // [ 0 2 0 ]
        std::stringstream s("  [ 1 3 5 ; 0 2 0 ]");
        s >> m;
        return m;
    }

    void init_matrix( Matrix& m, const char* file )
    {
        std::stringstream stream( file );
        stream >> m;
    }

public:
    void testIndexOperator ( )
    {
        Matrix m( 2, 2 );
        TS_ASSERT( m[ 0 ][ 1 ] == 0 );

        m = a_matrix_3by2();
        TS_ASSERT( m[ 0 ][ 0 ] == 1 );

        init_matrix(m, "  [ 1 3 5 ; 0 2 1 ]");
        TS_ASSERT( m[ 0 ][ 0 ] == 1 );

        std::stringstream ss;
        ss << m;
        ss >> m;
        TS_ASSERT( m[ 0 ][ 0 ] == 1 );
    }
    void test1ArgConstructor ()
    {
        Matrix m{3};
        TS_ASSERT ( m.rows() == 3 );
        TS_ASSERT ( m.cols() == 1 );
    }

    void test2ArgConstructor () 
    {
        Matrix m{2,2};
        TS_ASSERT ( m.rows() == 2 );
        TS_ASSERT ( m.cols() == 2 );
    }

    void testAssigmentOperator ()
    {
        TS_TRACE ( " assignment test");
        Matrix m,m2;
        init_matrix(m, " [ 1 3 4 5 ; 1 2 3 4 ; 2 2 1 1 ]");
        std::cout << m << std::endl;
        TS_TRACE ( " assigning ");
        m2 = m;
        TS_TRACE ( " assigning ok");


        TS_ASSERT_EQUALS ( m , m2 );
    }
    void testCopyConstructor ()
    {
        Matrix m;
        init_matrix(m, " [ 1 3 4 5 ; 1 2 3 4 ; 2 2 1 1 ]");
        Matrix m2{m};
        TS_ASSERT_EQUALS ( m , m2 );
        
    }
};

#endif

