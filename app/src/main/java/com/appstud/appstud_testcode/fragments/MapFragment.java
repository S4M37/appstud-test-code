package com.appstud.appstud_testcode.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.appstud.appstud_testcode.R;
import com.appstud.appstud_testcode.activities.MainActivity;
import com.appstud.appstud_testcode.models.GoogleSearchModel;
import com.appstud.appstud_testcode.services.OnLocationChangeListener;
import com.appstud.appstud_testcode.utils.WidgetUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ProgressBar progressBar;
    private View rootView;
    private GoogleMap googleMaps;//got project limits at google console
    private GoogleApiClient mGoogleApiClient;
    private LayoutInflater inflater;


    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.map_fragment, container, false);
        this.inflater = inflater;
        initializeView();
        return rootView;
    }


    private void initializeMapView(View rootView, Bundle savedInstanceState) {
        MapView mapView = rootView.findViewById(R.id.map_view);
        //MapsInitializer.initialize(getContext());
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeMapView(rootView, savedInstanceState);
    }

    private void initializeView() {
        progressBar = rootView.findViewById(R.id.progress_bar);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMaps = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMaps.setMyLocationEnabled(true);

        // display google location after
        ((MainActivity) getActivity()).onLocationChangeListeners.add(new OnLocationChangeListener() {
            @Override
            public void onLocationChangeListener(Location myLastKnownLocation, List<GoogleSearchModel> googleSearchModelArrayList) {
                googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLastKnownLocation.getLatitude(),
                        myLastKnownLocation.getLongitude()), 14));
                progressBar.setVisibility(View.GONE);
                for (GoogleSearchModel googleSearchModel : googleSearchModelArrayList) {
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.valueOf(googleSearchModel.getGeometry().location.lat),
                            Double.valueOf(googleSearchModel.getGeometry().location.lng))).title(googleSearchModel.getName())
                            .icon(BitmapDescriptorFactory.fromBitmap(WidgetUtils.getMarkerBitmapFromView(inflater, googleSearchModel)));
                    googleMaps.addMarker(marker);
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
