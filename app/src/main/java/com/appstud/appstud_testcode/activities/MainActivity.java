package com.appstud.appstud_testcode.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appstud.appstud_testcode.R;
import com.appstud.appstud_testcode.adapters.MainViewPagerAdapter;
import com.appstud.appstud_testcode.config.Const;
import com.appstud.appstud_testcode.fragments.ListFragment;
import com.appstud.appstud_testcode.fragments.MapFragment;
import com.appstud.appstud_testcode.models.GoogleSearchModel;
import com.appstud.appstud_testcode.services.OnLocationChangeListener;
import com.appstud.appstud_testcode.services.OnRequestLocationListener;
import com.appstud.appstud_testcode.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location myLastLocation;
    private String queryText;
    private TextView activityTitle;

    private static final int UPDATE_INTERVAL = 10000;
    private static final int FASTEST_INTERVAL = 5000;

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        setContentView(R.layout.activity_main);
        initializeToolbar();
        initializeTabView();

        hasPermissions(this, PERMISSIONS);
        startLocationUpdates();

        Utils.onRequestLocationListener = new OnRequestLocationListener() {
            @Override
            public void onRequestLocationListener() {
                startLocationUpdates();
            }
        };
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setSubtitleTextColor(Color.parseColor("#FFFFFF"));
        this.activityTitle = (TextView) findViewById(R.id.activity_title);
    }

    private void initializeTabView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        activityTitle.setText(getString(R.string.activity_title_map));
                        (((LinearLayout) tabLayout.getTabAt(0).getCustomView()).getChildAt(0)).setAlpha(1);
                        (((LinearLayout) tabLayout.getTabAt(1).getCustomView()).getChildAt(0)).setAlpha((float) 0.5);
                        ((TextView) ((LinearLayout) tabLayout.getTabAt(0).getCustomView()).getChildAt(1)).setTextColor(Color.parseColor("#ffffff"));
                        ((TextView) ((LinearLayout) tabLayout.getTabAt(1).getCustomView()).getChildAt(1)).setTextColor(Color.parseColor("#50ffffff"));
                        break;
                    case 1:
                        activityTitle.setText(getString(R.string.activity_title_list));
                        (((LinearLayout) tabLayout.getTabAt(1).getCustomView()).getChildAt(0)).setAlpha(1);
                        (((LinearLayout) tabLayout.getTabAt(0).getCustomView()).getChildAt(0)).setAlpha((float) 0.5);
                        ((TextView) ((LinearLayout) tabLayout.getTabAt(0).getCustomView()).getChildAt(1)).setTextColor(Color.parseColor("#50ffffff"));
                        ((TextView) ((LinearLayout) tabLayout.getTabAt(1).getCustomView()).getChildAt(1)).setTextColor(Color.parseColor("#ffffff"));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons(tabLayout);
    }

    private void setupTabIcons(TabLayout tabLayout) {
        LinearLayout tabOne = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((TextView) tabOne.getChildAt(1)).setText(getString(R.string.activity_title_map));
        ((TextView) tabOne.getChildAt(1)).setTextColor(Color.parseColor("#ffffff"));
        ((ImageView) tabOne.getChildAt(0)).setImageResource(R.drawable.ic_tab_map);

        tabLayout.getTabAt(0).setCustomView(tabOne);
        LinearLayout tabTow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((TextView) tabTow.getChildAt(1)).setText(getString(R.string.activity_title_list));
        ((TextView) tabTow.getChildAt(1)).setTextColor(Color.parseColor("#50ffffff"));
        ((ImageView) tabTow.getChildAt(0)).setImageResource(R.drawable.ic_tab_list);
        (tabTow.getChildAt(0)).setAlpha((float) 0.5);
        tabLayout.getTabAt(1).setCustomView(tabTow);
    }


    private void setupViewPager(ViewPager viewPager) {
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MapFragment.newInstance(), "Map");
        adapter.addFragment(ListFragment.newInstance(), "List");
        viewPager.setAdapter(adapter);
    }

    protected void startLocationUpdates() {
        // Create the location request
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        retrieveGoogleSearchData();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    //Request Permissions
    private String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((MainActivity) context, permissions, Const.MY_PERMISSIONS_REQUEST_LOCALISATION);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Const.MY_PERMISSIONS_REQUEST_LOCALISATION: {
                startLocationUpdates();
            }
            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        Log.d("location", "onLocationChanged latitude: " + location.getLatitude());
        Log.d("location", "onLocationChanged longitude: " + location.getLongitude());
        myLastLocation = location;
        retrieveGoogleSearchData();
    }

    public void retrieveGoogleSearchData() {

        Call<ResponseBody> call = Utils.getGoogleApiRetrofitServices().nearbySearch(getString(R.string.google_api_key),
                myLastLocation.getLatitude() + "," + myLastLocation.getLongitude(), 2000, "bar", queryText);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("code", "onResponse: " + response.code());
                Log.d("message", "onResponse: " + response.message());
                if (response.code() != 200) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    Gson gson = Utils.getGson();
                    List<GoogleSearchModel> nearbySearchResults = new ArrayList<>();
                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {
                        GoogleSearchModel googleSearchModel = gson.fromJson(jsonArray.getString(i), GoogleSearchModel.class);
                        nearbySearchResults.add(googleSearchModel);
                    }
                    //wake up list and map fragment to handle google result
                    for (OnLocationChangeListener onLocationChangeListener : onLocationChangeListeners) {
                        if (onLocationChangeListener != null) {
                            onLocationChangeListener.onLocationChangeListener(myLastLocation, nearbySearchResults);
                        }
                    }

                } catch (NullPointerException | JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public List<OnLocationChangeListener> onLocationChangeListeners = new ArrayList<>();
}
