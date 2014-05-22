#ifndef CALENDAR_H_
#define CALENDAR_H_


#include "date.h"
#include "gregorian.h"
#include <list>

namespace lab2 {
    template<typename C>
    class Calendar
        {
          public:
          Calendar() : events ( std::list<Calendar::Event>{} ) {
          }
            
            friend std::ostream& operator<<(std::ostream& ostream,
                                            const Calendar<C>& d ) {
                typename std::list<Calendar<C>::Event>::const_iterator it =
                    d.events.begin();
                for ( ; it != d.events.end() ; it++ ) {
                    Calendar<C>::Event event = *it;
                    // if (event.event_date() > d.current_date ) {
                    ostream << event << std::endl;
                    // }
                }
                return ostream;
            }


          bool set_date (int year, int month , int day ) {
            std::cout << "set date  " << std::endl;
            C date;
              try {
                date = C(year,month,day);
                std::cout << "date  " << date << std::endl;
              } catch (std::out_of_range ignore ) {
                return false;
              }
              current_date = date;
              return true;
            }
          
          C get_date() {
            return current_date;
          }

          

          bool add_event ( std::string str , C date ) {
            typename std::list<Calendar<C>::Event>::iterator it =
              events.begin();
            for (; it != events.end(); it++ ) {
              Calendar<C>::Event event = *it;
              if ( event.event_date() == date &&
                   event.event_str() == str )
                return false;
              else if (event.event_date() > date ) {
                events.insert ( it , Event ( date , str ) );
                return true;
              }
            }
            events.push_back ( Calendar<C>::Event ( date , str ));
            return true;
          }
          
          bool add_event(std::string str , int day) {
            C event_date;
            try {
              event_date =
                C(current_date.year(), current_date.month(), day);
              return add_event ( str, event_date );
            } catch ( std::out_of_range ign ) {
              return false;
            }
          }

          bool add_event(std::string str , int day , int month ) {
            C event_date;
            try {
              event_date =
                C(current_date.year(), month , day);
              return add_event ( str, event_date );
            } catch ( std::out_of_range ign ) {
              return false;
            }
          }

          bool add_event(std::string str , int day , int month , int year ) {
            std::cout << "ADD event " << std::endl;
            C event_date;
            try {
              event_date =
                C(year, month , day);
              std::cout << "event_date :" << event_date << std::endl;
              return add_event ( str, event_date );
            } catch ( std::out_of_range ign ) {
              return false;
            }
          }

          bool remove_event (std::string str, int day ) {
            C event_date;
            try {
              event_date = C(current_date.year(), current_date.month(),
                             day );
              return remove_event ( str , event_date ) ;
            } catch ( std::out_of_range ign ) {
              return false;
            }
          }

          bool remove_event (std::string str , int month , int day ) {
            C event_date;
            try {
              event_date = C(current_date.year(), month,
                             day );
              return remove_event ( str , event_date ) ;
            } catch ( std::out_of_range ign ) {
              return false;
            }
          }

          bool remove_event (std::string str , int year , int month , int day )
          {
            C event_date;
            try {
              event_date = C(year, month,
                             day );
              return remove_event ( str , event_date ) ;
            } catch ( std::out_of_range ign ) {
              return false;
            }
          }

          bool remove_event (std::string str , C date ) {
            typename std::list<Calendar<C>::Event>::iterator it =
              events.begin();
            for (; it != events.end(); it++ ) {
              Calendar<C>::Event event = *it;
              if ( event.event_date() == date
                   && event.event_str() == str ) {
                events.remove ( event );
                return true;
              }
            }
            return false;
          }
          // inner class --
          class Event {
          public:
            Event (C date, std::string s): date (date), str(s) { }

            bool operator==(const Event& event) {
              if (event.event_date() == date && 
                  event.event_str() ==  str ) {
                return true;
              }
              return false;
            }
            friend std::ostream& operator<<(std::ostream& ostream,
                                            const Event& e )
            {
              ostream << e.event_date() << ": " << e.event_str();
              return ostream;
            }
            C event_date() const { 
              return date; 
            }
            std::string event_str() const { 
              return str; 
            }

          private:
            C date;
            std::string str;
          };
          /// inner class end --

          std::list<Calendar<C>::Event> events;
          
        protected:
          C current_date;
    };
}


#endif
