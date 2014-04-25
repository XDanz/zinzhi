package com.tailf.test.maapi.phases;

public class Test2{
    public static void main(String args[]) {
        int i;

        System.err.println("---------------------------------------");
        System.err.println("Java MAAPI Asynchronous Phase tests");
        System.err.println("---------------------------------------");

        org.junit.runner.JUnitCore.main(PhaseTest2.class.getName());

    }

}