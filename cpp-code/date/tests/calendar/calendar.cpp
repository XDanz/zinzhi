#include <cxxtest/TestSuite.h>
#include <set>
#include "gregorian.h"
#include "calendar.h"
#include "kattistime.h"

using namespace lab2;
using namespace std;

class CalendarTestSuite : public CxxTest::TestSuite {
  
public:
    typedef Calendar<Gregorian>::Event GEvent;
    typedef Calendar<Gregorian> GCalendar;
    void testAdd () {
        
        time_t t;
        time(&t);
        set_k_time(t);
        string event_str = "Basket";
        int day = 3;

        std::set<GEvent> mySet;
        GCalendar cal;

        Gregorian g;
        Gregorian the_third(g.year(), g.month(), day);

        GEvent expected {the_third,event_str};
        mySet.insert(expected);
        TS_ASSERT_EQUALS(1,mySet.size());
        
        cal.add_event(event_str , day);
        std::list<GEvent> evs = cal.all_events();
        
        for (auto e : evs) {
            mySet.erase(e);
            TS_ASSERT_EQUALS(expected, e);
        }
        TS_ASSERT_EQUALS(0, mySet.size());
    }
    
    void test2() {
        TS_TRACE(" Start test 2 ");
        TS_ASSERT ( 1+1 > 1);
    }

};




