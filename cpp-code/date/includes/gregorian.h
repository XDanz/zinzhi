#ifndef GREGORIAN_H_
#define GREGORIAN_H_

#include "date.h"
/* #include "kattistime.h" */
#include <stdexcept>
#include <string>
#include <cmath>

// Derivide class for Gregorian calender dates.
namespace lab2 {
  class Gregorian : public Date {
  public:
    explicit Gregorian();
    // Creates a gregorian date based on parameters year,month, day
    Gregorian(int,int,int);
    // Creates a gregorian date based on generic Date
    Gregorian(const Date*);
    // Creates a gregorian date based on generic Date
    Gregorian(const Date&);

    template <class D>
    Gregorian(const D* d) {
      this->adjust_date_from_mjd(d->mod_julian_day());
    }

    template <class D>
    Gregorian(const D& d) {
      this->adjust_date_from_mjd(d.mod_julian_day());
    }
    // (inherit from base 1.) )
    // return the current year that this gregorian date represents
    virtual const int year() const;

    // (inherit from base 2.) )
    // return integer in range 1-12 of the month that this gregorian date
    // represents.
    virtual const int month() const;

    // (inherit from base 3.) )
    // return the current gregorian calender day that this date represents
    virtual const int day() const;

    // (inherit from base 4.) )
    // return integer in range 1-7
    // (Mon=1,Tus=2,Wed=3,Thu=4,Fri=5,Sat=6, Sun=7)
    virtual const int week_day() const;

    // (inherit from base 5.) )
    // returns number of days per week
    virtual const int days_per_week() const;

    // (inherit from base 6.) )
    // return number of days in the current month
    virtual const int days_this_month() const;

    // (inherit from base 7.) )
    // return number of months in the current year
    virtual const int months_per_year() const;

    // (inherit from base 8.) )
    // return a string representation of the current week day.
    virtual const std::string week_day_name() const;

    // (inherit from base 9.) )
    // return a string representation of the current month.
    virtual const std::string month_name() const;

    // (inherit from base 10.) )
    // Add number of specified years to the current date
    // returns a new date representing the current date + adding
    // the specified years to the current date.
    virtual Date& add_year(const int);

    // (inherit from base 11.) )
    // Same as above but adds only 1 year
    virtual Date& add_year();

    // (inherit from base 12.) )
    // Add number of specified months to the current gregorian date
    // returns a new date representing the current date + adding
    // the specified months to the current date.
    virtual Date& add_month(const int);

    // (inherit from base 13.) )
    // Same as above but adds only 1 month
    virtual Date& add_month();

    // (inherit from base 14.) )
    const int mod_julian_day() const;

    // (inherit from base 15.) )
    virtual void toString(std::ostream&) const;

    // test function
    void set_mod_julian_day(int mjd);
    
    /* virtual Date& operator=(const Date&); */
    Gregorian& operator=(const Gregorian&);
    
      protected:
    // (inherit from base 16.) )
    virtual void adjust_date_from_mjd(int base);

    const bool is_valid_date(int, int,int);

    // return number of days in the month and year
    virtual const int days_of_month(int month, int year) const;

    // return true if the year is a leap year
    virtual bool leap_year(int year) const;

    virtual Date& sub_month();

    std::string months[13];

    int curr_day;
    int curr_month;
    int curr_year;

  };
}

#endif
