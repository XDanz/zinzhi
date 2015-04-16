package test;

import org.apache.log4j.*;
import org.joda.time.*;

public class Test {
    private static Logger log = Logger.getLogger(Test.class);

    public static void 
        main ( String arg[] ) {
        log.info ( " Hello!" );
        DateTime dt = DateTime.now();
        log.info( "dt " + dt);
    }
}
