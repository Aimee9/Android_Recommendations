package com.epicodus.recommendations;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;


import com.epicodus.recommendations.api.Etsy;
import com.epicodus.recommendations.google.GoogleServicesHelper;
import com.epicodus.recommendations.model.ActiveListings;
import com.epicodus.recommendations.model.Listing;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusOneButton;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Guest on 11/10/15.
 */
public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
implements Callback<ActiveListings>, GoogleServicesHelper.GoogleServicesListener {

    public static final int REQUEST_CODE_PLUS_ONE = 10;
    public static final int REQUEST_CODE_SHARE = 11;

    private MainActivity activity;
    private LayoutInflater inflater;
    private ActiveListings activeListings;

    private boolean isGooglePlayServicesAvailable;

    public ListingAdapter(MainActivity activity) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.isGooglePlayServicesAvailable = false;

    }

    @Override
    public ListingHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ListingHolder(inflater.inflate(R.layout.layout_listing, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ListingHolder listingHolder, int i) {
        final Listing listing = activeListings.results[i];
        listingHolder.titleView.setText(listing.title);
        listingHolder.priceView.setText(listing.price);
        listingHolder.shopNameView.setText(listing.Shop.shop_name);

        if (isGooglePlayServicesAvailable) {
            listingHolder.plusOneButton.setVisibility(View.VISIBLE);
            listingHolder.plusOneButton.initialize(listing.url, REQUEST_CODE_PLUS_ONE);
            listingHolder.plusOneButton.setAnnotation(PlusOneButton.ANNOTATION_NONE);

            listingHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new PlusShare.Builder(activity)
                            .setType("text/plain")
                            .setText("Checkout this item on Etsy" + listing.title)
                            .setContentUrl(Uri.parse(listing.url))
                            .getIntent();

                    activity.startActivityForResult(intent, REQUEST_CODE_SHARE);
                }
            });
        } else {
            listingHolder.plusOneButton.setVisibility(View.GONE);

            listingHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Checkout this item on Etsy " + listing.title + " " + listing.url);
                    intent.setType("text/plain");

                    activity.startActivityForResult(Intent.createChooser(intent, "Share"), REQUEST_CODE_SHARE);
                }
            });
        }

        Picasso.with(listingHolder.imageView.getContext())
                .load(listing.Images[0].url_570xN)
                .into(listingHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if (activeListings == null)
            return 0;

        if(activeListings.results == null)
            return 0;

        return activeListings.results.length;
    }

    @Override
    public void success(ActiveListings activeListings, Response response) {
        this.activeListings = activeListings;
        notifyDataSetChanged();
        this.activity.showList();
    }

    @Override
    public void failure(RetrofitError error) {
        this.activity.showError();
    }

    public ActiveListings getActiveListings() {
        return activeListings;
    }

    @Override
    public void onConnected() {

        if (getItemCount() == 0) {
            Etsy.getActiveListings(this);
        }
        isGooglePlayServicesAvailable = true;
        notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {

        if (getItemCount() == 0) {
            Etsy.getActiveListings(this);
        }
        isGooglePlayServicesAvailable = false;
        notifyDataSetChanged();
    }

    public class ListingHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleView;
        public TextView shopNameView;
        public TextView priceView;
        public PlusOneButton plusOneButton;
        public ImageView shareButton;

        public ListingHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.listing_image);
            titleView = (TextView) itemView.findViewById(R.id.listing_title);
            shopNameView = (TextView) itemView.findViewById(R.id.listing_shop_name);
            priceView = (TextView) itemView.findViewById(R.id.listing_price);
            plusOneButton = (PlusOneButton) itemView.findViewById(R.id.listing_plus_one_btn);
            shareButton = (ImageView) itemView.findViewById(R.id.listing_share_btn);
        }
    }
}
