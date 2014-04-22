#include <iostream>
#include "vector.h"
#include <vector>
#include <cassert>

using std::cout;
#define SZ(V) (V).size()
#define SZ_PTR(V) (V)->size()

#define EQ_SIZE(V,N) assert(SZ(V) == (N))
#define EQ_SIZE_PTR(V,N) assert(SZ_PTR(V) == (N))

#define EQ_V_SIZE(V0,V1) assert(SZ(V0) == SZ(V1))

#define PB(V,A) (V).push_back((A))

#define PRINT(V) do {                                   \
    cout << "(sz:" << SZ(V) << " [";                    \
    for ( size_t i = 0 ; i< SZ(V) ; i++ )               \
      cout << (V)[i] << ((i < SZ(V)-1)? "," : "");      \
    cout << "]) \n";                                    \
  } while (0)

#define EQ(v0,v1) do {                          \
    EQ_V_SIZE(v0,v1);                           \
    for ( size_t i = 0 ; i < SZ(v0); i++ )      \
      assert((v0)[i] == (v1)[i]);               \
  } while (0)

#define EQ_W_STD_V(V0,V1) do {                  \
    EQ_V_SIZE(V0,V1);                           \
    for ( size_t i = 0 ; i < SZ(V0); i++ )      \
      assert((V0)[i] == (V1)[i]);               \
  } while (0)


bool is_not_equal(const Vector& v0,const Vector& v1);

void test_default_constructor()
{

  {
    // size() should be equal to 0
    Vector v0;
    EQ_SIZE(v0,0);
    

    // out_of_range check --start
    bool throwed = false;
    try {
      v0[1] = 10;
    } catch(std::out_of_range e) {
      std::cout << e.what() << std::endl;
      throwed = true;
    }
    assert(throwed);
    // out_of_range check --end

    // v0 = {6,7,8,9,10};
    PRINT(v0);
  }  // Block end--

  Vector *v0 = new Vector();
  EQ_SIZE_PTR(v0,0);

  delete v0;

}

void test_size_constructor()
{
  // size() should be equal to 10
  Vector v0(10);
  EQ_SIZE(v0,10);
  PRINT(v0);


  std::vector<unsigned> vref(10,0);
  PRINT(vref);
  EQ_W_STD_V(v0,vref);
}

void test_cpy_constructor()
{
  // Copy constr.
  Vector v0(10);    PRINT(v0);
  Vector v1 = v0;   PRINT(v1);

  Vector v2(v1);    PRINT(v2);

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

void test_mv_constructor() 
{

  {
    Vector v0(10);
    for (size_t i=0; i < v0.size(); i++) v0[i] = i;

    Vector v1 = std::move(v0);

    std::vector<unsigned> vref = {0,1,2,3,4,5,6,7,8,9};
    PRINT(v1);
    EQ_W_STD_V(v1,vref);
  }


}

// Test The Assignment move
void test_mv_assignment() 
{
  cout << " Test Mv \n";
  {
    Vector v0 (10);
    Vector v1;
    for (size_t i = 0; i < v0.size(); i++) v0[i] = i;
    cout <<  "v0 ="; PRINT(v0);

    std::vector<unsigned> vref = {0,1,2,3,4,5,6,7,8,9};
    v1 = std::move(v0);
    cout << "v1 =";    PRINT(v1);
    EQ_W_STD_V(v1,vref);
  }

  {
    Vector v0(0);
    Vector v1;
    for (size_t i = 0; i < v0.size(); i++) v0[i] = i;
    v1 = std::move(v0);
    PRINT(v1);
  }


}

void test_cpy_assignment(void) 
{
  Vector v0(10); 
  Vector v1(5);

  for(size_t i = 0; i < v0.size(); i++) v0[i] = i;
  cout <<  "v0 ="; PRINT(v0);
  
  v1 = v0;

  std::vector<unsigned> vref = {0,1,2,3,4,5,6,7,8,9};
  cout << "ref="; PRINT(v1);
  EQ_W_STD_V(v1,vref);

  // The assignment of v0[0] should not affect v1[0] 
  v0[0] = 6;
  EQ_W_STD_V(v1,vref);

}


void test_init_list()
{
  Vector v0;
  v0 = {1,2,3}; 
  cout << "v0=";
  PRINT(v0);
  std::vector<unsigned> vref = {1,2,3};
  EQ_W_STD_V(v0,vref);
  
  v0 = {1,2,3,4}; 
  cout << "v0=";
  PRINT(v0);

  vref = {1,2,3,4}; PRINT(vref);
  EQ_W_STD_V(v0,vref);

  // odd case
  v0 = {};
  vref = {};
  EQ_W_STD_V(v0,vref);

}

void test_indx_operator()
{
  Vector v(3);
  v[2] = 3;
  unsigned x = v[2];
  assert(x == v[2]);
}


static struct  {
  void (*test_fun)(void);
  bool run;
  const char *name;
} UTests[] = { { &test_default_constructor, false, "---Default Ctor---"} ,
               { &test_size_constructor, false , "---Constructor Size---"},
               { &test_cpy_constructor, false ,"----Constructor Copy---" },
               { &test_mv_constructor, true , "---Constructor Move---"},
               { &test_mv_assignment, true , "---Move Assignment ---"},
               { &test_cpy_assignment, true, "---Cpy Assignment---"},
               { &test_init_list, true   ,"---Initalization list---" },
               { &test_indx_operator, false  ,"---Index operator---" }

};
static int ntests = sizeof(UTests)/sizeof(UTests[0]);

bool is_not_equal(const Vector& v0,const Vector& v1)
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
      cout << "Test(" << i << ") \"" << UTests[i].name << "\" ok \n\n";
    } else {
      cout << " ignored \n";
    }
  }
  return 0;
}
