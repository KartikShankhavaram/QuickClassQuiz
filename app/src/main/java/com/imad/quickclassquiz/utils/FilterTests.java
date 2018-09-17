package com.imad.quickclassquiz.utils;

import com.imad.quickclassquiz.dataModel.Test;

import java.util.ArrayList;

public class FilterTests {

    public static ArrayList<Test> getStartedTestList(ArrayList<Test> list) {
        ArrayList<Test> filteredList = new ArrayList<>();
        for(Test test: list) {
            if(test.getMasterCode() != null && test.getAccessCode() != null)
                filteredList.add(test);
        }
        return filteredList;
    }

    public static ArrayList<Test> getUpcomingTestList(ArrayList<Test> list) {
        ArrayList<Test> filteredList = new ArrayList<>();
        for(Test test: list) {
            if(test.getMasterCode() == null && test.getAccessCode() == null)
                filteredList.add(test);
        }
        return filteredList;
    }
}
