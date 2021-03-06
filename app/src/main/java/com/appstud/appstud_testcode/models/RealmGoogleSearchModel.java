package com.appstud.appstud_testcode.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmGoogleSearchModel extends RealmObject {

    public RealmGoogleSearchModel() {

    }

    public RealmGoogleSearchModel(String id, String name, String icon, String vicinity, String place_id) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.vicinity = vicinity;
        this.place_id = place_id;
    }

    @PrimaryKey
    private String id;
    private String name;
    private String icon;
    private String vicinity;
    private String place_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

}

