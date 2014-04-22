#include "date.h"

using namespace lab2;

// The Abstract class implementation
Date::Date()
{ }

// The assigment operator adjust the current date to
// the specified date's Modified Julian Day.
// return the date adjusted to the specified date d
// Modified Julan Day number
Date& Date::operator=(const Date& d) {
  this->adjust_date_from_mjd(d.mod_julian_day());
  return *this;
}

// Compare the current date with the specified date d
// for equallity.
//
// comparision is done easy since we use Modified Julian
// Day number
// return true if the Modified Julian Day number is the same
// false otherwise.
bool Date::operator==(const Date& d) const {
  return this->mod_julian_day() == d.mod_julian_day();
}

// Compare the current date with the specified date d
// for un-equallity.
//
// comparision is done easily since we use Modified Julian
// Day number.
// return true if the Modified Julian Day number is unequal
// false otherwise.
bool Date::operator!=(const Date& d) const {
  return this->mod_julian_day() != d.mod_julian_day();
}

// Compare the current date with the specified date d
//
// comparision is done easily since we use Modified Julian
// Day number.
// return true if the Modified Julian Day number is strict less than
// the specified date d's Modified Julian Day number.
bool Date::operator<(const Date& d) const {
  return this->mod_julian_day() < d.mod_julian_day();
}

// Compare the current date with the specified date d
//
// comparision is done easily since we use Modified Julian
// Day number.
// return true if the Modified Julian Day number is less than
// or equal the specified date d's Modified Julian Day number.
bool Date::operator<=(const Date& d) const {
  return this->mod_julian_day() <= d.mod_julian_day();
}

// Compare the current date with the specified date d
//
// comparision is done easily since we use Modified Julian
// Day number.
// return true if the Modified Julian Day number is greater than
// the specified date d's Modified Julian Day number.
bool Date::operator>(const Date& d) const {
  return this->mod_julian_day() > d.mod_julian_day();
}

// Compare the current date with the specified date d
//
// comparision is done easily since we use Modified Julian
// Day number.
// return true if the Modified Julian Day number is greater than
// or equal the specified date d's Modified Julian Day number.
bool Date::operator>=(const Date& d) const {
  return this->mod_julian_day() >= d.mod_julian_day();
}

// (pre) increment the current date with one day
// return the new date incrementet by one day.
Date& Date::operator++() {
  this->adjust_date_from_mjd(this->mod_julian_day()+1);
  return *this;
}


// decrement the current date with one day
// return the new date decrementet by one day.
Date& Date::operator--() {
  this->adjust_date_from_mjd(this->mod_julian_day()-1);
  return *this;
}

// Add n days to this date n could be negative
Date& Date::operator+=(const int n) {
  this->adjust_date_from_mjd(this->mod_julian_day()+n);
  return *this;
}
// Substract n days from this date
Date& Date::operator-=(const int n) {
  this->adjust_date_from_mjd(this->mod_julian_day()-n);
  return *this;
}

// substract the current day with the specified date.
// return number of days between the current date and the specified date.
int Date::operator-(const Date& d) const {
  return (this->mod_julian_day() - d.mod_julian_day());
}

std::ostream& operator<<(std::ostream& os, const Date& d) {
  d.toString(os);
  return os;
}

