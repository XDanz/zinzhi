#include <iostream>

namespace ABC {
    class A {
    public:
      A(): _a{0},_b{0},_c{0} {
        std::cout << "A::A() " << std::endl;
      };
        A(const A& obj)            {
            
            std::cout << "A::A(const &A) =>" << std::endl;
            _a = obj._a; _b = obj._b; _c = obj._c;
            std::cout << "A::A(const &A) => ok" << std::endl;
        };

        A(A&& obj) {
            
        }
        
        A& operator=(const A& a)  {
            std::cout << "A::operator=(const &A) =>" << std::endl;
            // this->adjust_values(a.calc());
            std::cout << "A::operator=(const &A) => ok" << std::endl;
            return *this;
        }

        // virtual void adjust_values(int i) = 0;

        // virtual int calc() const  = 0;

        // virtual void toString(std::ostream&) const = 0;

        int getA() const { return _a; };
        int getB() const { return _b; };
        int getC() const { return _c; };

    protected:
        int _a;
        int _b;
        int _c;

        friend std::ostream& operator<<(std::ostream& os, const A& obj)
            {
                os << "a =" << obj._a << "b = " << obj._b << "c =" << obj._c;
                return os;
            } 
    };

}
        

int
main(int argc, char *argv[])
{
    using namespace ABC;
    using namespace std;
    cout << "initialize a" << endl;
    A a{};
    cout << "ok" << endl;
    
    A b = a;
    A c;

    cout << "b = " << b << endl;

    c = a;

    cout << "c = " << c << endl;
    
}
