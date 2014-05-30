#include <iostream>

class A {
public:
  A()
  {
    std::cout << "[" << this << "]: The default ctor" << std::endl;
  }
  A(const A & ref)
  {
    std::cout << "[" << this << "]: The copy ctor src:" <<
      &ref << std::endl;
  }
  ~A()
  {
    std::cout << "[" << this << "]: The destructor" << std::endl; }

  A(char * s)
  {
    std::cout << "[" << this << "]: Some other ctor s=" << s
              << std::endl;}
  A & operator=(const A & s)
  {
    std::cout << "[" << this << "]: The assignment operator src:" <<
      &s <<  std::endl;
    return *this;
  }
};

void no_ref(A a) {
  std::cout << "\t\t  no_ref(" << &a << ") =>" << std::endl;
  std::cout << "\t\t  no_ref(" << &a << ") => ok" << std::endl;
}
void with_ref(const A & a) {
  std::cout << "\t\t with_ref(a:" << &a << ") =>" << std::endl;
  std::cout << "\t\t with_ref(a:" << &a << ") => ok" << std::endl;
  

}


int main() {
  std::cout << " ==== main ==== \n";
  A a("my name is a");
  std::cout << "\t\t &a=" << &a << "\n";
  A b = a;         // vad är skillnaden
  A c(a);          // mellan dessa
  A d;             // tre tekniker?
  d = a;
  std::cout << "\t\t  Call no_ref(" << &a << ") =>" << std::endl;
  no_ref(a);  // Bildas temporära objekt?
  std::cout << "\t\t  Call no_ref(" << &a << ") => ok" << std::endl;

  with_ref(a);     // Bildas temporära objekt?

  A *aa = new A[5];
  delete aa;       // Vad kommer att hända?
  std::cout << " ==== main ==== END \n";
  return 0;
}
