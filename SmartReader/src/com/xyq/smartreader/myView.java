package com.xyq.smartreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class myView extends View {

	private int l = 20;
	private int t = 20;
	private int r = 100;
	private int b = 100;

	Bitmap bitmap;
	Bitmap bitmapOld;

	@SuppressLint({ "NewApi", "NewApi" })
	public myView(Context context, Bitmap bitmap) {
		super(context);
		this.bitmap = Bitmap.createScaledBitmap(bitmap, 480, 540, true);
		// setBackgroundColor(Color.BLACK);
		// setAlpha(0.8f);
	}

	int xy[] = new int[2];
	int temp[] = new int[2];

	int tempM[] = new int[2];
	boolean mFlag = false;
	boolean mFlagM = false;
	boolean mFlag1 = false;
	boolean mFlag2 = false;
	boolean kaishi = true;
	int change = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xy[0] = (int) event.getX();
			xy[1] = (int) event.getY();
			if (xy[0] < r && xy[0] > l && xy[1] < b && xy[1] > t) {
				mFlag2 = false;
				mFlag = true;
				mFlag1 = false;
			} else {
				mFlag = false;

			}
			if (xy[0] < r && xy[0] > l && xy[1] == t) {
				mFlag1 = true;

			} else {

				if (tmL == 0 || tmR == 0 || tmT == 0) {

				} else {
					if (bitmapOld == null) {
						Log.i("RG", "bitmap--->>>h" + bitmap.getHeight());
						Log.i("RG", "bitmap--->>>W" + bitmap.getWidth());
						Log.i("RG", "tmL--->>>W" + tmL);
						Log.i("RG", "tmT--->>>W" + tmT);
						Log.i("RG", "tmR--->>>W" + tmR);
						Log.i("RG", "tmB--->>>W" + tmB);
						bitmapOld = Bitmap.createBitmap(bitmap, tmL, tmT, tmR
								- tmL, tmB - tmT, null, true);
					} else {
						bitmapOld = null;

						bitmapOld = Bitmap.createBitmap(bitmap, tmL, tmT, tmR
								- tmL, tmB - tmT, null, true);
					}
				}
			}
		case MotionEvent.ACTION_MOVE:
			temp[0] = (int) event.getX();
			temp[1] = (int) event.getY();
			if (mFlag) {
				if (xy[0] == temp[0] && xy[1] == temp[1]) {

				} else {
					mFlagM = true;
					l = temp[0];
					t = temp[1];

					if (tuodongH > 0) {
						r = l + tuodongW;
						b = t + tuodongH;
					} else {
						r = l + 80;
						b = t + 80;
					}
					invalidate();
				}
			}
			if (mFlag1) {
				Log.i("RG", "panduanl you wentil ---->>>" + xy[0] + ",,,,"
						+ xy[1]);
				if (temp[0] > xy[0] || temp[1] < xy[1]) {
					Log.i("RG", "-------mFlag1------>>>>>>" + temp[0] + ",,,,"
							+ temp[1]);
					mFlag2 = true;

					change += 5;
					// l = xy[0];
					// t = xy[1];
					tempM[0] = r = (l + 80) + change;
					tempM[1] = b = (t + 80) + change;
					invalidate();

				} else {
					change -= 5;
					tempM[0] = r = (l + 80) + change;
					tempM[1] = b = (t + 80) + change;
					invalidate();
				}
			}
		}
		return true;
	}

	int tuodongH;
	int tuodongW;

	int tmL;
	int tmT;
	int tmR;
	int tmB;

	public Bitmap getBitmap() {

		return bitmapOld;
	}

	public void Pcanvas(Canvas canvas) {
		Paint mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeMiter(6);
		mPaint.setStrokeWidth(3);
		mPaint.setColor(Color.RED);

		canvas.drawRect(l, t, r, b, mPaint);
		tuodongH = b - t;
		tuodongW = r - l;
		Paint mPaint1 = new Paint();

		mPaint1.setColor(Color.BLACK);
		mPaint1.setAlpha(100);
		canvas.drawRect(0, 0, 480, t, mPaint1);// 1111
		canvas.drawRect(0, b, 480, 800, mPaint1);
		canvas.drawRect(0, t, l, b, mPaint1);
		canvas.drawRect(r, t, 480, b, mPaint1);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (mFlag1 && mFlag2) {

			Pcanvas(canvas);
			// mFlag1 = false;
			// mFlag2 = false;
			return;
		}
		if (mFlag == true && mFlagM == true) {
			Paint mPaint = new Paint();
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeMiter(6);
			mPaint.setStrokeWidth(3);
			mPaint.setColor(Color.RED);

			canvas.drawRect(l, t, r, b, mPaint);
			tmL = l;
			tmT = t;
			tmR = r;
			tmB = b;
			Paint mPaint1 = new Paint();

			mPaint1.setColor(Color.BLACK);
			mPaint1.setAlpha(100);
			canvas.drawRect(0, 0, 480, t, mPaint1);// 1111
			canvas.drawRect(0, b, 480, 800, mPaint1);
			canvas.drawRect(0, t, l, b, mPaint1);
			canvas.drawRect(r, t, 480, b, mPaint1);
			return;
		}
		if (kaishi) {
			Paint mPaint = new Paint();
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeMiter(6);
			mPaint.setStrokeWidth(3);
			mPaint.setColor(Color.RED);

			canvas.drawRect(l, t, r, b, mPaint);
			Paint mPaint1 = new Paint();

			mPaint1.setColor(Color.BLACK);
			mPaint1.setAlpha(100);
			canvas.drawRect(0, 0, 480, t, mPaint1);// 1111
			canvas.drawRect(0, b, 480, 800, mPaint1);
			canvas.drawRect(0, t, l, b, mPaint1);
			canvas.drawRect(r, t, 480, b, mPaint1);
			kaishi = false;
		} else {
			return;
		}

		super.onDraw(canvas);
	}
}