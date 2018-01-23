package cliente.transporte.sd.transportecliente;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cliente.transporte.sd.transportecliente.common.Common;
import cliente.transporte.sd.transportecliente.helper.CustomInfo;
import cliente.transporte.sd.transportecliente.model.Notification;
import cliente.transporte.sd.transportecliente.model.FCMResponse;
import cliente.transporte.sd.transportecliente.model.Rider;
import cliente.transporte.sd.transportecliente.model.Sender;
import cliente.transporte.sd.transportecliente.model.Token;
import cliente.transporte.sd.transportecliente.rest.IFCMService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, PlaceSelectionListener{

    private SupportMapFragment mapFragment;
    private static final String TAG = "MapsActivity2";
    private GoogleMap mMap;

    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire geoFire;
    Marker mUserMarker;
    private DatabaseReference ref;

    ImageView imgExpandable;
    Button btnPickupRequest;
    BottomSheetClienteFragment mBottomSheet;

    boolean isDriverFound = false;
    String driverId = "";
    int radius = 1; //1km de distancia
    int distance = 1;
    private static final int LIMIT = 3;

    IFCMService ifcmService;

    DatabaseReference driversAvailable;
    PlaceAutocompleteFragment place_destination;
    AutocompleteFilter typeFilter;

    String mPlaceLocation, mPlaceDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ifcmService = Common.getFCMService();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        imgExpandable = (ImageView) findViewById(R.id.imgExpandable);
//        mBottomSheet = BottomSheetClienteFragment.newInstance("Rider boot sheet");
//        imgExpandable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());
//            }
//        });

        btnPickupRequest = (Button) findViewById(R.id.btnPickupRequest);
        btnPickupRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isDriverFound)
                    requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                else
                    sendRequestToDriver(driverId);
            }
        });

        place_destination = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        place_destination.setOnPlaceSelectedListener(this);
        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();
//        place_destination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//
//            }
//
//            @Override
//            public void onError(Status status) {
//
//            }
//        });

        setUpLocation();

        updateFirebaseToken();
    }

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_table);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);
    }

    private void sendRequestToDriver(String driverId) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_table);

        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                            Token token = postSnapShot.getValue(Token.class);

                            String json_lat_lng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                            String clienteToken = FirebaseInstanceId.getInstance().getToken();
                            Notification notification = new Notification(clienteToken, json_lat_lng);
                            Sender sender = new Sender(notification, token.getToken());
                            ifcmService.sendMessage(sender)
                                     .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                                            Log.w("ERROR", "error h "+response.code());
                                            Log.w("ERROR", "error m "+response.body().success);
