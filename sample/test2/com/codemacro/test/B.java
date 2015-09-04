package com.codemacro.test;

import com.codemacro.container.IBundleInitializer;
import com.codemacro.container.BundleContext;

public class B implements IBundleInitializer {
    static {
        System.out.println("====B====");
        Base b = new Base();
        b.print();
        System.out.println(B.class.getClassLoader());
        System.out.println("====B====");
    }

    public void start(BundleContext context) {
        System.out.println("B start: " + context.getName());
        new A();
    }

    public void stop(BundleContext context) {
        System.out.println("B stop: " + context.getName());
    }
}

