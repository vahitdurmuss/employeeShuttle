package com.example.vahitdurmuss.employeeshuttle.Shuttle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vahitdurmuss on 31/03/2017.
 */
public class Shuttle {
    // Constructor to convert JSON object into a Java class instance
    public int shuttle_ID;
    public String shuttle_Plate;
    public String shuttle_From;
    public String shuttle_To;
    public int location_ID;

    public Shuttle(JSONObject object){
        try {
            this.shuttle_ID=object.getInt("shuttle_ID");
            this.shuttle_Plate = object.getString("shuttle_Plate");
            this.shuttle_From=object.getString("shuttle_From");
            this.shuttle_To=object.getString("shuttle_To");
            this.location_ID=object.getInt("location_ID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    public static ArrayList<Shuttle> fromJson(JSONArray jsonObjects) {
        ArrayList<Shuttle> shuttles = new ArrayList<Shuttle>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                shuttles.add(new Shuttle(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return shuttles;
    }
}
