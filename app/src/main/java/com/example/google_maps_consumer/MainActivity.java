package com.example.google_maps_consumer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    //El codigo de la discordia
    //the playlist

    List<LatLng> lstlongitud;

    GoogleMap mapa;
    volley_request request_response_user;
    TextView txt_medida;

    // PlacesAPI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reference_map();
        lstlongitud=new ArrayList<>();
        request_response_user=new volley_request(this);
        txt_medida=findViewById(R.id.txtdistancia);
    }

    private void reference_map() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*GeoApiContext geoApiContext =new GeoApiContext.Builder().
                apiKey("AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0").
                build();*/

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);

        CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(new LatLng(40.69025015706544, -74.04540678273696), 18);
        /*LatLng madrid = new LatLng(40.69025015706544, -74.04540678273696);

        CameraPosition camPos = new CameraPosition.Builder()
                .target(madrid)
                .zoom(19)
                .bearing(45) //noreste arriba
                .tilt(70) //punto de vista de la cámara 70 grados
                .build();
        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);*/

        mapa.animateCamera(camUpd1);
        mapa.setOnMapClickListener(this);

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        LatLng punto = new LatLng(latLng.latitude,
                latLng.longitude);
        mapa.addMarker(new
                MarkerOptions().position(punto)
                .title("Estatua"));
        lstlongitud.add(latLng);
        if(lstlongitud.size()==6){
            PolylineOptions lineas = new
                    PolylineOptions()
                    .add(new LatLng(lstlongitud.get(0).latitude, lstlongitud.get(0).longitude))
                    .add(new LatLng(lstlongitud.get(1).latitude, lstlongitud.get(1).longitude))
                    .add(new LatLng(lstlongitud.get(2).latitude, lstlongitud.get(2).longitude))
                    .add(new LatLng(lstlongitud.get(3).latitude, lstlongitud.get(3).longitude))
                    .add(new LatLng(lstlongitud.get(4).latitude, lstlongitud.get(4).longitude))
                    .add(new LatLng(lstlongitud.get(0).latitude, lstlongitud.get(0).longitude));
            lineas.width(8);
            lineas.color(Color.RED);
            mapa.addPolyline(lineas);

            try{
                thread_response_data_users();
            }catch (Exception ex){
                Log.i("Error",ex.getMessage());
            }
        }
        if(lstlongitud.size()>6){
            mapa.clear();
            lstlongitud.clear();
        }

    }

    private void thread_response_data_users()
    {
        Thread thread_response=new Thread(new Runnable() {
            @Override
            public void run() {
                deserialize_json des=new deserialize_json();
                try
                {
                    for(int x=0;x<lstlongitud.size();x++)
                    {
                        String url="";
                        if(x+1<lstlongitud.size())
                            url="https://maps.googleapis.com/maps/api/distancematrix/json?destinations="+lstlongitud.get(x+1).latitude+", "+lstlongitud.get(x+1).longitude+"&origins="+lstlongitud.get(x).latitude+", "+lstlongitud.get(x).longitude+"&units=meters&key=AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0";
                        else
                            url="https://maps.googleapis.com/maps/api/distancematrix/json?destinations="+lstlongitud.get(0).latitude+", "+lstlongitud.get(0).longitude+"&origins="+lstlongitud.get(x).latitude+", "+lstlongitud.get(x).longitude+"&units=meters&key=AIzaSyAZmpF3k0bcm-3c-f_0feLZQZRwYu-gdr0";

                        Log.i("responseData", url);
                        request_response_user.get_request_volley(url);
                        while (request_response_user.getResponse()=="")
                        {
                            Log.i("responseData", "Getting data from " + request_response_user.getUrl());
                            Thread.sleep(500);
                        }
                        Log.i("responseData",request_response_user.getResponse());
                        des.process_json_Cad(request_response_user.getResponse());
                        request_response_user.setResponse("");
                    }

                    runOnUiThread(new Runnable() { //ejecuta la funcion o el proceso en el hilo principal
                        @Override
                        public void run() {
                            txt_medida.setText("Mide: "+des.getDistance_sum().toString()+"m");
                        }
                    });
                }
                catch (Exception ex){
                    Log.i("Error",ex.getMessage());
                }
            }
        });
        thread_response.start();
    }


}