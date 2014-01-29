
// This code is modified allot since NCS 2.0 where
// bugs have found.
package com.jarclassloader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;


/**
 * Reads the class bytes from jar files and other resources using
 * ClasspathResources
 *
 */
@SuppressWarnings("unchecked")
public class JarClassLoader extends AbstractClassLoader {
    /**
     * Class cache
     */
    protected final Map<String, Class> classes;

    // This repository for classes extracted from a .jar file
    // declared as private and should not be visible by
    // subclasses of this JarClassLoader
    protected final ClasspathResources classpathResources;

    // This needs to be investigated
    private char classNameReplacementChar;

    // Holds a reference to the instance of the inner class , that is
    // the loader that does the crucial part.
    private final ProxyClassLoader localLoader = new LocalLoader();

    // The name of the package , should same package name be a problem?
    //
    private String packageName;

    // Usefull for debugging this class should print all is traces
    // in LEVEL TRACE
    private final static 
        Logger log = Logger.getLogger( JarClassLoader.class );

    static {
        Method m = null;
        
        try {
            m = 
                ClassLoader.class
                .getDeclaredMethod("registerAsParallelCapable");
        } catch (NoSuchMethodException e ) {
            // its ok 
        }

        if ( m != null ) {
            try {
                m.setAccessible (true) ;
                m.invoke (null);
            } catch ( Exception e ) {
                log.error("", e ) ;
            }
        }
    }

    // The package name for which this JarClassLoader is
    // responsible for.
    public JarClassLoader(String packageName) {
        this.packageName = packageName;
        this.classpathResources = new ClasspathResources();
        this.classes =
            Collections.synchronizedMap( new HashMap<String, Class>() );
        initialize();
    }

    /**
     * Some initialization, add this JarClassLoader
     * to the list of known ClassLoaders.
     */
    public void initialize() {
        loaders.add( localLoader );
    }

    /**
     * Add a jar and Loads local/remote resources.
     *
     * @param jarPath is a jar file.
     */
    public void add(String jarPath) {
        classpathResources.loadResource( jarPath );
    }

    /**
     *  Returns the package name for which this classloader
     *  is responsible to load.
     *
     *  @return the package name of what package this classloader
     *  is responsible to load.
     **/
    public String getPackageName() {
        return packageName;
    }

    /**
     * Reads the class bytes from different local and remote resources using
     * ClasspathResources.
     *
     * @param className
     * @return A byte array for the given className stored in the
     *  repository.
     */
    protected byte[] loadClassBytes(String className) {
        className = formatClassName( className );
        byte[] temp = classpathResources.getResource( className );
        return temp;
    }

    /**
     * Removes the association between the className and the
     * bytes.
     *
     * Attempts to unload class, it only unloads the locally
     * loaded classes in the repository. This method
     * may not work properly . What happens if we define
     * a class twice?
     *
     * @param className
     */
    public void unloadClass(String className) {
        if (log.isTraceEnabled()) {
            log.trace( "Unloading class " + className );
        }
        if( classes.containsKey ( className ) ) {
            if (log.isTraceEnabled()) {
                log.trace( "Removing loaded class " + className );
            }
            classes.remove( className );

            try {
                classpathResources.unload( formatClassName( className ) );
            } catch (ResourceNotFoundException e) {
                throw new JclRuntimeException( "Something is very wrong!!!"
                                         + "The locally loaded classes " +
                                 "must be in sync with ClasspathResources",
                                               e );
            }
        } else {
            try {
                classpathResources.unload( formatClassName( className ) );
            } catch (ResourceNotFoundException e) {
                throw new JclRuntimeException( "Class could not be unloaded "
                    + "[Possible reason: Class belongs to the system]", e );
            }
        }
    }

    /**
     * Creates a path to a .class file from its canonical name.
     *
     * @param className
     * @return String
     */
    protected String formatClassName(String className) {
        className = className.replace( '/', '~' );

        if( classNameReplacementChar == '\u0000' ) {
            // '/' is used to map the package to the path
            className = className.replace( '.', '/' ) + ".class";
        } else {
            // Replace '.' with custom char, such as '_'
            className = className.replace( '.', classNameReplacementChar ) +
                ".class";
        }

        className = className.replace( '~', '/' );
        return className;
    }

