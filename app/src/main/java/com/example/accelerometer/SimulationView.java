package com.example.accelerometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by nicolasgirardot on 3/21/17.
 */

public class SimulationView extends View implements SensorEventListener {

    private Bitmap mField;
    private Bitmap mBasket;
    private Bitmap mBitmap;
    private static final int BALL_SIZE = 64;
    private static final int HOLE_SIZE = 80;

    private float mXOrigin;
    private float mYOrigin;
    private float mHorizontalBound;
    private float mVerticalBound;


    private Particule mBall = new Particule();

    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;
    private long mSensorTimeStamp;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Display mDisplay;

    public SimulationView(Context context) {
        super(context);

        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBitmap = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);

        Bitmap hole = BitmapFactory.decodeResource(getResources(), R.drawable.hole);
        mBasket = Bitmap.createScaledBitmap(hole, HOLE_SIZE, HOLE_SIZE, true);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDither = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                mSensorX = event.values[0];
                mSensorY = event.values[1];
                break;
            case Surface.ROTATION_90:
                mSensorX = -event.values[1];
                mSensorY = event.values[0];
                break;
            case Surface.ROTATION_180:
                mSensorX = -event.values[0];
                mSensorY = -event.values[1];
                break;
            case Surface.ROTATION_270:
                mSensorX = event.values[1];
                mSensorY = -event.values[0];
                break;
        }
        mSensorZ = event.values[2];
        mSensorTimeStamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startSimulation() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopSimulation() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBasket, mXOrigin - HOLE_SIZE / 2, mYOrigin - HOLE_SIZE / 2, null);

        mBall.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
        mBall.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);

        canvas.drawBitmap(mBitmap,
                (mXOrigin - BALL_SIZE / 2) + mBall.mPosX,
                (mYOrigin - BALL_SIZE / 2) - mBall.mPosY, null);

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXOrigin = w * 0.5f;
        mYOrigin = h * 0.5f;

        mHorizontalBound = (w - BALL_SIZE) * 0.5f;
        mVerticalBound = (h - BALL_SIZE) * 0.5f;
    }
}
