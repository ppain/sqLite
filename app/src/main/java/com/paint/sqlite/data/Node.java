package com.paint.sqlite.data;

import java.util.List;

public class Node {
    private long id;
    private int value;
    private List<Node> parentList;
    private List<Node> childList;

    public void setId(long id) {
        this.id = id;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setParentList(Node node) {
        this.parentList.add(node);
    }

    public void setChildList(Node node) {
        this.childList.add(node);
    }

    public long getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public List<Node> getParentList() {
        return parentList;
    }

    public List<Node> getChildList() {
        return childList;
    }
}
