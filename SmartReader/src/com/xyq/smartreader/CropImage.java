/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xyq.smartreader;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import com.googlecode.tesseract.android.TessBaseAPI;
/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImage extends MonitoredActivity {
    private static final String TAG = "CropImage";

    // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat =
            Bitmap.CompressFormat.JPEG; // only used with mSaveUri
    private Uri mSaveUri = null;
    private boolean mSetWallpaper = false;
    private int mAspectX, mAspectY;
    private boolean mDoFaceDetection = true;
    private boolean mCircleCrop = false;
    private final Handler mHandler = new Handler();

    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
    private int mOutputX, mOutputY;
    private boolean mScale;
    private boolean mScaleUp = true;

    boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    boolean mSaving;  // Whether the "save" button is already clicked.

    private CropImageView mImageView;
    private ContentResolver mContentResolver;

    private Bitmap mBitmap;
    HighlightView mCrop;

    private IImageList mAllImages;
    private IImage mImage;
    
    private static String LANGUAGE = "chi_sim";
    private static String textResult;
    private static Bitmap bitmapSelected;
    private static final int SHOWRESULT = 0x101;
    
    private TextToSpeech tts = null;
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {  
        @Override  
        public void onManagerConnected(int status) {  
            switch (status) {  
                case LoaderCallbackInterface.SUCCESS:{  
                } break;  
                default:{  
                    super.onManagerConnected(status);  
                } break;  
            }  
        }  
    };
    
    @Override  
    public void onResume(){  
        super.onResume();  
        //通过OpenCV引擎服务加载并初始化OpenCV类库，所谓OpenCV引擎服务即是  
        //OpenCV_2.4.3.2_Manager_2.4_*.apk程序包，存在于OpenCV安装包的apk目录中  
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);  
    } 
    
