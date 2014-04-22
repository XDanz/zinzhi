/* mjd.c - Modified Julian Day number conversion utility
 *
 * Usage:
 *	mjd [-q] [-j|-g yyyy-mm-dd] [-a|-b] [-t hh:mm[:ss]] [-d|m ddd.ff]
 *
 * Copyright (C) 1996 R.M.O'Leary <robin@acm.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>


const char *day_name[]={
    "Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"
};

long julian_calendar_to_jd(int y, int m, int d)
{
    if(m<3) { y--; m+=12; }
    y+=4800;
    m-=3;
    return (y*365) +(y/4)
        +(30*m) +(m*3+2)/5
        +d
        -32083
	;
}

long gregorian_calendar_to_jd(int y, int m, int d)
{
    if(m<3) { y--; m+=12; }
    y+=8000;
    /*return (y*365) +(y/4) -(y/100) +(y/400) -(y/4000)*/
    return (y*365) +(y/4) -(y/100) +(y/400)
        +(m*153+3)/5-92
        +d-1
        -1200820
	;
}

void jd_to_calendar(long jd, long (*calendar_to_jd)(int y, int m, int d), 
                    int *yp, int *mp, int *dp)
{
    int y,m,d;
    for(y=jd/366-4715; (*calendar_to_jd)(y+1,1,1)<=jd; y++);
    for(m=1; (*calendar_to_jd)(y,m+1,1)<=jd; m++);
    for(d=1; (*calendar_to_jd)(y,m,d+1)<=jd; d++);
    *yp=y; *mp=m; *dp=d;
}


static void print_date(int y, int m, int d, int quiet, int qualify_ad_bc, 
                       const char *calendar)
{
    if(quiet)
	printf("%04d %02d %02d ",y,m,d);
    else
        {
            if(qualify_ad_bc)
                printf("%s %04d", (y<1)?"BC":"AD", (y<1)?1-y:y);
            else
                printf("%04d", y);
            printf("-%02d-%02d %s",m,d, calendar);
        }
}


