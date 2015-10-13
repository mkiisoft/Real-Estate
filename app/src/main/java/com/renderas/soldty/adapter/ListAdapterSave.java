package com.renderas.soldty.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.renderas.soldty.PropertyActivity;
import com.renderas.soldty.R;
import com.renderas.soldty.sql.PropertyDB;
import com.renderas.soldty.utils.KeySaver;
import com.squareup.picasso.Picasso;

/**
 * List adapter for storing TODOs data
 *
 * @author itcuties
 */
public class ListAdapterSave extends ArrayAdapter<PropertyDB> {

    // List context
    private final Context context;
    // List values
    private final List<PropertyDB> cardList;

    // Animation Activity

    private static final String PACKAGE = "com.renderas.soldty";
    static float sAnimatorScale = 1;

    public ListAdapterSave(Context context, List<PropertyDB> cardList) {
        super(context, R.layout.fragment_favorites, cardList);
        this.context = context;
        this.cardList = cardList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TextView title, author, status, price;
        Typeface mThin, mBold, mLight;
        String subString = "";
        ImageView appicon;
        String bob_final, bob_usd;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.home_property_item, parent, false);

        mThin = Typeface.createFromAsset(rowView.getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(rowView.getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(rowView.getResources().getAssets(), "Light.ttf");

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int[] screenLocation = new int[2];
                arg0.getLocationOnScreen(screenLocation);
                int orientation = context.getResources().getConfiguration().orientation;

                Intent intent = new Intent(context, PropertyActivity.class);

                intent.putExtra("title", cardList.get(position).getTitle());
                intent.putExtra("full_image", cardList.get(position).getImgFull());
                intent.putExtra("list_thumb", cardList.get(position).getImgThumb());
                intent.putExtra("price", cardList.get(position).getPrice());
                intent.putExtra("status", cardList.get(position).getPropertyStatus());
                intent.putExtra("author_id", cardList.get(position).getAuthorID());
                intent.putExtra("agent", cardList.get(position).getAuthorName());
                intent.putExtra("agent_img", cardList.get(position).getAuthorImg());
                intent.putExtra("agent_email", cardList.get(position).getAuthorEmail());
                intent.putExtra("phone", cardList.get(position).getPhone());
                intent.putExtra("content", cardList.get(position).getDescription());
                intent.putExtra("ID", cardList.get(position).getPropertyID());
                intent.putExtra("property_type", cardList.get(position).getPropertyType());
                intent.putExtra("country", cardList.get(position).getPropertyCountry());
                intent.putExtra("city", cardList.get(position).getPropertyCity());
                intent.putExtra("lat_map", cardList.get(position).getMapLat());
                intent.putExtra("lng_map", cardList.get(position).getMapLng());
                intent.putExtra("bedrooms", cardList.get(position).getBedrooms());
                intent.putExtra("build_year", cardList.get(position).getBuildYear());
                intent.putExtra("condition", cardList.get(position).getCondition());
                intent.putExtra("area", cardList.get(position).getArea());
                intent.putExtra("premium", cardList.get(position).getPremium());
                intent.putExtra("gallery", cardList.get(position).getGallery());
                intent.putExtra("positionFav", String.valueOf(position));

                // Animation Activity
                intent.putExtra(PACKAGE + ".left", screenLocation[0]);
                intent.putExtra(PACKAGE + ".top", screenLocation[1]);
                intent.putExtra(PACKAGE + ".width", arg0.getWidth());
                intent.putExtra(PACKAGE + ".height", arg0.getHeight());
                intent.putExtra(PACKAGE + ".orientation", orientation);

                // Start SingleItemView Class
                context.startActivity(intent);

            }
        });

        appicon = (ImageView) rowView.findViewById(R.id.picture);

        Picasso.with(context).load(cardList.get(position).getImgThumb()).into(appicon);

        // Locate the TextViews in news_item.xml
        title = (TextView) rowView.findViewById(R.id.item_title);
        title.setText(cardList.get(position).getTitle().replace("&#8220;", "\"").replace("&#8221;", "\"").replace("&#8230;", "...").replace("&#8211;", "-").replace("&#8217;", "\'").replace("&amp;", "&"));
        title.setSelected(true);
        title.setTypeface(mLight);

        author = (TextView) rowView.findViewById(R.id.item_author);
        author.setText(cardList.get(position).getAuthorName().replace("&amp;", "&"));
        author.setSelected(true);
        author.setTypeface(mThin);

        status = (TextView) rowView.findViewById(R.id.status);
        status.setText(cardList.get(position).getPropertyStatus());
        status.setTypeface(mLight);

        price = (TextView) rowView.findViewById(R.id.price);
        bob_final = String.valueOf(Integer.parseInt(cardList.get(position).getPrice()) * Double.parseDouble(KeySaver.getStringSavedShare(context, "bob_rate")));
        bob_usd = cardList.get(position).getPrice();

        if (KeySaver.isExist(context, "exchange_true")) {
            price.setText("b$ " + bob_final.substring(0, bob_final.length() - 2));
        } else {
            price.setText("u$s " + bob_usd);
        }
        price.setTypeface(mLight);

        return rowView;
    }

}
