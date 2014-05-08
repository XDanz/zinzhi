package com.tailf.controller;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;


public class HAControllerRequest implements Serializable {
    private int BUFSIZE = 20 * 1024 * 1024;
    private Object message;

    public HAControllerRequest (Object message ) {
        this.message = message;
    }

    public Object getMessage() {
        return this.message;
    }

    public byte[] encode () throws IOException {
        
        ByteArrayOutputStream bos = 
            new ByteArrayOutputStream (BUFSIZE);
        
        ObjectOutputStream oos = 
            new ObjectOutputStream ( bos );
        oos.flush();
        
        oos.writeObject ( message );
        oos.close();
        return bos.toByteArray();
    }
}