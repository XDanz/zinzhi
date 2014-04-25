package com.tailf.test.maapi.config;

public class Test3{
    public static void main(String args[]) {
        int i;

        System.err.println("---------------------------------------");
        System.err.println("Java MAAPI reload_config() Test");
        System.err.println("---------------------------------------");

        org.junit.runner.JUnitCore.main(ConfigTest.class.getName());

    }

}