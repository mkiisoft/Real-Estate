package com.renderas.soldty;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paging.listview.PagingListView;
import com.renderas.soldty.adapter.PropertyListAdapter;
import com.renderas.soldty.sql.PropertyProvider;
import com.renderas.soldty.utils.FloatingActionButton;
import com.renderas.soldty.utils.JSONArrayfunctions;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.renderas.soldty.utils.CircularProgressBar;
import com.renderas.soldty.utils.KeySaver;
import com.renderas.soldty.utils.Utils;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.ailis.pherialize.MixedArray;
import de.ailis.pherialize.Pherialize;

import static com.renderas.soldty.sql.PropertyProvider.*;


public class Fragment_Home extends StatedFragment implements LocationListener {

    // Property
    JSONArray jsonnews;
    JSONArray jsonarray;
    JSONObject json_object;
    ArrayList<HashMap<String, String>> arraylist;
//    ListViewAdapter adapter;

    // ListView
    private ListView mListView;
    private CircularProgressBar progressCircular;

    // Universal Image Loader
    private DisplayImageOptions options;
    private ImageLoader il = ImageLoader.getInstance();
    private DisplayImageOptions opts;
    protected File cacheDir;

    // Typefaces
    private Typeface mThin, mBold, mLight;

    Context context;

    // Animation Activity

    private static final String PACKAGE = "com.renderas.soldty";
    static float sAnimatorScale = 1;

    // Json Parser
    static String TITLE = "title";
    static String PRICE = "price";
    static String SCREEN = "list_thumb";
    static String FULLIMG = "full_image";
    static String AUTHORID = "author_id";
    static String AUTHORNAME = "author_name";
    static String AUTHORIMG = "author_image";
    static String AUTHOREMAIL = "author_email";
    static String CONTENT = "content";
    static String WEBID = "ID";
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

    // Paginator
    private int pager = 1;

    // Swipe Layout
    private SwipeRefreshLayout swipeView;

    // Google Maps
    GoogleMap googleMap;
    MapView mapView;
    private int RESULT_OK;
    private int RESULT_CANCELED;

    // Exchange
    public String bob_final;
    public TextView price;
    public String bob_usd;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

    // Settings Premium
    public String parseURL = null;
    public String baseURL = "http://dev.soldty.com/wp-json/posts?type=property";
    private boolean connready;

    // Content Provider Variables
    private PropertyListAdapter PropertyAdapter;
    private Cursor c;
    private SQLiteDatabase database;
    private FloatingActionButton mapBtn;

    public Fragment_Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        if (KeySaver.isExist(getActivity(), "premium_true")) {
            parseURL = baseURL + parseURLPremium();
        } else {
            parseURL = baseURL + parseURLNormal();
        }

        mThin = Typeface.createFromAsset(rootView.getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(rootView.getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(rootView.getResources().getAssets(), "Light.ttf");
        
        // Content Provider

        progressCircular = (CircularProgressBar) rootView.findViewById(R.id.circularProgress);

        mListView = (ListView) rootView.findViewById(R.id.home_list_view);

        String URL = "content://com.renderas.soldty.sql.PropertyProvider/property";
        Uri property = Uri.parse(URL);
        c = getActivity().getContentResolver().query(property, null, null, null, "_id ASC");

        PropertyAdapter = new PropertyListAdapter(getActivity(), c);

        dropTable();

        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new PropertyListAdapter(getActivity(), c));
        swingBottomInAnimationAdapter.setAbsListView(mListView);

        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
        swingBottomInAnimationAdapter.getViewAnimator().setAnimationDurationMillis(400);
        mListView.setAdapter(swingBottomInAnimationAdapter);

        mapView = (MapView) rootView.findViewById(R.id.mapViewHome);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        mapBtn = (FloatingActionButton) rootView.findViewById(R.id.map_floating);
        mapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("pathURL", parseURL);
                startActivity(intent);
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);

