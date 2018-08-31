package com.fuel.my.myfuel4;

import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class parserXML {


    /**
     * Parser XML de hoteles
     */

        // Namespace general. null si no existe
        private static final String ns = null;

        // Constantes del archivo Xml
        private static final String ETIQUETA_PLACES = "places";
        private static final String ETIQUETA_PLACE = "place";
        private static final String ETIQUETA_PLACEID = "place_id";
        private static final String ETIQUETA_NAME = "name";

        private static final String ETIQUETA_CRE_ID = "cre_id";
        private static final String ETIQUETA_BRAND = "brand";
        private static final String ETIQUETA_CATEGORY = "category";
        private static final String ETIQUETA_LOCATION = "location";
        private static final String ETIQUETA_ADDRESS_STREET = "address_street";
        private static final String ETIQUETA_X = "x";
        private static final String ETIQUETA_Y = "y";
        private static final String ETIQUETA_GASPRICE ="gas_price";
        private  LatLng miUbicacion = null;

        public parserXML(LatLng miUbicacion){
            this.miUbicacion=miUbicacion;
        }



        /**
         * Parsea un flujo XML a una lista de objetos {@link Estaciones}
         *
         * @param in flujo
         * @return Lista de hoteles
         * @throws XmlPullParserException
         * @throws IOException
         */
        public List<Estaciones> parsear(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
                parser.setInput(in, null);
                parser.nextTag();
                return leerEstaciones(parser);
            } finally {
                in.close();
            }
        }

    public List<Precios> parsearPrecios(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            return leerEstacionPrecios(parser);
        } finally {
            in.close();
        }
    }

    private List<Precios> leerEstacionPrecios(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        List<Precios> listaPrecios = new ArrayList<Precios>();

        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PLACES);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nombreEtiqueta = parser.getName();
            // Buscar etiqueta <hotel>
            if (nombreEtiqueta.equals(ETIQUETA_PLACE)) {

                listaPrecios.add(leerEstaPrecio(parser));


            } else {
                System.out.println("Aqui");
                saltarEtiqueta(parser);
            }
        }
        return listaPrecios;
    }

    private Precios leerEstaPrecio(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PLACE);

        String placeid=null;

        String regular = null;
        String premium = null;
        String diesel=null;
        String actualizacion =null;

        placeid=parser.getAttributeValue(null,ETIQUETA_PLACEID);


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            switch (name) {
                case ETIQUETA_GASPRICE:
                    if("regular".equals(parser.getAttributeValue(null,"type"))){
                        actualizacion= parser.getAttributeValue(null,"update_time");
                        regular= leerPrecio(parser);

                     } else
                     if("premium".equals(parser.getAttributeValue(null,"type"))){
                         premium= leerPrecio(parser);
                     }
                     else{
                        diesel=leerPrecio(parser);
                    }


                    break;

                default:
                    saltarEtiqueta(parser);
                    break;
            }
        }
        return new Precios(placeid,
                regular,
                premium,
                diesel,
                actualizacion);
    }

    private String leerPrecio(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_GASPRICE);
        String precio = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_GASPRICE);
        return precio;
    }


    /**
         * Convierte una serie de etiquetas <hotel> en una lista
         *
         * @param parser
         * @return lista de hoteles
         * @throws XmlPullParserException
         * @throws IOException
         */
        private List<Estaciones> leerEstaciones(XmlPullParser parser)
                throws XmlPullParserException, IOException {
            List<Estaciones> listaEstaciones = new ArrayList<Estaciones>();

            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PLACES);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String nombreEtiqueta = parser.getName();
                // Buscar etiqueta <hotel>
                if (nombreEtiqueta.equals(ETIQUETA_PLACE)) {

                        listaEstaciones.add(leerEstacion(parser));
                        if(listaEstaciones.get(listaEstaciones.size()-1).getX()==null ||listaEstaciones.get(listaEstaciones.size()-1).getY()==null  )
                            listaEstaciones.remove(listaEstaciones.size()-1);

                } else {
                    System.out.println("Aqui");
                    saltarEtiqueta(parser);
                }
            }
            return listaEstaciones;
        }

        /**
         * Convierte una etiqueta <hotel> en un objero Hotel
         *
         * @param parser parser XML
         * @return nuevo objeto Hotel
         * @throws XmlPullParserException
         * @throws IOException
         */
        private Estaciones leerEstacion(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PLACE);
            String idCree = null;
            String nombre = null;
            String brand = null;
            String x= null;
            String y=null;
            String address = null;
            String placeid=null;

            String category=null;

            placeid=parser.getAttributeValue(null,ETIQUETA_PLACEID);


            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();

                switch (name) {
                    case ETIQUETA_CRE_ID:
                        idCree = leeridCree(parser);
                        break;
                    case ETIQUETA_NAME:
                        nombre = leerName(parser);
                        break;
                    case ETIQUETA_BRAND:
                        brand = leerBrand(parser);
                        break;
                    case ETIQUETA_CATEGORY:
                        category = leerCategory(parser);
                        break;
                    /*case ETIQUETA_ADDRESS_STREET:
                        address = leerAddress(parser);
                        break;*/
                    /*case ETIQUETA_X:
                        x = leerX(parser);
                        break;
                    case ETIQUETA_Y:
                        y = leerY(parser);
                        break;
                        */
                    case ETIQUETA_LOCATION:
                        /*System.out.println("Holaaaa");
                        Log.e("test","entre");
                        x= leerX(parser);
                        System.out.println(x);
                        y=leerY(parser);
                        address = leerAddress(parser);*/
                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }
                            String name2 = parser.getName();

                            switch (name2) {
                                case ETIQUETA_ADDRESS_STREET:
                                    address = leerAddress(parser);
                                    break;

                                case ETIQUETA_X:
                                    x=leerX(parser);
                                    if(Double.parseDouble(x)<=miUbicacion.longitude-0.1 || Double.parseDouble(x)>=miUbicacion.longitude+0.1)
                                        x=null;

                                    break;
                                case ETIQUETA_Y:
                                    y=leerY(parser);
                                    if(Double.parseDouble(y)<=miUbicacion.latitude-0.1 || Double.parseDouble(y)>=miUbicacion.latitude+0.1)
                                        y=null;
                                    break;
                                    default:
                                        saltarEtiqueta(parser);
                                        break;
                            }
                        }

                            break;
                    default:
                        saltarEtiqueta(parser);
                        break;
                }
            }
            return new Estaciones(placeid,
                    nombre,
                    brand,
                    x,
                    y,
                    idCree,
                    category,
                    address);
        }

        // Procesa la etiqueta <idHotel> de los hoteles
        private String leeridCree(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_CRE_ID);
            String idCree = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_CRE_ID);
            return idCree;
        }

        // Procesa las etiqueta <nombre> de los hoteles
        private String leerName(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_NAME);
            String nombre = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_NAME);
            return nombre;
        }

        // Procesa la etiqueta <precio> de los hoteles
        private String leerCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_CATEGORY);
            String category = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_CATEGORY);
            return category;
        }

    // Procesa la etiqueta <precio> de los hoteles
    private String leerAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_ADDRESS_STREET);
        String category = obtenerTexto(parser);

        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_ADDRESS_STREET);
        return category;
    }

    // Procesa la etiqueta <precio> de los hoteles
    private String leerBrand(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_BRAND);
        String BRAND = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_BRAND);
        return BRAND;
    }

    // Procesa la etiqueta <precio> de los hoteles
    private String leerX(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_X);
        String x = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_X);
        return x;
    }
    // Procesa la etiqueta <precio> de los hoteles
    private String leerY(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_Y);
        String y = obtenerTexto(parser);
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_Y);
        return y;
    }



        // Procesa la etiqueta <valoracion> de los hoteles
       /* private HashMap<String, String> leerValoracion(XmlPullParser parser)
                throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_VALORACION);
            String calificacion = parser.getAttributeValue(null, ATRIBUTO_CALIFICACION);
            String noOpiniones = parser.getAttributeValue(null, ATRIBUTO_OPINIONES);
            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_VALORACION);

            HashMap<String, String> atributos = new HashMap<>();
            atributos.put(ATRIBUTO_CALIFICACION, calificacion);
            atributos.put(ATRIBUTO_OPINIONES, noOpiniones);

            return atributos;
        }*/

        // Procesa las etiqueta <urlImagen> de los hoteles
      /*  private String leerUrlImagen(XmlPullParser parser) throws IOException, XmlPullParserException {
            String urlImagen;
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_URL_IMAGEN);
            urlImagen = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_URL_IMAGEN);
            return urlImagen;
        }*/

        // Procesa las etiqueta <descripcion> de los hoteles
      /*  private String leerDescripcion(XmlPullParser parser) throws IOException, XmlPullParserException {
            String descripcion = "";
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_DESCRIPCION);
            String prefijo = parser.getPrefix();
            if (prefijo.equals(PREFIJO))
                descripcion = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_DESCRIPCION);
            return descripcion;
        }*/

        // Obtiene el texto de los atributos
        private String obtenerTexto(XmlPullParser parser) throws IOException, XmlPullParserException {
            String resultado = "";
            if (parser.next() == XmlPullParser.TEXT) {
                resultado = parser.getText();
                parser.nextTag();
            }
            return resultado;
        }

        // Salta aquellos objeteos que no interesen en la jerarquía XML.
        private void saltarEtiqueta(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }


}
