package com.fuel.my.myfuel4;

import java.util.ArrayList;
import java.util.List;

public class Precios {
    private String place_id= null;
    private String regular = null;
    private String premium = null;
    private String diesel = null;
    private String actualizacion=null;

    public static List<Precios> precios = new ArrayList<>();

    public Precios(
            String place_id,
            String regular,
            String premium,
            String diesel,
            String actualizacion
    ){
        this.place_id=place_id;
        this.regular=regular;
        this.premium=premium;
        this.diesel=diesel;
        this.actualizacion=actualizacion;
    }

    public String getPlace_id(){
        return place_id;
    }

    public String getRegular(){
        return  regular;
    }

    public String getPremium(){
        return premium;
    }

    public String getDiesel(){
        return diesel;
    }

    public String getActualizacion(){
        return actualizacion;
    }
}
