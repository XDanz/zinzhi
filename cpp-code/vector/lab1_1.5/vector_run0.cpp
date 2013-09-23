#include <iostream>
#include "vector.h"
#include <cassert>

using std::cout;
#define SZ(V) (V).size()
#define SZ_PTR(V) (V)->size()

#define C(V) (V).capacity()
#define C_PTR(V) (V)->capacity()

#define EQ_SIZE(V,N) assert(SZ(V) == (N))
#define EQ_SIZE_PTR(V,N) assert(SZ_PTR(V) == (N))

#define EQ_CAP(V,N) assert(C(V) == (N))
#define EQ_CAP_PTR(V,N) assert(C_PTR(V) == (N))

#define EQ_V_SIZE(V0,V1) assert(SZ(V0) == SZ(V1))
#define EQ_V_CAP(V0,V1) assert(C(V0) == C(V1))




#define PB(V,A) (V).push_back((A))

#define PRINT(V) do {                                   \
    cout << "(sz:" << SZ(V) << ",cap:" << C(V) << " ["; \
    for ( size_t i = 0 ; i< SZ(V) ; i++ )               \
      cout << (V)[i] << ((i < SZ(V)-1)? "," : "");      \
    cout << "] \n";                                     \
  } while (0)

#define EQ(v0,v1) do {                          \
    EQ_V_SIZE(v0,v1);                           \
    EQ_V_CAP(v0,v1);                            \
    for ( size_t i = 0 ; i < SZ(v0); i++ )      \
      assert((v0)[i] == (v1)[i]);               \
  } while (0)

#define EQ_W_STD_V(V0,V1) do {                  \
    EQ_V_SIZE(V0,V1);                           \
    for ( size_t i = 0 ; i < SZ(V0); i++ )      \
      assert((V0)[i] == (V1)[i]);               \
  } while (0)


template<typename T>
bool is_not_equal(const Vector<T>& v0,const Vector<T>& v1);

void test_default_constructor()
{

  {
    // size() should be equal to 0
    // capacity() should be equal to CAPACITY
    Vector<int> v0;
    EQ_SIZE(v0,0);
    EQ_CAP(v0,CAPACITY);
    bool throwed = false;
    try {

      v0[1] = 10;

    } catch(std::out_of_range e) {
      std::cout << e.what() << std::endl;
      throwed = true;
    }

    assert(throwed);

    v0 = { 6,7,8,9,10 };
    PRINT(v0);
    v0.push_back(11);
    v0.push_back(12);
    PRINT(v0);
    EQ_CAP(v0,CAPACITY);
  }  // Block end--

  Vector<int> *v0 = new Vector<int>();
  EQ_SIZE_PTR(v0,0);
  EQ_CAP_PTR(v0,CAPACITY);
  v0->sort();
  EQ_SIZE_PTR(v0,0);
  EQ_CAP_PTR(v0,CAPACITY);
  v0->clear();
  EQ_SIZE_PTR(v0,0);
  EQ_CAP_PTR(v0,CAPACITY);

  delete v0;

}

void test_size_constructor()
{
  // size() should be equal to 0
  // capacity() should be equal to CAPACITY
  Vector<int> v0(10);
  EQ_SIZE(v0,10);
  EQ_CAP(v0,CAPACITY);
  PRINT(v0);

  Vector<int> v1;
  for ( size_t i = 0 ; i < 10 ; i++) PB(v1,0);
  EQ(v0,v1);
  PRINT(v1);
  v0.sort();
}

void test_cpy_constructor()
{
  // Copy constr.
  Vector<int> v0(10);
  for ( size_t i = 0 ; i < 10 ; i++) PB(v0,i);

  PRINT(v0);
  Vector<int> v1 = v0;
  PRINT(v1);
  Vector<int> v2(v1);
  PRINT(v2);

  EQ(v0,v1);
  EQ(v0,v2);

  v2[0] = 2;
  v1[0] = 1;

  // when assignmnet to an vector that has been initialized
  // from another they should be independent
  assert(is_not_equal(v0,v1));
  assert(is_not_equal(v0,v2));
  assert(is_not_equal(v1,v2));

}

void test_indx_operator()
{
  Vector<int> v(3);
  v[2] = 3;
  int x = v[2];
  assert(x == v[2]);
}

void test_init_list()
{
  unsigned int sz = 3;
  Vector<int> v0 = {1,2,3};
  std::vector<int> vref = {1,2,3};
  cout << "vref.size()=" << vref.size() << "\n";
  PRINT (v0);

  EQ_W_STD_V(v0,vref);

  PB(v0,++sz);
  PB(vref,sz);


  EQ_W_STD_V(v0,vref);

  v0.insert(4,5);
  std::vector<int>::iterator cit = vref.begin();
  vref.insert(cit+4,5);
  PRINT(v0);
  EQ_W_STD_V(v0,vref);
  PRINT(vref);
  // EQ_SIZE(v,++sz);

  v0.insert(0,0);
  vref.insert(cit,0);
  EQ_W_STD_V(v0,vref);
  PRINT(v0);
  PRINT(vref);

  for ( int i = 0; i < 43; i++) {
    PB(v0,i) ;
    PB(vref,i);
  }
  PRINT(v0);
  PRINT(vref);
  EQ_W_STD_V(v0,vref);
  PB(v0,50);
  PB(v0,51);
  PRINT(v0);
  EQ_SIZE(v0,51);

  // PRINT(v0);
  EQ_CAP(v0,2*CAPACITY);
  v0.unique_sort(false);

  PRINT(v0);
}

