/*
 *  calendar.h
 *  Calendar
 *
 *  Created by Fredrik Gustafsson on 11/1/10.
 *  Copyright 2010 Kungliga Tekniska Högskolan. All rights reserved.
 *
 */

#ifndef LAB2_CALENDAR_H_
#define LAB2_CALENDAR_H_

#include "date.h"
#include "gregorian.h"
#include <list>
#include <iostream>
#include <limits>
#include <stdio.h>

#define OUR_UNDEF INT_MIN+25

namespace lab2 {

  template <class T> struct Event {
    Event(const T& edate, std::string edesc) : date(edate), desc(edesc) {}
    T date;
    std::string desc;
    friend std::ostream& operator<<(std::ostream& os, const Event& ev) {
      os << ev.date << " : " << ev.desc << std::endl;
      return os;
    }
  };

  template <class T> class Calendar {
  public:
    Calendar() : now(T()), events(std::list<Event<T> > ()),
                 current_format(iCalendar) {}

    template <class S>
    Calendar<T> operator=(const Calendar<S>& c) {
      if ((void*)this == (void*)&c) {
        return *this;
      }
      this->events.clear();
      this->now = T(c.now);
      this->current_format = (typename Calendar<T>::format)c.current_format;
      typename std::list<Event<S> >::const_iterator it;
      for (it = c.events.begin(); it != c.events.end(); it++) {
        this->events.push_back(Event<T>(T(((Event<S>)*it).date),
                                        ((Event<S>)*it).desc));
      }
      return *this;
    }

    template <class S>
    Calendar<T>(const Calendar<S>& c) {
      this->now = T(c.now);
      this->current_format = (typename Calendar<T>::format)c.current_format;
      typename std::list<Event<S> >::const_iterator it;
      for (it = c.events.begin(); it != c.events.end(); it++) {
        this->events.push_back(Event<T>(T(((Event<S>)*it).date),
                                        ((Event<S>)*it).desc));
      }
    }

    bool set_date(int y,int m,int d) {
      T temp;
      try {
        temp = T(y,m,d);
      }
      catch (...) {
        return false;
      }
      now = temp;
      return true;
    }

    bool add_event(std::string desc) {
      return add_event(desc, now.day(), now.month(), now.year());
    }

    bool add_event(std::string desc, int d) {
      return add_event(desc, d, now.month(), now.year());
    }

    bool add_event(std::string desc, int d,int m) {
      return add_event(desc, d, m, now.year());
    }

    bool add_event(std::string desc, int d,int m,int y) {
      T temp;
      try {
        temp = T(y,m,d);
      }
      catch (...) {
        return false;
      }

      typename std::list<Event<T> >::iterator it;
      for (it = events.begin(); it != events.end(); it++) {
        if (((Event<T>)*it).date == temp && ((Event<T>)*it)
            .desc.compare(desc) == 0)
          {
            return false;
          }
        if (((Event<T>)*it).date > temp) {
          events.insert(it, Event<T>(temp, desc));
          return true;
        }
      }
      events.push_back(Event<T>(temp, desc));
      return true;
    }
    bool remove_event(std::string desc) {
      return remove_event(desc, now.day(),now.month(),now.year());
    }
    bool remove_event(std::string desc, int d) {
      return remove_event(desc, d, now.month(), now.year());
    }

    bool remove_event(std::string desc, int d,int m) {
      return remove_event(desc, d, m, now.year());
    }

    bool remove_event(std::string desc, int d,int m, int y) {
      T temp;
      try {
        temp = T(y,m,d);
      }
      catch (...) {
        return false;
      }

      typename std::list<Event<T> >::iterator it;
      for (it = events.begin(); it != events.end(); it++) {
        if (((Event<T>)*it).date == temp && ((Event<T>)*it).desc
            .compare(desc) == 0) {
          events.erase(it);
          return true;
        }
      }
      return false;
    }

    void print_list(const std::ostream& os) const {
      typename std::list<Event<T> >::const_iterator it;
      for (it = events.begin(); it != events.end(); it++) {
        if (((Event<T>)*it).date>now) {
          std::cout << *it;
        }
      }
    }

    void print_cal(const std::ostream& os) const {
      std::cout << "\t\t" << now.month_name() << " " <<
        now.year() << std::endl;
      std::cout << " må   ti   on   to   fr   lö   sö" << std::endl;
      bool first_run = true;
      for (T temp(now.year(),now.month(),1);
           temp.month() == now.month(); ++temp)
        {
          if (first_run) {
            for (int i=1; i<temp.week_day(); ++i) {
              std::cout << "     ";
            }
            first_run = false;
          }
          if (temp.day()<10) {
            std::cout << " ";
          }
          if (now.day() == temp.day()) {
            if (has_event(temp)) {
              std::cout << "<" << temp.day() << ">*";
            } else {
              std::cout << "<" << temp.day() << "> ";
            }
          } else {
            if (has_event(temp)) {
              std::cout << " " << temp.day() << "* ";
            } else {
              std::cout << " " << temp.day() << "  ";
            }
          }

          if (temp.week_day() == 7 && temp.days_this_month() != temp.day()) {
            std::cout << std::endl;
          }
        }
      std::cout << std::endl << std::endl;
      typename std::list<Event<T> >::const_iterator it;
      for (it = events.begin(); it != events.end(); it++) {
        if (((Event<T>)*it).date.month() ==
            now.month() && ((Event<T>)*it).date.year() == now.year())
          {
            std::cout << *it;
          }
      }
    }

    void print_ical(std::ostream& os) const {
      /*
        os << "BEGIN:VCALENDAR" << std::endl
        << "VERSION:2.0" << std::endl
        << "PRODID:-//YO MAMA//" << std::endl;

        typename std::list<Event<T> >::const_iterator it;
        for (it = events.begin(); it != events.end(); it++) {
        const Event<T>& c = *it;
        char *datestr = new char[4+2+2];
        sprintf(datestr, "%d%.2d%.2d", c.date.year(),
        c.date.month(), c.date.day());
        os << "BEGIN:VEVENT" << std::endl
        << "DTSTART:" << datestr << "T080000" << std::endl
        << "DTEND:" << datestr << "T090000" << std::endl
        << "SUMMARY:" << c.desc << std::endl
        << "END:VEVENT" << std::endl;
        free(datestr);
        }
        os << "END:VCALENDAR" << std::endl;
      */
    }

    friend std::ostream& operator<<(std::ostream& os, const Calendar<T>& c) {
      if (c.current_format == list) {
        c.print_list(os);
      } else if (c.current_format == cal) {
        c.print_cal(os);
      } else if (c.current_format == iCalendar) {
        c.print_ical(os);
      }
      return os;
    }

    enum format {
      list,
      cal,
      iCalendar
    };

    format current_format;
    T now;
    std::list<Event<T> > events;
  private:
    bool has_event(T& d) const {
      typename std::list<Event<T> >::const_iterator it;
      for (it = events.begin(); it != events.end(); it++) {
        if (((Event<T>)*it).date == d) {
          return true;
        }
      }
      return false;
    }
  };

}

#endif
