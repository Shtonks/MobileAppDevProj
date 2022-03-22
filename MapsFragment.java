package ie.ul.mapsfragtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
//import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationRequest;


public class MapsFragment extends Fragment implements View.OnClickListener {
    //Declaring variables whose scope needs to be the entire class (and only this class)
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    //When clicked on map, initialise marker options
                    MarkerOptions markerOptions = new MarkerOptions();
                    //Set pos of marker
                    markerOptions.position(latLng);
                    //Set title of marker
                    markerOptions.title(latLng.latitude + ":" + latLng.longitude);
                    //Remove markers
                    map.clear();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    map.addMarker(markerOptions);

                }
            });
            //default map type
            //map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

//            LatLng sydney = new LatLng(-34, 151);
//            map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            map.getUiSettings().setZoomControlsEnabled(true);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        //Declaring all variables I'll be using throughout this
        Button UL = (Button) view.findViewById(R.id.UL_btn);
        UL.setOnClickListener(this);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup2);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.normal_map:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.satellite_map:
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.pure_satellite_map:
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                }
            }
        });

        Button myLocBtn = view.findViewById(R.id.myLocBtn);

        //Initialising fused location provider (to find current location)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        myLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checking to see if permission has been given to access location
                if(ContextCompat.checkSelfPermission(getActivity()
                        , Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity()
                ,Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
                    //If permission granted, call this
                    getCurrentLocation();
                }else{
                    //When permission isnt granted
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });
        return view;
    }

    //Checking if permission was given or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && (grantResults.length > 0) &&
                (grantResults[0] + grantResults[1] ==PackageManager.PERMISSION_GRANTED)){
            //Permission granted. Call location method
            getCurrentLocation();
        }else{
            //Permission denied. Display appropriate text
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //Initialising the location manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Check if providers can be found
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            //Now we know location is permitted and findable. Retrieve it
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Initialise the location
                     Location loc = task.getResult();
                     if(loc != null){
                         //Finally, we have the location. Can set the marker
                         LatLng currentLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
                         map.clear();
                         map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 10));
                         map.addMarker(new MarkerOptions().position(currentLoc).title("You are here"));
                     }else{
                         //Initialising location request. Bit of a pain since old LocationRequest constructor was deprecated
                         LocationRequest request = LocationRequest.create();
                         request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                         request.setInterval(10000);
                         request.setFastestInterval(1000);
                         request.setNumUpdates(1);

                         //Initialising location callback
                         LocationCallback locationCallback = new LocationCallback() {
                             @Override
                             public void onLocationResult(@NonNull LocationResult locationResult) {
                                 Location loc1 = locationResult.getLastLocation();
                                 LatLng currentLoc = new LatLng(loc1.getLatitude(), loc1.getLongitude());
                                 map.clear();
                                 map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 10));
                                 map.addMarker(new MarkerOptions().position(currentLoc).title("You are here"));
                             }
                         };
                         //Request location updates
                         fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.myLooper());
                     }
                }
            });
        }else{
            //When location service not enabled by user, open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onClick(View view) {
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        LatLng Limerick = new LatLng(52.67393454451324, -8.575611950616514);
        map.addMarker(new MarkerOptions()
                .position(Limerick)
                .title("Limerick"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Limerick, 10));
//        map.moveCamera(CameraUpdateFactory.newLatLng(Limerick));
//        map.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void onRadioButtonClicked(View view) {
        System.out.println("INSIDE METHOD");

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.normal_map:
                if (checked)
                    System.out.println("NORMALLLLLL MAP");
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.satellite_map:
                if (checked)
                    System.out.println("Sat MAP");
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.pure_satellite_map:
                if (checked)
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
    }
}