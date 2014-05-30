#include <iostream>
struct Data { // 12 bytes 3*4 bytes
    int x, y, z;
  Data() { 
    std::cout << "[" << this << "]:" << " ctor \n";
  }
  ~Data() {
    std::cout << "[" << this << "]:" << " dtor \n";
  }
};  

Data ** foo(Data ** v, int x) {
  for (int i = 0; i < x; i++) {
    if (v[i] != 0)
      v[i] = new Data;
  }
  return v;
}

  
  

// }

int main () {
    const int size = 5;
    // allocate 5 new data objects on the heap
    // Data *ptr = new Data[5];
    // std::cout << sizeof (ptr[0]) << std::endl;
    
    Data  **v = new Data *[size];
    foo(v,size);
    
    // for ( int i = 0; i < size ; i++ ) {
    //   std::cout << "v[" << i << "]:" << v[i] << std::endl;
    // }
    
    // std::cout << "sizeof( v[0] ) " <<  sizeof ( v[0] ) << std::endl;
    
    // std::cout << "sizeof( v[0][0] ) " <<  sizeof ( v[0][0] ) << std::endl;
    // Data ** p = foo(v, size);
    // for ( int i = 0; i < size; i++)
    //   delete v[i];
    
    delete [] v;
}
