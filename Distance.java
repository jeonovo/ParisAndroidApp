/*
 * Distance.java 1.0.0 04 May 2015
 *
 * Copyright (c) School of Geography.
 * University of Leeds, Leeds, West Yorkshire, UK. LS2 9JT
 * All rights reserved.
 *
 * This code is provided under the Academic Academic Free License v. 3.0.
 * For details, please see the http://www.opensource.org/licenses/AFL-3.0.
 */

package jonny.map.mymapapp;

/**
 * Class which is instantiated into a new object to be put into both the full cafe and favourites ArrayLists.
 */
public class Distance {

    // Declaring variables
    private double distance;
    private int id;
    private String cafe;

    /**
     * Set the distance between user and cafe.
     * @param distance
     */
    public void setDistance(double distance){
        this.distance = distance;
    }

    /**
     * Gets the distance between user and cafe.
     * @return distance value
     */
    public double getDistance(){
        return distance;
    }

    /**
     * Gets the name of the cafe
     * @return The cafe name
     */
    public String getCafe() {
        return cafe;
    }

    /**
     * Sets the name of the cafe
     * @param cafe
     */
    public void setCafe(String cafe) {
        this.cafe = cafe;
    }

    /**
     * Sets the ID number of the cafe
     * @param id
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Gets the name of the cafe
     * @return cafe ID number
     */
    public int getId(){
        return id;
    }

}
