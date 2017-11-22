package com.appstud.appstud_testcode.realm;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.appstud.appstud_testcode.models.RealmGoogleSearchModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from GoogleSearchModel.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(RealmGoogleSearchModel.class);
        realm.commitTransaction();
    }

    //find all objects in the GoogleSearchModel.class
    public RealmResults<RealmGoogleSearchModel> getGoogleSearchModels() {

        return realm.where(RealmGoogleSearchModel.class).findAll();
    }

    //query a single item with the given id
    public RealmGoogleSearchModel getGoogleSearchModel(String id) {

        return realm.where(RealmGoogleSearchModel.class).equalTo("id", id).findFirst();
    }

    //check if GoogleSearchModel.class is empty
    public boolean hasGoogleSearchModels() {

        return !realm.allObjects(RealmGoogleSearchModel.class).isEmpty();
    }

    //query example
    public RealmResults<RealmGoogleSearchModel> queryedGoogleSearchModels() {

        return realm.where(RealmGoogleSearchModel.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();

    }
}
