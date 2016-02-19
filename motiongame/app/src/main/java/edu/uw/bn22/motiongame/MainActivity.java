package edu.uw.bn22.motiongame;

import android.app.Activity;
import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializes the motion and gesture trackers, sound ppo and drawing surface
        view = (DrawingSurfaceView)findViewById(R.id.drawingView);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mAccelerometer == null) {
            finish();
        }
        initializeSoundPool();

        //Creates a listening for a button that starts the game
        Button start = (Button)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                for (int i = 0; i < 5; i++) {
                    view.collectArray.add(new Ball(r.nextInt(1000), r.nextInt(100), 50));
                    view.avoidArray.add(new Ball(r.nextInt(1000), r.nextInt(100), 50));
                }
                playSound(2);
            }
        });
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

        soundIds[0] = mSoundPool.load(this, R.raw.saber_on, 0);
        soundIds[1] = mSoundPool.load(this, R.raw.saber_swing1, 0);
        soundIds[2] = mSoundPool.load(this, R.raw.saber_swing2, 0);
        soundIds[3] = mSoundPool.load(this, R.raw.saber_swing3, 0);
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
                Random r = new Random();
                int ran = r.nextInt(2);
                if (view.player.radius == 50) {
                    if (ran == 0) {
                        view.player.radius = 25;
                        Toast.makeText(getApplicationContext(), "Player Size Smaller", Toast.LENGTH_SHORT).show();
                    } else if (ran == 1) {
                        view.player.radius = 75;
                        Toast.makeText(getApplicationContext(), "Player Size Larger", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    view.player.radius = 50;
                    Toast.makeText(getApplicationContext(), "Player Size Normal", Toast.LENGTH_SHORT).show();
                }
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

    //
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