void test_capacity()
{
  Vector<int> vec(CAPACITY);
  PRINT(vec);

  vec.insert(CAPACITY,CAPACITY);
  PRINT(vec);
  EQ_CAP(vec,2*CAPACITY);

  Vector<int> vec2(CAPACITY*3);
  PRINT(vec2);
  PB(vec2,151);
  PRINT(vec2);
  vec2.clear();
  EQ_CAP(vec2,CAPACITY*4);
  PRINT(vec2);
  EQ_SIZE(vec2,0);
}

void test_erase()
{
  Vector<int> v = {1,1,2,3,3,4,4};
  v.erase(0);
  PRINT(v);
  std::vector<int> vref = {1,2,3,3,4,4};
  EQ_W_STD_V(v,vref);

  v.erase(5);
  PRINT(v);
  vref = {1,2,3,3,4};
  EQ_W_STD_V(v,vref);

  vref = {1,2,3,4};
  v.erase(3);
  PRINT(v);
  EQ_W_STD_V(v,vref);

  cout << " === next == " << "\n";
  Vector<int> v1;
  v1 = v;
  PRINT(v1);
  EQ_W_STD_V(v,vref);

  cout << " === insert  == " << "\n";
  v1.insert(0,1);
  PRINT(v1);
  vref = {1,1,2,3,4};
  PRINT(vref);
  EQ_W_STD_V(v1,vref);

  cout << " == inz == " << "\n";
  cout << " Before insert(3,2) \n";
  PRINT(v1);
  v1.insert(3,2);
  cout << " After insert(3,2) \n";
  PRINT(v1);
  vref = {1,1,2,2,3,4};
  EQ_W_STD_V(v1,vref);

  v1.insert(6,4);
  vref = {1,1,2,2,3,4,4};
  EQ_W_STD_V(v1,vref);

  PRINT(v1);

  PRINT(v);

}

void test_sort_asc ()
{

  Vector<int> v = {4,5,3,6,2,4,7,5,2,1};
  PRINT(v);
  v.sort();
  PRINT(v);
}

void test_sort_desc()
{
  Vector<int> v = {4,5,3,6,2,4,7,5,2,1};
  PRINT(v);
  v.sort(false);
  PRINT(v);
}

void test_unique_sort_asc()
{
  Vector<int> v = {4,5,5,5,3,6,2,2,2,4,7,5,2,1};
  PRINT(v);
  v.unique_sort();
  PRINT(v);
}


void test_unique_sort_desc()
{
  Vector<int> v = {4,5,5,5,3,6,2,2,2,4,7,5,2,1};
  PRINT(v);
  v.unique_sort(false);
  PRINT(v);
}

void test_exists()
{
  Vector<int> v = {1,2,3,4,5,6,7,8,9,19};
  assert(v.exists(1));
  assert(v.exists(19));
  assert(!v.exists(0));
  Vector<int> v0(0);
  assert(!v0.exists(9));

}

void test_2_constructor()
{
  Vector<int> v(5,6);
  PRINT(v);
}

void test_erase_again()
{
  Vector<int> v(0);
  bool throwed = false;
  try {
    v.erase(0);
  } catch (std::out_of_range e) {
    std::cout << e.what() << std::endl;
    throwed = true;
  }
  assert(throwed);


  PB(v,1);
  PRINT(v);
  v.erase(0);
  PRINT(v);

  PB(v,1);
  PB(v,2);

  v.erase(0);
  PRINT(v);


}




static struct  {
  void (*test_fun)(void);
  bool run;
  const char *name;
} UTests[] = { { &test_default_constructor, true, "Default Ctor"} ,
               { &test_size_constructor, true , "Constructor size"} ,
               { &test_cpy_constructor, true ,"Constructor Copy" },
               { &test_indx_operator, true  ,"Index operator" },
               { &test_init_list, true   ,"Initalization list" },
               { &test_capacity, true   , "Capacity" },
               { &test_erase, true  , "erase" },
               { &test_sort_asc, true, "Sorting Asc" },
               { &test_sort_desc, true, "Sorting Desc" },
               { &test_unique_sort_asc, true, "Sorting Uniqe Sort Asc" },
               { &test_unique_sort_desc, true, "Sorting Uniqe Sort Desc" },
               { &test_exists, true, "exists" },
               { &test_2_constructor, false, "2 val constr" },
               { &test_erase_again, true, "Erase again!" }
};
static int ntests = sizeof(UTests)/sizeof(UTests[0]);

template<typename T>
bool is_not_equal(const Vector<T>& v0,const Vector<T>& v1)
{  bool n_equal = false;
  for ( size_t i = 0; i < v0.size() ; i++) {
    if ( v0[i] != v1[i] ) {
      n_equal = true;
      break;
    }
  }
  return n_equal;
}



int main (int argc , char **argv)
{

  for ( int i = 0; i < ntests; i++ ) {
    cout << "Test(" << i << ") \"" << UTests[i].name << "\"\n";
    if ( UTests[i].run ) {
      UTests[i].test_fun();
      cout << " => ok \n";
    } else {
      cout << " ignored \n";
    }
  }
  return 0;
}
