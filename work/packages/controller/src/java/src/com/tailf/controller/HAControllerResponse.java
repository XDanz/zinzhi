package com.tailf.controller;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.IOException;

public class HAControllerResponse implements Serializable {
    private byte[] b;

    public HAControllerResponse (byte[] b ) {
        this.b = b;
    }

    public Object decode () throws IOException , ClassNotFoundException {
          
        ObjectInputStream ois = 
            new ObjectInputStream ( new ByteArrayInputStream(this.b) );
        
        return ois.readObject();
    }
}