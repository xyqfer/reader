package sk.tamex.cordova.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import android.provider.Settings;
import android.location.LocationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.net.wifi.WifiManager;

import android.content.Context;

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
        else if (action.equals("switchToWirelessSettings")) {
            Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            cordova.getActivity().startActivity(settingsIntent);
            boolean result = true;
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
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
