package com.jarclassloader;

/**
 * A factory class that loads classes from specified
 *  ClassLoader and tries to
 * instantiate their objects
 *
 *
 */
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

@SuppressWarnings("unchecked")
public class JclObjectFactory {
    private static JclObjectFactory jclObjectFactory =
        new JclObjectFactory();
    private static final Logger log =
        Logger.getLogger(JclObjectFactory.class);


    /**
     * private constructor
     */
    private JclObjectFactory() {    }

    /**
     * Returns the instance of the singleton factory
     *
     * @return JclObjectFactory
     */
    public static JclObjectFactory getInstance() {
        return jclObjectFactory;
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the default constructor
     *
     * @param cl
     * @param className
     * @return Object
     */
    public Object create(ClassLoader cl, String className)
        throws JclCreationCheckedException {
        return create( cl, className, (Object[]) null );
    }

    /**
     * Creates a object from the the specified <tt>className</tt> through
     * the specified ClassLoader <tt>cl</tt> with the arguments <tt>args</tt>.
     *
     * The class is not limited by a public constructor for creation.
     *
     * @param cl A classloader to load the specified <tt>className</tt>
     * @param className  A class name in its canonical representation
     * @param args Arguments to create the instance of the class
     * @return Object
     *
     * @throws JclCreationCheckedException if the ClassLoader
     * <tt>cl</tt> could not find the <tt>className</tt>.
     * If the instantiation of the specified <tt>className</tt>
     * could not be performed
     *
     */
    public Object create(ClassLoader cl, String className, Object... args)
        throws JclCreationCheckedException {
        if (log.isDebugEnabled()) {
            log.debug("create(" + cl + " " + className + ") =>");
        }
        Object instance = null;
        Class  cls      = null;

        if( args != null && args.length > 0) {
            Class[] types = new Class[args.length];

            for( int i = 0; i < args.length; i++ )
                types[i] = args[i].getClass();

            return create( cl, className, args, types );
        }

        try {

            String clName = "";
            if(cl instanceof JarClassLoader){
                clName += "JAR-CLASSLOADER-['" +
                    ((JarClassLoader)cl).getPackageName()
                    + "']";
            } else {
                clName = cl.toString();
            }

            if (log.isDebugEnabled()) {
                log.debug("CALL (" + cl + ").loadClass(" + className + ") =>");
            }
            log.debug( "holdsLock " + cl + " ? = " + 
                       Thread.currentThread().holdsLock ( this ));
            cls = cl.loadClass(className);
            log.debug( "holdsLock " + cl + " ? = " + 
                       Thread.currentThread().holdsLock ( this ));
            
            
            Constructor constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);

            if (log.isDebugEnabled()) {
                log.debug("CALL (" + cl + ").loadClass("+className + ") => ok");

                log.debug("CALL newInstance() =>");
            }
            instance = constructor.newInstance();
            if (log.isDebugEnabled()) {
                log.debug("CALL newInstance() => ok");
            }
            String instanceName = "";
            try {
                instanceName = instance.toString();
            } catch(Exception e) {
                log.warn("The Instance of class \"" + cls  +
                            "\" toString() throwed Exception");
                instanceName = cls.toString() + "-Instance";
            }

            if (log.isDebugEnabled()) {
                log.debug("NEW INSTANCE OF CLASS ['" + cls + "'] --> " +
                    "['" + instanceName + "]");

                log.debug("LOAD CLASS ['" + className + "'] CLASSLOADER:['"
                    + clName + "'] -->");
                log.debug("CREATE Instance ['" + className + "'] --> ['" +
                    instanceName + "']");
            }
        } catch(ClassNotFoundException e) {
            //log.error("Class \"" + className + "\" Not Found!",e);
            throw new JclCreationCheckedException(e,
                                                  JclCreationErrorCode
                                                  .LOAD_FAILURE,
                                                  cl,className,
                                                  args);
        } catch (InstantiationException e) {
            log.error("Could not instantiate class \"" +
                         className + "\"",e);
            throw new JclCreationCheckedException(e,
                                                  JclCreationErrorCode
                                                  .INSTANTIATION_FAILURE,
                                                  cl,className,
                                                  args);

        } catch(IllegalAccessException e) {
            // log.error("Could not instantiate class \"" +
            //              className + "\"",e);
            throw new JclCreationCheckedException(e,
                                JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                  cl,className,
                                                  args);
        } catch(NoSuchMethodException e) {
            // log.error("Could not instantiate class \"" +
            //           className + "\"",e);
            throw new JclCreationCheckedException(e,
                                JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                  cl,className,
                                                  args);

        } catch(InvocationTargetException e) {
            // log.error("Underlying constructor throwed exception " +
            //              "in \"" + className + "\" !",e);

            throw new JclCreationCheckedException(e,
                                JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                  cl,className,
                                                  args);
        }
        if (log.isDebugEnabled()) {
            log.debug("create(" + cl + " " + className + ") => ok");
        }
        return instance;
    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right arguments-constructor based on the passed types
     * parameter
     *
     * @param cl
     * @param className
     * @param args
     * @param types
     * @return Object
     */
    public Object create(ClassLoader cl, String className,
                         Object[] args, Class[] types)
    throws JclCreationCheckedException{
        Object obj = null;
        try {
            if( args == null || args.length == 0 ) {
                obj = cl.loadClass( className ).newInstance();
            } else {
                obj = cl.loadClass( className ).getConstructor( types )
                    .newInstance( args );
            }
        } catch(ClassNotFoundException e) {
            throw new JclCreationCheckedException(e,
                                    JclCreationErrorCode.LOAD_FAILURE,
                                                    cl,className,
                                                           args);
        } catch(InvocationTargetException e) {
            throw new JclCreationCheckedException(e,
                                    JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                    cl,className,
                                                           args);

        } catch(NoSuchMethodException e) {
            throw new JclCreationCheckedException(e,
                               JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                  cl,className,
                                                  args);

        } catch (InstantiationException e) {
            log.error("Could not instantiate class \"" +
                             className + "\"",e);

        }  catch(IllegalAccessException e) {
            throw new JclCreationCheckedException(e,
                               JclCreationErrorCode.INSTANTIATION_FAILURE,
                                             cl,className,
                                                  args);
        }
        return obj;
    }




    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right static factory method
     *
     * @param cl
     * @param className
     * @param methodName
     * @param args
     * @return Object
     */
    public Object create(ClassLoader cl, String className,
                         String methodName, Object... args)
        throws JclCreationCheckedException{
        if( args == null || args.length == 0 ) {
            try {
                return  cl.loadClass( className )
                    .getMethod( methodName ).invoke( null ) ;
            } catch(ClassNotFoundException e){
                log.error("Class \"" + className + "\" Not Found!",
                             e);
                throw new JclCreationCheckedException(e,
                                    JclCreationErrorCode.LOAD_FAILURE,
                                                    cl,className,
                                                           args);
            } catch(InvocationTargetException e){
                log.error("Underlying constructor throwed exception " +
                         "in \"" + className + "\" !",
                         e);
                throw new JclCreationCheckedException(e,
                                    JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                    cl,className,
                                                           args);

            } catch(NoSuchMethodException e){
                log.error("Constructor for \"" + className +
                             "\" Not Found!",e);
                throw new JclCreationCheckedException(e,
                               JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                  cl,className,
                                                  args);

            } catch(IllegalAccessException e){
                throw new JclCreationCheckedException(e,
                               JclCreationErrorCode.INSTANTIATION_FAILURE,
                                             cl,className,
                                                  args);
            }
        }


        Class[] types = new Class[args.length];

        for( int i = 0; i < args.length; i++ )
            types[i] = args[i].getClass();

        return create( cl, className, methodName, args, types );

    }

    /**
     * Creates the object of the specified class from the specified class loader
     * by invoking the right static factory method based on the types parameter
     *
     * @param cl
     * @param className
     * @param methodName
     * @param args
     * @param types
     * @return Object
     */
        public Object create(ClassLoader cl, String className,
                         String methodName, Object[] args, Class[] types)
            throws JclCreationCheckedException{
            Object obj = null;
            if( args == null || args.length == 0 ) {
            try {
                obj = cl.loadClass( className )
                    .getMethod( methodName ).invoke( null );
            } catch(ClassNotFoundException e){
                log.error("Class \"" + className + "\" Not Found!",
                             e);
                throw new JclCreationCheckedException(e,
                                    JclCreationErrorCode.LOAD_FAILURE,
                                                    cl,className,
                                                           args);
            } catch(InvocationTargetException e){
                log.error("Underlying constructor throwed exception " +
                         "in \"" + className + "\" !",
                         e);
                throw new JclCreationCheckedException(e,
                                    JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                    cl,className,
                                                           args);

            } catch(NoSuchMethodException e){
                log.error("Constructor for \"" + className +
                             "\" Not Found!",e);
                throw new JclCreationCheckedException(e,
                               JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                  cl,className,
                                                  args);

            } catch(IllegalAccessException e){
                throw new JclCreationCheckedException(e,
                               JclCreationErrorCode.INSTANTIATION_FAILURE,
                                             cl,className,
                                                  args);
            }
        } else {
            try {
                obj = cl.loadClass( className )
                    .getMethod( methodName, types ).invoke( null, args );
            } catch(ClassNotFoundException e){
                log.error("Class \"" + className + "\" Not Found!",
                             e);
                throw new JclCreationCheckedException(e,
                                    JclCreationErrorCode.LOAD_FAILURE,
                                                    cl,className,
                                                           args);
            } catch(InvocationTargetException e){
                log.error("Underlying constructor throwed exception " +
                         "in \"" + className + "\" !",
                         e);
                throw new JclCreationCheckedException(e,
                                    JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                    cl,className,
                                                           args);

            } catch(NoSuchMethodException e){
                log.error("Constructor for \"" + className +
                             "\" Not Found!",e);
                throw new JclCreationCheckedException(e,
                               JclCreationErrorCode.INSTANTIATION_FAILURE,
                                                  cl,className,
                                                  args);

            } catch(IllegalAccessException e){
                throw new JclCreationCheckedException(e,
                               JclCreationErrorCode.INSTANTIATION_FAILURE,
                                             cl,className,
                                                  args);
            }
            }

            return obj ;
    }

    // /**
    //  * Creates a proxy
    //  *
    //  * @param object
    //  * @return Object
    //  */
    // private Object newInstance(Object object) {
    //     return object;
    // }
}