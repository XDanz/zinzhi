package com.tailf.test.maapi1;

import org.apache.log4j.Logger;
//import com.tailf.test.navu.TestNavuContainer;
//import com.tailf.test.navu.TestNavuList;

import org.junit.runner.*;

import org.apache.log4j.Logger;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;
//import com.tailf.ncs.ns.NcsAlarms;

public class TestExtra{

    private static Logger LOGGER =
        Logger.getLogger(TestExtra.class);

    public static void main(String[] arg){
        org.junit.runner.JUnitCore.main(ExtraTest1.class.getName(),
                                        ExtraTest2.class.getName());

    }


}