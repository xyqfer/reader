package com.xyq.smartreader;

import java.io.File;
import java.io.FileNotFoundException;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v4.app.NavUtils;

public class CropActivity extends Activity implements OnClickListener {
	
	private static String LANGUAGE = "chi_sim";
    private static String textResult;
    private static Bitmap bitmapSelected;
    private static final int SHOWRESULT = 0x101;

	Button save;
	Button cencal;

	ImageView mImageView;
	ImageView mCaijian;
	myView my;
	Bitmap bitmap;
	LayoutParams lp = new LayoutParams(480, 600);
	
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

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop);
		
		
		Intent intent = getIntent();
		String path = intent.getStringExtra("path");
		init();
		Log.i("path", path);
		//bitmap = decodeUri(Uri.fromFile(new File(path)));
//		try {
//			bitmap = decodeUri(Uri.fromFile(new File(path)));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//Uri imageUri = Uri.fromFile(new File(path));
		//bitmap = decodeUriAsBitmap(imageUri);

		
		//bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		bitmap = BitmapFactory.decodeFile("/sdcard/temp.jpg");
		//bitmap = BitmapFactory.decodeFile(path);
		my = new myView(CropActivity.this, bitmap);
		LinearLayout layout = new LinearLayout(CropActivity.this);

		layout.addView(my);
		layout.setTop(50);
		this.addContentView(layout, lp);

	}

	private void init() {
		save = (Button) findViewById(R.id.save);
		cencal = (Button) findViewById(R.id.cencal);
		mImageView = (ImageView) findViewById(R.id.imageview);
		mCaijian = (ImageView) findViewById(R.id.caijian);
		save.setOnClickListener(this);
		cencal.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			//Log.i("RG", "CLICK-----");
			Bitmap bitmap = my.getBitmap();
			bitmapSelected = bitmap;
			//mImageView.setImageBitmap(bitmap);
			
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
			
			break;
		case R.id.cencal:
			CropActivity.this.finish();
			break;
		}

	}
	
	public Bitmap getBitmapFromUri(Uri uri) {
    	try {
    		Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
    		return bitmap;
    	} catch (Exception e) {
    		Log.e("getbitmap", "" + uri);
    		return null;
    	}
    }
	
	public Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

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

}