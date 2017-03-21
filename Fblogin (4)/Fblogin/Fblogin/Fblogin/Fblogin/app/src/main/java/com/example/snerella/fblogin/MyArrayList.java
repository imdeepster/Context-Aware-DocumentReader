package com.example.snerella.fblogin;

import java.util.ArrayList;

/**
 * Created by pooji on 11/13/2016.
 */
public class MyArrayList extends ArrayList<Integer> {

    public void insert(int x){
        // loop through all elements
        for (int i = 0; i < this.size(); i++) {
            // if the element you are looking at is smaller than x,
            // go to the next element
            if (get(i) < x) continue;
            // if the element equals x, return, because we don't add duplicates
            if (get(i) == x) return;
            // otherwise, we have found the location to add x
            add(i, x);
            return;
        }


        // we looked through all of the elements, and they were all
        // smaller than x, so we add ax to the end of the list
        add(x);
    }






    public int lowerValueOfPage(int value)
    {
        int pageValue;
        int returnValue = -1;
        pageValue = value;
        if(size()>0) {
            for (int i = 0; i < size(); i++) {
                if (get(i) < pageValue) {
                    returnValue = get(i);
                }
                else
                    break;
                // pageValue = returnValue;
            }

        }    return returnValue;
    }

    public int greaterValueOfPage(int value)
    {
        int pageValue;
        int returnValue = -1;
        pageValue = value;
        if(size()>0) {
            for (int i = size()-1; i >=0 ; i--) {
                if (get(i) > pageValue) {
                    returnValue = get(i);
                }
                else
                    break;
                // pageValue = returnValue;
            }

        }    return returnValue;
    }





}
