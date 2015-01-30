package com.xyq.smartreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

// Toast.makeText(getApplicationContext(), "Ä¬ÈÏToastÑùÊ½", Toast.LENGTH_SHORT).show();

public class StartActivity extends ActionBarActivity {
	
	private static final int NONE = 0;
    private static final int PHOTO_GRAPH = 1;
    private static final int PHOTO_ZOOM = 2;
    private static final int PHOTO_RESOULT = 3;
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final int SHOWRESULT = 0x101;
    private static String LANGUAGE = "chi_sim";
    private static String textResult;
    private static Bitmap bitmapSelected;
    
    public static Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SHOWRESULT:
					Log.i("showresult", textResult);
					break;
					
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void takePhoto(View v) {
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg")));
        startActivityForResult(intent, PHOTO_GRAPH);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;
        
        if (requestCode == PHOTO_GRAPH) {
            File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
            startPhotoZoom(Uri.fromFile(picture));
        }

        if (data == null)
            return;

        if (requestCode == PHOTO_ZOOM) {
            startPhotoZoom(data.getData());
        }
        
        if (requestCode == PHOTO_RESOULT) {   
    		Bundle bundle = data.getExtras();  
    		Bitmap bitmap = (Bitmap) bundle.get("data");
    		bitmapSelected = bitmap;
    		new Thread(new Runnable() {
				@Override
				public void run() {
					textResult = doOcr(bitmapSelected, LANGUAGE);
					Message msg = new Message();
					msg.what = SHOWRESULT;
					myHandler.sendMessage(msg);
//    					Looper.prepare();
//    					Toast.makeText(StartActivity.this, textResult, Toast.LENGTH_SHORT).show();
//    					Looper.loop();
				}
			}).start();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESOULT);
    }
    
    public String doOcr(Bitmap bitmap, String language) {
		TessBaseAPI baseApi = new TessBaseAPI();

		baseApi.init(getSDPath(), language);

		bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		baseApi.setImage(bitmap);

		String text = baseApi.getUTF8Text();

		baseApi.clear();
		baseApi.end();

		return text;
	}
    
    public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		}
		return sdDir.toString();
	}
    
    
    
    public void browserPaper(View v) {
    	
    }
}
