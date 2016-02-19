package edu.uw.bn22.motiongame;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MainActivity extends Activity implements SensorEventListener{

    private static final String TAG = "**MOTION**";

    private DrawingSurfaceView view;

    private GestureDetectorCompat mDetector;

    private SensorManager mSensorManager;

    private Sensor mAccelerometer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (DrawingSurfaceView)findViewById(R.id.drawingView);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(mAccelerometer == null) { //we don't have one
            Log.v(TAG, "No accelerometer");
            finish();
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "" + event);

        boolean gesture = mDetector.onTouchEvent(event);

        if(gesture) return true;
        int action = MotionEventCompat.getActionMasked(event);

        switch(action){
            case MotionEvent.ACTION_DOWN:
                Log.v(TAG,"Finger down!");
                //shold be synchronized!
                view.ball.cx = event.getX();
                view.ball.cy = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "Finger up!");
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                //second finger

                //multi-touch pseudo-example thing
                //int mSecondPointerId = event.getPointerId(1);


            case MotionEvent.ACTION_MOVE:
                //shold be synchronized!
//                view.ball.cx = event.getX();
//                view.ball.cy = event.getY();

                //event.findPointerIndex(mSecondPointerId)
                //respond to second finger

                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onResume() {
        //register sensor
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //unregister sensor
        mSensorManager.unregisterListener(this, mAccelerometer);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(Math.abs(event.values[0]) > 2.0){
            Log.v(TAG, "Shook left: "+event.values[0]);
            view.ball.dx = 10 * event.values[0];
        }
        else if(Math.abs(event.values[0]) < -2.0){
            Log.v(TAG, "Shook Right: " +event.values[0]);
            view.ball.dx = -10 * event.values[0];
        }
        else if(Math.abs(event.values[1]) > 2.0){
            Log.v(TAG, "Shook up: "+event.values[1]);
            view.ball.dy = -10 * event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true; //we've got this
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float scaleFactor = .03f;

            //fling!
            Log.v(TAG, "Fling! "+ velocityX + ", " + velocityY);
            view.ball.dx = -1*velocityX*scaleFactor;
            view.ball.dy = -1*velocityY*scaleFactor;

            return true; //we got this
        }
    }

    //starter pseudo-example
    class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            detector.getScaleFactor();

            return super.onScale(detector);
        }
    }

}
