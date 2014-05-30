#include <iostream>
#include "Matrix.h"
#include <fstream>
#include <sstream>


// class Matrix
// { 
// public:
//   Matrix(unsigned int r = 1, unsigned int c = 1): row(r)  
//   {
//     for(int i = 0; i < r; i++) 
//       row[i] = new Vector<int>(c);
//   }

//   Matrix(const Matrix& matrix) { }
  
//   ~Matrix() {
//     for(int i = 0; i < row.size(); i++)      
//       delete row[i];
//   }
  
//   Vector<int>& operator[](unsigned int i){
//     return *row[i];
//   }
//   const Vector<int>& operator[](unsigned int i) const{
//     return *row[i];
//   }
  
  
//   Matrix operator+( Matrix& matris) {
    
//     Matrix msum(this->rows(),this->columns());
    
//     for(int i = 0; i < this->rows(); i++){
//       for(int j = 0; j < this->columns(); j++){
// 	msum[i][j] = (*this)[i][j] + matris[i][j];
//       }
//     }
//     return msum;

//   }
  

//   unsigned rows() const  {  
//     return row.size();      
//   }

//   unsigned columns() const  { 
//     return row[0]->size();    
//   }


//   friend ostream& operator<<(const ostream& out, const Matrix& matrix)
//   {
//     for(int i = 0; i < matrix.rows(); i++){
//       std::cout << "[" ;
      
//       for(int j = 0 ; j < matrix.columns(); j++)
// 	std::cout << " " << matrix[i][j] << " ";
      
//       std::cout << "]" << std::endl;
//     }

//     return cout;

//   }
   
  
// protected:
//   Vector<Vector<int> *> row;

// };

int main() {
  Matrix a;
  std::stringstream s("  [ 1 3 5 ; 0 2 0 ]");

  std::cout << "feeding .." << std::endl;
  s >> a;

  // Matrix b(3,3);
  std::stringstream s2("[ 1 2 -3 ; 5 6 7 ] ");
  s2 >> a;

  // Matrix b(3,3);
  
  a[0][0] = 1;
  // a[2][2] = 2;

  // b[0][0] = 1;
  // b[2][2] = 2;

  std::cout << a  << std::endl;
  // std::cout << "a+a=" <<  (a+b) << std::endl;

}
  