    /**
     * The Local class loader is where the critical code comes in for
     * a component or shared class jar. The shared/package jars
     * should already be in the repository when this classloader
     * will fetch the class bytes from.
     * The AbsctractClassload will invoke the Proxy Local loader before
     * or after some other Proxy Loaders has not found the class
     * that the loaders are trying to define.
     *
     * The methods contained in this class is not the overridden methods
     * that one usually expects from java.lang.Classloader
     * instead the methods is called from the overwritten methods
     * from the java.lang.Classloader.
     */
    class LocalLoader extends ProxyClassLoader {
        private final Logger log = Logger.getLogger ( LocalLoader.class ) ;
        public Enumeration<URL> getResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);

            ArrayList<URL> urlArr = null;
            try {
                urlArr = classpathResources.getURLResources(name);
            } finally {
                dismiss(this, name);
            }
            if (urlArr == null || urlArr.size() == 0) return null;
            return Collections.enumeration(urlArr);
        }

        public URL findResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            ArrayList<URL> urlArr = null;
            try {
                urlArr = classpathResources.getURLResources(name);
            } finally {
                dismiss(this, name);
            }
            if (urlArr == null) return null;

            if (urlArr.size() > 0) {
                return urlArr.get(0);
            }
            return null;
        }

        public Enumeration<URL> findResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            ArrayList<URL> urlArr = null;
            try {
                urlArr = classpathResources.getURLResources(name);
            } finally {
                dismiss(this, name);
            }
            if (urlArr == null || urlArr.size() == 0) return null;
            return Collections.enumeration(urlArr);
        }
        //load class by its className and resolve it if resolveIt is true
        //check whether the name is in the repository and defined
        //
        public Class loadClass(String className, boolean resolveIt) {
            if (log.isTraceEnabled()) {
                log.trace("XCALL (" + this + ").loadClass(" +
                    className + ","  + resolveIt  + ") =>");
                log.trace ("JarClassLoader.this=" + 
                           JarClassLoader.this );
                // log.trace( "holdsLock " + this + " ? = " + 
                //        Thread.currentThread().holdsLock ( this ));

            }
            if (isAlreadyAttempted(this, className)) {
                return null;
            }

            Class result = null;
            byte[] classBytes;

            //Check the cache..
            attempt(this, className);
            try {
                result = classes.get( className );
            } finally {
                dismiss(this, className);
            }

            if( result != null ) {
                if (log.isTraceEnabled()) {
                    log.trace("XCALL (" + this + ").loadClass(" +
                        className + ","  + resolveIt  + ") => found");
                }
                return result;

            } else {
                if (log.isTraceEnabled()) {
                    log.trace(" not found on local cache");
                }
            }

            classBytes = loadClassBytes( className );

            if( classBytes == null ) {
                if (log.isTraceEnabled()) {
                    log.trace("NO classbytes was not found for '" + className);
                    log.trace("XCALL (" + this + ").loadClass(" +
                        className + ","  + resolveIt  + ") => not found");
                }
                return null;
            }
            if (log.isTraceEnabled()) {
                log.trace( className + " has " + classBytes.length + " bytes");
            }

            //            synchronized (classes) {
                result = classes.get(className);
                if (result != null) {
                    // some other thread has raced and won loading this class
                    if (log.isTraceEnabled()) {
                        log.trace( className + " already loaded ");
                        log.trace("XCALL (" + this + ").loadClass(" +
                            className + ","  + resolveIt  + ") => not found");
                    }
                    return result;
                }
                if (log.isTraceEnabled()) {
                    log.trace("(" + JarClassLoader.this + 
                              ").defineClass("+className+ ") =>");
                    log.trace( "holdsLock " + JarClassLoader.this + " ? = " + 
                       Thread.currentThread()
                               .holdsLock ( 
                                           JarClassLoader.this ));
                }
                result = defineClass(className,classBytes,0,classBytes.length);

                log.trace( "holdsLock " + JarClassLoader.this + " ? = " + 
                       Thread.currentThread().holdsLock ( 
                                                         JarClassLoader.this ));


                if( result == null ) {
                    log.warn("Could not Define class "+ className);
                    if (log.isTraceEnabled()) {
                        log.trace("XCALL (" +this+ ").loadClass(" +className +
                                 ","  + resolveIt+") => defineClass failure!");
                    }
                    return null;
                }
                if (log.isTraceEnabled()) {
                    log.trace ( "(" + JarClassLoader.this + ").defineClass("+
                                className + ") => defined");
                }



                // Preserve package name.
                // Maybe this needs to be reconsidered
                // shouldn't the definePackage() comes before
                // the defineClass ?
                // and should the Manifest information be read?
                // today no Manifest info is considered.
                if (result.getPackage() == null) {
                    // check that the class has a package
                    // if not do not call the definePackage()
                    if ( className.lastIndexOf ('.') != -1 ) {
                        String packageName =
                            className.substring( 0,
                                                 className
                                                 .lastIndexOf( '.' ) );


                        if (log.isTraceEnabled()) {
                            log.trace ( "(" + this +
                                ").definePackage(" + packageName + ") =>");
                        }
                        definePackage(packageName, null, null, null, null,
                                      null, null, null );
                        if (log.isTraceEnabled()) {
                            log.trace ( "(" + this + ").definePackage(" +
                                packageName + ") => ok");
                        }
                    }
                }

                if (resolveIt) {
                    if (log.isTraceEnabled()) {
                        log.trace ( " resolving () => ");
                    }
                    resolveClass( result );
                    if (log.isTraceEnabled()) {
                        log.trace ( " resolving () => resolved");
                    }
                }
                classes.put( className, result );
                // } // synchronized end
            if ( result != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning class " + className +
                               " loaded with "  + JarClassLoader.this );
                    log.trace( "holdsLock " + JarClassLoader.this + " ? = " + 
                               Thread.currentThread()
                               .holdsLock ( JarClassLoader.this ));

                    log.trace ("JarClassLoader.this=" + 
                               JarClassLoader.this );
                    log.trace("XCALL (" + this + ","+resolveIt+").loadClass(" +
                        className + ") => found");
                }
            }
            return result;
        }

        public InputStream loadResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            byte[] arr = null;
            attempt(this, name);
            try {
                arr = classpathResources.getResource( name );
            } finally {
                dismiss(this, name);
            }
            if( arr != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning newly loaded resource " + name );
                }
                return new ByteArrayInputStream( arr );
            }

            return null;
        }

        public URL getResource(String name){
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            URL resource = null;
            attempt(this, name);
            try {
                resource = classpathResources.getURLResource(name);
            } finally {
                dismiss(this, name);
            }
            if (log.isInfoEnabled()) {
                log.info( "Resource class " + resource +
                    " loaded with Local classloader" );
            }

            return resource;
        }

    }
    // -- Local Loader inner class end.

    public char getClassNameReplacementChar() {
        return classNameReplacementChar;
    }

    public void setClassNameReplacementChar(char classNameReplacementChar) {
        this.classNameReplacementChar = classNameReplacementChar;
    }

    /**
     * Returns all loaded classes and resources
     *
     * @return Map
     */
    public Map<String, byte[]> getLoadedResources() {
        return classpathResources.getResources();
    }

    /**
     * Return the inner local loader as a ProxyClassLoader.
     *
     * @return Local JCL ProxyClassLoader
     */
    public ProxyClassLoader getLocalLoader() {
        return localLoader;
    }

    /**
     * Returns all JCL-loaded classes as an immutable Map
     *
     * @return Map
     */
    public Map<String, Class> getLoadedClasses() {
        return Collections.unmodifiableMap( classes );
    }

    /**
     * Returns all JCL-loaded classes as a shallow copy
     *
     * @return Map with the class name associated with
     * the class bytes
     */
    public Map<String,Class> getLoadedClassesAsCopy ( ) {
        return new HashMap<String,Class> ( classes );
    }
}
