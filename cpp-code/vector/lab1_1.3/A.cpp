#include <iostream>

class A {
public:
  A()
  {
    std::cout << "[" << this << "]: The default ctor" << std::endl;
  }
  A(const A & ref)
  {
    std::cout << "[" << this << "]: The copy ctor src-cpy:" <<
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
    std::cout << "[" << this << "]: The assignment operator src-cpy:" <<
      &s <<  std::endl;
    return *this;
  }
};

void no_ref(A a) {
  std::cout << "no_ref(a:" << &a << ") =>" << std::endl;
  std::cout << "no_ref(a:" << &a << ") => ok" << std::endl;
}
void with_ref(const A & a) {}


int main() {
  std::cout << " ==== main ==== \n";
  A a("my name is a");
  std::cout << "&a=" << &a << "\n";
  A b = a;         // vad är skillnaden
  A c(a);          // mellan dessa
  A d;             // tre tekniker?
  d = a;
  std::cout << " Call no_ref(a:" << &a << ")" << std::endl;
  no_ref(a);  // Bildas temporära objekt?
  std::cout << " Call no_ref(a:" << &a << ")" << std::endl;

  with_ref(a);     // Bildas temporära objekt?

  A *aa = new A[5];
  delete aa;       // Vad kommer att hända?
  std::cout << " ==== main ==== END \n";
  return 0;
}
