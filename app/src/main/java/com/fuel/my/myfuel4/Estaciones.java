package com.fuel.my.myfuel4;
import java.util.ArrayList;
import java.util.List;

public class Estaciones {


    /**
     * Clase que representa cada hotel del archivo XML
     */
        private String place_id;
        private String name;
        private String brand;
        private String x;
        private String y;
        private String cre_id;
        private String category;
        private String address_street;
        private Precios miPrecio;


        // Proveedor estático de datos para el adaptador
        public static List<Estaciones> estaciones = new ArrayList<>();

        public Estaciones(String place_id,
                     String name,
                     String brand,
                     String  x,
                     String y,
                     String cre_id,
                     String category,
                     String address_street
                          ) {
            this.place_id = place_id;
            this.name = name;
            this.brand = brand;
            this.x = x;
            this.y = y;
            this.cre_id = cre_id;
            this.category = category;
            this.address_street = address_street;
        }

        public String getX() {
            return x;
        }

    public String getY() {
        return y;
    }

        public String getNombre() {
            return name;
        }

        public String getPlace_id() {
            return place_id;
        }

        public String getBrand() {
            return brand;
        }

        public String getCre_id() {
            return cre_id;
        }

        public String getCategory() {
            return category;
        }

        public String getAddress_street() {
            return address_street;
        }

        public void setPrecio(Precios miPrecio){
            this.miPrecio=miPrecio;
        }

}
