#include <iostream>

namespace ABC {
    class A {
    public:
        A() { 
            std::cout << "A::A() " << std::endl;
        };
    
        A& operator=(const A& a)  {
            std::cout << "A::operator=(const &A) =>" << std::endl;
            this->adjust_values(a.calc());
            std::cout << "A::operator=(const &A) => ok" << std::endl;
            return *this;
        }

        virtual void adjust_values(int i) = 0;

        virtual int calc() const  = 0;

        virtual void toString(std::ostream&) const = 0;

        int getA() const { return _a; };
        int getB() const { return _b; };
        int getC() const { return _c; };

    protected:
        int _a;
        int _b;
        int _c;
    };

    class C;

    class B: public A {
    public:
        B() {};
        B(int a, int b, int c) {
            //: A::_a{a}, A::_b{b},A::_c{c} {
            _a = a; _b = b; _c = c;
            std::cout << "B::B(int,int,int)" << std::endl;
        }
    
        virtual void toString(std::ostream& os) const {
            os << "ABC::B@" << this;
            os << "{a:";
            os << _a;
            os << " b:";
            os << _b;
            os << " c:";
            os << _c << "}";
        }
    
        int calc  ( ) const {
            std::cout << " B::calc() _a = " << _a  << "\n" ;
            return 1;
        }
        void adjust_values(int i ) {
            _a = _a+i, _b = _b + i, _c = _c + i;
            std::cout << " B::adjust_values(" << i << ") " << std::endl;
        }

        A& operator=(const A& a) {
            std::cout << "in A& B::operator(const A&) =>\n";
            this->A::operator=(a);
            //adjust_values(a.calc());
            std::cout << "in A& B::operator(const A&) => ok\n";
            return *this;
        }
    };


    class C: public A {
    public:
        C(int a, int b, int c) {
            std::cout << "C::C(int,int,int)" << std::endl;
            _a = a; _b = b; _c = c;
        }

        void toString(std::ostream& os) const {
            os << "ABC::C@" << this << "{a:";
            os << _a;
            os << " b:";
            os << _b;
            os << " c:";
            os << _c << "}";
        }

        void adjust_values(int i ) {
            _a = _a+i, _b = _b + i, _c = _c + i;
            std::cout << " C::adjust_values() _a = " << _a  << "\n" ;
        }

        int calc () const {
            std::cout << " C::calc() _a = " << _a  << "\n" ;
            return 2;
        }
    };

    std::ostream& operator<<(std::ostream& os, const A& d) {
        d.toString(os);
        return os;
    }
} // namespace


int main (int argc , char** argv) {
    ABC::B b1{1,2,3};
    std::cout << " b1 = " << b1 << std::endl;

    ABC::B b2{2,3,4};
    std::cout << "b2 = " << b2 << std::endl;

    ABC::C c1{11,22,33};
    std::cout << " c1 = " << c1 << std::endl;
    b1 = c1;
    std::cout << " b1= " << b1 << std::endl;

    b1 = b2;
    std::cout << "b1 = " << b1 << std::endl;

    return 0;
}
