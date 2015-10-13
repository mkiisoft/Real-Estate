package com.renderas.soldty.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renderas.soldty.R;
import com.renderas.soldty.utils.KeySaver;
import com.renderas.soldty.utils.RoundedImageView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.renderas.soldty.sql.PropertyDB;
import com.squareup.picasso.Picasso;

/**
 * List adapter for storing TODOs data
 *
 * @author itcuties
 */
public class PropertyListAdapter extends CursorAdapter {

    // List context
    private final Context context;
    // List values
    private List<PropertyDB> cardList;
    TextView category;
    TextView title;
    TextView dateitem;
    TextView viewsitem;
    TextView commentsitem;
    TextView itemtitle;
    ImageView trending;
    ImageView supertrending;
    public RoundedImageView channelic;
    public RoundedImageView channelDelete;
    String subString = "";

    int idMarca;

    Typeface texto;
    Typeface textofat;

    // Animation Activity

    private static final String PACKAGE = "com.renderas.soldty";
    static float sAnimatorScale = 1;

    // Universal Image Loader
    private DisplayImageOptions options;
    private ImageLoader il = ImageLoader.getInstance();
    private DisplayImageOptions opts;
    private File cacheDir;
    private LayoutInflater cursorInflater;
    private String channel_title;
    private Cursor cursor;
    private TextView itemcategory;
    private String channel_category;
    private TextView appNumber;

    public PropertyListAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
        //this.cardList = cardList;

        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        cacheDir = StorageUtils.getCacheDirectory(context);

        opts = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheFileCount(100)
                .build();
        if (il.isInited()) {
            il.destroy();
        }
        il.init(config);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Declare Variables
        TextView title, author, status, price;
        Typeface mThin, mBold, mLight;
        String subString = "";
        ImageView appicon;
        LinearLayout premium;
        String bob_final, bob_usd;

        mThin = Typeface.createFromAsset(view.getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(view.getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(view.getResources().getAssets(), "Light.ttf");

        appicon = (ImageView) view.findViewById(R.id.picture);

        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex("list_thumb"))).into(appicon);

        // Locate the TextViews in news_item.xml
        title = (TextView) view.findViewById(R.id.item_title);
        title.setText(cursor.getString(cursor.getColumnIndex("title")).replace("&#8220;", "\"").replace("&#8221;", "\"").replace("&#8230;", "...").replace("&#8211;", "-").replace("&#8217;", "\'").replace("&amp;", "&"));
        title.setSelected(true);
        title.setTypeface(mLight);

        author = (TextView) view.findViewById(R.id.item_author);
        author.setText(cursor.getString(cursor.getColumnIndex("author_name")).replace("&amp;", "&"));
        author.setSelected(true);
        author.setTypeface(mThin);

        status = (TextView) view.findViewById(R.id.status);
        status.setText(cursor.getString(cursor.getColumnIndex("name_status")));
        status.setTypeface(mLight);

        price = (TextView) view.findViewById(R.id.price);
        premium = (LinearLayout) view.findViewById(R.id.premium_layout);

        if (cursor.getString(cursor.getColumnIndex("premium")).contentEquals("true")) {
            premium.setVisibility(View.VISIBLE);
        } else {
            premium.setVisibility(View.GONE);
        }

        bob_final = String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex("price"))) * Double.parseDouble(KeySaver.getStringSavedShare(context, "bob_rate")));
        bob_usd = cursor.getString(cursor.getColumnIndex("price"));

        if (KeySaver.isExist(context, "exchange_true")) {
            price.setText("b$ " + bob_final.substring(0, bob_final.length() - 2));
        } else {
            price.setText("u$s " + bob_usd);
        }

        price.setTypeface(mLight);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Cursor c = getCursor();

        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.home_property_item, parent, false);

        return v;
    }

}
