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
    //if (v[i] != 0)  {
      v[i] = new Data;
      
    //   //} else {
    //   std::cout << "v[" << i << "]=" << v[i] << "\n";
    // }
  }
  // ;


    return v;
}

int main () {
    const int size = 5;
    Data  **v = new Data * [size];
    std::cout << "sizeof(Data)=" << sizeof(Data) << "\n";
    std::cout << "sizeof(int)=" << sizeof(int) << "\n";
    std::cout << "sizeof(v)/v[0]= "<< sizeof(Data)/sizeof(v[0]) << "\n";
    //foo(v, size);
    Data ** p = foo(v, size);
    // for ( int i = 0; i < size; i++)
    //   delete p[i];
    delete [] p;
}
