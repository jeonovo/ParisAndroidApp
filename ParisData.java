/*
 * ParisData.java 1.0.0 04 May 2015
 *
 * Copyright (c) School of Geography.
 * University of Leeds, Leeds, West Yorkshire, UK. LS2 9JT
 * All rights reserved.
 *
 * This code is provided under the Academic Academic Free License v. 3.0.
 * For details, please see the http://www.opensource.org/licenses/AFL-3.0.
 */

package jonny.map.mymapapp;

import java.util.ArrayList;

/**
 * Singleton class so there is only one instance of this storage arraylist from which all the data in the app is taken from.
 */
public class ParisData  {

    // ArrayList where all the data is stored
    ArrayList<Storage> list = new ArrayList<>();

    // Create singleton variable
    private static final ParisData pData = new ParisData();

    // Get the instance of the class
    public static ParisData getInstance() {

        return pData;

    }

    /**
     * Creates an object of the storage class with the parameters then adds the object to the arraylist.
     * @param cafe
     * @param address
     * @param lat
     * @param lng
     * @param id
     */
    public void setData(String cafe, String address,  double lat, double lng, int id){

        Storage place = new Storage();

        place.setName(cafe);

        place.setAddress(address);

        place.setLat(lat);

        place.setLng(lng);

        place.setId(id);

        list.add(place);

    }




}
