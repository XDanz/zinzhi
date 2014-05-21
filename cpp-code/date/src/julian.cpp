#include "julian.h"

using namespace lab2;

Julian::Julian()
{
  Gregorian g;
  this->adjust_date_from_mjd(g.mod_julian_day());
}

Julian::Julian(int year,int month,int day)
{
  is_valid_date(year, month, day);

  curr_year = year;
  curr_month = month;
  curr_day = day;
}

Julian::Julian(const Date& d)
{  this->adjust_date_from_mjd(d.mod_julian_day()); }

Julian::Julian(const Date* d)
{  this->adjust_date_from_mjd(d->mod_julian_day()); }

const bool Julian::is_valid_date(int year,int month,int day) {
  //TODO: alert when we're out of year range
  if (month < 1 || month > 12) {
    throw std::out_of_range("Month out of range");
  }

  if (day < 1 || day > days_of_month(month, year)) {
    throw std::out_of_range("Day out of range");
  }
  return true;
}

// const int Julian::year() const
// { return curr_year; }

// const int Julian::month() const
// { return curr_month; }

// const int Julian::day() const
// {  return curr_day; }

// Algorithm taken from http://5dspace-time.org/Calendar/Algorithm.html
const int Julian::week_day() const
{
  int monthTableOffset[13] =
  /* start counting from 1 */
  {-1, 0,3,3,6,1,4,6,2,5,0,3,5};
  int century = (int)curr_year / 100;
  int centuryOffset = 7-((century + 3) % 7);

  int centuryYear = curr_year % 100;

  // calculate year offset
  int yearOffset = (centuryYear + ((int)centuryYear/4)) % 7;

  if (leap_year(curr_year) && (curr_month <= 2)) {  --yearOffset;  }

  int dayOffset = curr_day % 7;

  int weekday =
    (centuryOffset + yearOffset +
     monthTableOffset[curr_month] + dayOffset) % 7;

  return (weekday == 0)? 7: weekday;
}


const int Julian::days_per_week() const
{  return 7; }


const int Julian::days_this_month() const
{  return days_of_month(curr_month, curr_year); }

const int Julian::months_per_year() const
{  return 12; }


const std::string Julian::week_day_name() const
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
  case 0:
    return "sunday";
  }
  return NULL;
}


const std::string Julian::month_name() const
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


Date& Julian::add_year(const int n)
{ curr_year += n;
  if (curr_month == 2 && curr_day == 29 && !leap_year(curr_year)) {
    curr_day = 28;
  }
  return *this;
}

Date& Julian::add_month(const int n)
{
  if (n < 0)
    for (int i = 0; i > n; --i) {
      sub_month();
    }
  else
    for (int i = 0; i < n; ++i) {
      add_month();
    }
  return *this;
}

 Date& Julian::add_year()
{  this->add_year(1);
  return *this;
}

Date& Julian::add_month()
{  int old_month = curr_month, old_year = curr_year;

  if (++curr_month > 12) {
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

Date& Julian::sub_month()
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

const int Julian::days_of_month(int month, int year) const
{

  switch (month) {
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
  case 9:       //September
    return 30;
  case 10:      //Oktober
    return 31;
  case 11:      //November
    return 30;
  case 12:      //December
    return 31;
  default:
    return 0;
  }
}

// perform the nececessary calculation to adjust
// curr_year, curr_year , curr_day from
// modified julian day number.
void Julian::adjust_date_from_mjd(int mjd)
{
  double Z;
  double Q = mjd + 2400000.5 + 0.5;
  const double dayFrac = modf(Q, &Z);
  int A = Z;
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

lab2::Julian& Julian::operator=(const Date& d) {
  lab2::Date::operator=(d);
  return *this;
}

const int Julian::mod_julian_day() const
{
  int y = curr_year, m = curr_month, d = curr_day;

  if ( m < 3) { y--; m+=12; }
  y += 4800;
  m-= 3;
  int jd = (y*365) +(y/4) +(30*m) +(m*3+2)/5+d-32083;
  return jd-2400000.5;
}

bool Julian::leap_year(int year) const
{  return ((year % 4) == 0); }


void Julian::toString(std::ostream& os) const {
  os  << this->curr_year << "-";

  if (this->curr_month <= 9) {
    os << "0";
  }
  os << curr_month << "-";
  if (curr_day <= 9) {
    os << "0";
  }
  os << curr_day;
}





