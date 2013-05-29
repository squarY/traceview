/**
 * @(#)TestApplication.java, 2013-5-27. Copyright 2013 RenRen, Inc. All rights
 *                           reserved.
 */
package com.renren.traceview;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanyan
 */
public class TestApplication {
    public void test() {
        doTest1();
        doTest2();
    }

    public void doTest1() {
        List<String> s = new ArrayList<String>();
        s.add("test");
    }

    public void doTest2() {
        System.out.println("a list");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void containerMethod() {
        while (true) {
            test();
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TestApplication test = new TestApplication();
        test.containerMethod();
    }
}
