#include <cxxtest/TestSuite.h>

#include "must_follow_a.h"

class MyTestSuite : public CxxTest::TestSuite 
{
public:

  // Testcase 1

  // This testcase sets up a 5 sized array (precondition). Note that
  // the second argument (length) to must_follow_a is 4. The
  // expected result is successs.

  // Do make additional tests of your own and try it out. 

  void test_a_is_second_to_last( void )
  {
    char vek[] = {'x', 'x', 'a', 'b', 'x'};
    int result = must_follow_a(vek, 4, 'a', 'b');
    TS_ASSERT_EQUALS( result, 1);
  }

  void test_a_is_last(void )
  { 
    char vek[] = {'x', 'x', 'x', 'a', 'b'};
    int result = must_follow_a(vek, 4, 'a', 'b');
    TS_ASSERT_EQUALS( result, 1);
  }


  // additional test of my own
  void test_a_is_second_and_fourth(void) 
  {
    char vek[] = { 'a','b','x','a','b'};
    int result = must_follow_a(vek,5,'a','b');
    TS_ASSERT_EQUALS( result, 2);
  }

  void test_is_failing (void) 
  {
    char vek[] = { 'a','b','a','b','a','b'};
    int result = must_follow_a(vek,5,'a','b');
    TS_ASSERT_EQUALS( result, 2);
  }

  void test_a_is_third(void)
  {
    char vek[] = {'b','b','a','b','b'};
    int result = must_follow_a(vek,3,'a','b');
    TS_ASSERT_EQUALS( result , 0);

  }

};
