package test;

import com.jarclassloader.*;
import org.apache.log4j.Logger;

public class Test {
    
    // static JarClassLoader mycl2;
    private static final Logger log = Logger.getLogger ( Test.class ) ;
    
    public static void main (String... arg ) {

        try {
            
            
            log.info(" Test start ");
            final JarClassLoader mycl1 = new JarClassLoader("mycl1");
            log.info (" MYCL1= " + mycl1 );
            
            JarClassLoader mycl2 = new JarClassLoader("mycl2");            
            log.info (" MYCL2= " + mycl2 );
            mycl1.add ("build/jars/package1.jar");
            mycl2.add ("build/jars/package2.jar");


            mycl1.addLoader ( mycl2 );
            mycl2.addLoader ( mycl1 );

            Thread t1 = 
                new Thread (
                            new Runnable() {
                                public void run ( ) {
                                    try {
                                    Class c =
                                        mycl1.loadClass ("com.package1.A");
                                    } catch ( Throwable e ) {
                                        log.error("",e);
                                    }

                                }
                            });
            t1.start();
            
            
            Class c = mycl2.loadClass ("com.package2.C");
            
                                        
        } catch ( Throwable e ) {
            log.error("",e);
            
        }
        

    }
}