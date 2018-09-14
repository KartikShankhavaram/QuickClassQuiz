package com.imad.quickclassquiz.utils;

import com.imad.quickclassquiz.dataModel.Test;

public class StaticValues {
    private static Test currentTest;

    public static Test getCurrentTest() {
        return currentTest;
    }

    public static void setCurrentTest(Test currentTest) {
        StaticValues.currentTest = currentTest;
    }

    public static void clearTest() {
        currentTest = null;
    }
}
