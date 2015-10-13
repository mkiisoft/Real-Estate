package com.renderas.soldty.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;

import com.renderas.soldty.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.Header;

public class Utils {

	//Set all the navigation icons and always to set "zero 0" for the item is a category
//	public static int[] iconNavigation = new int[] {
//		0, R.drawable.ic_launcher};
//
//	//get title of the item navigation
//	public static String getTitleItem(Context context, int position){
//		String[] titulos = context.getResources().getStringArray(R.array.nav_menu_items);
//		return titulos[position];
//	}

    public static HashMap<String, String> convertHeadersToHashMap(Header[] headers) {
        HashMap<String, String> result = new HashMap<String, String>(headers.length);
        for (Header header : headers) {
            result.put(header.getName(), header.getValue());
        }
        return result;
    }
	
	public static void saveFile( File filename, byte[] data ) throws IOException {
        FileOutputStream fOut = new FileOutputStream( filename );

        fOut.write( data );
        
        fOut.flush();
        fOut.close();
	}

    public static String decodeUTF8(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }
	
	public static void unbindDrawables(View view) {
		//Log.d("Gamba Utils","Limpiando Bitmaps");
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
            view.setBackgroundDrawable(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
               	unbindDrawables(((ViewGroup) view).getChildAt(i));
                
            }
            ((ViewGroup) view).removeAllViews();
        }
        System.gc();
    } 
	
	public static byte[] readFile( File filename ) throws IOException {
		FileInputStream fIn = new FileInputStream( filename );
		
		byte[] buffer = new byte[ fIn.available() ];
		fIn.read(buffer);
		fIn.close();
		return buffer;
	}
	
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size=1024;
        try {
            byte[] bytes=new byte[buffer_size];
            for(;;) {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        } catch(Exception ex){}
    }

    public static boolean testConection(Context context){
        boolean HaveConnectedWifi = false;
        boolean HaveConnectedMobile = false;
        boolean result;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    HaveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    HaveConnectedMobile = true;
        }
        result = HaveConnectedWifi || HaveConnectedMobile;
        return result;
    }
	
}