        connready = Utils.testConection(getActivity());
        if (connready) {
            AsyncConnection(parseURL + String.valueOf(pager));
        } else {
            Toast.makeText(getActivity(), "Revisa tu conexion para ver las propiedades", Toast.LENGTH_SHORT).show();

        }

        myLocation();

        swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        swipeView.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_red_dark, android.R.color.holo_red_light, android.R.color.holo_red_dark);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        try {
                            dropTable();
                            pager = 1;
                            AsyncConnection(parseURL + String.valueOf(pager));
                            swingBottomInAnimationAdapter.notifyDataSetChanged();
                        } catch (Exception e) {

                        }
                    }
                }, 500);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                c.moveToPosition(position);

                Intent intent = new Intent(getActivity(), PropertyActivity.class);
                intent.putExtra("title", c.getString(c.getColumnIndex("title")));
                intent.putExtra("full_image", c.getString(c.getColumnIndex("full_image")));
                intent.putExtra("list_thumb", c.getString(c.getColumnIndex("list_thumb")));
                intent.putExtra("price", c.getString(c.getColumnIndex("price")));
                intent.putExtra("status", c.getString(c.getColumnIndex("name_status")));
                intent.putExtra("author_id", c.getString(c.getColumnIndex("author_id")));
                intent.putExtra("agent", c.getString(c.getColumnIndex("author_name")));
                intent.putExtra("agent_img", c.getString(c.getColumnIndex("author_image")));
                intent.putExtra("agent_email", c.getString(c.getColumnIndex("author_email")));
                intent.putExtra("phone", c.getString(c.getColumnIndex("phone")));
                intent.putExtra("content", c.getString(c.getColumnIndex("content")));
                intent.putExtra("ID", c.getString(c.getColumnIndex("ID")));
                intent.putExtra("property_type", c.getString(c.getColumnIndex("property_type")));
                intent.putExtra("country", c.getString(c.getColumnIndex("country")));
                intent.putExtra("city", c.getString(c.getColumnIndex("city")));
                intent.putExtra("lat_map", c.getString(c.getColumnIndex("lat_map")));
                intent.putExtra("lng_map", c.getString(c.getColumnIndex("lng_map")));
                intent.putExtra("bedrooms", c.getString(c.getColumnIndex("bedrooms")));
                intent.putExtra("build_year", c.getString(c.getColumnIndex("built_year")));
                intent.putExtra("condition", c.getString(c.getColumnIndex("condition")));
                intent.putExtra("area", c.getString(c.getColumnIndex("area")));
