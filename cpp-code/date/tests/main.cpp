// projektlokala headerfiler
#define private public
// #include "kattistime.h"    
#include "julian.h"        
#include "gregorian.h"      
#include "calendar.h"

// STL headerfiler
#include <iostream>
#include <assert.h>             // assert(b) ger felmeddelande om b falsk
#include <ctime>
#include <fstream>


// Obs att testerna förutsätter att dina klasser returnerar
// månader och dagar indexerade från ETT. Testerna är inte
// fullständiga
#include <stdio.h>

int main()
{
    using namespace lab2;        // Exponera funktionalitet i namnrymden lab2
       
       
    Calendar<Gregorian> gcal;
    ////////////////////////////////////////////////////////////
    // Sätt tiden. OBS skicka inte nedanstående kod till kattis
    //time_t mytime;
    //time(&mytime);
    //set_time(mytime);
    ////////////////////////////////////////////////////////////
       
   
        // std::cout << "----------------------------------------" << std::endl;
        // Calendar<Gregorian> cal;
        // cal.set_date(2000, 12, 2);
        // cal.add_event("Basketträning", 4, 12, 2000);
        // cal.add_event("Basketträning", 11, 12, 2000);
        // cal.add_event("Nyårsfrukost", 1, 1, 2001);
        // cal.add_event("Första advent", 1); // år = 2000, månad = 12
        // cal.add_event("Vårdagjämning", 20, 3); // år = 2000
        // cal.add_event("Julafton", 24, 12);
        // cal.add_event("Kalle Anka hälsar god jul", 24); // också på julafton
        // cal.add_event("Julafton", 24);
        // // En likadan händelse samma datum ska
        // // ignoreras och inte sättas in i kalendern
        // cal.add_event("Min första cykel", 20, 12, 2000);
        // cal.remove_event("Basketträning", 4);
        // std::cout << cal;
        // // OBS! Vårdagjämning och första advent är
        // // före nuvarande datum och skrivs inte ut
        // std::cout << "----------------------------------------" << std::endl;
        // cal.remove_event("Vårdagjämning", 20, 3, 2000);
        // cal.remove_event("Kalle Anka hälsar god jul", 24, 12, 2000);
        // cal.set_date(2000, 11, 2);
        // if (! cal.remove_event("Julafton", 24)) {
        //   std::cout << " cal.remove_event(\"Julafton\", 24) tar inte" << std::endl
        //             << " bort något eftersom aktuell månad är november" << std::endl;
        // }
        // cal.set_date(2000, 12, 2);
        // std::cout << "----------------------------------------" << std::endl;
        // Calendar<Julian> jcal;
        // jcal = cal;
        // std::cout << jcal;
        // std::cout << "----------------------------------------" << std::endl;
        // Calendar<Gregorian> scal(jcal);
        // std::cout << scal;
       
        // std::ofstream myfile;
        // myfile.open("cal.ics");
        // myfile << scal;
        // myfile.close();
       
       
        return 0;
}
