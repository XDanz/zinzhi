package com.jarclassloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Logger;

/**
 * JarResources reads jar files and loads the class
 * content/bytes in a HashMap
 *
 *
 */
public class JarResources {

    protected Map<String, byte[]> jarEntryContents;
    protected Map<String, ArrayList<URL>> jarEntryLocations;

    protected Map<String, URL> jars;
    protected boolean collisionAllowed = true;

    private static Logger logger = Logger.getLogger( JarResources.class );

    /**
     * Default constructor
     */
    public JarResources() {
        jarEntryContents = new HashMap<String, byte[]>();
        jarEntryLocations = new HashMap<String, ArrayList<URL>>();
        jars = new HashMap<String, URL>();
    }

    /**
     * @param name
     * @return byte[]
     */
    public byte[] getResource(String name) {
        return jarEntryContents.get( name );
    }

    /**
     * Finds the resource with the given name.
     *
     * <p> The name of a resource is a '<tt>/</tt>'-separated path name that
     * identifies the resource.
     *
     * <p> This method will search its repository for the
     * resource returns the URL object if the resource name was found
     * null otherwise.
     *
     * @param  name
     *         The resource name
     *
     * @return  A <tt>URL</tt> object for reading the resource, or
     *          <tt>null</tt> if the resource could not be found.
     *
     */
    public URL getURLResource(String name) {
        URL url = null;

        if ( jars.containsKey ( name ) ) {

            String theUrl = "jar:" + jars.get(name) + "!/" + name;
            try {
                url = new URL(theUrl);
            } catch(MalformedURLException e) {
                url = null;
            }
        }
        return url;
    }

    public ArrayList<URL> getURLResources(String name){

        ArrayList<URL> jurls = jarEntryLocations.get(name);
        if (jurls == null) {
            return null;
        }
        ArrayList<URL> resultUrls = new ArrayList<URL>();
        for (URL url : jurls) {
            String theUrl = "jar:" + url.toString()+ "!/" + name;
            URL rurl = null;
            try {
                rurl = new URL(theUrl);
            } catch (MalformedURLException e) {
                logger.error("",e);
            }
            if (rurl != null) {
                resultUrls.add(rurl);
            }
        }
        return resultUrls;
    }

    /**
     * Returns an immutable Map of all jar resources
     *
     * @return Map
     */
    public Map<String, byte[]> getResources() {
        return Collections.unmodifiableMap( jarEntryContents );
    }

    /**
     * Reads the specified jar file
     *
     * @param jarFile
     */
    public void loadJar(String jarFile) {
        if( logger.isTraceEnabled() ) {
            logger.trace( "Loading jar: " + jarFile );
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream( jarFile );

            loadJar( fis,new File(new File(jarFile).getAbsolutePath())
                     .toURI().toURL() );
        } catch (IOException e) {
            throw new JclRuntimeException( e );
        } finally {
            if( fis != null )
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new JclRuntimeException( e );
                }
        }
    }

    /**
     * Reads the jar file from a specified URL
     *
     * @param url
     */
    public void loadJar(URL url) {

        if( logger.isTraceEnabled() ) {
            logger.trace( "Loading jar: " + url.toString() );
        }
        InputStream in = null;
        try {
            in = url.openStream();
            loadJar( in ,url);
        } catch (IOException e) {
            throw new JclRuntimeException( e );
        } finally {
            if( in != null )
                try {
                    in.close();
                } catch (IOException e) {
                    throw new JclRuntimeException( e );
                }
        }
    }

    /**
     * Load the jar contents from InputStream
     *
     */
    public void loadJar(InputStream jarStream,URL url) {

        BufferedInputStream bis = null;
        JarInputStream jis = null;

        try {
            bis = new BufferedInputStream( jarStream );
            jis = new JarInputStream( bis );

            JarEntry jarEntry = null;
            while (( jarEntry = jis.getNextJarEntry() ) != null) {
                if( logger.isTraceEnabled() ) {
                    logger.trace( dump( jarEntry ) );
                }
                if( jarEntry.isDirectory() ) {
                    continue;
                }

                ArrayList<URL> jurls =
                    jarEntryLocations.get(jarEntry.getName());
                if (jurls == null) {
                    jurls = new ArrayList<URL>();
                    jarEntryLocations.put(jarEntry.getName(), jurls);
                }
                jurls.add(url);

                if( jarEntryContents.containsKey( jarEntry.getName() ) ) {
                    if( !collisionAllowed )
                        throw new JclRuntimeException( "Class/Resource " +
                           jarEntry.getName() + " already loaded" );
                    else {
                        if( logger.isTraceEnabled() ) {
                            logger.trace( "Class/Resource " +
                                          jarEntry.getName()
                                   + " already loaded; ignoring entry..." );
                        }
                        continue;
                    }
                }

                if( logger.isTraceEnabled() ) {
                    logger.trace( "Entry Name: " + jarEntry.getName() + ", "
                                  + "Entry Size: " + jarEntry.getSize() );
                }

                byte[] b = new byte[2048];
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int len = 0;
                while (( len = jis.read( b ) ) > 0) {
                    out.write( b, 0, len );
                }

                // add to internal resource HashMap
                jarEntryContents.put( jarEntry.getName(), out.toByteArray() );
                jars.put(jarEntry.getName(), url);

                if( logger.isTraceEnabled() ) {
                    logger.trace( jarEntry.getName() + ": size=" +
                                  out.size() + " ,csize="
                                  + jarEntry.getCompressedSize() );
                }
                out.close();
            }
        } catch (IOException e) {
            throw new JclRuntimeException( e );
        } catch (NullPointerException e) {
            if( logger.isTraceEnabled() ) {
                logger.trace( "Done loading." );
            }
        } finally {
            if( jis != null )
                try {
                    jis.close();
                } catch (IOException e) {
                    throw new JclRuntimeException( e );
                }

            if( bis != null )
                try {
                    bis.close();
                } catch (IOException e) {
                    throw new JclRuntimeException( e );
                }
        }

    }

    /**
     * For debugging
     *
     * @param je
     * @return String
     */
    private String dump(JarEntry je) {
        StringBuffer sb = new StringBuffer();
        if( je.isDirectory() ) {
            sb.append( "d " );
        } else {
            sb.append( "f " );
        }

        if( je.getMethod() == JarEntry.STORED ) {
            sb.append( "stored   " );
        } else {
            sb.append( "defalted " );
        }

        sb.append( je.getName() );
        sb.append( "\t" );
        sb.append( "" + je.getSize() );
        if( je.getMethod() == JarEntry.DEFLATED ) {
            sb.append( "/" + je.getCompressedSize() );
        }

        return ( sb.toString() );
    }
}
