
import java.util.*;
public class Dates {

    public static void main ( String arg[] ) {
        String yy = arg[0];
        String mm = arg[1];
        String dd = arg[2];

        GregorianCalendar cal = 
            new GregorianCalendar ( 
                                   Integer.parseInt ( yy ),
                                   Integer.parseInt ( mm ),
                                   Integer.parseInt ( dd ));

        cal.add ( Calendar.YEAR, Integer.parseInt( arg[3]));
        cal.add ( Calendar.MONTH, Integer.parseInt( arg[4]));
        cal.add ( Calendar.DAY_OF_MONTH, Integer.parseInt( arg[5]));
        
        System.out.println("DATE: " + cal.get(Calendar.YEAR) + "-" + 
                           cal.get(Calendar.MONTH) + "-" + 
                           cal.get(Calendar.DAY_OF_MONTH)
                           );
        System.out.println ("Date:" + cal.getTime());

        // System.out.println ( "cal:" + cal );
        
    }


}