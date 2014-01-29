package com.jarclassloader;


public class JclCreationCheckedException extends JclCheckedException {
    private ClassLoader classLoader;
    private String className;
    private Object[] args;

    /**
     * Default serial id
     */
    private static final long serialVersionUID = 1L;

    public JclCreationCheckedException(Throwable cause,
                                       JclCreationErrorCode errorCode,
                                       ClassLoader classLoader,
                                       String className,
                                       Object... args){
        super(cause);
        this.classLoader = classLoader;
        this.className = className;
        this.args = args;
    }
}