//    public static Handler myHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case SHOWRESULT:
//					Log.i("showresult", textResult);
//					break;
//					
//				default:
//					break;
//			}
//			super.handleMessage(msg);
//		}
//	};

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContentResolver = getContentResolver();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cropimage);
        
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if (status == TextToSpeech.SUCCESS)
                {
                    int result = tts.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Toast.makeText(getApplicationContext(), "Language is not available.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                	Toast.makeText(getApplicationContext(), "Language is available.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mImageView = (CropImageView) findViewById(R.id.image);

//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
        
        mAspectX = 1;
        mAspectY = 1;
        
        //mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.psb);
        
        //File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        mBitmap = BitmapFactory.decodeFile("/sdcard/temp.jpg");
    

        // save
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String mPicName = formatter.format(date);
        File file = getFiles(mPicName+".jpg");
        
        mSaveUri = Uri.fromFile(file);
        if (mSaveUri != null) {
            String outputFormatString = Bitmap.CompressFormat.JPEG.toString();
            if (outputFormatString != null) {
                mOutputFormat = Bitmap.CompressFormat.valueOf(
                        outputFormatString);
            }
        }
        /*if (extras != null) {
            if (extras.getString("circleCrop") != null) {
                mCircleCrop = true;
                mAspectX = 1;
                mAspectY = 1;
            }
            mSaveUri = (Uri) extras.getParcelable(MediaStore.EXTRA_OUTPUT);
            if (mSaveUri != null) {
                String outputFormatString = extras.getString("outputFormat");
                if (outputFormatString != null) {
                    mOutputFormat = Bitmap.CompressFormat.valueOf(
                            outputFormatString);
                }
            } else {
                mSetWallpaper = extras.getBoolean("setWallpaper");
            }
            mBitmap = (Bitmap) extras.getParcelable("data");
            mAspectX = extras.getInt("aspectX");
            mAspectY = extras.getInt("aspectY");
            mOutputX = extras.getInt("outputX");
            mOutputY = extras.getInt("outputY");
            mScale = extras.getBoolean("scale", true);
            mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
            mDoFaceDetection = extras.containsKey("noFaceDetection")
                    ? !extras.getBoolean("noFaceDetection")
                    : true;
        }

        if (mBitmap == null) {
            Uri target = intent.getData();
//            mAllImages = ImageManager.makeImageList(mContentResolver, target,
//                    ImageManager.SORT_ASCENDING);
            mImage = mAllImages.getImageForUri(target);
            if (mImage != null) {
                // Don't read in really large bitmaps. Use the (big) thumbnail
                // instead.
                // TODO when saving the resulting bitmap use the
                // decode/crop/encode api so we don't lose any resolution.
                mBitmap = mImage.thumbBitmap(IImage.ROTATE_AS_NEEDED);
            }
        }*/

        if (mBitmap == null) {
            finish();
            return;
        }

        // Make UI fullscreen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.discard).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });

        findViewById(R.id.save).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        onSaveClicked();
                    }
                });

        startFaceDetection();
    }

    private void startFaceDetection() {
        if (isFinishing()) {
            return;
        }

        mImageView.setImageBitmapResetBase(mBitmap, true);

        Util.startBackgroundJob(this, null,
                getResources().getString(R.string.runningFaceDetection),
                new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                final Bitmap b = (mImage != null)
                        ? mImage.fullSizeBitmap(IImage.UNCONSTRAINED,
                        1024 * 1024)
                        : mBitmap;
                mHandler.post(new Runnable() {
                    public void run() {
                        if (b != mBitmap && b != null) {
                            mImageView.setImageBitmapResetBase(b, true);
                            mBitmap.recycle();
                            mBitmap = b;
                        }
                        if (mImageView.getScale() == 1F) {
                            mImageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mRunFaceDetection.run();
            }
        }, mHandler);
    }

    private void onSaveClicked() {
        // TODO this code needs to change to use the decode/crop/encode single
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (mCrop == null) {
            return;
        }

        if (mSaving) return;
        mSaving = true;

        Bitmap croppedImage;

        // If the output is required to a specific size, create an new image
        // with the cropped image in the center and the extra space filled.
        if (mOutputX != 0 && mOutputY != 0 && !mScale) {
            // Don't scale the image but instead fill it so it's the
            // required dimension
            croppedImage = Bitmap.createBitmap(mOutputX, mOutputY,
                    Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(croppedImage);

            Rect srcRect = mCrop.getCropRect();
            Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

            int dx = (srcRect.width() - dstRect.width()) / 2;
            int dy = (srcRect.height() - dstRect.height()) / 2;

            // If the srcRect is too big, use the center part of it.
            srcRect.inset(Math.max(0, dx), Math.max(0, dy));

            // If the dstRect is too big, use the center part of it.
            dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

            // Draw the cropped bitmap in the center
            canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

            // Release bitmap memory as soon as possible
            mImageView.clear();
            mBitmap.recycle();
        } else {
            Rect r = mCrop.getCropRect();

            int width = r.width();
            int height = r.height();

            // If we are circle cropping, we want alpha channel, which is the
            // third param here.
            croppedImage = Bitmap.createBitmap(width, height,
                    mCircleCrop
                    ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mBitmap, r, dstRect, null);

            // Release bitmap memory as soon as possible
            mImageView.clear();
            mBitmap.recycle();

            if (mCircleCrop) {
                // OK, so what's all this about?
                // Bitmaps are inherently rectangular but we want to return
                // something that's basically a circle.  So we fill in the
                // area around the circle with alpha.  Note the all important
                // PortDuff.Mode.CLEAR.
                Canvas c = new Canvas(croppedImage);
                Path p = new Path();
                p.addCircle(width / 2F, height / 2F, width / 2F,
                        Path.Direction.CW);
                c.clipPath(p, Region.Op.DIFFERENCE);
                c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            }

            // If the required dimension is specified, scale the image.
            if (mOutputX != 0 && mOutputY != 0 && mScale) {
                croppedImage = Util.transform(new Matrix(), croppedImage,
                        mOutputX, mOutputY, mScaleUp, Util.RECYCLE_INPUT);
            }
        }
        
        // openCV test
//        Mat rgbMat = new Mat();  
//        Mat grayMat = new Mat();  
//        //获取lena彩色图像所对应的像素数据  
//        Utils.bitmapToMat(croppedImage, rgbMat);  
//        //将彩色图像数据转换为灰度图像数据并存储到grayMat中  
//        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);  
//        //创建一个灰度图像  
//        Bitmap grayBmp = Bitmap.createBitmap(croppedImage.getWidth(), croppedImage.getHeight(), Config.RGB_565);  
//        //将矩阵grayMat转换为灰度图像  
//        Utils.matToBitmap(grayMat, grayBmp);  
        //imageView.setImageBitmap(grayBmp);
        
        //point test
        //convert the image to black and white
//        Mat imgSource = new Mat();
//        Mat grayMat = new Mat();
//        Utils.bitmapToMat(croppedImage, imgSource);
//        Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.Canny(imgSource, imgSource, 50, 50);
//
//        //apply gaussian blur to smoothen lines of dots
//        Imgproc.GaussianBlur(imgSource, imgSource, new  org.opencv.core.Size(5, 5), 5);
//
//        //find the contours
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        double maxArea = -1;
//        int maxAreaIdx = -1;
//        Log.d("size",Integer.toString(contours.size()));
//        MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
//        MatOfPoint2f approxCurve = new MatOfPoint2f();
//        MatOfPoint largest_contour = contours.get(0);
//        //largest_contour.ge
//        List<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
//        //Imgproc.drawContours(imgSource,contours, -1, new Scalar(0, 255, 0), 1);
//
//        for (int idx = 0; idx < contours.size(); idx++) {
//            temp_contour = contours.get(idx);
//            double contourarea = Imgproc.contourArea(temp_contour);
//            //compare this contour to the previous largest contour found
//            if (contourarea > maxArea) {
//                //check if this contour is a square
//                MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
//                int contourSize = (int)temp_contour.total();
//                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
//                Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize*0.05, true);
//                if (approxCurve_temp.total() == 4) {
//                    maxArea = contourarea;
//                    maxAreaIdx = idx;
//                    approxCurve=approxCurve_temp;
//                    largest_contour = temp_contour;
//                }
//            }
//        }
//
//       Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BayerBG2RGB);
//       //sourceImage =Highgui.imread(Environment.getExternalStorageDirectory().
//       //          getAbsolutePath() +"/scan/p/1.jpg");
//       double[] temp_double;
//       temp_double = approxCurve.get(0,0);       
//       Point p1 = new Point(temp_double[0], temp_double[1]);
//       Log.i("p1", temp_double[0] + " " + temp_double[1]);
//       //Core.circle(imgSource,p1,55,new Scalar(0,0,255));
//       //Imgproc.warpAffine(sourceImage, dummy, rotImage,sourceImage.size());
//       temp_double = approxCurve.get(1,0);       
//       Point p2 = new Point(temp_double[0], temp_double[1]);
//       Log.i("p2", temp_double[0] + " " + temp_double[1]);
//      // Core.circle(imgSource,p2,150,new Scalar(255,255,255));
//       temp_double = approxCurve.get(2,0);       
//       Point p3 = new Point(temp_double[0], temp_double[1]);
//       Log.i("p3", temp_double[0] + " " + temp_double[1]);
//       //Core.circle(imgSource,p3,200,new Scalar(255,0,0));
//       temp_double = approxCurve.get(3,0);       
//       Point p4 = new Point(temp_double[0], temp_double[1]);
//       Log.i("p4", temp_double[0] + " " + temp_double[1]);
       
       // end test

        mImageView.setImageBitmapResetBase(croppedImage, true);
        mImageView.center(true, true);
        mImageView.mHighlightViews.clear();
        
        bitmapSelected = croppedImage;
        
//        new Thread(new Runnable() {
//			@Override
//			public void run() {
//					textResult = doOcr(bitmapSelected, LANGUAGE);
//					tts.speak(textResult, TextToSpeech.QUEUE_FLUSH, null);
//					Log.i("showresult", textResult);
//					Looper.prepare();
//					Toast.makeText(CropImage.this, textResult, Toast.LENGTH_SHORT).show();
//					Looper.loop();
//			}
//
//			private String doOcr(Bitmap bitmap, String language) {
//				// TODO Auto-generated method stub
//				TessBaseAPI baseApi = new TessBaseAPI();
//
//				baseApi.init(getSDPath(), language);
//
//				bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//
//				baseApi.setImage(bitmap);
//
//				String text = baseApi.getUTF8Text();
//
//				baseApi.clear();
//				baseApi.end();
//
//				return text;
//			}
//
//			private String getSDPath() {
//				// TODO Auto-generated method stub
//				File sdDir = null;
//				boolean sdCardExist = Environment.getExternalStorageState().equals(
//						android.os.Environment.MEDIA_MOUNTED);
//				if (sdCardExist) {
//					sdDir = Environment.getExternalStorageDirectory();
//				}
//				return sdDir.toString();
//			}
//		}).start();

        // croppedImage
        // Return the cropped image directly or save it to the specified URI.
//        Bundle myExtras = getIntent().getExtras();
//        if (myExtras != null && (myExtras.getParcelable("data") != null
//                || myExtras.getBoolean("return-data"))) {
//            Bundle extras = new Bundle();
//            extras.putParcelable("data", croppedImage);
//            setResult(RESULT_OK,
//                    (new Intent()).setAction("inline-data").putExtras(extras));
//            finish();
//        } else {
//            final Bitmap b = croppedImage;
//            final int msdId = mSetWallpaper
//                    ? R.string.wallpaper
//                    : R.string.savingImage;
//            Util.startBackgroundJob(this, null,
//                    getResources().getString(msdId),
//                    new Runnable() {
//                public void run() {
//                    saveOutput(b);
//                }
//            }, mHandler);
//            Toast.makeText(this, "path: "+mSaveUri.getPath(), Toast.LENGTH_LONG).show();
//        }
    }

    private void saveOutput(Bitmap croppedImage) {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 75, outputStream);
                }
            } catch (IOException ex) {
                // TODO: report error to caller
                Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
            } finally {
//                Util.closeSilently(outputStream);
            }
            Bundle extras = new Bundle();
            setResult(RESULT_OK, new Intent(mSaveUri.toString())
                    .putExtras(extras));
        } else if (mSetWallpaper) {
            try {
                WallpaperManager.getInstance(this).setBitmap(croppedImage);
                setResult(RESULT_OK);
            } catch (IOException e) {
                Log.e(TAG, "Failed to set wallpaper.", e);
                setResult(RESULT_CANCELED);
            }
        } else {
            Bundle extras = new Bundle();
            extras.putString("rect", mCrop.getCropRect().toString());

            File oldPath = new File(mImage.getDataPath());
            File directory = new File(oldPath.getParent());

            int x = 0;
            String fileName = oldPath.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf("."));

            // Try file-1.jpg, file-2.jpg, ... until we find a filename which
            // does not exist yet.
            while (true) {
                x += 1;
                String candidate = directory.toString()
                        + "/" + fileName + "-" + x + ".jpg";
                boolean exists = (new File(candidate)).exists();
                if (!exists) {
                    break;
                }
            }

            try {
                int[] degree = new int[1];
                /*Uri newUri = ImageManager.addImage(
                        mContentResolver,
                        mImage.getTitle(),
                        mImage.getDateTaken(),
                        null,    // TODO this null is going to cause us to lose
                                 // the location (gps).
                        directory.toString(), fileName + "-" + x + ".jpg",
                        croppedImage, null,
                        degree);*/

                /*setResult(RESULT_OK, new Intent()
                        .setAction(newUri.toString())
                        .putExtras(extras));*/
            } catch (Exception ex) {
                // basically ignore this or put up
                // some ui saying we failed
                Log.e(TAG, "store image fail, continue anyway", ex);
            }
        }

        final Bitmap b = croppedImage;
        mHandler.post(new Runnable() {
            public void run() {
//                mImageView.clear();
//                b.recycle();
            }
        });

