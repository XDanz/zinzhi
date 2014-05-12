// projektlokala headerfiler
#include "kattistime.h"
#include "julian.h"
#include "gregorian.h"

// STL headerfiler
#include <iostream>
#include <assert.h>             // assert(b) ger felmeddelande om b falsk
#include <ctime>


// Obs att testerna f?ruts?tter att dina klasser returnerar
// m?nader och dagar indexerade fr?n ETT.


int main()
{
    using namespace lab2;        // Exponera funktionalitet i namnrymden lab2

    ////////////////////////////////////////////////////////////
    // S?tt tiden. OBS skicka inte nedanst?ende kod till kattis
    time_t mytime;
    time(&mytime);
    set_k_time(mytime);
    ////////////////////////////////////////////////////////////


    Julian tj;                  // ok: defaultkonstruktor gives dagens datum
    Gregorian gtoday;           // ok  gregorian to..
    std::cout << "Today is " << gtoday << std::endl;
    assert(tj - gtoday == 0);
    Gregorian tg(2006, 10, 31); // ok: set the date explicit
                                // the following should work to
                                // Gregorian(2000, Gregorian::October, 31)
    Date &j = tj;               // access of func through  Dates interface
    Date &g = tg;
    Date &today = gtoday;


    time_t tp;
    time(&tp);
    struct tm *t = gmtime(&tp);
    int year  = t->tm_year + 1900;
    int month = t->tm_mon + 1;      // m?naderna och dagarna
    int day   = t->tm_mday;         // indexed from one

    std::cout << "Testing constructors..." << std::endl;
    assert(today.year() == year &&          // r?tt initierad
           today.month() == month &&
           today.day() == day);
    
    assert(g.year() == 2006 &&              // r?tt initierad
           g.month() == 10 &&
           g.day() == 31);                  // obs! ettindexerade

    std::cout << "Testing access..." << std::endl;
    assert(g.days_per_week() == 7);         // r?tt antal dagar per vecka
    assert(j.days_per_week() == 7);         // r?tt antal dagar per vecka
    assert(g.days_this_month() == 31);      // r?tt antal dagar denna m?nad
    assert(g.months_per_year() == 12);      // r?tt antal m?nader per ?r
    assert(j.months_per_year() == 12);      // r?tt antal m?nader per ?r
    assert(g.week_day() == 2); // r?tt veckodag

    std::cout << "Testing manipulation..." << std::endl;
    ++g;                                    // prefix ?kning
    assert(g.week_day() == 3); // r?tt veckodag
    --g;                                    // prefix minskning
    assert(g.week_day() == 2); // r?tt veckodag
    g += 2;                                 // l?gg till tv? dagar
    assert(g.week_day() == 4); // r?tt veckodag
    g -= 3;                     // dra bort tre dagar
    g.add_month();              // l?gg till en m?nad
    g.add_month(-1);            // dra bort en m?nad
    g.add_year(10);             // vad ?r det f?r veckodag p? din f?delsedag om tio ?r?

    std::cout << "Testing miscellaneous functions..." << std::endl;
    Julian jj(tj);              // kopieringskonstruktor
    const Gregorian gg;
    gg.year();                  // gg konstant, l?sa g?r bra
    g = gg;                     // tilldelning
    if(g == gg ||               // j?mf?relse
       g != gg ||               // j?mf?relse
       g < gg ||                // j?mf?relse
       g >= gg)                 // j?mf?relse
      {}

    std::cout << "Testing boundary violations";
    Gregorian temp(1900, 1, 1);
    Date &d = temp;

    // loopa ?ver dagar och kolla att inga gr?nser ?ver/underskrids
    for(int i = 0; i < 100000; ++i, ++d)
      {
        if(!(i % 5000))        // utskrift p? framsteg
          {
            std::cout << ".";
            flush(std::cout);
          }
        int m[] = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if(d.year() >= 1900 &&
           d.month() >= 1 && d.month() <= 12 &&
           d.day() >= 1 && d.day() <= m[d.month() - 1])
          {}
        else
          {
            std::cout << std::endl;
            std::cout << "boundary checks failed: " << std::endl;
            std::cout << d.year() << ", "
                      << d.month() << ", "
                      << d.day() << std::endl;
            return 1;
          }
      }
    std::cout << std::endl;

    std::cout << "Testing leap years..." << std::endl;
    // testa om skott?ren f?r sekelskiften fungerar som de ska
    for(int y = 1958; y < 2500; y += 100)
      {
        Gregorian temp(y, 2, 28);
        Date &e = temp;
        ++e;                                   // l?gg till en dag
        if((y % 400 == 0 && e.day() == 29) ||  // skottdag (obs! ETTindexerad)
           (y % 400 != 0 && e.day() == 1))     // ej skottdag
          {}
        else
          {
            std::cout << std::endl << "leap day test failed: " << std::endl;
            std::cout << e.year() << ", "
                      << e.month() << ", "
                      << e.day() << std::endl;
            return 1;
          }
      }

    std::cout << std::endl << "All tests were successful." << std::endl;

    // f?ljande ska inte g? att kompilera
#if 0
    gg.leap_year();      // error: kan inte komma protected/private medlem
    gg = g;              // fel: kan inte tilldela konstant
    ++gg;                 // fel: kan inte ?ndra p? en konstant
#endif

    return 0;
}
