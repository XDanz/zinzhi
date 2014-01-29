package com.jarclassloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;

public abstract class ProxyClassLoader 
    implements Comparable<ProxyClassLoader> {
    // Default order
    protected int order = 5;
    // Enabled by default
    protected boolean enabled = true;
    protected HashSet<String> currentLoadAttempts = new HashSet<String>();

    public int getOrder() {
        return order;
    }

    /**
     * Set loading order
     *
     * @param order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Loads the class
     *
     * @param className
     * @param resolveIt
     * @return class
     */
    public abstract Class loadClass(String className,
                                    boolean resolveIt);
    public abstract URL getResource(String name);
    //public abstract String getName();

    /**
     * Loads the resource
     *
     * @param name
     * @return InputStream
     */
    public abstract InputStream loadResource(String name);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int compareTo(ProxyClassLoader o) {
        return order - o.getOrder();
    }

    public abstract Enumeration<URL> getResources(String name)
        throws IOException;

    public abstract URL findResource(String name);

    public abstract Enumeration<URL> findResources(String name)
        throws IOException;

    synchronized protected void attempt(ProxyClassLoader cl, String className){
        currentLoadAttempts.add(cl.toString() + className);
    }

    synchronized protected void dismiss(ProxyClassLoader cl, String className){
        currentLoadAttempts.remove(cl.toString() + className);
    }

    synchronized protected boolean isAlreadyAttempted(ProxyClassLoader cl,
                                                      String className) {
        return currentLoadAttempts.contains(cl.toString() + className);
    }

}
