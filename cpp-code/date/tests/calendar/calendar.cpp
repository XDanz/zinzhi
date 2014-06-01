#include <cxxtest/TestSuite.h>
#include "gregorian.h"
#include "calendar.h"

using namespace lab2;

class CalendarTestSuite : public CxxTest::TestSuite {
  
public:
  void testAdd () 
  {
    Calendar<Gregorian> cal;
    std::list<Calendar<Gregorian>::Event> evs = cal.all_events();
    
    for ( auto e : evs ) {

    }
    
  }

};




