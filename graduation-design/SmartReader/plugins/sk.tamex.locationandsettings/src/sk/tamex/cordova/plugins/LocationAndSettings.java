package sk.tamex.cordova.plugins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import android.provider.Settings;
import android.location.LocationManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.net.wifi.WifiManager;
import android.net.Uri;

import android.content.Context;
import android.content.res.AssetManager;

public class LocationAndSettings extends CordovaPlugin {

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("switchToLocationSettings")) {
             Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
             cordova.getActivity().startActivity(settingsIntent);
             boolean result = true;
             callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
             return true;
        }
        else if (action.equals("switchToWifiSettings")) {
             Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
             cordova.getActivity().startActivity(settingsIntent);
             boolean result = true;
             callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
             return true;
        }
        else if (action.equals("switchToInstallTTS")) {
            AssetManager assetManager = cordova.getActivity().getAssets();

            InputStream in = null;
            OutputStream out = null;

            try {
                in = assetManager.open("www/tts/com.iflytek.speechcloud.apk");
                out = new FileOutputStream("/sdcard/com.iflytek.speechcloud.apk");

                byte[] buffer = new byte[1024];

                int read;
                while((read = in.read(buffer)) != -1) {

                    out.write(buffer, 0, read);

                }

                in.close();
                in = null;

                out.flush();
                out.close();
                out = null;

                Intent intent = new Intent(Intent.ACTION_VIEW);

                intent.setDataAndType(Uri.fromFile(new File("/sdcard/com.iflytek.speechcloud.apk")),
                    "application/vnd.android.package-archive");

                cordova.getActivity().startActivity(intent);

            } catch(Exception e) { }

            boolean result = true;
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
            return true;
        }
        else if (action.equals("switchToTTSSettings")) {
        	Intent intent = new Intent();
        	intent.setAction("com.android.settings.TTS_SETTINGS");
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            cordova.getActivity().startActivity(intent);
            boolean result = true;
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
            
            Intent mStartActivity = new Intent(cordova.getActivity(), LocationAndSettings.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(cordova.getActivity(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager)cordova.getActivity().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            System.exit(0);
            
            return true;
        }
        else if (action.equals("isGpsEnabled")) {
            LocationManager locationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean result = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
            return true;
        }
        else if (action.equals("isWirelessNetworkLocationEnabled")) {
            LocationManager locationManager = (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean result = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
            return true;
        }
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
        return false;
    }
}
