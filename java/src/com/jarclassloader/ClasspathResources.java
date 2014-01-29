package com.jarclassloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Class that builds a local classpath by loading resources from different
 * files/paths
 *
 *
 */
public class ClasspathResources
    extends JarResources {

    private static Logger logger =
        Logger.getLogger( ClasspathResources.class );
    private boolean ignoreMissingResources = true;

    public ClasspathResources() {
        super();
    }

    /**
     * Reads the resource content
     *
     * @param resource
     */
    private void loadResourceContent(String resource, String pack) {
        File resourceFile = new File( resource );
        String entryName = "";
        FileInputStream fis = null;
        byte[] content = null;
        try {
            fis = new FileInputStream( resourceFile );
            content = new byte[(int) resourceFile.length()];

            if( fis.read( content ) != -1 ) {

                if( pack.length() > 0 ) {
                    entryName = pack + "/";
                }

                entryName += resourceFile.getName();

                if( jarEntryContents.containsKey( entryName ) ) {
                    if( !collisionAllowed )
                        throw new JclRuntimeException( "Resource " +
                                                       entryName +
                                                       " already loaded" );
                    else {
                        if( logger.isTraceEnabled() ) {
                            logger.trace( "Resource " +
                                          entryName +
                                    " already loaded; ignoring entry..." );
                        }
                        return;
                    }
                }

                if( logger.isTraceEnabled() ) {
                    logger.trace( "Loading resource: " + entryName );
                }
                jarEntryContents.put( entryName, content );
            }
        } catch (IOException e) {
            throw new JclRuntimeException( e );
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                throw new JclRuntimeException( e );
            }
        }
    }

    /**
     * Attempts to load a remote resource (jars, properties files, etc)
     *
     * @param url
     */
    private void loadRemoteResource(URL url) {
        if( logger.isTraceEnabled() ) {
            logger.trace( "Attempting to load a remote resource." );
        }
        if( url.toString().toLowerCase().endsWith( ".jar" ) ) {
            loadJar( url );
            return;
        }

        InputStream stream = null;
        ByteArrayOutputStream out = null;
        try {
            stream = url.openStream();
            out = new ByteArrayOutputStream();

            int byt;
            while (( ( byt = stream.read() ) != -1 )) {
                out.write( byt );
            }

            byte[] content = out.toByteArray();

            if( jarEntryContents.containsKey( url.toString() ) ) {
                if( !collisionAllowed )
                    throw new JclRuntimeException( "Resource " +
                                                   url.toString() +
                                                   " already loaded" );
                else {
                    if( logger.isTraceEnabled() ) {
                        logger.trace( "Resource " + url.toString() +
                                      " already loaded; ignoring entry..." );
                    }
                    return;
                }
            }

            if( logger.isTraceEnabled() ) {
                logger.trace( "Loading remote resource." );
            }
            jarEntryContents.put( url.toString(), content );
        } catch (IOException e) {
            throw new JclRuntimeException( e );
        } finally {
            if( out != null )
                try {
                    out.close();
                } catch (IOException e) {
                    throw new JclRuntimeException( e );
                }
            if( stream != null )
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new JclRuntimeException( e );
                }
        }
    }

    /**
     * Reads the class content
     *
     * @param clazz
     * @param pack
     */
    private void loadClassContent(String clazz, String pack) {
        File cf = new File( clazz );
        FileInputStream fis = null;
        String entryName = "";
        byte[] content = null;

        try {
            fis = new FileInputStream( cf );
            content = new byte[(int) cf.length()];

            if( fis.read( content ) != -1 ) {
                entryName = pack + "/" + cf.getName();

                if( jarEntryContents.containsKey( entryName ) ) {
                    if( !collisionAllowed )
                        throw new JclRuntimeException( "Class " + entryName +
                                                " already loaded" );
                    else {
                        if( logger.isTraceEnabled() ) {
                            logger.trace( "Class " + entryName +
                                      " already loaded; ignoring entry..." );
                        }
                        return;
                    }
                }

                if( logger.isTraceEnabled() ) {
                    logger.trace( "Loading class: " + entryName );
                }
                jarEntryContents.put( entryName, content );
            }
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
     * Reads local and remote resources
     *
     * @param url
     */
    public void loadResource(URL url) {
        try {
            // Is Local
            loadResource( new File( url.toURI() ), "" );
        } catch (IllegalArgumentException iae) {
            // Is Remote
            loadRemoteResource( url );
        } catch (URISyntaxException e) {
            throw new JclRuntimeException( "URISyntaxException", e );
        }
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     *
     * @param path
     */
    public void loadResource(String path) {
        if( logger.isTraceEnabled() ) {
            logger.trace( "Resource: " + path );
        }
        File fp = new File( path );

        if( !fp.exists() && !ignoreMissingResources ) {
            throw new JclRuntimeException( "File/Path does not exist" );
        }

        loadResource( fp, "" );
    }

    /**
     * Reads local resources from - Jar files - Class folders - Jar Library
     * folders
     *
     * @param fol
     * @param packName
     */
    private void loadResource(File fol, String packName) {
        if( fol.isFile() ) {
            if( fol.getName().toLowerCase().endsWith( ".class" ) ) {
                loadClassContent( fol.getAbsolutePath(), packName );
            } else {
                if( fol.getName().toLowerCase().endsWith( ".jar" ) ) {
                    loadJar( fol.getAbsolutePath());

                } else {
                    loadResourceContent( fol.getAbsolutePath(), packName );
                }
            }

            return;
        }

        if( fol.list() != null ) {
            for( String f : fol.list() ) {
                File fl = new File( fol.getAbsolutePath() + "/" + f );

                String pn = packName;

                if( fl.isDirectory() ) {

                    if( !pn.equals( "" ) )
                        pn = pn + "/";

                    pn = pn + fl.getName();
                }

                loadResource( fl, pn );
            }
        }
    }

    /**
     * Removes the loaded resource
     *
     * @param resource
     */
    public void unload(String resource) {
        if( jarEntryContents.containsKey( resource ) ) {
            if( logger.isTraceEnabled() ) {
                logger.trace( "Removing resource " + resource );
            }
            jarEntryContents.remove( resource );
        } else {
            throw new ResourceNotFoundException( resource,
                          "Resource not found in local ClasspathResources" );
        }
    }

    public boolean isCollisionAllowed() {
        return collisionAllowed;
    }

    public void setCollisionAllowed(boolean collisionAllowed) {
        this.collisionAllowed = collisionAllowed;
    }

    public boolean isIgnoreMissingResources() {
        return ignoreMissingResources;
    }

    public void setIgnoreMissingResources(boolean ignoreMissingResources) {
        this.ignoreMissingResources = ignoreMissingResources;
    }
}
