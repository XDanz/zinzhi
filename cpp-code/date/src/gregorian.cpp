#include "gregorian.h"

using namespace lab2;

Gregorian::Gregorian()
{ 
  time_t temp = time(NULL);
  struct tm* tmtemp = gmtime(&temp);
  curr_year = 1900+tmtemp->tm_year;
  curr_month = tmtemp->tm_mon+1;
  curr_day = tmtemp->tm_mday;
}

Gregorian::Gregorian(int year,int month,int day)
{  is_valid_date(year, month, day);
  curr_year = year;
  curr_month = month;
  curr_day = day;
}

Gregorian::Gregorian(const Date* d)
{ this->adjust_date_from_mjd(d->mod_julian_day()); }


Gregorian::Gregorian(const Date& d)
{ this->adjust_date_from_mjd(d.mod_julian_day()); }

const bool Gregorian::is_valid_date(int year,int month,int day)
{
  //TODO: alert when we're out of year range
  if (month < 1 || month > 12) {
    throw std::out_of_range("Month out of range");
  }
  if (day < 1 || day > days_of_month(month,year)) {
    throw std::out_of_range("Day out of range");
  }
  if (year < 1858 || year > 2558) {
    throw std::out_of_range("Year out of range");
  }
  return true;
}
    

lab2::Gregorian& Gregorian::operator=(const Gregorian& d) {
  return *this;
}


const int Gregorian::year() const
{   return curr_year; }

const int Gregorian::month() const
{   return curr_month; }

const int Gregorian::day() const
{   return curr_day; }

// Algorithm taken from http://5dspace-time.org/Calendar/Algorithm.html
const int Gregorian::week_day() const
{
  int monthTableOffset[13] =
  /* start counting from 1 */
    {-1, 0,3,3,6,1,4,6,2,5,0,3,5};

  int century = (int)curr_year/100;
  int centuryOffset =  ((39-century) % 4 ) * 2;

  int centuryYear = curr_year % 100;

  // calculate year offset
  int yearOffset = (centuryYear + ((int)centuryYear/4)) % 7;

  if (leap_year(curr_year) && (curr_month < 3)) { --yearOffset; }

  int dayOffset = curr_day % 7;

  int weekday = (centuryOffset + yearOffset +
                 monthTableOffset[curr_month] + dayOffset) % 7;

  while (weekday < 1) { weekday+=7; }

  return weekday;
}

const int Gregorian::days_per_week() const
{   return 7; }

const int Gregorian::days_this_month() const
{   return days_of_month(curr_month, curr_year); }

const int Gregorian::months_per_year() const
{  return 12; }

const std::string Gregorian::week_day_name() const
{
  switch (week_day()) {
  case 1:
    return "monday";
  case 2:
    return "tuesday";
  case 3:
    return "wednesday";
  case 4:
    return "thursday";
  case 5:
    return "friday";
  case 6:
    return "saturday";
  case 7:
    return "sunday";
  }
  return NULL;
}


const std::string Gregorian::month_name() const
{  switch(curr_month) {
  case 1:
    return "january";
  case 2:
    return "february";
  case 3:
    return "march";
  case 4:
    return "april";
  case 5:
    return "may";
  case 6:
    return "june";
  case 7:
    return "july";
  case 8:
    return "august";
  case 9:
    return "september";
  case 10:
    return "october";
  case 11:
    return "november";
  case 12:
    return "december";
  }
  return NULL;
}

const int Gregorian::days_of_month(int month, int year) const
{  switch (month) {
  case 1:        //Januari
    return 31;
  case 2:        //Februari
    return (leap_year(year) ? 29 : 28);
  case 3:        //Mars
    return 31;
  case 4:        //April
    return 30;
  case 5:        //Maj
    return 31;
  case 6:        //Juni
    return 30;
  case 7:        //Juli
    return 31;
  case 8:        //Augusti
    return 31;
  case 9:        //September
    return 30;
  case 10:       //Oktober
    return 31;
  case 11:    //November
    return 30;
  case 12:    //December
    return 31;
  default:
    return 0;
  }
}

Date& Gregorian::add_year(const int n)
{  curr_year += n;
  if (curr_month == 2 && curr_day == 29 && !leap_year(curr_year)) {
    curr_day = 28;
  }
  return *this;
}

Date& Gregorian::add_month(const int n)
{
  if (n < 0) {
    for (int i = n; i < 0; ++i) {
      sub_month();
    }
  } else {
    for (int i = 0; i < n; ++i) {
      add_month();
    }
  }
  return *this;
}

Date& Gregorian::add_year()
{  this->add_year(1);
  return *this;
}

Date& Gregorian::add_month()
{ int old_month = curr_month, old_year = curr_year;

  if (++curr_month > 12 ) {
    curr_month = 1;
    curr_year++;
  }
  if (curr_day > days_this_month()) {
    curr_year = old_year;
    curr_month = old_month;
    (*this)+=30;
  }
  return *this;
}

Date& Gregorian::sub_month()
{
  int old_month = curr_month, old_year = curr_year;
  if (--curr_month < 1 ) {
    curr_month = 12;
    --curr_year;
  }
  if (curr_day > days_this_month()) {
    curr_year = old_year;
    curr_month = old_month;
    (*this)-=30;
  }
  return *this;
}


void Gregorian::set_mod_julian_day(int mjd) {
  adjust_date_from_mjd(mjd);
}

// perform the nececessary calculation to adjust
// curr_year, curr_year , curr_day from
// modified julian day number.
void Gregorian::adjust_date_from_mjd(int mjd)
// jd_to_calendar
{  // double MJDC  = 2400000.5;
  // Integer part of Q
  double Z;
  double Q = mjd + 2400000.5 + 0.5;

  const double dayFrac = modf(Q, &Z);

  // alpha = W
  int W = int((Z - 1867216.25) / 36524.25);
  double X = W/4;

  int A = int(Z + 1 + W - X);
  const int B = A + 1524;
  const int C = int((B - 122.1) / 365.25);

  const int D = int(365.25 * C);
  const int E = int((B - D) / 30.6001);


  curr_day = B - D - int(30.6001 * E) + dayFrac;

  if (E < 14)
    curr_month = E - 1;
  else
    curr_month = E - 13;
  if (curr_month > 2)
    curr_year = C - 4716;
  else
    curr_year = C - 4715;
  
}

// The (inverse) of the above calculate Modified Julian Day
// from current year,mont,day
const int Gregorian::mod_julian_day() const
{
  // gregorian calendar to mjd
  int y = curr_year, m = curr_month, d = curr_day;

  y += 8000;
  if ( m < 3 ) { y--; m+=12; }
  int jd =  (y*365) +(y/4) -(y/100) +(y/400) -1200820
    +(m*153+3)/5-92
    +d-1;

  return jd-2400000.5;
}

void Gregorian::toString(std::ostream& os) const {
  os  << "Greg@" << this << " (" << this->curr_year << "-";
  
  if (this->curr_month <= 9) {
    os << "0";
  }
  os << curr_month << "-";
  if (curr_day <= 9) {
    os << "0";
  }
  os << curr_day << " " << week_day_name() << "," << mod_julian_day() << ")";
}

bool Gregorian::leap_year(int year) const
{   return ((year&3) == 0 && (year%100 != 0 || year % 400 == 0)); }
