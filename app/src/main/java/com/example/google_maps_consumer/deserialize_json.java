package com.example.google_maps_consumer;

import android.util.Log;
import android.util.Patterns;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class deserialize_json {

    private Float distance_sum;


    public deserialize_json()
    {
        distance_sum=0f;
    }


    public void process_json_Cad(String cad_json)
    {
        try {
            String distance=new JSONObject(cad_json).
                    getJSONArray("rows").
                    getJSONObject(0).
                    getJSONArray("elements").
                    getJSONObject(0).
                    getJSONObject("distance").getString("value");
                    Log.i("distancepepilla",distance);
                    Pattern p =Pattern.compile("[\\d.]+");
                    Matcher m=p.matcher(distance);
                    m.find();
                    if(distance.contains("km")){
                        distance_sum+=Float.parseFloat(m.group())*1000;
                    }else
                        distance_sum+=Float.parseFloat(m.group());
                    Log.i("clave",distance_sum.toString());

        }catch (Exception ex){
            Log.i("Error",ex.getMessage());
        }
    }

    public Float getDistance_sum() {
        return distance_sum;
    }

    public void setDistance_sum(Float distance_sum) {
        this.distance_sum = distance_sum;
    }
}
