package com.renderas.soldty.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renderas.soldty.PropertyActivity;
import com.renderas.soldty.R;
import com.renderas.soldty.utils.KeySaver;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mariano on 26/08/2015.
 */
public class ListAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    //ImageLoader imageLoader;
    HashMap<String, String> resultp = new HashMap<String, String>();
    LinearLayout premium;
    private String bob_final, bob_usd;

    // Json Parser
    static String TITLE = "title";
    static String PRICE = "price";
    static String SCREEN = "list_thumb";
    static String FULLIMG = "full_image";
    static String AUTHORID = "author_id";
    static String AUTHORNAME = "author_name";
    static String AUTHORIMG = "author_image";
    static String AUTHOREMAIL = "author_email";
    static String AUTHORPHONE = "phone";
    static String CONTENT = "content";
    static String ID = "ID";
    static String TYPE = "property_type";
    static String COUNTRY = "country";
    static String CITY = "city";
    static String STATUS = "name_status";
    static String MAPLAT = "lat_map";
    static String MAPLNG = "lng_map";
    static String BEDROOMS = "bedrooms";
    static String BUILDYEAR = "built_year";
    static String CONDITION = "condition";
    static String AREA = "area";
    static String PREMIUM = "premium";
    static String GALLERY = "gallery_array";

    public ListAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView title, author, status, price;
        Typeface mThin, mBold, mLight;
        String subString = "";
        ImageView appicon;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.home_property_item, parent, false);
        // Get the position
        resultp = data.get(position);

        mThin = Typeface.createFromAsset(itemView.getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(itemView.getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(itemView.getResources().getAssets(), "Light.ttf");

        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int[] screenLocation = new int[2];
                arg0.getLocationOnScreen(screenLocation);
                int orientation = context.getResources().getConfiguration().orientation;

                // Get the position
                resultp = data.get(position);
                Intent intent = new Intent(context, PropertyActivity.class);
                intent.putExtra("title", resultp.get(TITLE));
                intent.putExtra("full_image", resultp.get(FULLIMG));
                intent.putExtra("list_thumb", resultp.get(SCREEN));
                intent.putExtra("price", resultp.get(PRICE));
                intent.putExtra("status", resultp.get(STATUS));
                intent.putExtra("author_id", resultp.get(AUTHORID));
                intent.putExtra("agent", resultp.get(AUTHORNAME));
                intent.putExtra("agent_img", resultp.get(AUTHORIMG));
                intent.putExtra("agent_email", resultp.get(AUTHOREMAIL));
                intent.putExtra("phone", resultp.get(AUTHORPHONE));
                intent.putExtra("content", resultp.get(CONTENT));
                intent.putExtra("ID", resultp.get(ID));
                intent.putExtra("property_type", resultp.get(TYPE));
                intent.putExtra("country", resultp.get(COUNTRY));
                intent.putExtra("city", resultp.get(CITY));
                intent.putExtra("lat_map", resultp.get(MAPLAT));
                intent.putExtra("lng_map", resultp.get(MAPLNG));
                intent.putExtra("bedrooms", resultp.get(BEDROOMS));
                intent.putExtra("build_year", resultp.get(BUILDYEAR));
                intent.putExtra("condition", resultp.get(CONDITION));
                intent.putExtra("area", resultp.get(AREA));
                intent.putExtra("premium", resultp.get(PREMIUM));
                intent.putExtra("gallery", resultp.get(GALLERY));
                intent.putExtra("positionFav", String.valueOf(position));

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Start SingleItemView Class
                context.startActivity(intent);

            }
        });

        appicon = (ImageView) itemView.findViewById(R.id.picture);

        Glide.with(context).load(resultp.get(SCREEN)).into(appicon);

        // Locate the TextViews in news_item.xml
        title = (TextView) itemView.findViewById(R.id.item_title);
        title.setText(resultp.get(TITLE).replace("&#8220;", "\"").replace("&#8221;", "\"").replace("&#8230;", "...").replace("&#8211;", "-").replace("&#8217;", "\'").replace("&amp;", "&"));
        title.setSelected(true);
        title.setTypeface(mLight);

        author = (TextView) itemView.findViewById(R.id.item_author);
        author.setText(resultp.get(AUTHORNAME).replace("&amp;", "&"));
        author.setSelected(true);
        author.setTypeface(mThin);

        status = (TextView) itemView.findViewById(R.id.status);
        status.setText(resultp.get(STATUS));
        status.setTypeface(mLight);

        premium = (LinearLayout) itemView.findViewById(R.id.premium_layout);

        if (resultp.get(PREMIUM).contentEquals("true")) {
            premium.setVisibility(View.VISIBLE);
        } else {
            premium.setVisibility(View.GONE);
        }

        price = (TextView) itemView.findViewById(R.id.price);
        bob_final = String.valueOf(Integer.parseInt(resultp.get(PRICE)) * Double.parseDouble(KeySaver.getStringSavedShare(context, "bob_rate")));
        bob_usd = resultp.get(PRICE);

        if (KeySaver.isExist(context, "exchange_true")) {
            price.setText("b$ " + bob_final.substring(0, bob_final.length() - 2));
        } else {
            price.setText("u$s " + bob_usd);
        }
        price.setTypeface(mLight);

        return itemView;
    }
}