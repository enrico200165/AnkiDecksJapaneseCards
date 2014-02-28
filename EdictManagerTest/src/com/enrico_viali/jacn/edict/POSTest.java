package com.enrico_viali.jacn.edict;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class POSTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testReplaceKE_Inf() {
        POS m = new POS();
        System.out.println(m.replaceKE_Inf("   (ik)  (ok)  (P) (blah)  asassa (P"));
        System.out.println(m.replaceKE_Inf("   (ik  ok)  (P) (blah)  asassa (P"));
        System.out.println(m.replaceKE_Inf("()"));
        System.out.println(m.replaceKE_Inf("a"));
        System.out.println("<" + m.replaceKE_Inf("a") + ">");
        System.out.println("<" + m.replaceKE_Inf("") + ">");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        fail("Not yet implemented"); // TODO
    }

}