int main(int argc, char *argv[])
{
    static struct option long_options[] =
	{
            {"ad",			0, 0, 'a'},
            {"bc",			0, 0, 'b'},
            {"gregorian_calendar",	1, 0, 'g'},
            {"julian_calendar",	1, 0, 'j'},
            {"julian_day",		1, 0, 'd'},
            {"modified_julian_day", 1, 0, 'm'},
            {"time",		1, 0, 't'},
            {"quiet",		0, 0, 'q'},
            {"help",		0, 0, 'h'},
            {0, 0, 0, 0}
	};
    int c;
    int is_bc=0;
    int is_julian_day=0;
    int is_gregorian_calendar=0;
    int is_julian_calendar=0;
    int is_modified_julian_day=0;
    int quiet=0;
    char *date=(char*)0;
    char *time=(char*)0;
    int bad=0;
    int help=0;
    long jd=0;
    double time_offset=0.0; /* Noon */

    while( (c=getopt_long(argc,argv,"abg:j:d:m:qt:h",long_options, 
                          NULL)) != -1 )
	{
	    switch (c)
                {
		case 'a': is_bc=0; break;
		case 'b': is_bc=1; break;
		case 'd': is_julian_day=1; date=optarg; break;
		case 'g': is_gregorian_calendar=1; date=optarg; break;
		case 'j': is_julian_calendar=1; date=optarg; break;
		case 'm': is_modified_julian_day=1; date=optarg; break;
		case 't': time=optarg; break;
		case 'q': quiet=1; break;
		case 'h': help=1; break;

		case '?': bad++; break;

		default:
		    fprintf(stderr,"%s: BUG: option %c not handled!\n",
                            argv[0],c);
                }
	}

    if(argc!=optind)
	{
            fprintf(stderr,"%s: too many arguments (``%s'')\n",
		    argv[0],argv[optind+2]);
            bad++;
	}

    if(date)
	{
	    if(is_gregorian_calendar || is_julian_calendar)
                {
                    int y,m,d;
                    if(sscanf(date,"%d-%d-%d",&y,&m,&d)!=3 )
                        {
                            fprintf(stderr,"%s: ``%s'' is not a valid date.\n",
                                    argv[0],date);
                            bad++;
                        }
                    if(is_bc)
                        y=1-y;
                    if(is_gregorian_calendar)
                        jd=gregorian_calendar_to_jd(y,m,d);
                    else
                        jd=julian_calendar_to_jd(y,m,d);
                    time_offset=-0.5; /* start from midnight */
                }
	    else /* is_julian_day or is_modified_julian_day */
                {
                    double fjd;
                    if(sscanf(date,"%lf",&fjd)!=1 )
                        {
                       fprintf(stderr,"%s: ``%s'' is not a valid day number.\n",
                               argv[0],date);
                            bad++;
                        }
                    if(is_modified_julian_day)
                        fjd+=2400000.5;
                    jd=fjd;/* fractional part truncated because jd is an int */
                    time_offset=fjd-jd;
                }
	}

    if(time)
	{
	    int h,m,s=0;
	    if(sscanf(time,"%d:%d:%d",&h,&m,&s)<2 )
                {
                  fprintf(stderr,"%s: ``%s'' is not a valid time hh:mm[:ss]\n",
                          argv[0],time);
                  bad++;
                }

	    /* Calendar dates and Modified Julian Day numbers start at
	     * midnight, as do conventional clock times, so no adjustment
	     * is needed.
	     * However, a Julian Day begins at noon (12:00) and continues
	     * through midnight (23:59, 00:00) up to the following noon
	     * (11:59).  So if we are given hours 00 to 11, they actually
	     * mean hours 12 to 23 after noon; but given 12 to 23 refers
	     * to hours 0 to 11 after noon.
	     */
	    if(is_julian_day)
                {
                    if(h<12) h+=12; else h-=12;
                }
	    time_offset+=(double)((h*60+m)*60+s)/(24*60*60);
	}

    /* Paranoia.  I don't think this happens, but just in case... */
    if(time_offset<=-0.5)
	{
	    jd--;
	    time_offset+=1.0;
	}
    if(time_offset>=0.5)
	{
	    jd++;
	    time_offset-=1.0;
	}
    if(help)
        printf(
       "Modified Julian Day number conversion utility\n"
       "Copyright (C) 1996 R.M.O'Leary <robin@acm.org>\n"
       "\n"
       "This program is free software; you can redistribute it and/or modify\n"
       "it under the terms of the GNU General Public License as published by\n"
       "the Free Software Foundation; either version 2 of the License, or\n"
       "(at your option) any later version.\n"
       "\n");
    
    if(bad || help)
	{
	    fprintf(help?stdout:stderr,
  "Usage: %s [-q] [-j|-g yyyy-mm-dd] [-a|-b] [-t hh:mm[:ss]] [-d|m ddd.ff]\n",
                    argv[0]);
	    if(help)
		printf(
  "    -h --help   display this message\n"
  "    -q --quiet  makes output more computer-friendly (default off)\n"
  "Converting from Julian or Gregorian calendars:\n"
  "    -j --julian yyyy-mm-dd\n"
  "                convert date specified in the Julian calendar\n"
  "    -g --gregorian yyyy-mm-dd\n"
  "                convert date specified in the Gregorian calendar\n"
  "    -a --ad     indicates that year is AD (default)\n"
  "    -b --bc     indicates that year is BC\n"
  "    -t --time hh:mm[:ss]\n"
  "                time of day (default 12:00 Noon)\n"
  "Converting from Julian or Modified Julian Day numbers:\n"
  "    -d --julian_day ddd.dd\n"
  "                convert date and time specified as a Julian Day number\n"
  "    -m --modified_julian_day ddd.dd\n"
  "  convert date and time specified as a Modified Julian Day number\n"
                    );
	    else
		fprintf(stderr,
                        "       %s -h    for more information\n",
                        argv[0]);
	    exit(bad);
	}

    {
        int y,m,d;
        int hour,minute,second;
        int s;

        printf(quiet?"%.10g %.10g " :"JD %.10g, MJD %.10g,\n",
               jd+time_offset,
               jd+time_offset-2400000.5);
        
        s=(time_offset+0.5)*24*60*60;
        second =  s%60;
        minute = (s/60)%60;
        hour   =  s/60/60;
        printf(
            quiet?"%s %02d %02d %02d "
            :"%s, %02d:%02d:%02d,\n",
            day_name[((long)jd%7+7)%7],
            hour,minute,second);
        jd_to_calendar(jd,gregorian_calendar_to_jd,&y,&m,&d);
        print_date(y, m, d, quiet, is_bc, "Gregorian calendar");
        if(!quiet)printf(",\n");

        jd_to_calendar(jd,julian_calendar_to_jd,&y,&m,&d);
        print_date(y, m, d, quiet, is_bc, "Julian calendar");
        if(!quiet)printf(".\n");
    }
    return 0;
}
