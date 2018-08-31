package com.fuel.my.myfuel4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private LatLng miUbicacion;
    private List<Precios> myPrices = null;

    private static final int Request_User_Location_Code=99;
    private MarkerOptions options = new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_maps);
        //new DownloadFileFromURL().execute("https://publicacionexterna.azurewebsites.net/publicaciones/places");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }

    }

    public boolean checkUserLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return  false;
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case Request_User_Location_Code:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(this,"Permiso denegado",Toast.LENGTH_SHORT);
                }
                return ;
        }
    }



    protected synchronized void buildGoogleApiClient(){
        googleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation= location;




        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        miUbicacion=latLng;
        new TareaDescargaXmlPrecios().execute("https://publicacionexterna.azurewebsites.net/publicaciones/prices");

        new TareaDescargaXml().execute("https://publicacionexterna.azurewebsites.net/publicaciones/places");

    /*
        latlngs.add(new LatLng(location.getLatitude()-0.00005,location.getLongitude()-0.0005));
        latlngs.add(new LatLng(location.getLatitude()+.0055,location.getLongitude()-0.0030));
        latlngs.add(new LatLng(location.getLatitude()-0.0080,location.getLongitude()+0.0065));
*/
        /*for (Estaciones myentries : entries) {
            options.position(new LatLng(Double.parseDouble(myentries.getX()),Double.parseDouble(myentries.getY())));
            options.title(myentries.getNombre());
            options.snippet(myentries.getAddress_street());
            mMap.addMarker(options);
        }*/
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,12);
        mMap.animateCamera(cameraUpdate);
        if(googleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

    }


    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(iconResID)));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest= new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");


        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String root = Environment.getExternalStorageDirectory().toString();
                System.out.println("Downloading");
                URL url = new URL(f_url[0]);

                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                OutputStream output = new FileOutputStream(root+"/estaciones.xml");
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }



        /**
         * After completing background task
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            System.out.println("Downloaded");


        }

    }


    private class TareaDescargaXml extends AsyncTask<String, Void, List<Estaciones>> {

        @Override
        protected List<Estaciones> doInBackground(String... urls) {
            try {
                return parsearXmlDeUrl(urls[0]);
            } catch (IOException e) {
                return null; // null si hay error de red
            } catch (XmlPullParserException e) {
                return null; // null si hay error de parsing XML
            }
        }

        @Override
        protected void onPostExecute(List<Estaciones> result) {
            // Actualizar contenido del proveedor de datos
            //Estaciones.estaciones = result;
            final List<Precios> precios=null;

           /* try {
               precios = parsearXmlDeUrlPre("https://publicacionexterna.azurewebsites.net/publicaciones/prices");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            for (Estaciones myentries : result) {


                options.position(new LatLng(Double.parseDouble(myentries.getY()),Double.parseDouble(myentries.getX() )));
                options.title(myentries.getNombre());

                for (Precios misPrecios: myPrices){
                    if(misPrecios.getPlace_id().equals(myentries.getPlace_id())){
                        options.snippet("Regular: $"+misPrecios.getRegular()+"| Premium: $"+misPrecios.getPremium()+"| Diesel: $"+misPrecios.getDiesel()+"|Actualización:"+misPrecios.getActualizacion());
                    }
                }


                mMap.addMarker(options);

            }
            // Actualizar la vista del adaptador
           // adaptador.notifyDataSetChanged();
        }
    }


    private class TareaDescargaXmlPrecios extends AsyncTask<String, Void, List<Precios>> {

        @Override
        protected List<Precios> doInBackground(String... urls) {
            try {
                return parsearXmlDeUrlPre(urls[0]);
            } catch (IOException e) {
                return null; // null si hay error de red
            } catch (XmlPullParserException e) {
                return null; // null si hay error de parsing XML
            }
        }

        @Override
        protected void onPostExecute(List<Precios> result) {
            // Actualizar contenido del proveedor de datos
            myPrices=result;
            // Actualizar la vista del adaptador
            // adaptador.notifyDataSetChanged();
        }
    }

    private List<Estaciones> parsearXmlDeUrl(String urlString)
            throws XmlPullParserException, IOException {
        InputStream stream = null;
        parserXML parserXml = new parserXML(miUbicacion);
        List<Estaciones> entries = null;

        try {
            stream = descargarContenido(urlString);
            entries = parserXml.parsear(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }

    private List<Precios> parsearXmlDeUrlPre(String urlString)
            throws XmlPullParserException, IOException {
        InputStream stream = null;
        parserXML parserXml = new parserXML(miUbicacion);
        List<Precios> entries = null;

        try {
            stream = descargarContenido(urlString);
            System.out.println("hola");
            entries = parserXml.parsearPrecios(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }

    private InputStream descargarContenido(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Iniciar la petición
        conn.connect();
        return conn.getInputStream();
    }



}

