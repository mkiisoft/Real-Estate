package com.renderas.soldty;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.renderas.soldty.adapter.ListAdapter;
import com.renderas.soldty.utils.CircularProgressBar;
import com.renderas.soldty.utils.KeySaver;
import com.renderas.soldty.utils.Utils;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mariano on 17/08/2015.
 */
public class SearchActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private EditText searchBox;
    private String searchURL = "http://dev.soldty.com/wp-json/posts?type=property&filter[s]=";
    private JSONArray jsonsearch;
    private ArrayList<HashMap<String, String>> arraylist;
    private JSONObject json_object;
    private ListView mListView;
    private CircularProgressBar progressCircular;
    private String bob_final, bob_usd;
    private ImageView searchClean;

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
    private RelativeLayout searchEmpty;
    private LinearLayout mFilterMain, mFilterLocation, mFilterSale, mFilterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Búsqueda");

        searchClean = (ImageView) findViewById(R.id.search_clean);
        searchClean.setVisibility(View.INVISIBLE);
        searchClean.setEnabled(false);

        searchEmpty = (RelativeLayout) findViewById(R.id.search_empty_box);

        searchBox = (EditText) findViewById(R.id.search_box);
        searchBox.clearFocus();
        searchBox.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && searchBox.getText().toString().trim().length() > 0) {
                    clearFiilterVisibility();
                    searchBox.clearFocus();
                    AsyncConnection(searchURL + searchBox.getText().toString());
                    return true;
                } else if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && searchBox.getText().toString().trim().length() == 0) {
                    Toast.makeText(SearchActivity.this, "Completa el campo de búsqueda", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        searchClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    searchClean.setVisibility(View.VISIBLE);
                    searchClean.setEnabled(true);
                } else {
                    searchClean.setVisibility(View.INVISIBLE);
                    searchClean.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mListView = (ListView) findViewById(R.id.search_list_view);

        progressCircular = (CircularProgressBar) findViewById(R.id.circularProgress);
        progressCircular.setVisibility(View.INVISIBLE);

        mFilterMain = (LinearLayout) findViewById(R.id.filter_settings);
        mFilterMain.setEnabled(false);

        mFilterLocation = (LinearLayout) findViewById(R.id.filter_location);
        mFilterLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                intent.putExtra("location", "true");
                startActivity(intent);
            }
        });
        mFilterSale = (LinearLayout) findViewById(R.id.filter_sale);
        mFilterSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                intent.putExtra("sale", "true");
                startActivity(intent);
            }
        });
        mFilterType = (LinearLayout) findViewById(R.id.filter_type);
        mFilterType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                intent.putExtra("type", "true");
                startActivity(intent);
            }
        });

    }

    public void emprtySearchAppear(){
        searchEmpty.setAlpha(0);
        searchEmpty.setVisibility(View.VISIBLE);
        searchEmpty.animate().alpha(1).setDuration(500);
    }

    public void filterVisibility(){
        if(mFilterMain.getVisibility() == View.GONE){
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            mFilterMain.setAlpha(0);
            mFilterMain.setVisibility(View.VISIBLE);
            mFilterMain.animate().alpha(1).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mFilterMain.setEnabled(true);
                    mFilterMain.setClickable(true);
                }
            });
        }else{
            mFilterMain.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mFilterMain.setVisibility(View.GONE);
                    mFilterMain.setEnabled(false);
                }
            });
        }
    }

    public void clearFiilterVisibility(){
        mFilterMain.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                mFilterMain.setVisibility(View.GONE);
                mFilterMain.setEnabled(false);
            }
        });
    }

    private class PropertySearch {

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

    public void AsyncConnection(String urlConnection) {

        new PropertySearch().get(urlConnection, null, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                progressCircular.setVisibility(View.VISIBLE);
                searchEmpty.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {

                try {

                    arraylist = new ArrayList<HashMap<String, String>>();

                    jsonsearch = new JSONArray(Utils.decodeUTF8(response));

                    if(jsonsearch.length() == 0){
                        emprtySearchAppear();
                    }

                    for (int e = 0; e < jsonsearch.length(); e++) {
                        HashMap<String, String> map = new HashMap<String, String>();

                        json_object = jsonsearch.getJSONObject(e);

                        // Retrive JSON Objects
                        map.put("ID", json_object.getString("ID"));
                        map.put("title", json_object.getString("title"));
                        Log.e("name", json_object.getString("title"));
                        map.put("author", json_object.getString("author"));
                        map.put("content", json_object.getString("content"));
                        JSONObject json_author = json_object.getJSONObject("author");
                        JSONObject json_meta_user = json_author.getJSONObject("meta");
                        JSONObject json_meta_sub = json_meta_user.getJSONObject("meta");
                        JSONArray json_phone = json_meta_sub.getJSONArray("phone");
                        map.put("phone", json_phone.getString(0));
                        map.put(AUTHORID, json_author.getString("ID"));
                        map.put("author_name", json_author.getString("name"));
                        map.put("author_image", json_author.getString("avatar"));
                        map.put("author_email", json_author.getString("email_contacto"));
                        JSONObject json_map = json_object.getJSONObject("latlng");
                        map.put("lat_map", json_map.getString("lat"));
                        map.put("lng_map", json_map.getString("lng"));
                        JSONObject json_feature_image = json_object.getJSONObject("featured_image");
                        JSONObject json_image_meta = json_feature_image.getJSONObject("attachment_meta");
                        JSONObject json_sizes = json_image_meta.getJSONObject("sizes");
                        JSONObject json_thumbnail = json_sizes.getJSONObject("javo-small");
                        JSONObject json_large = json_sizes.getJSONObject("javo-large");
                        map.put("list_thumb", json_thumbnail.getString("url"));
                        map.put("full_image", json_large.getString("url"));
                        JSONObject json_terms = json_object.getJSONObject("terms");
                        JSONArray json_city = json_terms.getJSONArray("property_city");
                        for (int c = 0; c < json_city.length(); c++) {
                            JSONObject json_country = json_city.getJSONObject(0);
                            map.put("country", json_country.getString("name"));

                            if (c > 0) {
                                JSONObject json_the_city = json_city.getJSONObject(1);
                                map.put("city", json_the_city.getString("name"));

                                Log.e("ciudad", json_the_city.getString("name"));
                            } else {
                                map.put("city", "");
                            }

                        }

                        JSONArray json_property_type = json_terms.getJSONArray("property_type");
                        for (int t = 0; t < json_property_type.length(); t++) {
                            JSONObject json_obj_type = json_property_type.getJSONObject(t);

                            map.put("property_type", json_obj_type.getString("name"));
                        }

                        JSONArray json_status = json_terms.getJSONArray("property_status");
                        for (int x = 0; x < json_status.length(); x++) {
                            JSONObject json_obj = json_status.getJSONObject(x);

                            map.put("name_status", json_obj.getString("name"));
                        }

                        JSONArray json_price = json_object.getJSONArray("sale_price");
                        map.put("price", json_price.getString(0));

                        JSONArray json_bedrooms = json_object.getJSONArray("bedrooms");
                        map.put("bedrooms", json_bedrooms.getString(0));

                        JSONArray json_year = json_object.getJSONArray("built_year");
                        map.put("built_year", json_year.getString(0));

                        JSONArray json_area = json_object.getJSONArray("area");
                        map.put("area", json_area.getString(0));

                        try {
                            boolean bool = json_object.getBoolean("detail_images");

                            if (bool == false) {
                                map.put("gallery_array", "[]");
                            }
                        } catch (JSONException f) {
                            JSONArray json_gallery = json_object.getJSONArray("detail_images");
                            map.put("gallery_array", String.valueOf(json_gallery));
                        }

                        map.put("condition", json_object.getString("estado"));
                        map.put("premium", String.valueOf(json_object.getBoolean("premium")));

                        // Set the JSON Objects into the array
                        arraylist.add(map);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressCircular.setVisibility(View.GONE);

                        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new ListAdapter(getApplicationContext(), arraylist));
                        swingBottomInAnimationAdapter.setAbsListView(mListView);

                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
                        swingBottomInAnimationAdapter.getViewAnimator().setAnimationDurationMillis(400);

                        mListView.setAdapter(swingBottomInAnimationAdapter);
                    }
                });
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("nonhome", "true");
                startActivity(intent);
                return true;
            case R.id.action_filter:
                filterVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