//                intent.putExtra("bedrooms", c.getString(c.getColumnIndex("bedrooms")));
                intent.putExtra("premium", c.getString(c.getColumnIndex("premium")));
                intent.putExtra("gallery", c.getString(c.getColumnIndex("gallery_array")));
                intent.putExtra("positionFav", String.valueOf(position));

                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onLocationChanged(Location location) {

//        googleMap.clear();
        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

        mp.title("Mi Ubicación");
//        mp.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));

//        googleMap.addMarker(mp);

        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    public void myLocation(){
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 20000, 0, this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class PropertyClient {

        private AsyncHttpClient client = new AsyncHttpClient();

        public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(getAbsoluteUrl(url), params, responseHandler);
        }

        private String getAbsoluteUrl(String relativeUrl) {
            return relativeUrl;
        }
    }

    public void dropTable(){
        if(c.getCount() > 0){
            String providerURL = "content://com.renderas.soldty.sql.PropertyProvider/property";
            Uri properties = Uri.parse(providerURL);
            getActivity().getContentResolver().delete(properties, null, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == -1) {
                if (KeySaver.isExist(getActivity(), "premium_true")) {
                    dropTable();
//                    mListView.setAdapter(swingBottomInAnimationAdapter);
                    parseURL = baseURL + parseURLPremium();
                    pager = 1;
                    AsyncConnection(parseURL + String.valueOf(pager));
                    swingBottomInAnimationAdapter.notifyDataSetChanged();
                }
            }
            if (resultCode == 0) {
                if (!KeySaver.isExist(getActivity(), "premium_true")) {
                    dropTable();
                    pager = 1;
                    parseURL = baseURL + parseURLNormal();
                    AsyncConnection(parseURL + String.valueOf(pager));
                    swingBottomInAnimationAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (swingBottomInAnimationAdapter != null) {
            swingBottomInAnimationAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.e("Se va", "se fue");
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivityForResult(intent, 123);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String parseURLPremium() {
        return "&filter[meta_key]=premium&filter[meta_value]=1&filter[posts_per_page]=10&page=";
    }

    public String parseURLNormal() {
        return "&filter[meta_key]=premium&filter[meta_type]=DECIMAL&filter[order]=DESC&filter[orderby]=meta_value_num&filter[posts_per_page]=10&page=";
    }

    public boolean isCursorEmpty(Cursor cursor){
        if(!cursor.moveToFirst() || cursor.getCount() == 0){
            return true;
        }
        return false;
    }

    public void AsyncConnection(String urlConnection) {

        new PropertyClient().get(urlConnection, null, new AsyncHttpResponseHandler() {

                    private static final int LOADER_CURSOR = 0;
                    private android.app.LoaderManager.LoaderCallbacks mCursorCallbacks;
                    Exception error;

                    @Override
                    public void onStart() {
                        if(pager <= 1){
                            progressCircular.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] response) {
                        final int totalPages = Integer.parseInt(Utils.convertHeadersToHashMap(headers).get("X-WP-TotalPages"));

                        try {
                            arraylist = new ArrayList<HashMap<String, String>>();
                            // Retrieve JSON Objects from the given URL address
                            jsonnews = new JSONArray(Utils.decodeUTF8(response));

                            for (int async = 0; async < jsonnews.length(); async++) {
                                ContentValues values = new ContentValues();
                                HashMap<String, String> map = new HashMap<String, String>();

                                json_object = jsonnews.getJSONObject(async);

                                // Retrive JSON Objects
                                values.put(WEBID, json_object.getString("ID"));
                                values.put(TITLE, json_object.getString("title"));
                                values.put(CONTENT, json_object.getString("content"));
                                JSONObject json_author = json_object.getJSONObject("author");
                                JSONObject json_meta_user = json_author.getJSONObject("meta");
                                JSONObject json_meta_sub = json_meta_user.getJSONObject("meta");
                                JSONArray json_phone = json_meta_sub.getJSONArray("phone");
                                values.put(AUTHORID, json_author.getString("ID"));
                                values.put(AUTHORNAME, json_author.getString("name"));
                                values.put(AUTHORIMG, json_author.getString("avatar"));
                                values.put(AUTHOREMAIL, json_author.getString("email_contacto"));
                                values.put(AUTHOREPHONE, json_phone.getString(0));
                                JSONObject json_map = json_object.getJSONObject("latlng");
                                values.put(MAPLAT, json_map.getString("lat"));
                                values.put(MAPLNG, json_map.getString("lng"));
                                JSONObject json_feature_image = json_object.getJSONObject("featured_image");
                                JSONObject json_image_meta = json_feature_image.getJSONObject("attachment_meta");
                                JSONObject json_sizes = json_image_meta.getJSONObject("sizes");
                                JSONObject json_thumbnail = json_sizes.getJSONObject("javo-small");
                                JSONObject json_large = json_sizes.getJSONObject("javo-large");
                                values.put(SCREEN, json_thumbnail.getString("url"));
                                values.put(FULLIMG, json_large.getString("url"));
                                JSONObject json_terms = json_object.getJSONObject("terms");
                                JSONArray json_city = json_terms.getJSONArray("property_city");
                                for (int c = 0; c < json_city.length(); c++) {
                                    JSONObject json_country = json_city.getJSONObject(0);
                                    values.put(COUNTRY, json_country.getString("name"));
                                    if (c > 0) {
                                        JSONObject json_the_city = json_city.getJSONObject(1);
                                        values.put(CITY, json_the_city.getString("name"));
                                    } else {
                                        values.put(CITY, "");
                                    }

                                }

                                JSONArray json_property_type = json_terms.getJSONArray("property_type");
                                for (int t = 0; t < json_property_type.length(); t++) {
                                    JSONObject json_obj_type = json_property_type.getJSONObject(t);
                                    values.put(TYPE, json_obj_type.getString("name"));
                                }

                                JSONArray json_status = json_terms.getJSONArray("property_status");
                                for (int e = 0; e < json_status.length(); e++) {
                                    JSONObject json_obj = json_status.getJSONObject(e);
                                    values.put(STATUS, json_obj.getString("name"));
                                }

                                JSONArray json_price = json_object.getJSONArray("sale_price");
                                values.put(PRICE, json_price.getString(0));
//                                for (int p = 0; p < json_price.length(); p++) {
//
//                                }

                                JSONArray json_bedrooms = json_object.getJSONArray("bedrooms");

                                for (int b = 0; b < json_bedrooms.length(); b++) {
                                    values.put(BEDROOMS, json_bedrooms.getString(0));
                                }

                                JSONArray json_year = json_object.getJSONArray("built_year");

                                for (int y = 0; y < json_year.length(); y++) {
                                    values.put(BUILDYEAR, json_year.getString(0));
                                }

                                JSONArray json_area = json_object.getJSONArray("area");

                                for (int ar = 0; ar < json_area.length(); ar++) {
                                    values.put(AREA, json_area.getString(0));
                                }

                                try {
                                    boolean bool = json_object.getBoolean("detail_images");

                                    if (bool == false) {
                                        values.put(GALLERY, "[]");
                                    }
                                } catch (JSONException e) {
                                    JSONArray json_gallery = json_object.getJSONArray("detail_images");
                                    values.put(GALLERY, String.valueOf(json_gallery));
                                }
                                values.put(CONDITION, json_object.getString("estado"));
                                values.put(PREMIUM, String.valueOf(json_object.getBoolean("premium")));

                                Uri uri = getActivity().getContentResolver().insert(CONTENT_URI, values);
                            }
                        } catch (Exception e) {

                        }
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                if (pager <= 1) {
                                                                    googleMap.clear();
                                                                }
                                                                // Remove Progress
                                                                progressCircular.setVisibility(View.GONE);

                                                                for (int markers = 0; markers < jsonnews.length(); markers++) {
                                                                    try {
                                                                        JSONObject json_marker = jsonnews.getJSONObject(markers);

                                                                        JSONObject obj_map = json_marker.getJSONObject("latlng");
                                                                        String lat = obj_map.getString("lat");
                                                                        String lng = obj_map.getString("lng");
                                                                        String address = obj_map.getString("address");
                                                                        String premium_map = String.valueOf(json_marker.getBoolean("premium"));
                                                                        String title_map = json_marker.getString("title");

                                                                        final LatLng LATLNG = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                                                                        if (premium_map.contentEquals("true")) {
                                                                            Marker melbourne = googleMap.addMarker(new MarkerOptions()
                                                                                    .position(LATLNG)
                                                                                    .title(title_map)
                                                                                    .snippet(address)
                                                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_premium)));
                                                                        } else {
                                                                            Marker melbourne = googleMap.addMarker(new MarkerOptions()
                                                                                    .position(LATLNG)
                                                                                    .title(title_map)
                                                                                    .snippet(address)
                                                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_default)));
                                                                        }

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                if (pager < totalPages) {
                                                                    pager++;
                                                                    AsyncConnection(parseURL + String.valueOf(pager));
                                                                    swingBottomInAnimationAdapter.notifyDataSetChanged();
                                                                }


                                                            }
                                                        }

                            );
                        }catch(Exception e){

                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(getActivity(), "Desliza para recargar. Revisa tu conexion", Toast.LENGTH_SHORT).show();
                        progressCircular.setVisibility(View.GONE);
                    }

                }

        );
    }
}
