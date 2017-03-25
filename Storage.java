/*
 * Storage.java 1.0.0 04 May 2015
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
 * Class is made into an object and stored within an ArrayList for each object within the JSON array.
 */
public class Storage  {

    // Declaring the variables
    private String name;
    private String address;
    private double lat;
    private double lng;
    private int id;

    /**
     * Gets the name of the cafe.
     * @return cafe name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the cafe
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Latitude of the cafe.
     * @return the Latitude of the cafe
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets the Latitude of the cafe.
     * @param lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Gets the Longitude of the cafe.
     * @return the Longitude of the cafe.
     */
    public double getLng() {
        return lng;
    }

    /**
     * Sets the Longitude of the cafe.
     * @param lng
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    /**
     * Gets the cafe address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the cafe address.
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the cafe id.
     * @return id number.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the cafe id number.
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


}
