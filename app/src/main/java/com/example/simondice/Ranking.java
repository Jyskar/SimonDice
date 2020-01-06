package com.example.simondice;

import java.io.Serializable;

public class Ranking implements Comparable<Ranking>, Serializable {
    private String name;
    private long points;

    public Ranking(String name, long points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    @Override
    public int compareTo(Ranking ranking) {
        Long p = getPoints();
        return p.compareTo(ranking.getPoints());

    }

}
