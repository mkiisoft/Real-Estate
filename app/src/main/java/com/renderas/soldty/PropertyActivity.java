package com.renderas.soldty;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;

import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.renderas.soldty.sql.ContentDAO;
import com.renderas.soldty.utils.AspectRatioImageView;
import com.renderas.soldty.utils.ExpandableHeightGridView;
import com.renderas.soldty.utils.FloatingActionButton;
import com.renderas.soldty.utils.JSONArrayfunctions;
import com.renderas.soldty.utils.JSONfunctions;
import com.renderas.soldty.utils.KeySaver;
import com.renderas.soldty.utils.RoundedImageView;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.AbstractHttpEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.views.ParallaxScrollView;

import com.loopj.android.http.*;
import com.renderas.soldty.utils.SquareImageView;
import com.renderas.soldty.utils.WorkAroundMapFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by Mariano on 03/08/2015.
 */
public class PropertyActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private String mTitle, mMainPhoto, mThumbPhoto, mPrice, mAgent, mAgentID,
            mAgentEmail, mAgentPhoto, mDetailsContent, mPropertyType, mCountry, mCity,
            ID, mMapLat, mMapLng, mStatus, mBedrooms, mBuildYear, mCondition,
            mPremium, mArea, mAgentPhone, mGalleryArray, mPosition;
    private ColorDrawable cd;
    private int mPositionInt;

    // Universal Image Loader

    private DisplayImageOptions options;
    private ImageLoader il = ImageLoader.getInstance();
    private DisplayImageOptions opts;
    private File cacheDir;

    // Widgets

    private AspectRatioImageView mFullImage;
    private TextView mDetailsPrice;
    private TextView mAuthor;
    private RoundedImageView mAuthorImg;
    private TextView mContent;
    private ParallaxScrollView scrollView;
    private Button mViewMore;
    private TextView mType;
    private TextView mCountryCity;
    private Typeface mThin, mBold, mLight;
    private LinearLayout mAuthorLayout;

    // Google Maps
    GoogleMap googleMap;
    MapView mapView;

    // Decode

    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    ArrayList<HashMap<String, String>> arraylist;
    private FloatingActionButton saveBtn, emailBtn, callBtn;
    private MarkerOptions markerOptions;
    private TextView mDetailsBedrooms;
    private TextView mBuilding;
    private TextView mEstado;
    private TextView mSqrFeet;
    private String bob_final;
    private ExpandableHeightGridView mGallery;

    private static final String PACKAGE = "com.renderas.soldty";

    static String THUMB = "gallery_thumb";
    static String FULL = "gallery_full";
    private ScaleInAnimationAdapter ScaleInAnimationAdapter;
    ArrayList<HashMap<String, String>> arraygallery;
    private GridViewAdapter adapter;
    private LinearLayout mGalleryLayout;
    private Handler handler;
    private ContentDAO doa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);

        cd = new ColorDrawable(Color.parseColor("#fa1505"));
        cd.setAlpha(0);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        mThin = Typeface.createFromAsset(getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(getResources().getAssets(), "Light.ttf");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(cd);

        mAuthorLayout = (LinearLayout) findViewById(R.id.segment_author);

        final Intent propertyIntent = getIntent();

        if (propertyIntent != null && propertyIntent.getStringExtra("from_agent") != null) {
            mAuthorLayout.setVisibility(View.GONE);
        } else {
            mAuthorLayout.setVisibility(View.VISIBLE);
        }

        mPosition = propertyIntent.getStringExtra("positionFav");
        mPositionInt = Integer.parseInt(mPosition);
        Log.e("Position", String.valueOf(mPositionInt));
        mTitle = propertyIntent.getStringExtra("title");
        mStatus = propertyIntent.getStringExtra("status");
        mAgent = propertyIntent.getStringExtra("agent").replace("&amp;", "&");
        mAgentID = propertyIntent.getStringExtra("author_id");
        mAgentPhoto = propertyIntent.getStringExtra("agent_img");
        mAgentEmail = propertyIntent.getStringExtra("agent_email");
        mAgentPhone = propertyIntent.getStringExtra("phone");
        mThumbPhoto = propertyIntent.getStringExtra("list_thumb");
        mMainPhoto = propertyIntent.getStringExtra("full_image");
        mPrice = propertyIntent.getStringExtra("price");
        mDetailsContent = propertyIntent.getStringExtra("content");
        mPropertyType = propertyIntent.getStringExtra("property_type");
        mCountry = propertyIntent.getStringExtra("country");
        mCity = propertyIntent.getStringExtra("city");
        ID = propertyIntent.getStringExtra("ID");
        mMapLat = propertyIntent.getStringExtra("lat_map");
        mMapLng = propertyIntent.getStringExtra("lng_map");

        mBedrooms = propertyIntent.getStringExtra("bedrooms");
        mBuildYear = propertyIntent.getStringExtra("build_year");
        mCondition = propertyIntent.getStringExtra("condition");
        mArea = propertyIntent.getStringExtra("area");
        mPremium = propertyIntent.getStringExtra("premium");

        mGalleryArray = propertyIntent.getStringExtra("gallery");

        mGalleryLayout = (LinearLayout) findViewById(R.id.gallery_layout);
        mGallery = (ExpandableHeightGridView) findViewById(R.id.gallery_grid);

        int mGetBuildingOld = year - Integer.parseInt(mBuildYear);

        String mBuildOld = String.valueOf(mGetBuildingOld);

        doa = new ContentDAO(getApplicationContext());
        int favid = Integer.parseInt(ID);

        saveBtn = (FloatingActionButton) findViewById(R.id.save_floating);
        emailBtn = (FloatingActionButton) findViewById(R.id.email_floating);
        callBtn = (FloatingActionButton) findViewById(R.id.call_floating);

        if(doa != null){
            favid = doa.existFavorNot(ID, "0");
        }

        if(favid>0){
            saveBtn.setIcon(R.drawable.ic_star_white_24dp);
        }else{
            saveBtn.setIcon(R.drawable.ic_star_empty_white_24dp);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            private int favid;
            @Override
            public void onClick(View v) {
                favid = doa.existFavorNot(propertyIntent.getStringExtra("ID"), "0");
                if(favid>0){
                    doa.deleteFavOrNot(Integer.valueOf(favid));
                    saveBtn.setIcon(R.drawable.ic_star_empty_white_24dp);
                }else{
                    doa.createFavOrNot(mPositionInt, mTitle, mPrice, mThumbPhoto, mMainPhoto, mAgentID, mAgent,
                            mAgentPhoto, mAgentEmail, mAgentPhone, mDetailsContent, ID, mPropertyType, mCountry, mCity,
                            mStatus, mMapLat, mMapLng, mBedrooms, mBuildYear, mCondition, mArea, mPremium, mGalleryArray, "0");
                    saveBtn.setIcon(R.drawable.ic_star_white_24dp);
                }
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mAgentEmail});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre la propiedad: " + mTitle);
                intent.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(intent, "Enviar email:"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "No cuentas con apps para envio de Email", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                }
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mAgentPhone.trim() ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        mFullImage = (AspectRatioImageView) findViewById(R.id.fullimg);
        mAuthorImg = (RoundedImageView) findViewById(R.id.profile_settings_img);
        mDetailsPrice = (TextView) findViewById(R.id.details_price);

        bob_final = String.valueOf(Integer.parseInt(mPrice) * Double.parseDouble(KeySaver.getStringSavedShare(this, "bob_rate")));

        if (KeySaver.isExist(this, "exchange_true")) {
            mDetailsPrice.setText("b$ " + bob_final.substring(0, bob_final.length() - 2));
        } else {
            mDetailsPrice.setText("u$s " + mPrice);
        }

        mDetailsPrice.setTypeface(mBold);

        mDetailsBedrooms = (TextView) findViewById(R.id.details_bedrooms);
        mDetailsBedrooms.setText(mBedrooms);
        mDetailsBedrooms.setTypeface(mBold);

        mBuilding = (TextView) findViewById(R.id.details_old);
        mBuilding.setText(mBuildOld + " años");
        mBuilding.setTypeface(mBold);

        mEstado = (TextView) findViewById(R.id.details_condition);
        mEstado.setText(mCondition);
        mEstado.setTypeface(mBold);

        mSqrFeet = (TextView) findViewById(R.id.details_sqmeters);
        mSqrFeet.setText(mArea);
        mSqrFeet.setTypeface(mBold);

        mAuthor = (TextView) findViewById(R.id.authors);
        mAuthor.setText(mAgent);
        mAuthor.setTypeface(mLight);

        mContent = (TextView) findViewById(R.id.text_details_property);
        mContent.setText(Html.fromHtml(mDetailsContent));
        mContent.setTypeface(mLight);

        mType = (TextView) findViewById(R.id.property_type);
        mType.setText(mPropertyType + " en " + mStatus);
        mType.setTypeface(mBold);

        mCountryCity = (TextView) findViewById(R.id.country_city);
        if (!mCity.contentEquals("")) {
            mCountryCity.setText(mCountry + " - " + mCity);
        } else {
            mCountryCity.setText(mCountry);
        }

        mCountryCity.setTypeface(mLight);

        getSupportActionBar().setTitle(mTitle);

        mViewMore = (Button) findViewById(R.id.view_more);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mViewMore.setElevation(0);
        }

        mViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AgentActivity.class);
                intent.putExtra("agent_id", mAgentID);
                intent.putExtra("agent_name", mAgent);
                intent.putExtra("agent_image", mAgentPhoto);
                startActivity(intent);
            }
        });

        scrollView = (ParallaxScrollView) findViewById(R.id.scroll_view);
        if (scrollView instanceof Parallaxor) {
            ((Parallaxor) scrollView).parallaxViewBy(mFullImage, 0.6f);
        }

        scrollView.setOnScrollViewListener(new ParallaxScrollView.OnScrollViewListener() {

            @Override
            public void onScrollChanged(ParallaxScrollView v, int l, int t,
                                        int oldl, int oldt) {
                cd.setAlpha(getAlphaforActionBar(v.getScrollY()));
            }

        });

        final LatLng LATLNG = new LatLng(Double.parseDouble(mMapLat), Double.parseDouble(mMapLng));

        if (googleMap == null) {
            googleMap = ((WorkAroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            ((WorkAroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView))
                    .setListener(new WorkAroundMapFragment.OnTouchListener() {
                        @Override
                        public void onTouch() {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                        }
                    });

            if (mPremium.contentEquals("true")) {
                Marker melbourne = googleMap.addMarker(new MarkerOptions()
                        .position(LATLNG)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_premium)));
            } else {
                Marker melbourne = googleMap.addMarker(new MarkerOptions()
                        .position(LATLNG)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_default)));
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LATLNG, 10));

            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

