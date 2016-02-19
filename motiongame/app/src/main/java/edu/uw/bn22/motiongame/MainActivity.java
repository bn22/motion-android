package edu.uw.bn22.motiongame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MenuInflater;
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

    private SoundPool mSoundPool;
    private int[] soundIds;
    private boolean[] loadedSound;
    private Random r = new Random();

    private float width;
    private float height;
    private long lastUpdate = 0;
    private float last_x;
    private float last_y;
    private float last_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializes the motion and gesture trackers, sound ppo and drawing surface
        view = (DrawingSurfaceView)findViewById(R.id.drawingView);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        if (mAccelerometer == null) {
            finish();
        }
        initializeSoundPool();

        //Gets the screen size and turns them into floats
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width =(float)size.x;
        height = (float)size.y;
        view.height = height;
        view.width = width;

        //Starts the game with 15 enemies
        for (int i = 0; i < 15; i++) {
            float a = r.nextFloat() * (width - 0f);
            float b = r.nextFloat() * (height - 100f) + 100f;
            while (a - 15f < 0f || a + 15f > width || b - 15f < 100f || b + 15f > width) {
                a = r.nextFloat() * (height - 0f);
                b = r.nextFloat() * (width - 100f) + 100f;
            }
            Ball obstacle = new Ball(a, b, 15);
            view.ballArray.add(obstacle);
        }
    }

    //Full screens the application
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    //Initializes sounds so it can be played during the game
    @SuppressWarnings("deprecation")
    private void initializeSoundPool(){
        final int MAX_STREAMS = 4;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attribs = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS)
                    .setAudioAttributes(attribs)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        soundIds = new int[5];
        loadedSound = new boolean[5];

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    if(sampleId == soundIds[0]) {loadedSound[0] = true;}// playSound(0);}
                    else if(sampleId == soundIds[1]) loadedSound[1] = true;
                    else if(sampleId == soundIds[2]) loadedSound[2] = true;
                    else if(sampleId == soundIds[3]) loadedSound[3] = true;
                    else if(sampleId == soundIds[4]) loadedSound[4] = true;
                }
            }
        });

        soundIds[0] = mSoundPool.load(this, R.raw.loz, 0); //Add Sound
        soundIds[1] = mSoundPool.load(this, R.raw.oracle, 0); //Start Game
        soundIds[2] = mSoundPool.load(this, R.raw.la_link_throw, 0); // Collision Sound
        soundIds[3] = mSoundPool.load(this, R.raw.la_fanfare_item, 0); // Finish Song
        soundIds[4] = mSoundPool.load(this, R.raw.saber_swing4, 0);
    }

    //Creates a method that can be called to play sounds
    public void playSound(int index){
        if(loadedSound[index]){
            mSoundPool.play(soundIds[index],1,1,1,0,1);
        }
    }

    //Handles touch control from the user
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "" + event);

        boolean gesture = mDetector.onTouchEvent(event);
        if(gesture) return true;

        int action = MotionEventCompat.getActionMasked(event);

        switch(action){
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "Finger up!");
                float x = r.nextFloat() * (width - 0f);
                float y = r.nextFloat() * (height - 100f) + 100f;
                Log.v(TAG, String.valueOf(x));
                Log.v(TAG, String.valueOf(y));
                while (x - 15f < 0f || x + 15f > width || y - 15f < 100f || y + 15f > width) {
                    x = r.nextFloat() * (height - 0f);
                    y = r.nextFloat() * (width - 100f) + 100f;
                }
                Ball obstacle = new Ball(x, y, 15);
                view.ballArray.add(obstacle);
                playSound(0);
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    //Restarts the sensor to track movement
    @Override
    protected void onResume() {
        //register sensor
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    //Stops the sensor to track movement when the user puts this app in the background
    @Override
    protected void onPause() {
        //unregister sensor
        mSensorManager.unregisterListener(this, mAccelerometer);
        super.onPause();
    }

    //When inputs a motion commad. Movement of the phone will move the character while shaking it will restart the game
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (view.collide) {
            playSound(2);
            view.collide = false;
        }

        if (view.end) {
            playSound(3);
            Toast.makeText(this, "You have won", Toast.LENGTH_SHORT).show();
            view.end = false;
        }

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            Ball ball = view.player;
            ball.dx = x * -15;
            ball.dy = y * 10;

            //ShakeActivity that detect's a shake event
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long timeDifference = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ timeDifference * 10000;
                if (speed > 600) {
                    view.player = new Ball(width / 2, height, 10);
                    playSound(1);
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Listens for gesture inputs from the user
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
}
