package com.ysoft.dctrl.utils;

import java.util.LinkedList;

/**
 * Created by kuhn on 10/31/2017.
 */
public class LimitedSizeQueue<T> extends LinkedList<T> {
    private int limit;

    public LimitedSizeQueue(int limit){
        this.limit = limit;
    }

    @Override
    public void addFirst(T t) {
        if(size() < limit){
            super.addFirst(t);
        }
        else {
            while(size() >= limit) {
                super.removeLast();
            }
            super.addFirst(t);
        }
    }
}
