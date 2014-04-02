package com.jarclassloader;

/**
 * 
 */
public class JclCheckedException extends Exception {
    /**
     * Default serial id
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Default constructor
     */
    public JclCheckedException() {
        super();
    }

    /**
     * @param message
     */
    public JclCheckedException(String message) {
        super( message );
    }

    /**
     * @param cause
     */
    public JclCheckedException(Throwable cause) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public JclCheckedException(String message, Throwable cause) {
        super( message, cause );
    }
}