//        finish();
    }
    
    private File getFiles(String fileName){
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),fileName);
        if (file != null){
            return file;
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAllImages != null) {
            mAllImages.close();
        }
        super.onDestroy();
    }

    Runnable mRunFaceDetection = new Runnable() {
        @SuppressWarnings("hiding")
        float mScale = 1F;
        Matrix mImageMatrix;
        FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
        int mNumFaces;

        // For each face, we create a HightlightView for it.
        private void handleFace(FaceDetector.Face f) {
            PointF midPoint = new PointF();

            int r = ((int) (f.eyesDistance() * mScale)) * 2;
            f.getMidPoint(midPoint);
            midPoint.x *= mScale;
            midPoint.y *= mScale;

            int midX = (int) midPoint.x;
            int midY = (int) midPoint.y;

            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            RectF faceRect = new RectF(midX, midY, midX, midY);
            faceRect.inset(-r, -r);
            if (faceRect.left < 0) {
                faceRect.inset(-faceRect.left, -faceRect.left);
            }

            if (faceRect.top < 0) {
                faceRect.inset(-faceRect.top, -faceRect.top);
            }

            if (faceRect.right > imageRect.right) {
                faceRect.inset(faceRect.right - imageRect.right,
                               faceRect.right - imageRect.right);
            }

            if (faceRect.bottom > imageRect.bottom) {
                faceRect.inset(faceRect.bottom - imageRect.bottom,
                               faceRect.bottom - imageRect.bottom);
            }

            hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop,false
                     /*mAspectX != 0 && mAspectY != 0*/);

            mImageView.add(hv);
        }

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // make the default size about 4/5 of the width or height
//            int cropWidth = Math.min(width, height) * 4 / 5;
//            int cropHeight = cropWidth;
            int cropWidth = width;
            int cropHeight = height;

            if (mAspectX != 0 && mAspectY != 0) {
                if (mAspectX > mAspectY) {
                	// �����辩缉���
                    cropHeight = cropWidth * mAspectY ;// mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX ;// mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;
            
            Mat imgSource = new Mat();
            Utils.bitmapToMat(mBitmap, imgSource);
            //convert the image to black and white 
            Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BGR2GRAY);
            //convert the image to black and white does (8 bit)
            Imgproc.Canny(imgSource, imgSource, 50, 50);

            //apply gaussian blur to smoothen lines of dots
            Imgproc.GaussianBlur(imgSource, imgSource, new  org.opencv.core.Size(5, 5), 5);

            //find the contours
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            double maxArea = -1;
            int maxAreaIdx = -1;
            Log.d("size",Integer.toString(contours.size()));
            MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint largest_contour = contours.get(0);
            //largest_contour.ge
            List<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
            //Imgproc.drawContours(imgSource,contours, -1, new Scalar(0, 255, 0), 1);

            for (int idx = 0; idx < contours.size(); idx++) {
                temp_contour = contours.get(idx);
                double contourarea = Imgproc.contourArea(temp_contour);
                //compare this contour to the previous largest contour found
                if (contourarea > maxArea) {
                    //check if this contour is a square
                    MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
                    int contourSize = (int)temp_contour.total();
                    MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
                    Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize*0.05, true);
                    if (approxCurve_temp.total() == 4) {
                        maxArea = contourarea;
                        maxAreaIdx = idx;
                        approxCurve=approxCurve_temp;
                        largest_contour = temp_contour;
                    }
                }
            }

           Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BayerBG2RGB);
           double[] temp_double;
           float x1, y1, x2, y2;
           temp_double = approxCurve.get(0,0);       
           Point p1 = new Point(temp_double[0], temp_double[1]);
           x1 = (float)temp_double[0];
           y1 = (float)temp_double[1];
           //Core.circle(imgSource,p1,55,new Scalar(0,0,255));
           //Imgproc.warpAffine(sourceImage, dummy, rotImage,sourceImage.size());
           temp_double = approxCurve.get(1,0);       
           Point p2 = new Point(temp_double[0], temp_double[1]);
          // Core.circle(imgSource,p2,150,new Scalar(255,255,255));
           temp_double = approxCurve.get(2,0);       
           Point p3 = new Point(temp_double[0], temp_double[1]);
           x2 = (float)temp_double[0];
           y2 = (float)temp_double[1];
           //Core.circle(imgSource,p3,200,new Scalar(255,0,0));
           temp_double = approxCurve.get(3,0);       
           Point p4 = new Point(temp_double[0], temp_double[1]);

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            //RectF cropRect = new RectF(x1, y1, x2, y2);
         // �����辩缉���
            
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,false
                     /*mAspectX != 0 && mAspectY != 0*/);
            mImageView.add(hv);
        }

        // Scale the image down for faster face detection.
        private Bitmap prepareBitmap() {
            if (mBitmap == null) {
                return null;
            }

            // 256 pixels wide is enough.
            if (mBitmap.getWidth() > 256) {
                mScale = 256.0F / mBitmap.getWidth();
            }
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap
                    .getWidth(), mBitmap.getHeight(), matrix, true);
            return faceBitmap;
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();
            Bitmap faceBitmap = prepareBitmap();

            mScale = 1.0F / mScale;
            if (faceBitmap != null && mDoFaceDetection) {
                FaceDetector detector = new FaceDetector(faceBitmap.getWidth(),
                        faceBitmap.getHeight(), mFaces.length);
                mNumFaces = detector.findFaces(faceBitmap, mFaces);
            }

            if (faceBitmap != null && faceBitmap != mBitmap) {
                faceBitmap.recycle();
            }

            mHandler.post(new Runnable() {
                public void run() {
                    mWaitingToPick = mNumFaces > 1;
//                    if (mNumFaces > 0) {
//                        for (int i = 0; i < mNumFaces; i++) {
//                            handleFace(mFaces[i]);
//                        }
//                    } else {
//                        makeDefault();
//                    }
                    makeDefault();
                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() == 1) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                    }

                    if (mNumFaces > 1) {
                        Toast t = Toast.makeText(CropImage.this,
                                R.string.multiface_crop_help,
                                Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
            });
        }
    };
}

