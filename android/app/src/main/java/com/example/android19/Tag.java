package com.example.android19;

import java.io.Serializable;

public class Tag implements Serializable{

    private String name;
    private String location;


    public Tag(String name, String location)
    {
        this.name = name;
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String temp)
    {
        name = temp;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String temp)
    {
        location = temp;
    }
}
