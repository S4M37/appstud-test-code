package com.appstud.appstud_testcode.services;

import android.location.Location;

import com.appstud.appstud_testcode.models.GoogleSearchModel;

import java.util.List;

//triggered when location changes or data retried from Google
public interface OnLocationChangeListener {
    void onLocationChangeListener(Location myLastKnownLocation, List<GoogleSearchModel> googleSearchModelArrayList);
}
