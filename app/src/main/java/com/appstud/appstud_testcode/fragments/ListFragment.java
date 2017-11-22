package com.appstud.appstud_testcode.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appstud.appstud_testcode.R;
import com.appstud.appstud_testcode.activities.MainActivity;
import com.appstud.appstud_testcode.adapters.GoogleSearchRecyclerViewAdapter;
import com.appstud.appstud_testcode.models.GoogleSearchModel;
import com.appstud.appstud_testcode.services.OnLocationChangeListener;
import com.appstud.appstud_testcode.utils.Utils;

import java.util.List;


public class ListFragment extends Fragment {
    private RecyclerView listPlaces;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GoogleSearchRecyclerViewAdapter googleSearchRecyclerViewAdapter;

    public static ListFragment newInstance() {
        Bundle args = new Bundle();
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);
        initializeListPlaces(rootView);
        initializeListSwipRefresh(rootView);
        return rootView;
    }

    private void initializeListSwipRefresh(View rootView) {
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.onRequestLocationListener != null) {
                    Utils.onRequestLocationListener.onRequestLocationListener();
                }
            }
        });
    }

    private void initializeListPlaces(View rootView) {
        listPlaces = rootView.findViewById(R.id.list_places);
        listPlaces.setLayoutManager(new LinearLayoutManager(getContext()));


        // display google location in adapter and attached to ReyclerView
        ((MainActivity) getActivity()).onLocationChangeListeners.add(new OnLocationChangeListener() {
            @Override
            public void onLocationChangeListener(Location myLastKnownLocation, List<GoogleSearchModel> googleSearchModelArrayList) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (googleSearchRecyclerViewAdapter == null) {
                    googleSearchRecyclerViewAdapter = new GoogleSearchRecyclerViewAdapter(getContext(), googleSearchModelArrayList);
                    listPlaces.setAdapter(googleSearchRecyclerViewAdapter);
                } else {
                    googleSearchRecyclerViewAdapter.setItemlist(googleSearchModelArrayList);
                }
            }
        });
    }
}
