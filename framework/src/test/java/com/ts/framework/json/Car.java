package com.ts.framework.json;

import java.util.HashMap;

/**
 * @author wl
 */
public class Car {
    private int id;
    private String name;
    private HashMap<Integer, Integer> map;

    public Car(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, Integer> map) {
        this.map = map;
    }
}
