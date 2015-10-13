package com.renderas.soldty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
import com.renderas.soldty.utils.ScaleImageView;

import java.io.File;

/**
 * Created by Mariano on 10/08/2015.
 */
public class FullImageActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private Typeface mThin, mBold, mLight;
    private Button mUserLogout;
    private String mFullImg;
    private ScaleImageView mImage;

    // Universal Image Loader

    private DisplayImageOptions options;
    private ImageLoader il = ImageLoader.getInstance();
    private DisplayImageOptions opts;
    private File cacheDir;
    private CircularProgressBar progressCircular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_img);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mThin = Typeface.createFromAsset(getResources().getAssets(), "Thin.ttf");
        mBold = Typeface.createFromAsset(getResources().getAssets(), "Bold.ttf");
        mLight = Typeface.createFromAsset(getResources().getAssets(), "Light.ttf");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00FFFFFF")));

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

        final Intent intent = getIntent();

        mFullImg = intent.getStringExtra("image_full");

        Log.e("URL", mFullImg);

        mImage = (ScaleImageView) findViewById(R.id.full_image_zoom);
        progressCircular = (CircularProgressBar) findViewById(R.id.circularProgress);

        il.displayImage(mFullImg, mImage, opts, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String s, View itemView) {
                progressCircular.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View itemView, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View itemView, Bitmap bitmap) {
                progressCircular.setVisibility(View.GONE);
                ScaleImageView imageView = (ScaleImageView) itemView;
                if (bitmap != null) {
                    FadeInBitmapDisplayer.animate(imageView, 600);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

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
}
