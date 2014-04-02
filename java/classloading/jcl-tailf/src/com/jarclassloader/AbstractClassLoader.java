package com.jarclassloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Abstract class loader that can load classes from jar files.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClassLoader extends ClassLoader {

    private final static Logger log = 
        Logger.getLogger(AbstractClassLoader.class);

    protected final List<ProxyClassLoader> loaders =
        new ArrayList<ProxyClassLoader>();

    private final ProxyClassLoader systemLoader = new SystemLoader();
    private final ProxyClassLoader parentLoader = new ParentLoader();
    private final ProxyClassLoader currentLoader = new CurrentLoader();
    private final ProxyClassLoader threadLoader = new ThreadContextLoader();
    
    /**
     * No arguments constructor
     */
    public AbstractClassLoader() {
        loaders.add( systemLoader );
        loaders.add( parentLoader );
        loaders.add( currentLoader );
        loaders.add( threadLoader );
    }

    public List<ProxyClassLoader> getProxyLoaders() {
        return loaders;
    }

    public URL getResource(String name) {
        if( name == null || name.trim().equals( "" ) )
            return null;

        Collections.sort( loaders );

        URL url = null;

        if( url == null ) {
            for( ProxyClassLoader l : loaders ) {
                if( l.isEnabled() ) {
                    url = l.getResource(name);
                    if( url != null )
                        break;
                }
            }
        }

        return url;

    }

    public void addLoader(JarClassLoader loader) {
        loaders.add(loader.getLocalLoader());
    }

    public void removeLoader ( JarClassLoader loader ) {
        if (log.isInfoEnabled()) {
            log.info(" removeLoader (" + loader.getLocalLoader() + ") =>" );
        }
        ProxyClassLoader rr = null;
        for ( int i = 0; i < loaders.size() ; i++ ) {
            log.info ( " xxx =" + loaders.get(i));
            if (loaders.get(i).equals( loader.getLocalLoader())) {
                rr = loaders.remove(i);
                break;
            }
        }

        if (log.isInfoEnabled()) {
            log.info(" removeLoader (" + loader.getLocalLoader() + ") => " +
                 rr );
        }
    }

    public void addLoader(ProxyClassLoader loader) {
        loaders.add( loader );
    }


    public Enumeration<URL> getResources(String name) throws IOException {
        if( name == null || name.trim().equals( "" ) )
            return null;
        Collections.sort( loaders );
        ArrayList<URL> urlArr = new ArrayList<URL>();
        for( ProxyClassLoader l : loaders ) {
            if( l.isEnabled() ) {
                Enumeration<URL> eurl = l.getResources(name);
                if (eurl == null) continue;
                while( eurl.hasMoreElements()) {
                    urlArr.add(eurl.nextElement());
                }
            }
        }

        return Collections.enumeration(urlArr);
    }

    protected URL findResource(String name) {
        if( name == null || name.trim().equals( "" ) )
            return null;

        Collections.sort( loaders );

        ArrayList<URL> urlArr = new ArrayList<URL>();
        for( ProxyClassLoader l : loaders ) {
            if( l.isEnabled() ) {
                URL url = l.findResource(name);
                if (url != null) {
                    return url;
                }
            }
        }
        return null;
    }

    protected Enumeration<URL>
        findResources(String name) throws IOException {
        if( name == null || name.trim().equals( "" ) )
            return null;

        Collections.sort( loaders );
        ArrayList<URL> urlArr = new ArrayList<URL>();
        for( ProxyClassLoader l : loaders ) {
            if( l.isEnabled() ) {
                Enumeration<URL> eurl = l.findResources(name);
                if (eurl == null) continue;
                while( eurl.hasMoreElements()) {
                    urlArr.add(eurl.nextElement());
                }
            }
        }

        return Collections.enumeration(urlArr);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    public Class loadClass(String className) throws ClassNotFoundException {
        return ( loadClass( className, true ) );
    }

    

    /**
     * Overrides the loadClass method to load classes from other resources,
     * JarClassLoader is the only subclass in this project that loads classes
     * from jar files in package-name/private-jar , 
     * package-name/shared-jar directories.
     * 
     * 1.CurrentLoader (order=2)
     * 2.ParentLoader  (order=3)
     * 3.ThreadContextLoader (order=4)
     * 4.SystemLoader        (order=5)
     * 5.LocalLoader         (order=5)
     *
     * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
     */
    public Class loadClass(String className, boolean resolveIt)
        throws ClassNotFoundException {
        if (log.isTraceEnabled()) {
            log.trace("*(" + 
                      this + ").loadClass(" + className + "," + 
                      resolveIt + ") =>");
            log.trace( "holdsLock " + this + " ? = " + 
                       Thread.currentThread().holdsLock ( this ));

            // System.err.println(" ====== STACK X-START-X ====== ");
            // Thread.currentThread().dumpStack();
            // System.err.println(" ====== STACK X- END -X ====== ");
            
        }
        if ( className == null || className.trim().equals( "" ) )
            return null;

        Collections.sort( loaders );
        // System.err.println( " == LOADERS ORDER -STA- == ");
        // for ( ProxyClassLoader l : loaders ) {
        //    System.err.println ( "(" + l.getOrder() +  ") " + l);
        // }
        // System.err.println( " == LOADERS ORDER -END- == ");

        Class clazz = null;

        if ( clazz == null ) {

            for ( ProxyClassLoader l : loaders ) {
                if (log.isTraceEnabled()) {
                    log.trace( "(" + 
                               l.getOrder() + ")CALL (" +  l + ").loadClass(" +
                        className + "," + resolveIt + ") =>");
                }
                clazz = l.loadClass(className, resolveIt );

                if ( clazz != null ) {
                    if (log.isTraceEnabled()) {
                        log.trace(
                                  "(" + 
                                  l.getOrder()+")CALL (" +  l + ").loadClass(" +
                                  className + "," + resolveIt + ") => found");
                    }
                    break;
                }
                if ( log.isTraceEnabled() ) {
                    log.trace("(" + 
                              l.getOrder()+")CALL (" +  l + ").loadClass(" +
                        className + "," + resolveIt + ") => not found");
                }
            }
        }

        if( clazz == null )
            throw new ClassNotFoundException( className );

        if (log.isTraceEnabled()) {
            log.trace( "holdsLock " + this + " ? = " + 
                       Thread.currentThread().holdsLock ( this ));
            log.trace("*(" + 
                      this + ").loadClass(" + className + "," + 
                      resolveIt + ") => ok");
        }

        return clazz;
    }

    /**
     * Overrides the getResourceAsStream method to load non-class
     * resources from other sources,
     *  JarClassLoader is the only subclass in this project that
     * loads non-class resources from jar files
     *
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(String name) {
        if( name == null || name.trim().equals( "" ) )
            return null;

        Collections.sort( loaders );

        InputStream is = null;

        if( is == null ) {
            for( ProxyClassLoader l : loaders ) {
                if( l.isEnabled() ) {
                    is = l.loadResource(name);
                    if( is != null )
                        break;
                }
            }
        }

        return is;

    }

    // The inner ProxyClassLoaders  --
    /**
     * System class loader
     *
     */
    class SystemLoader extends ProxyClassLoader {
        private final Logger log = Logger.getLogger ( SystemLoader.class );
        public Enumeration<URL> getResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            Enumeration<URL> eurl = null;
            try {
                eurl = getSystemClassLoader().getResources(name);
            } finally {
                dismiss(this, name);
            }
            return eurl;
        }

        public URL findResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            Enumeration<URL> eurl;
            attempt(this, name);
            try {
                eurl = getSystemClassLoader().getResources(name);
            } catch (IOException e) {
                eurl = null;
            } finally {
                dismiss(this, name);
            }
            if (eurl == null) return null;
            while(eurl.hasMoreElements()) {
                URL url = eurl.nextElement();
                if (url.toString().endsWith(name)) {
                    return url;
                }
            }
            return null;
        }

        public Enumeration<URL> findResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            Enumeration<URL> eurl = null;
            try {
                eurl = getSystemClassLoader().getResources(name);
            } finally {
                dismiss(this, name);
            }
            return eurl;
        }


        public SystemLoader() {
            order = 5;
        }

        public URL getResource(String name){
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            URL resource = null;
            try {
                resource = getSystemResource(name);
            } finally {
                dismiss(this, name);
            }
            if (log.isTraceEnabled()) {
                log.trace( "Returning system resource  " + resource +
                    " loaded with system classloader" );
            }
            return resource;
        }

        public Class loadClass(String className, boolean resolveIt) {
            if (isAlreadyAttempted(this, className)) {
                return null;
            }
            Class result;
            attempt(this, className);
            try {
                if (log.isTraceEnabled()) {
                    log.trace("XCALL ("+getSystemClassLoader()+").loadClass(" +
                        className + ") =>");
                }
                result = findSystemClass( className );
            } catch (ClassNotFoundException e) {
                result = null;
                if (log.isTraceEnabled()) {
                    log.trace("XCALL ("+getSystemClassLoader()+").loadClass(" +
                        className + ") => not found");
                }

            } finally {
                dismiss(this, className);
            }
            if ( result != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning class " + className +
                               " loaded with "  + getSystemClassLoader() );
                    log.trace("XCALL ("+getSystemClassLoader()+").loadClass(" +
                        className + ") => found");
                }
            }

            return result;
        }

        public InputStream loadResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            InputStream is = null;
            try {
                is = getSystemResourceAsStream( name );
            } finally {
                dismiss(this, name);
            }
            if( is != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning system resource " + name );
                }
                return is;
            }

            return null;
        }

    }

    /**
     * Parent class loader
     *
     */
    class ParentLoader extends ProxyClassLoader {
        private final Logger log =
            Logger.getLogger( ParentLoader.class );

        public ParentLoader() {
            order = 3;
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            Enumeration<URL> eurl = null;
            try {
                eurl = getParent().getResources(name);
            } finally {
                dismiss(this, name);
            }
            return eurl;
        }

        public URL findResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            Enumeration<URL> eurl;
            attempt(this, name);
            try {
                eurl = getParent().getResources(name);
            } catch (IOException e) {
                eurl =  null;
            } finally {
                dismiss(this, name);
            }
            if (eurl == null) return null;
            while(eurl.hasMoreElements()) {
                URL url = eurl.nextElement();
                if (url.toString().endsWith(name)) {
                    return url;
                }
            }
            return null;
        }

        public Enumeration<URL>
            findResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            Enumeration<URL> eurl = null;
            try {
                eurl = getParent().getResources(name);
            } finally {
                dismiss(this, name);
            }
            return eurl;
        }

        public URL getResource(String name){
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            URL resource = null;
            try {
                resource = getParent().getResource(name);
            } finally {
                dismiss(this, name);
            }
            if (log.isTraceEnabled()) {
                log.trace( "Returning parent resource  " + resource +
                    " loaded with system classloader" );
            }
            return resource;
        }

        public Class loadClass(String className, boolean resolveIt) {
            if (isAlreadyAttempted(this, className)) {
                return null;
            }
            Class result = null;
            attempt(this, className);
            try {

                if (log.isTraceEnabled()) {
                    log.trace("XCALL (" + getParent() + ").loadClass(" +
                        className + ") =>");
                }
                result = getParent().loadClass(className);

            } catch (ClassNotFoundException e) {
                result = null;
                if (log.isTraceEnabled()) {
                    log.trace("XCALL (" + getParent() + ").loadClass(" +
                        className + ") => not found");
                }
            } finally {
                dismiss(this, className);
            }
            if ( result != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning class " + className +
                               " loaded with " + getParent() );
                    log.trace("XCALL (" + getParent() + ").loadClass(" +
                        className + ") => found");
                }
            }

            return result;
        }

        public InputStream loadResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }

            attempt(this, name);
            InputStream is = null;
            try {
                is = getParent().getResourceAsStream( name );
            } finally {
                dismiss(this, name);
            }
            if( is != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning resource " +
                        name + " loaded with parent classloader" );
                }
                return is;
            }
            return null;
        }

    }

    /**
     * Current class loader
     *
     */
    class CurrentLoader extends ProxyClassLoader {
        private final Logger log =
            Logger.getLogger ( CurrentLoader.class );
        public CurrentLoader() {
            order = 2;
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            Enumeration<URL> eurl = null;
            try {
                eurl = getClass().getClassLoader().getResources(name);
            } finally {
                dismiss(this, name);
            }
            return eurl;
        }


        public URL findResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            Enumeration<URL> eurl;
            attempt(this, name);
            try {
                eurl = getClass().getClassLoader().getResources(name);
            } catch (IOException e) {
                eurl = null;
            } finally {
                dismiss(this, name);
            }
            if (eurl == null) return null;
            while(eurl.hasMoreElements()) {
                URL url = eurl.nextElement();
                if (url.toString().endsWith(name)) {
                    return url;
                }
            }
            return null;
        }

        public Enumeration<URL>
            findResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            Enumeration<URL> eurl = null;
            try {
                eurl = getClass().getClassLoader().getResources(name);
            } finally {
                dismiss(this, name);
            }
            return eurl;
        }

        public URL getResource(String name){
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            attempt(this, name);
            URL resource = null;
            try {
                resource = getClass().getResource(name);
            } finally {
                dismiss(this, name);
            }
            if (log.isTraceEnabled()) {
                log.trace( "Resource class " + name +
                    " loaded with parent classloader" );
            }

            return resource;
        }

        public Class loadClass(String className, boolean resolveIt) {
            if (isAlreadyAttempted(this, className)) {
                return null;
            }

            Class result;
            attempt(this, className);
            try {
                if (log.isTraceEnabled()) {
                    log.trace(" XCALL (" + getClass().getClassLoader() +
                              ").loadClass ("+ className + ") =>");
                }
                result = getClass().getClassLoader().loadClass( className );

            } catch (ClassNotFoundException e) {
                result = null;
                if (log.isTraceEnabled()) {
                    log.trace(" XCALL (" + getClass().getClassLoader() +
                              ").loadClass ("+ className + ") => not found ");
                }
            } finally {
                dismiss(this, className);
            }
            if ( result != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "class " + className +
                               " loaded with " + getClass().getClassLoader() );
                    log.trace(" XCALL (" + getClass().getClassLoader() +
                              ").loadClass ("+ className + ") => found");
                }
            }

            return result;
        }

        public InputStream loadResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }

            attempt(this, name);
            InputStream is = null;
            try {
                is = getClass().getClassLoader().getResourceAsStream( name );
            } finally {
                dismiss(this, name);
            }

            if( is != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning resource " + name +
                        " loaded with current classloader" );
                }
                return is;
            }

            return null;
        }
    }

    /**
     * ProxyClassloader delegates loading of classes and
     *  resources to the context classloader of the invoking Thread
     */
    class ThreadContextLoader extends ProxyClassLoader {
        private final Logger log =
            Logger.getLogger ( ThreadContextLoader.class );
        public ThreadContextLoader() {
            order = 4;
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            ClassLoader threadCL =
                Thread.currentThread().getContextClassLoader();

            attempt(this, name);
            Enumeration<URL> eurl = null;
            try {
                eurl = threadCL.getResources(name);
            } finally {
                dismiss(this, name);
            }
            return eurl;
        }

        public URL findResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            ClassLoader threadCL =
                Thread.currentThread().getContextClassLoader();
            Enumeration<URL> eurl;
            attempt(this, name);
            try {
                eurl = threadCL.getResources(name);
            } catch (IOException e) {
                eurl = null;
            } finally {
                dismiss(this, name);
            }
            if (eurl == null) return null;
            while(eurl.hasMoreElements()) {
                URL url = eurl.nextElement();
                if (url.toString().endsWith(name)) {
                    return url;
                }
            }
            return null;
        }

        public Enumeration<URL> findResources(String name) throws IOException {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            ClassLoader threadCL =
                Thread.currentThread().getContextClassLoader();
            attempt(this, name);
            Enumeration<URL> eurl = null;
            try {
                eurl = threadCL.getResources(name);
            } finally {
                dismiss(this, name);
            }
            return eurl;
        }

        public URL getResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            ClassLoader threadCL = Thread.currentThread().
                                          getContextClassLoader();

            attempt(this, name);
            URL resource = null;
            try {
                resource = threadCL.getResource(name);
            } finally {
                dismiss(this, name);
            }
            if (log.isTraceEnabled()) {
                log.trace( "resource " + name +
                    " loaded with thread-context classloader" );
            }
            return resource;
        }

        public Class loadClass(String className, boolean resolveIt) {
            ClassLoader threadCL = Thread.currentThread().
                getContextClassLoader();

            if (log.isTraceEnabled()) {
                log.trace("XCALL (" + threadCL + ").loadClass(" +
                    className + ") =>");
            }
            if (isAlreadyAttempted(this, className)) {
                if (log.isTraceEnabled()) {
                    log.trace("XCALL (" + threadCL + ").loadClass(" +
                        className + ") => not found (attempted)");
                }
                return null;

            }
            Class result;
            attempt(this, className);
            try {
                result = threadCL.loadClass( className );
            } catch (ClassNotFoundException e) {
                result = null;
                if (log.isTraceEnabled()) {
                    log.trace("XCALL (" + threadCL + ").loadClass(" +
                        className + ") => not found");
                }
            } finally {
                dismiss(this, className);
            }

            if ( result != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning class " + className +
                               " loaded with "  + threadCL );
                    log.trace("XCALL (" + threadCL + ").loadClass(" +
                        className + ") => found");
                }
            }

            return result;
        }

        public InputStream loadResource(String name) {
            if (isAlreadyAttempted(this, name)) {
                return null;
            }
            ClassLoader threadCL = Thread.currentThread().
                                          getContextClassLoader();
            attempt(this, name);
            InputStream is = null;
            try {
                 is = threadCL.getResourceAsStream( name );
            } finally {
                dismiss(this, name);
            }
            if( is != null ) {
                if (log.isTraceEnabled()) {
                    log.trace( "Returning resource " + name +
                        " loaded with thread context classloader" );
                }
                return is;
            }
            return null;
        }

    }


    public ProxyClassLoader getSystemLoader() {
        return systemLoader;
    }

    public ProxyClassLoader getParentLoader() {
        return parentLoader;
    }

    public ProxyClassLoader getCurrentLoader() {
        return currentLoader;
    }

    public ProxyClassLoader getThreadLoader() {
        return currentLoader;
    }

}
