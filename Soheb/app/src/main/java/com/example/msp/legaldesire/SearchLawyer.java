package com.example.msp.legaldesire;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchLawyer extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "searchlawyer123";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    GoogleMap mGoogleMap;

    DatabaseReference mDatabase;

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton;

    MapView mMapView;
    Spinner spinner1, spinner2, spinner3;
    Button mSearch;

    double mLocationLat, mLocationLng;
    boolean isNear = false, isRated = false, isType = false, isCorporate = false, isCriminal = false, isCivil = false;
    int searchByDistance;
    float searchByRating;
    String searchByType;
    boolean mIfChatExist;


    public SearchLawyer() {
        // Required empty public constructor
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override//Connect to Google API
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override//Disconnect from Google API
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    //Check if Google play services are available
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }

    //this method will return the location mode- return 3 for high accuracy which is required to accurately find user location.
    public int getLocationMode(Context context) throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
    }

    //prompt user to enable gps if disabled
    private void enableGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    //prompt user to enable high accuracy to if disabled
    private void enableHighAccuracy() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Enable High Accuracy to accurately compute your Location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        Log.d(TAG, "onCreate ...............................");

        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGps();
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "GPS is enabled");
            try {
                int isHighAccuracyEnabled = getLocationMode(getContext());  //will return 3 if high accuracy is enabled
                if (isHighAccuracyEnabled != 3)
                    enableHighAccuracy();
                else Log.d(TAG, "High Accuracy enabled");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "GPS is disabled");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            getActivity().finish();
            Log.d(TAG, "google play services not found");
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.search_lawyer, container, false);

        spinner1 = (Spinner) v.findViewById(R.id.spinner1);
        spinner2 = (Spinner) v.findViewById(R.id.spinner2);
        spinner3 = (Spinner) v.findViewById(R.id.spinner3);
        mSearch = (Button) v.findViewById(R.id.btn_search);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        this.infoWindow = (ViewGroup) getLayoutInflater(savedInstanceState).inflate(R.layout.custom_marker, null);
        this.infoTitle = (TextView) infoWindow.findViewById(R.id.title);
        this.infoSnippet = (TextView) infoWindow.findViewById(R.id.snippet);

        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(this);//this will call the onCallBack method, which will load the map on screen

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());//backup! In case the map failed to load on .getMapAsync()
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Handling Events
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isNear = true;
                Log.d(TAG, "search by distance");
                String str = spinner1.getSelectedItem().toString();
                Log.d(TAG, str + " selected");

                if (str.equals("5Km")) {
                    searchByDistance = 5;
                    Log.d(TAG, "search by distance=" + searchByDistance);

                } else if (str.equals("10Km")) {
                    searchByDistance = 10;
                    Log.d(TAG, "search by distance=" + searchByDistance);
                } else if (str.equals("20Km")) {
                    searchByDistance = 20;
                } else if (str.equals("50Km")) {
                    searchByDistance = 50;
                } else {
                    isNear = false;
                    Log.d(TAG, "search by distance is false");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isRated = true;
                Log.d(TAG, "search by rating");
                String str = spinner2.getSelectedItem().toString();
                Log.d(TAG, str + " selected");
                if (str.equals("1+")) {
                    searchByRating = 1;
                    Log.d(TAG, "1search by rating=" + searchByRating);
                } else if (str.equals("2+")) {
                    searchByRating = 2;
                    Log.d(TAG, "2search by rating=" + searchByRating);
                } else if (str.equals("3+")) {
                    searchByRating = 3;
                    Log.d(TAG, "3search by rating=" + searchByRating);
                } else if (str.equals("4+")) {
                    searchByRating = 4;
                    Log.d(TAG, "4search by rating=" + searchByRating);
                } else {
                    isRated = false;
                    Log.d(TAG, "search by rating is false");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isType = true;
                Log.d(TAG, "search by type");
                String str = spinner3.getSelectedItem().toString();
                searchByType = str;
                Log.d(TAG, searchByType + " selected");
                if (searchByType.equals("--------")) {
                    isType = false;
                    Log.d(TAG, "search by type disabled");
                } else {
                    Log.d(TAG, "search by enabled");
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentLocation == null) {
                    Toast.makeText(getContext(), "Unable to determine location, GPS is disabled", Toast.LENGTH_SHORT).show();
                } else {
                    categorizeData();

                }
            }
        });
        return v;
    }


    public void categorizeData() {
        Log.d(TAG, "Inside categorizeData()");
        mGoogleMap.clear();

        if (isNear == true && isRated == false && isType == false) {//Search only by distance
            Log.d(TAG, "search by distance only");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lname = postSnapshot.child("Name").getValue(String.class);
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        String laddress = postSnapshot.child("Address").getValue(String.class);
                        double lrating = Double.parseDouble(postSnapshot.child("Rating").getValue(String.class));
                        String ltype = postSnapshot.child("Type").getValue(String.class);
                        double llat = Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class));
                        double llng = Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class));
                        Location marker = new Location("Location");
                        marker.setLatitude(llat);
                        marker.setLongitude(llng);
                        double distance = mCurrentLocation.distanceTo(marker);
                        Log.d(TAG, "distance:" + distance);

                        if (distance <= searchByDistance * 1000) {
                            Log.d(TAG, "distance within " + searchByDistance + ":" + distance);
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(llat, llng))
                                    .title(lname).snippet("address:" + laddress + "\n" + "Rating:" + lrating)).setTag(lemail);
                        } else {
                            Log.d(TAG, "distance not within " + searchByDistance + ":" + distance);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (isNear == false && isRated == true && isType == false) {//Search only by rating
            Log.d(TAG, "search by rating only");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lname = postSnapshot.child("Name").getValue(String.class);
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        String laddress = postSnapshot.child("Address").getValue(String.class);
                        double lrating = Double.parseDouble(postSnapshot.child("Rating").getValue(String.class));
                        String ltype = postSnapshot.child("Type").getValue(String.class);
                        double llat = Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class));
                        double llng = Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class));

                        if (lrating >= searchByRating) {
                            Log.d(TAG, "Rating above " + searchByRating + ":" + lrating);
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(llat, llng))
                                    .title(lname).snippet("address:" + laddress + "\n" + "Rating:" + lrating)).setTag(lemail);
                            ;
                        } else {
                            Log.d(TAG, "Rating below " + searchByRating + ":" + lrating);

                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (isNear == false && isRated == false && isType == true) {//Search only by type
            Log.d(TAG, "search by type only");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lname = postSnapshot.child("Name").getValue(String.class);
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        String laddress = postSnapshot.child("Address").getValue(String.class);
                        double lrating = Double.parseDouble(postSnapshot.child("Rating").getValue(String.class));
                        String ltype = postSnapshot.child("Type").getValue(String.class);
                        double llat = Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class));
                        double llng = Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class));

                        if (searchByType.equals(ltype)) {
                            Log.d(TAG, "Type same: " + searchByType + ":" + ltype);
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(llat, llng))
                                    .title(lname).snippet("address:" + laddress + "\n" + "Rating:" + lrating)).setTag(lemail);
                            ;
                        } else {
                            Log.d(TAG, "Type different: " + searchByType + ":" + ltype);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (isNear == true && isRated == true && isType == false) {//Search by distance and rating
            Log.d(TAG, "search by distance and rating");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lname = postSnapshot.child("Name").getValue(String.class);
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        String laddress = postSnapshot.child("Address").getValue(String.class);
                        double lrating = Double.parseDouble(postSnapshot.child("Rating").getValue(String.class));
                        String ltype = postSnapshot.child("Type").getValue(String.class);
                        double llat = Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class));
                        double llng = Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class));

                        Location marker = new Location("Location");
                        marker.setLatitude(llat);
                        marker.setLongitude(llng);
                        double distance = mCurrentLocation.distanceTo(marker);
                        if (distance <= (searchByDistance * 1000) && lrating >= searchByRating) {
                            Log.d(TAG, "distance within " + searchByDistance + ":" + distance);
                            Log.d(TAG, "Rating above " + searchByRating + ":" + lrating);
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(llat, llng))
                                    .title(lname).snippet("address:" + laddress + "\n" + "Rating:" + lrating)).setTag(lemail);
                        } else {
                            Log.d(TAG, "Distance or Rating not within condition:distance" + distance + " Rating:" + lrating);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (isNear == true && isRated == false && isType == true) {//Search by distance and type
            Log.d(TAG, "search by distance and type");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lname = postSnapshot.child("Name").getValue(String.class);
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        String laddress = postSnapshot.child("Address").getValue(String.class);
                        double lrating = Double.parseDouble(postSnapshot.child("Rating").getValue(String.class));
                        String ltype = postSnapshot.child("Type").getValue(String.class);
                        double llat = Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class));
                        double llng = Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class));

                        Location marker = new Location("Location");
                        marker.setLatitude(llat);
                        marker.setLongitude(llng);
                        double distance = mCurrentLocation.distanceTo(marker);
                        if (distance <= (searchByDistance * 1000) && searchByType.equals(ltype)) {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(llat, llng))
                                    .title(lname).snippet("address:" + laddress + "\n" + "Rating:" + lrating)).setTag(lemail);
                            Log.d(TAG, "distance within " + searchByDistance + ":" + distance);
                            Log.d(TAG, "Type same: " + searchByType + ":" + ltype);
                        } else {
                            Log.d(TAG, "Distance or Type not within condition:distance" + distance + " Type:" + ltype);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (isNear == false && isRated == true && isType == true) {//Search by rating and type
            Log.d(TAG, "search by rating and type");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lname = postSnapshot.child("Name").getValue(String.class);
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        String laddress = postSnapshot.child("Address").getValue(String.class);
                        double lrating = Double.parseDouble(postSnapshot.child("Rating").getValue(String.class));
                        String ltype = postSnapshot.child("Type").getValue(String.class);
                        double llat = Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class));
                        double llng = Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class));

                        Location marker = new Location("Location");
                        marker.setLatitude(llat);
                        marker.setLongitude(llng);
                        if (lrating >= searchByRating && searchByType.equals(ltype)) {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(llat, llng))
                                    .title(lname).snippet("address:" + laddress + "\n" + "Rating:" + lrating)).setTag(lemail);
                            ;
                            Log.d(TAG, "Rating above " + searchByRating + ":" + lrating);
                            Log.d(TAG, "Type same: " + searchByType + ":" + ltype);
                        } else {
                            Log.d(TAG, "Distance or Type not within condition:rating" + lrating + " Type:" + ltype);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (isNear == true && isRated == true && isType == true) {//Search by distance,rating and type
            Log.d(TAG, "search by rating and type");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lname = postSnapshot.child("Name").getValue(String.class);
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        String laddress = postSnapshot.child("Address").getValue(String.class);
                        double lrating = Double.parseDouble(postSnapshot.child("Rating").getValue(String.class));
                        String ltype = postSnapshot.child("Type").getValue(String.class);
                        double llat = Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class));
                        double llng = Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class));

                        Location marker = new Location("Location");
                        marker.setLatitude(llat);
                        marker.setLongitude(llng);
                        double distance = mCurrentLocation.distanceTo(marker);
                        if (distance <= (searchByDistance * 1000) && lrating >= searchByRating && searchByType.equals(ltype)) {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(llat, llng))
                                    .title(lname).snippet("address:" + laddress + "\n" + "Rating:" + lrating)).setTag(lemail);
                            Log.d(TAG, "Rating above " + searchByRating + ":" + lrating);
                            Log.d(TAG, "Type same: " + searchByType + ":" + ltype);
                            Log.d(TAG, "distance within " + searchByDistance + ":" + distance);
                        } else {
                            Log.d(TAG, "Distance or Type not within condition:distance:" + distance + "rating" + lrating + " Type:" + ltype);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else if (isNear == false && isRated == false && isType == false) {//Search everything
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lname = postSnapshot.child("Name").getValue(String.class);
                        String lemail = postSnapshot.child("Email").getValue(String.class);
                        String laddress = postSnapshot.child("Address").getValue(String.class);
                        double lrating = Double.parseDouble(postSnapshot.child("Rating").getValue(String.class));
                        String ltype = postSnapshot.child("Type").getValue(String.class);
                        double llat = Double.parseDouble(postSnapshot.child("Latitude").getValue(String.class));
                        double llng = Double.parseDouble(postSnapshot.child("Longitude").getValue(String.class));

                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(llat, llng))
                                .title(lname).snippet("address:" + laddress + "\n" + "Rating:" + lrating));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    String lemail, lname, uemail, uname;
    Bundle bundle = new Bundle();


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                infoTitle.setText(marker.getTitle());
                infoSnippet.setText(marker.getSnippet());
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        bundle.putBoolean("chat_exists", false);


        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                String tag = (String) marker.getTag();
                String name = marker.getTitle();
                SharedPreferences preferences = getContext().getSharedPreferences("store_name_and_email", Context.MODE_PRIVATE);
                final String personName = preferences.getString("person_name", null);
                final String personEmail = preferences.getString("person_email", null);
                final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");

                root.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String lawyer_email = postSnapshot.child("Lawyer Email").getValue(String.class);
                            String user_email = postSnapshot.child("User Email").getValue(String.class);
                            if (lawyer_email.equals(marker.getTag()) && user_email.equals(personEmail)) {
                                mIfChatExist = true;
                                lemail = lawyer_email;
                                uemail = user_email;
                                Log.d(TAG, "Chat exists");
                                bundle.putBoolean("chat_exist", true);
                                bundle.putString("lawyer_email", lemail);
                                bundle.putString("user_email", uemail);
                                Chat_Module chat_module = new Chat_Module();
                                chat_module.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, chat_module).commit();
                                break;
                            }
                        }
                        if (!mIfChatExist) {
                            Log.d(TAG, "Chat doesnt exist OK");
                            Log.d(TAG, "Chat conversation doesn't exist");
                            HashMap<String, Object> Message = new HashMap<String, Object>();
                            // Message.put("user", "");
                            //  Message.put("msg", "");
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("Lawyer Email", marker.getTag());
                            map.put("Lawyer Name", marker.getTitle());
                            map.put("User Name", personName);
                            map.put("User Email", personEmail);
                            map.put("Message", Message);
                            map.put("Lawyer Seen", false);
                            map.put("User Seen", false);


                            root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "New chat module created");
                                        Bundle bundle = new Bundle();
                                        bundle.putString("lawyer_email", marker.getTag().toString());
                                        bundle.putString("user_email", personEmail);
                                        Chat_Module chat_module = new Chat_Module();
                                        chat_module.setArguments(bundle);
                                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.replace(R.id.fragment_container, chat_module).commit();
                                    } else {
                                        Log.d(TAG, "Failed to create new chat module");
                                    }
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Log.d(TAG, "inside searchlayer module");
                Toast.makeText(getContext(), "Window CLicked", Toast.LENGTH_SHORT).show();

            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLocationLat = mCurrentLocation.getLatitude();
        mLocationLng = mCurrentLocation.getLongitude();

        Log.d(TAG, "Location change:" + mLocationLat + "," + mLocationLng);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //
                    //     Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }
}
