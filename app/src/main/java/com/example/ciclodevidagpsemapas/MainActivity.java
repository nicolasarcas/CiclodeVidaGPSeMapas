package com.example.ciclodevidagpsemapas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private TextView locationTextView;

    private double latitude;
    private double longitude;

    private static final int GPS_REQUEST_CODE = 1001;

    private void configurarGPS(){
        locationManager =
                (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                //distanceTo - distancia entre dois lugares

                String mostrar=
                        String.format(
                                Locale.getDefault(),
                                "Lat: %f  Long: %f",
                                latitude,
                                longitude
                        );
                locationTextView.setText(mostrar);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.locationTextView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        configurarGPS();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(
                        String.format(
                                Locale.getDefault(),
                                "geo: %f, %f?=restaurantes",
                                latitude,
                                longitude
                        )
                );
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        uri
                );
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){ //SE A PERMISSÃO FOI DADA, ATIVA LOCALIZAÇÃO

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, //tempo minimo desde a ultima atualização
                    5, //distancia minima desde a ultima atualização da location
                    locationListener);
        }
        else{//CASO A PERMISSÃO NAO FOI DADA, PEDE PERMISSÃO AO USUÁRIO
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    GPS_REQUEST_CODE //DIFERENCIA A QUE PEDIDO FOI DADA A RESPOSTA
            );
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == GPS_REQUEST_CODE){
            //resposta pelo GPS
            if(grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){

                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000, //tempo minimo desde a ultima atualização
                        5, //distancia minima desde a ultima atualização da location
                        locationListener);
            }
            else{
                Toast.makeText(
                        this, getString(R.string.no_gps_no_app),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}
