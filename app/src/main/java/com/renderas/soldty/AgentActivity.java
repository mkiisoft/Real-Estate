package com.renderas.soldty;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.renderas.soldty.utils.JSONArrayfunctions;
import com.renderas.soldty.utils.KeySaver;
import com.renderas.soldty.utils.RoundedImageView;
import com.renderas.soldty.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.chrisjenx.paralloid.Parallaxor;
import uk.co.chrisjenx.paralloid.views.ParallaxListView;


public class AgentActivity extends ActionBarActivity {

    // Property
    JSONArray jsonnews;
    JSONArray jsonarray;
    JSONObject json_object;
    ArrayList<HashMap<String, String>> arraylist;
    ListViewAdapter adapter;

    // ListView
    private ParallaxListView mListView;
    private CircularProgressBar progressCircular;

    // Universal Image Loader
    private DisplayImageOptions options;
    private ImageLoader il = ImageLoader.getInstance();
    private DisplayImageOptions opts;
    protected File cacheDir;

    // Typefaces
    private Typeface mThin, mBold, mLight;

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

    // Swipe Layout
    private SwipeRefreshLayout swipeView;
    private String mAgentID, mAgentName, mAgentPhoto;
    private Toolbar mToolbar;
    private RoundedImageView mAgentImage;
    private TextView mAgentTitle;
    private String bob_final, bob_usd;
    private RelativeLayout mAgentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));

        final Intent agentIntent = getIntent();

        mAgentID = agentIntent.getStringExtra("agent_id");
        mAgentName = agentIntent.getStringExtra("agent_name");
        mAgentPhoto = agentIntent.getStringExtra("agent_image");

        mThin = Typeface.createFromAsset(getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(getResources().getAssets(), "Light.ttf");

        mAgentProfile = (RelativeLayout) findViewById(R.id.agent_profile);

        progressCircular = (CircularProgressBar) findViewById(R.id.circularProgress);

        mAgentImage = (RoundedImageView) findViewById(R.id.agent_profile_image);
        mAgentTitle = (TextView) findViewById(R.id.agent_profile_name);
        mAgentTitle.setText(mAgentName);
        mAgentTitle.setTypeface(mBold);

        cacheDir = StorageUtils.getCacheDirectory(this);

        opts = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .diskCacheFileCount(100)
                .build();
        if (il.isInited()) {
            il.destroy();
        }

        il.init(config);

        il.displayImage(mAgentPhoto, mAgentImage, opts, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String s, View itemView) {

            }

            @Override
            public void onLoadingFailed(String s, View itemView, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View itemView, Bitmap bitmap) {
                RoundedImageView imageView = (RoundedImageView) itemView;
                if (bitmap != null) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }

        });

        mListView = (ParallaxListView) findViewById(R.id.home_list_view);

        if (mListView instanceof Parallaxor) {
            ((Parallaxor) mListView).parallaxViewBy(mAgentProfile, 0.6f);
        }

        boolean connready = Utils.testConection(this);
        if (connready) {
//            whenOffline.setVisibility(View.GONE);
            new DownloadJSON().execute();
        } else {
//            whenOffline.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Revisa tu conexion para ver las propiedades", Toast.LENGTH_SHORT).show();

        }

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
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
                            new DownloadJSON().execute();
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
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
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

    private class DownloadJSON extends AsyncTask<Void, Void, Boolean> {

        private static final int LOADER_CURSOR = 0;
        private LoaderManager.LoaderCallbacks mCursorCallbacks;
        Exception error;


        protected void onPreExecute() {
            super.onPreExecute();
            progressCircular.setVisibility(View.VISIBLE);

        }

        protected Boolean doInBackground(Void... params) {

            try {
                // Create an array
                arraylist = new ArrayList<HashMap<String, String>>();
                // Retrieve JSON Objects from the given URL address
                jsonnews = JSONArrayfunctions.getJSONfromURL("http://dev.soldty.com/wp-json/posts?type=property&filter[author]=" + mAgentID);

                for (int i = 0; i < jsonnews.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();

                    json_object = jsonnews.getJSONObject(i);

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
                    map.put("author_id", json_author.getString("ID"));
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
                    for (int e = 0; e < json_status.length(); e++) {
                        JSONObject json_obj = json_status.getJSONObject(e);

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
                    } catch (JSONException e) {
                        JSONArray json_gallery = json_object.getJSONArray("detail_images");
                        map.put("gallery_array", String.valueOf(json_gallery));
                    }

                    map.put("condition", json_object.getString("estado"));
                    map.put("premium", String.valueOf(json_object.getBoolean("premium")));

                    // Set the JSON Objects into the array
                    arraylist.add(map);
                }

                return true;


            } catch (Exception e) {
                error = e;

                return false;
            }

        }

        protected void onPostExecute(Boolean result) {

            try {
                if (result) {
                    // Remove Progress
                    progressCircular.setVisibility(View.GONE);
                    // Pass the results into ListViewAdapter
                    adapter = new ListViewAdapter(getApplicationContext(), arraylist);
                    // Set the adapter to the ListView

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new ListViewAdapter(getApplicationContext(), arraylist));
                    swingBottomInAnimationAdapter.setAbsListView(mListView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(300);
                    swingBottomInAnimationAdapter.getViewAnimator().setAnimationDurationMillis(400);

                    mListView.setAdapter(swingBottomInAnimationAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Desliza para recargar. Revisa tu conexion", Toast.LENGTH_SHORT).show();
                    progressCircular.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }

        }
    }

    public class ListViewAdapter extends BaseAdapter {

        // Declare Variables
        Context context;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> data;
        //ImageLoader imageLoader;
        HashMap<String, String> resultp = new HashMap<String, String>();

        public ListViewAdapter(Context context,
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
                    .diskCacheFileCount(100)
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
                    int orientation = getResources().getConfiguration().orientation;

                    // Get the position
                    resultp = data.get(position);
                    Intent intent = new Intent(getApplicationContext(), PropertyActivity.class);
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
                    intent.putExtra("from_agent", "from_agent");
                    intent.putExtra("lat_map", resultp.get(MAPLAT));
                    intent.putExtra("lng_map", resultp.get(MAPLNG));
                    intent.putExtra("bedrooms", resultp.get(BEDROOMS));
                    intent.putExtra("build_year", resultp.get(BUILDYEAR));
                    intent.putExtra("condition", resultp.get(CONDITION));
                    intent.putExtra("area", resultp.get(AREA));
                    intent.putExtra("premium", resultp.get(PREMIUM));
                    intent.putExtra("gallery", resultp.get(GALLERY));
                    intent.putExtra("positionFav", String.valueOf(position));

                    // Animation Activity
                    intent.putExtra(PACKAGE + ".left", screenLocation[0]);
                    intent.putExtra(PACKAGE + ".top", screenLocation[1]);
                    intent.putExtra(PACKAGE + ".width", arg0.getWidth());
                    intent.putExtra(PACKAGE + ".height", arg0.getHeight());
                    intent.putExtra(PACKAGE + ".orientation", orientation);

                    // Start SingleItemView Class
                    startActivity(intent);

                }
            });

            appicon = (ImageView) itemView.findViewById(R.id.picture);

            Picasso.with(AgentActivity.this).load(resultp.get(SCREEN)).into(appicon);

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

            price = (TextView) itemView.findViewById(R.id.price);
            bob_final = String.valueOf(Integer.parseInt(resultp.get(PRICE)) * Double.parseDouble(KeySaver.getStringSavedShare(getApplicationContext(), "bob_rate")));
            bob_usd = resultp.get(PRICE);

            if (KeySaver.isExist(getApplicationContext(), "exchange_true")) {
                price.setText("b$ " + bob_final.substring(0, bob_final.length() - 2));
            } else {
                price.setText("u$s " + bob_usd);
            }
            price.setTypeface(mLight);

            return itemView;
        }
    }
}
