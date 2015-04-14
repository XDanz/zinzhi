#include <iostream>
#include "date.h"
#include "gregorian.h"
#include "calendar.h"
#include "kattistime.h"

using namespace std;
using namespace lab2;

int 
main ( int argc, char ** argv ) 
{
    set_k_time(NULL);

    Calendar<Gregorian> cal;
    cal.set_date(2000, 12, 2);
    std::cout << "curr_date: " << cal.get_date() << std::endl;
    cal.add_event("Basketträning", 4, 12, 2000);
    cal.add_event("Nyårsfrukost", 1, 1, 2001);
    cal.add_event("Första advent", 1);          // year = 2000, month = 12
    cal.add_event("Vårdagjämning", 20, 3);      // year  = 2000
    cal.add_event("Julafton", 24, 12);
    cal.add_event("Kalle Anka hälsar god jul", 24); // also on christmas
    cal.add_event("Julafton", 24); // should be ignored
    cal.add_event("Min första cykel", 20, 12, 2000);
    cal.remove_event("Basketträning", 4);
    // std::cout << cal; // OBS! Vårdagjämning och första advent är
    //                   // före nuvarande datum och skrivs inte ut
    // std::cout << "----------------------------------------" << std::endl;
    // cal.remove_event("Vårdagsjämning", 20, 3, 2000);
    // cal.remove_event("Kalle Anka hälsar god jul", 24, 12, 2000);
    // cal.set_date(2000, 11, 2);
    // if (! cal.remove_event("Julafton", 24)) {
    //   std::cout << " cal.remove_event(\"Julafton\", 24) tar inte"<< std::endl
    //             << " bort något eftersom aktuell månad är november" << std::endl;
    // }
    // std::cout << "----------------------------------------" << std::endl;
    std::cout << cal;

    list<Calendar<Gregorian>::Event> ll = cal.date_events();
    for ( auto a : ll )
        cout << "Event:" << a << endl;
}

