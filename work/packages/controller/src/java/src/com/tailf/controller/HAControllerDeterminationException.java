package com.tailf.controller;



public class HAControllerDeterminationException extends HAControllerException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HAControllerDeterminationException ( String msg ) {
        super ( msg );
    }

    public HAControllerDeterminationException ( ) {
        this ( "Remote/Local HA Node could not be determined" );
    }

}
