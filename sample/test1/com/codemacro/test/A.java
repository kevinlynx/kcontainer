package com.codemacro.test;

public class A {
    static {
        System.out.println("====A====");
        System.out.println(A.class.getClassLoader());
        Base b = new Base();
        b.print();
        Export e = new Export();
        e.print();
        System.out.println("====A====");
    }
}

