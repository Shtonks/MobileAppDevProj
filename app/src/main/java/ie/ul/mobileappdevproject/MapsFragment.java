package ie.ul.mobileappdevproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsFragment extends Fragment implements View.OnClickListener {
    //Declaring variables whose scope needs to be the entire class (and only this class)
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;

    //Created the OnMapReadyCallback needed for map creation (when using a fragment like I am)
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        //Method where map manipulation stuff goes (e.g. UI settings, placing markers, etc)
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //Initialising map. V important for other methods in this code
            map = googleMap;

            //When map is tapped, will place a marker at that spot
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    try {
                        //Geocoder is used to searches for places using their longitude and latitude and giving back their location(address)
                        Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);
                        List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                        //When map is tapped a marker is initialised and given its location and title
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        //This specifically displays the entire address of a location
                        markerOptions.title(addresses.get(0).getAddressLine(0));

                        //Remove any old markers
                        map.clear();
                        //Go to new location with a nice smooth, prebuilt transition
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, map.getCameraPosition().zoom));
                        //Marker is added to map
                        map.addMarker(markerOptions);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });
            //Gives the option yo use the +/- buttons in the corner of screen to zoom instead of just finger pinching
            map.getUiSettings().setZoomControlsEnabled(true);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        //Initialising the view by getting the layout from the corresponding .xml file
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        //Declaring and initialising the find CSIS button
        Button UL = (Button) view.findViewById(R.id.UL_btn);
        //Calling listener for this button
        UL.setOnClickListener(this);

        //Radio group of radio buttons to allow selection for different map types to be used
        //Finding and initialising the radiogroup found in the .xml file
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup2);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            //Called whenever different radio button is selected
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
                    //When permission isn't granted
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
                         try {
                             //Same as when explained on line 68
                             Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);
                             List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

                             LatLng currentLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
                             map.clear();
                             map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
                             map.addMarker(new MarkerOptions().position(currentLoc).title(addresses.get(0).getAddressLine(0)));
                         }catch (IOException e){
                             e.printStackTrace();
                         }

                     }else{
                         //Initialising location request
                         LocationRequest request = LocationRequest.create();
                         request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                         request.setInterval(10000);
                         request.setFastestInterval(1000);
                         request.setNumUpdates(1);

                         //Initialising location callback
                         LocationCallback locationCallback = new LocationCallback() {
                             @Override
                             public void onLocationResult(@NonNull LocationResult locationResult) {
                                 try {
                                     Location loc1 = locationResult.getLastLocation();
                                     Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);
                                     List<Address> addresses = geocoder.getFromLocation(loc1.getLatitude(), loc1.getLongitude(), 1);

                                     //Finally, we have the location. Can set the marker
                                     LatLng currentLoc1 = new LatLng(loc1.getLatitude(), loc1.getLongitude());
                                     map.clear();
                                     map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc1, 15));
                                     map.addMarker(new MarkerOptions().position(currentLoc1).title(addresses.get(0).getAddressLine(0)));
                                 }catch (IOException e){
                                     e.printStackTrace();
                                 }
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

    //This methods sets up the actual map fragment
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    //Method called for going directly to the CSIS location on the map
    @Override
    public void onClick(View view) {
        map.clear();
        LatLng Limerick = new LatLng(52.67393454451324, -8.575611950616514);
        map.addMarker(new MarkerOptions()
                .position(Limerick)
                .title("CSIS"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Limerick, 15));
    }
}