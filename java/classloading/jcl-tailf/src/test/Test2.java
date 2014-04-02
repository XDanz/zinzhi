package test;
import java.lang.reflect.Method;
import com.jarclassloader.JarClassLoader;

public class Test2 {

    static class A {
        protected synchronized String loadString () {
            System.out.println ( "A.loadString() ");
            return "";
        }
    }

    static class B extends A {
        public String loadString ( ) {
            System.out.println ( "B.loadString()");
            return "";
        }
    }

    public static void main ( String... arg ) {
        JarClassLoader jcl = new JarClassLoader("JarClassLoader");
        

        // A b = new B();
        // b.loadString();

    }


}