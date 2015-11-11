package com.renderas.soldty;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.bus.ActivityResultBus;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.bus.ActivityResultEvent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.renderas.soldty.utils.KeySaver;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private String urlExchange = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20%28%22USDBOB%22%29&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlExchange, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] response) {

                try {

                    JSONObject obj = new JSONObject(decodeUTF8(response));
                    JSONObject query = obj.getJSONObject("query");
                    JSONObject results = query.getJSONObject("results");
                    JSONObject rate = results.getJSONObject("rate");
                    String bobrate = rate.getString("Rate").substring(0, 4);
                    String bobdate = rate.getString("Date");
                    String bobtime = rate.getString("Time");
                    SimpleDateFormat enUS = new SimpleDateFormat("MM/dd/yyyy");
                    SimpleDateFormat esES = new SimpleDateFormat("dd/MM/yyyy");
                    Date mUStoES = enUS.parse(bobdate);
                    KeySaver.saveShare(MainActivity.this, "bob_rate", bobrate);
                    KeySaver.saveShare(MainActivity.this, "bob_date", esES.format(mUStoES));
                    KeySaver.saveShare(MainActivity.this, "bob_time", bobtime);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alerta!");
        alertDialog.setMessage("Dispositivo no compatible");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        if (!isGooglePlayServicesAvailable()) {
            alertDialog.show();
        }else{
            displayView(0);
        }
    }

    String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, 123);
            return true;
        }

        if(id == R.id.action_search){
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
            displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new Fragment_Home();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new Fragment_Favorites();
                title = getString(R.string.title_favorites);
                break;
            case 2:
                fragment = new Fragment_Help();
                title = getString(R.string.title_agentes);
                break;
            case 3:
                if(KeySaver.isExist(MainActivity.this, "user_id")){
                    Intent user_intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(user_intent);
                    title = getString(R.string.title_access);
                }else{
                    Intent access_intent = new Intent(MainActivity.this, AccessActivity.class);
                    startActivity(access_intent);
                    title = getString(R.string.title_access);
                }

                break;
            case 4:
                if(KeySaver.isExist(MainActivity.this, "user_id")){
                    Intent new_intent = new Intent(getApplicationContext(), AgentActivity.class);
                    new_intent.putExtra("agent_id", KeySaver.getStringSavedShare(MainActivity.this, "user_id"));
                    new_intent.putExtra("agent_name", KeySaver.getStringSavedShare(MainActivity.this, "user_name"));
                    new_intent.putExtra("agent_image", KeySaver.getStringSavedShare(MainActivity.this, "user_avatar"));
                    startActivity(new_intent);
                }
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}
