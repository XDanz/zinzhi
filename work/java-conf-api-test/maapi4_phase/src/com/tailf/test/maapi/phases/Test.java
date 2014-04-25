package com.tailf.test.maapi.phases;

public class Test{
    public static void main(String args[]) {
        int i;

        System.err.println("---------------------------------------");
        System.err.println("Java MAAPI Synchronous Phase tests");
        System.err.println("---------------------------------------");

        org.junit.runner.JUnitCore.main(PhaseTest.class.getName());

    }

}