//                                            Toast.makeText(HomeActivity.this, response.message(), Toast.LENGTH_SHORT);

                                            if(response.body().success == 1){
                                                Toast.makeText(HomeActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                            }else
                                                Toast.makeText(HomeActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.w("ERROR", t.getMessage());
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void requestPickupHere(String uid) {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference(Common.pickup_request_table);
        GeoFire geoFire = new GeoFire(dbReference);
        geoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        if(mUserMarker.isVisible())
            mUserMarker.remove();


        mUserMarker = mMap.addMarker(new MarkerOptions()
                .title("Me levar aqui")
                .snippet("")
                .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mUserMarker.showInfoWindow();

        btnPickupRequest.setText("A solicitar transp...");

        findDriver();

         }

    private void findDriver() {
        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(Common.driver_table);
        GeoFire geoFire = new GeoFire(drivers);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(! isDriverFound ){
                    isDriverFound = true;
                    driverId = key;
                    btnPickupRequest.setText("CHAMAR");
                    Toast.makeText(HomeActivity.this, key, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //se nao encontrar dentro de 1km aumentar a distancia
                if(! isDriverFound && radius < LIMIT){
                    radius ++;
                    findDriver();
                }else{
                    Toast.makeText(HomeActivity.this, "Não há transportadores por perto", Toast.LENGTH_LONG).show();
                    btnPickupRequest.setText("PROCURAR TRANSPORTE");
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
        }
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        }else {
            if(checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
                    displayLocation();
            }
        }
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){

//criar LatLng a partir da lastLocation e este e o ponto central
            LatLng center = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            //distancia em metros
            //
            LatLng northSide = SphericalUtil.computeOffset(center, 100000, 0);
            LatLng southSide = SphericalUtil.computeOffset(center, 100000, 180);

            LatLngBounds bounds = LatLngBounds.builder()
                    .include(northSide)
                    .include(southSide)
                    .build();

            place_destination.setBoundsBias(bounds);
            place_destination.setFilter(typeFilter);

            driversAvailable = FirebaseDatabase.getInstance().getReference(Common.driver_table);
            driversAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadAllDriversAvailable(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();


                        if (mUserMarker != null)
                            mUserMarker.remove();

                        mUserMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title("Estas aqui"));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

//                        rotateMarker(mCurrentMarker, -360, mMap);
                        loadAllDriversAvailable(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        }else {
            Log.w(TAG, "displayLocation: cant get it");
        }
    }

    private void loadAllDriversAvailable(final LatLng location) {

        //remover todos marcadores no mapa
        mMap.clear();

        //adicionar minha  localizacao
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Estas aqui"));

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Common.driver_table);

        Log.w(TAG, "loadAllDriversAvailable: "+driverLocation.getKey());
        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.latitude, location.longitude), distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                    //key to get number from user table
                Log.w(TAG, "onKeyEntered: "+key );
                FirebaseDatabase.getInstance().getReference(Common.user_driver_table)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Rider cliente = dataSnapshot.getValue(Rider.class);

                                Log.w(TAG, "onDataChange: "+cliente.getPhone() );
                                mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(location.latitude, location.longitude))
                                                .flat(true)
                                                .title(cliente.getName())
                                                .snippet("Cell: "+cliente.getPhone())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "onCancelled: "+databaseError );
                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {
                Log.w(TAG, "onKeyExited: "+key );
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.w(TAG, "onCancelled: "+key );
            }

            @Override
            public void onGeoQueryReady() {
                if( distance < LIMIT){
                    distance ++;
                    loadAllDriversAvailable(location);
                } // procurar dentro de 3 km
                Log.w(TAG, "onGeoReady: " );
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.w(TAG, "onGeoError: "+error );
            }
        });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_REQUEST).show();
            else {
                Toast.makeText(this, "Dispositivo nao suportado", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean isSuccess = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map));
            if (!isSuccess)
                Log.w("ERROR", "Map style load failed");
        }catch (Resources.NotFoundException ex){
            ex.printStackTrace();
        }
       mMap = googleMap;
       mMap.getUiSettings().setZoomControlsEnabled(true);
       mMap.getUiSettings().setZoomGesturesEnabled(true);
       mMap.setInfoWindowAdapter(new CustomInfo(this));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
                Toast.makeText(getApplicationContext(),
                        "geocoder present", Toast.LENGTH_SHORT).show();
                Address returnAddress = addresses.get(0);

                String localityString = returnAddress.getLocality();
                String city = returnAddress.getCountryName();
                String region_code = returnAddress.getCountryCode();
                String zipcode = returnAddress.getPostalCode();

                str.append(localityString + "");
                str.append(city + "" + region_code + "");
                str.append(zipcode + "");

                mPlaceLocation = str.toString();

            } else {
                Toast.makeText(getApplicationContext(),
                        "geocoder not present", Toast.LENGTH_SHORT).show();
            }

// } else {
// Toast.makeText(getApplicationContext(),
// "address not available", Toast.LENGTH_SHORT).show();
// }
        } catch (IOException e) {
// TODO Auto-generated catch block

            Log.e("tag", e.getMessage());
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        mMap.clear();
        mPlaceDestination = place.getAddress().toString();

        mUserMarker = mMap.addMarker(new MarkerOptions().position(mUserMarker.getPosition())
                .icon(BitmapDescriptorFactory.defaultMarker())
                .title("Pegar aqui"));


        mMap.addMarker(new MarkerOptions().position(place.getLatLng())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        .title("Destino")
        .snippet(mPlaceDestination));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));


        BottomSheetClienteFragment mBottomSheet = BottomSheetClienteFragment.newInstance(mPlaceLocation, mPlaceDestination, mLastLocation);
        super.onPostResume(); //Solucao para bug
        mBottomSheet.show(getSupportFragmentManager(), mBottomSheet.getTag());

    }

    @Override
    public void onError(Status status) {

    }
}
