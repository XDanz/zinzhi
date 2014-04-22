#ifndef JULIAN_H_
#define JULIAN_H_

#include "gregorian.h"

namespace lab2 {
    class Julian : public Gregorian {
      public:
        Julian();
        Julian(int,int,int);
        Julian(const Date*);
        Julian(const Date&);
        Julian(const Gregorian*);
        Julian(const Gregorian&);
        const int mod_julian_day() const;
        virtual Date& add_year();
        virtual Date& add_year(const int);
        virtual Date& add_month(const int);
        virtual const int months_per_year() const;
        virtual const std::string week_day_name() const;
        virtual const std::string month_name() const;
        const int week_day() const;
        const int days_per_week() const;
        const int days_this_month() const;
        virtual Date& add_month();
        void toString(std::ostream&) const;
        Julian& operator=(const Date&) ;

      protected:
        // (inherit from base 16.) )
        virtual void adjust_date_from_mjd(int mjd);

        virtual Date& sub_month();
        // return true if the year is a leap year
        virtual bool leap_year(int year) const;

        const bool is_valid_date ( int , int , int );

        virtual const int days_of_month(int month, int year) const;
        /* const void set_ymd_from_base(int base); */
    };
}
#endif
