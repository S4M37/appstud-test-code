package com.appstud.appstud_testcode.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appstud.appstud_testcode.R;
import com.appstud.appstud_testcode.config.Endpoints;
import com.appstud.appstud_testcode.models.GoogleSearchModel;
import com.appstud.appstud_testcode.views.NetworkImageView;

import java.util.List;


public class GoogleSearchRecyclerViewAdapter extends RecyclerView.Adapter<GoogleSearchRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private List<GoogleSearchModel> itemlist;

    public GoogleSearchRecyclerViewAdapter(Context context, List<GoogleSearchModel> itemlist) {
        this.context = context;
        this.itemlist = itemlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.google_search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GoogleSearchModel googleSearchModel = itemlist.get(position);
        holder.label.setText(googleSearchModel.getName());
        if (googleSearchModel.getPhotos() != null && googleSearchModel.getPhotos().length > 0) {
            //// TODO: 22/11/2017 always got google maps quota error
            // example :
            // https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyCJ-ZXF9bJLte4_kTa2vBCvXdAsXLTeKuA&photoreference=CmRaAAAAtZ-C2y_VQ7O__J30NbL2zjgzqRCid4b9f0qTGYQHAW4eK9oICLv1CnzH3m2DHdYRfoBVN4CJI9hgNOrBFxm2M9CR5W74QDkjV0ukDJjBIQARmCLO2s8s1k4XGYM5NfaPEhB3hUys3214ygbwgFVRr28YGhRqRSvPd3uYDrKCcD9gAJ_SjGOPOg
            holder.img.setImageUrl(Endpoints.googlePhotos + "?key=" + context.getString(R.string.google_api_key) +
                    "&photoreference=" + googleSearchModel.getPhotos()[0].photo_reference);
            Log.d("image", "onBindViewHolder: " + Endpoints.googlePhotos + "?key=" + context.getString(R.string.google_api_key) +
                    "&photoreference=" + googleSearchModel.getPhotos()[0].photo_reference + "&maxwidth=1000&&maxheight=500");
        } else {
            holder.img.setImageUrl(googleSearchModel.getIcon());
        }
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        NetworkImageView img;

        ViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.item_label);
            img = (NetworkImageView) itemView.findViewById(R.id.item_img);
        }
    }

    public void setItemlist(List<GoogleSearchModel> itemlist) {
        this.itemlist = itemlist;
        notifyDataSetChanged();
    }
}
