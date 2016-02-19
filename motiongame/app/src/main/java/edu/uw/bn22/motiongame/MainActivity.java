package edu.uw.bn22.motiongame;

import android.app.Activity;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MainActivity extends Activity {

    private static final String TAG = "**MOTION**";

    private DrawingSurfaceView view;

    private GestureDetectorCompat mDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (DrawingSurfaceView)findViewById(R.id.drawingView);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
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
