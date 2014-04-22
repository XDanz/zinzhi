#ifndef LAB2_DATE_H_
#define LAB2_DATE_H_
#include <iostream>
#include <string>

// Base class for generic calendar Date. It is intended
// to be derived by specialized Dates in a specific calendar system
// To be able to compare and performe aritmethics to
// different Dates we use Modified Julian Day as the base.
// which is a modified version of Julian date obtained by
// subtracting 2,400,000.5 days from the Julian date JD,
//  MJD = JD - 2400000.5
// The MJD therefore gives the number of days since midnight on
// November 17, 1858. This date corresponds to 2400000.5 days after day
// 0 of the Julian calendar.
// The member function mod_julian_day() must be
// implemented for all calendar systems derivied from this Date class.
namespace lab2 {
  class Date {
  public:
    explicit Date();
    // return the current year that this date represents
    // 1.)
    virtual const int year() const = 0;

    // return integer in range 1-12
    // 2.)
    virtual const int month() const = 0;

    // return the current day that this date represents
    // 3.)
    virtual const int day() const = 0;

    // return integer in range 1-7
    // (Mon=1,Tus=2,Wed=3,Thu=4,Fri=5,Sat=6, Sun=7)
    // 4.)
    virtual const int week_day() const = 0;

    // returns number of days per week
    // 5.)
    virtual const int days_per_week() const = 0;

    // return number of days in the current month
    // 6.)
    virtual const int days_this_month() const = 0;

    // return number of months in the current year
    // 7.)
    virtual const int months_per_year() const = 0;

    // return a string representation of the current week day.
    // 8.)
    virtual const std::string week_day_name() const = 0;

    // return a string representation of the current month.
    // 9.)
    virtual const std::string month_name() const = 0;

    // Add number of specified years to the current date
    // returns a new date representing the current date + adding
    // the specified years to the current date.
    // 10.)
    virtual Date& add_year(const int) = 0;

    // Same as above but adds only 1 year
    // 11.)
    virtual Date& add_year() = 0;

    // Add number of specified months to the current date
    // returns a new date representing the current date + adding
    // the specified months to the current date.
    // 12.)
    virtual Date& add_month(const int) = 0;

    // Same as above but adds only 1 month
    // 13.)
    virtual Date& add_month() = 0;

    // return number of days since since 1858-11-17
    // each calander system must defined this member function
    // that return number of days since 00:00 UTC Nov 17,1858 in
    // Gregorian Calendar system.
    // MDJ (Modified Julian day)
    // 14.)
    virtual const int mod_julian_day() const =0;


    // 15.)
    virtual void toString(std::ostream&) const = 0;

    // prefix operator increrment the date by one
    // returns the new Date representing the current Date (before incr)
    // adding one day.
    Date& operator++();

    // prefix operator decrerment the date by one
    // returns the new Date representing the current Date (before incr)
    // subtract one day.
    Date& operator--();

    Date& operator+=(const int);

    Date& operator-=(const int);
    
    // assignment operator
    Date& operator=(const Date&);

    // -- comparasion operators.
    // check to dates for equallity
    // return true weather the current date is equal the specified
    // date false otherwise.
    bool operator==(const Date&) const;

    // check two dates for inequallity
    // return true weather the current date is un-equal the specified
    // date false otherwise.
    bool operator!=(const Date&) const;

    // Compare two dates returns true if this date is less the
    // argument date
    bool operator<(const Date&) const;

    // Compare two dates returns true if this date is less the
    // or equal the argument date
    bool operator<=(const Date&) const;

    // Compare two dates returns true if this date is greater the
    // argument date
    bool operator>(const Date&) const;

    // Compare two dates returns true if this date is greater or equal the
    // argument date
    bool operator>=(const Date&) const;

    // substract the current date with the specified date.
    // return number of days between the current date and the specified
    // date.
    int operator-(const Date&) const;

    friend std::ostream& operator<<(std::ostream& os,const Date& d) {
      d.toString(os);
      return os;
    }
  protected:
    // Adjust the current date to the specified Modified Julian Date
    // number
    virtual void adjust_date_from_mjd(int mjd) =0;
  };
}

#endif