//            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//                @Override
//                public void onMapClick(LatLng arg0) {

//            });

            markerOptions = new MarkerOptions();
            markerOptions.position(LATLNG);

            new ReverseGeocodingTask(getBaseContext()).execute(LATLNG);

        }

        Glide.with(this).load(mMainPhoto).into(mFullImage);

        Glide.with(this).load(mAgentPhoto).into(mAuthorImg);


        new createGallery().execute();
    }

    private class createGallery extends AsyncTask<Void, Void, Boolean> {

        Exception error;

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(Void... params) {
            try {
                arraylist = new ArrayList<HashMap<String, String>>();

                JSONArray main_gallery = new JSONArray(mGalleryArray);

                for (int e = 0; e < main_gallery.length(); e++) {
                    HashMap<String, String> mapa = new HashMap<String, String>();

                    JSONObject item_gallery = main_gallery.getJSONObject(e);

                    JSONObject sizes = item_gallery.getJSONObject("sizes");
                    mapa.put("gallery_thumb", sizes.getString("thumbnail"));
                    mapa.put("gallery_full", sizes.getString("javo-huge"));

                    arraylist.add(mapa);
                }

                return true;
            } catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {

            try {
                if (result) {
//                    adapter = new GridViewAdapter(getApplicationContext(), arraylist);
                    ScaleInAnimationAdapter = new ScaleInAnimationAdapter(new GridViewAdapter(PropertyActivity.this, arraylist));
                    ScaleInAnimationAdapter.setAbsListView(mGallery);

                    assert ScaleInAnimationAdapter.getViewAnimator() != null;
                    ScaleInAnimationAdapter.getViewAnimator().setInitialDelayMillis(400);
                    ScaleInAnimationAdapter.getViewAnimator().setAnimationDurationMillis(600);
                    if (ScaleInAnimationAdapter.getCount() == 0) {
                        mGalleryLayout.setVisibility(View.GONE);
                    }

                    mGallery.setAdapter(ScaleInAnimationAdapter);
                    mGallery.setExpanded(true);
                }
            } catch (Exception e) {

            }
        }

    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;

            List<Address> addresses = null;
            String addressText = "";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                addressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            // Clear previous marker.
            googleMap.clear();
            // This will be displayed on taping the marker
            markerOptions.title(mTitle);
            markerOptions.snippet(addressText);
            if (mPremium.contentEquals("true")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_premium));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_default));
            }


            // Placing a marker on the touched position
            googleMap.addMarker(markerOptions).showInfoWindow();

        }
    }

    String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getAlphaforActionBar(int scrollY) {
        int minDist = 0, maxDist = 550;
        if (scrollY > maxDist) {
            return 255;
        } else {
            if (scrollY < minDist) {
                return 0;
            } else {
                return (int) ((255.0 / maxDist) * scrollY);
            }
        }
    }

    private class updateInfo extends AsyncTask<Void, Void, Boolean> {

        private static final int LOADER_CURSOR = 0;
        private LoaderManager.LoaderCallbacks mCursorCallbacks;
        Exception error;

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Boolean doInBackground(Void... params) {
            try {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://dev.soldty.com/wp-json/posts/" + ID + "/meta/4245");
                post.setHeader("Accept", "application/json");
                post.setHeader("User-Agent", "Apache-HttpClient/4.1 (java 1.5)");
                post.setHeader("Authorization", getB64Auth("soldtyusers", "Lcde3@upmcd"));
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("value", "750000"));
                AbstractHttpEntity ent = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
                ent.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
                ent.setContentEncoding("UTF-8");
                post.setEntity(ent);
                post.setURI(new URI("http://dev.soldty.com/wp-json/posts/" + ID + "/meta/4245"));
                HttpResponse response = client.execute(post);

                return true;

            } catch (Exception e) {
                error = e;

                return false;
            }
        }

        protected void onPostExecute(Boolean result) {

            try {
                if (result) {

                } else {
                    Toast.makeText(PropertyActivity.this, "Error al contactar el servidor", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            }

        }
    }

    private String getB64Auth(String login, String pass) {
        String source = login + ":" + pass;
        String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }

    public class GridViewAdapter extends BaseAdapter {

        // Declare Variables
        Context context;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> data;
        //ImageLoader imageLoader;
        HashMap<String, String> resultp = new HashMap<String, String>();

        public GridViewAdapter(Context context,
                               ArrayList<HashMap<String, String>> arraylist) {
            this.context = context;
            data = arraylist;

            cacheDir = StorageUtils.getCacheDirectory(context);

            opts = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageForEmptyUri(R.drawable.ic_profile)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    .diskCache(new UnlimitedDiscCache(cacheDir))
                    .build();
            if (il.isInited()) {
                il.destroy();
            }
            il.init(config);
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
            SquareImageView appicon;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.gridview_item, parent, false);
            // Get the position
            resultp = data.get(position);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    int[] screenLocation = new int[2];
                    arg0.getLocationOnScreen(screenLocation);
                    int orientation = getResources().getConfiguration().orientation;

                    // Get the position
                    resultp = data.get(position);
                    Intent intent = new Intent(PropertyActivity.this, FullImageActivity.class);
                    // Pass all data imagen
                    intent.putExtra("image_full", resultp.get(FULL));

                    // Animation Activity
                    intent.putExtra(PACKAGE + ".left", screenLocation[0]);
                    intent.putExtra(PACKAGE + ".top", screenLocation[1]);
                    intent.putExtra(PACKAGE + ".width", arg0.getWidth());
                    intent.putExtra(PACKAGE + ".height", arg0.getHeight());
                    intent.putExtra(PACKAGE + ".orientation", orientation);

                    // Start SingleItemView Class
                    startActivity(intent);

//                    getActivity().overridePendingTransition(0, 0);

                }
            });

            appicon = (SquareImageView) itemView.findViewById(R.id.grid_thumb_img);

            il.displayImage(resultp.get(THUMB), appicon, opts, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String s, View itemView) {

                }

                @Override
                public void onLoadingFailed(String s, View itemView, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View itemView, Bitmap bitmap) {
                    SquareImageView imageView = (SquareImageView) itemView;
                    if (bitmap != null) {
                        FadeInBitmapDisplayer.animate(imageView, 500);
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }

            });

            return itemView;
        }
    }
}
