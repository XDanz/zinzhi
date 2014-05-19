package com.tailf.controller;



public class HAControllerException extends Exception {


    public HAControllerException ( String msg ) {
        super ( msg );
    }

    public HAControllerException ( Throwable e ) {
        super ( e );
    }

}