class CropImageView extends ImageViewTouchBase {
    ArrayList<HighlightView> mHighlightViews = new ArrayList<HighlightView>();
    HighlightView mMotionHighlightView = null;
    float mLastX, mLastY;
    int mMotionEdge;

    @Override
    protected void onLayout(boolean changed, int left, int top,
                            int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mBitmapDisplayed.getBitmap() != null) {
            for (HighlightView hv : mHighlightViews) {
                hv.mMatrix.set(getImageMatrix());
                hv.invalidate();
                if (hv.mIsFocused) {
                    centerBasedOnHighlightView(hv);
                }
            }
        }
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomIn() {
        super.zoomIn();
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomOut() {
        super.zoomOut();
        for (HighlightView hv : mHighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        for (int i = 0; i < mHighlightViews.size(); i++) {
            HighlightView hv = mHighlightViews.get(i);
            hv.mMatrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    // According to the event's position, change the focus to the first
    // hitting cropping rectangle.
    private void recomputeFocus(MotionEvent event) {
        for (int i = 0; i < mHighlightViews.size(); i++) {
            HighlightView hv = mHighlightViews.get(i);
            hv.setFocus(false);
            hv.invalidate();
        }

        for (int i = 0; i < mHighlightViews.size(); i++) {
            HighlightView hv = mHighlightViews.get(i);
            int edge = hv.getHit(event.getX(), event.getY());
            if (edge != HighlightView.GROW_NONE) {
                if (!hv.hasFocus()) {
                    hv.setFocus(true);
                    hv.invalidate();
                }
                break;
            }
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CropImage cropImage = (CropImage) getContext();
        if (cropImage.mSaving) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (cropImage.mWaitingToPick) {
                    recomputeFocus(event);
                } else {
                    for (int i = 0; i < mHighlightViews.size(); i++) {
                        HighlightView hv = mHighlightViews.get(i);
                        int edge = hv.getHit(event.getX(), event.getY());
                        if (edge != HighlightView.GROW_NONE) {
                            mMotionEdge = edge;
                            mMotionHighlightView = hv;
                            mLastX = event.getX();
                            mLastY = event.getY();
                            mMotionHighlightView.setMode(
                                    (edge == HighlightView.MOVE)
                                    ? HighlightView.ModifyMode.Move
                                    : HighlightView.ModifyMode.Grow);
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (cropImage.mWaitingToPick) {
                    for (int i = 0; i < mHighlightViews.size(); i++) {
                        HighlightView hv = mHighlightViews.get(i);
                        if (hv.hasFocus()) {
                            cropImage.mCrop = hv;
                            for (int j = 0; j < mHighlightViews.size(); j++) {
                                if (j == i) {
                                    continue;
                                }
                                mHighlightViews.get(j).setHidden(true);
                            }
                            centerBasedOnHighlightView(hv);
                            cropImage.mWaitingToPick = false;
                            return true;
                        }
                    }
                } else if (mMotionHighlightView != null) {
                    centerBasedOnHighlightView(mMotionHighlightView);
                    mMotionHighlightView.setMode(
                            HighlightView.ModifyMode.None);
                }
                mMotionHighlightView = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (cropImage.mWaitingToPick) {
                    recomputeFocus(event);
                } else if (mMotionHighlightView != null) {
                    mMotionHighlightView.handleMotion(mMotionEdge,
                            event.getX() - mLastX,
                            event.getY() - mLastY);
                    mLastX = event.getX();
                    mLastY = event.getY();

                    if (true) {
                        // This section of code is optional. It has some user
                        // benefit in that moving the crop rectangle against
                        // the edge of the screen causes scrolling but it means
                        // that the crop rectangle is no longer fixed under
                        // the user's finger.
                        ensureVisible(mMotionHighlightView);
                    }
                }
                break;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                center(true, true);
                break;
            case MotionEvent.ACTION_MOVE:
                // if we're not zoomed then there's no point in even allowing
                // the user to move the image around.  This call to center puts
                // it back to the normalized location (with false meaning don't
                // animate).
                if (getScale() == 1F) {
                    center(true, true);
                }
                break;
        }

        return true;
    }

    // Pan the displayed image to make sure the cropping rectangle is visible.
    private void ensureVisible(HighlightView hv) {
        Rect r = hv.mDrawRect;

        int panDeltaX1 = Math.max(0, getLeft() - r.left);
        int panDeltaX2 = Math.min(0, getRight() - r.right);

        int panDeltaY1 = Math.max(0, getTop() - r.top);
        int panDeltaY2 = Math.min(0, getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnHighlightView(HighlightView hv) {
        Rect drawRect = hv.mDrawRect;

        float width = drawRect.width();
        float height = drawRect.height();

        float thisWidth = getWidth();
        float thisHeight = getHeight();

        float z1 = thisWidth / width * .6F;
        float z2 = thisHeight / height * .6F;

        float zoom = Math.min(z1, z2);
        zoom = zoom * this.getScale();
        zoom = Math.max(1F, zoom);

        if ((Math.abs(zoom - getScale()) / zoom) > .1) {
            float [] coordinates = new float[] {hv.mCropRect.centerX(),
                                                hv.mCropRect.centerY()};
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300F);
        }

        ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mHighlightViews.size(); i++) {
            mHighlightViews.get(i).draw(canvas);
        }
    }

    public void add(HighlightView hv) {
        mHighlightViews.add(hv);
        invalidate();
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
