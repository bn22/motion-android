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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

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
        if (mAccelerometer == null) {
            finish();
        }

        Button start = (Button)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                for (int i = 0; i < 5; i++) {
                    view.collectArray.add(new Ball(r.nextInt(1000), r.nextInt(100), 50));
                    view.avoidArray.add(new Ball(r.nextInt(1000), r.nextInt(100), 50));
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "" + event);

        boolean gesture = mDetector.onTouchEvent(event);
        if(gesture) return true;

        int action = MotionEventCompat.getActionMasked(event);

        switch(action){
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "Finger up!");
                Random r = new Random();
                int ran = r.nextInt(2);
                if (view.player.radius == 75) {
                    if (ran == 0) {
                        view.player.radius = 50;
                    } else if (ran == 1) {
                        view.player.radius = 100;
                    }
                } else {
                    view.player.radius = 75;
                }
                Toast.makeText(getApplicationContext(), "Player Size Changed", Toast.LENGTH_SHORT).show();
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
            view.player.dx = 10 * event.values[0];
            view.player.dy = 10 * event.values[0];
        }
        else if(Math.abs(event.values[1]) > 2.0){
            Log.v(TAG, "Shook up: "+event.values[1]);
            view.player.dx = -10 * event.values[0];
            view.player.dy = -10 * event.values[0];
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
            view.player.dx = -1*velocityX*scaleFactor;
            view.player.dy = -1*velocityY*scaleFactor;

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
