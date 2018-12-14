package com.xebia.demo;

import org.junit.Test;

public class KotlinJavaIntegrationTest {
    @Test
    public void integrateWithKotlin() {
        System.out.println((new FirestoreDemo()).doIt());
    }
}
