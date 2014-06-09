package com.tailf.controller;



public class HAControllerException extends Exception {
    
    public static final long serialVersionUID = 42L;
    
    public HAControllerException ( String msg ) {
        super ( msg );
    }

    public HAControllerException ( Throwable e ) {
        super ( e );
    }

}
