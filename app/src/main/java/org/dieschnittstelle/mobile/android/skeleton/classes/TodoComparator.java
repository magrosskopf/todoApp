package org.dieschnittstelle.mobile.android.skeleton.classes;

import java.util.Comparator;

public class TodoComparator implements Comparator<Todo> {

    public int compare(Todo left, Todo right) {
        return (int) (right.getExpiry() -  left.getExpiry());
    }
}
