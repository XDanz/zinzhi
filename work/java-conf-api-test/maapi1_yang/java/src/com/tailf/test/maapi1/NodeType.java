package com.tailf.test.maapi1;


public enum NodeType{

    /*
     * Enum to use when decoding flags.
     */

    CS_NODE_IS_DYN     (1 << 0),
        CS_NODE_IS_WRITE   (1 << 1),
        CS_NODE_IS_CDB     (1 << 2),
        CS_NODE_IS_ACTION  (1 << 3),
        CS_NODE_IS_PARAM   (1 << 4),
        CS_NODE_IS_RESULT  (1 << 5),
        CS_NODE_IS_NOTIF   (1 << 6),
        CS_NODE_IS_CASE    (1 << 7);

    private int value;

    NodeType(int bit){
        value= bit;
    }


    /**
     * @return the integer value of the enum.
     */
    public int getValue(){
        return value;
    }

}
