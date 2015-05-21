/*
 * Phonegap VolumeControl Plugin for Android
 * Cordova 2.2.0
 * Author: Manuel Simpson
 * Email: manusimpson[at]gmail[dot]com
 * Date: 12/28/2012
 */

package com.develcode.plugins.volumeControl;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.media.*;

public class VolumeControl extends CordovaPlugin {

	public static final String SET = "setVolume";
	public static final String GET = "getVolume";
	private static final String TAG = "VolumeControl";

	private Context context;
	private AudioManager manager;

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		boolean actionState = true;
		context = cordova.getActivity().getApplicationContext();
		manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if (SET.equals(action)) {
			try {
				//Get the volume value to set
				int volume = getVolumeToSet(args.getInt(0));
				boolean play_sound;

				if(args.length() > 1 && !args.isNull(1)) {
					play_sound = args.getBoolean(1);
				} else {
					play_sound = true;
				}

				//Set the volume
				manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, (play_sound ? AudioManager.FLAG_PLAY_SOUND : 0));
				callbackContext.success();
			} catch (Exception e) {
				LOG.d(TAG, "Error setting volume " + e);
				actionState = false;
			}
		} else if(GET.equals(action)) {
				//Get current system volume
				int currVol = getCurrentVolume();
				String strVol= String.valueOf(currVol);
				callbackContext.success(strVol);
				LOG.d(TAG, "Current Volume is " + currVol);
		} else {
			actionState = false;
		}
		return actionState;
	}

	private int getVolumeToSet(int percent) {
		int volLevel;
		int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volLevel = Math.round((percent * maxVolume) / 100);

		return volLevel;
	}

	private int getCurrentVolume() {
		try {
			int volLevel;
			int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			int currSystemVol = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
			volLevel = Math.round((currSystemVol * 100) / maxVolume);

			return volLevel;
		} catch (Exception e) {
			LOG.d(TAG, "getVolume error: " + e);
			return 1;
		}
	}
}
