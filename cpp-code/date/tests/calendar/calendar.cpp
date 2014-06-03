#include <cxxtest/TestSuite.h>
#include "gregorian.h"
#include "calendar.h"
#include "kattistime.h"

using namespace lab2;
using namespace std;

class CalendarTestSuite : public CxxTest::TestSuite {
  
public:
  void testAdd () 
  {
    time_t t;
    time(&t);
    set_k_time(t);
    Calendar<Gregorian> cal;
    cal.add_event("Basket " , 3);
    std::list<Calendar<Gregorian>::Event> evs = cal.all_events();
    
    for ( auto e : evs ) {


    }
    
  }

};




