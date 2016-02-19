package edu.uw.bn22.motiongame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * An example SurfaceView for generating graphics on
 * @author Joel Ross
 * @version Winter 2016
 */
public class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceView";

    private int viewWidth, viewHeight; //size of the view

    private Bitmap bmp; //image to draw on

    private SurfaceHolder mHolder; //the holder we're going to post updates to
    private DrawingRunnable mRunnable; //the code htat we'll want to run on a background thread
    private Thread mThread; //the background thread

    private Paint redPaint; //drawing variables (pre-defined for speed)
    private Paint bluePaint;
    private Paint greenPaint;
    private Paint yellowPaint;
    private Paint cyanPaint;
    private Random r = new Random();

    public Ball player;
    public ArrayList<Paint> paintArray = new ArrayList<Paint>();
    public ArrayList<Ball> ballArray = new ArrayList<Ball>();

    public float a;
    public float b;
    public float c;
    public float d;

    /**
     * We need to override all the constructors, since we don't know which will be called
     */
    public DrawingSurfaceView(Context context) {
        this(context, null);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        viewWidth = 1; viewHeight = 1; //positive defaults; will be replaced when #surfaceChanged() is called

        // register our interest in hearing about changes to our surface
        mHolder = getHolder();
        mHolder.addCallback(this);

        mRunnable = new DrawingRunnable();

        //set up drawing variables ahead of timme
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);
        bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint.setColor(Color.BLUE);
        paintArray.add(bluePaint);
        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setColor(Color.GREEN);
        paintArray.add(greenPaint);
        yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint.setColor(Color.YELLOW);
        paintArray.add(yellowPaint);
        cyanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cyanPaint.setColor(Color.CYAN);
        paintArray.add(cyanPaint);

        player = new Ball(100, 150, 10);
    }

    /**
     * Helper method for the "game loop"
     */
    public void update(){
        //update the "game state" here (move things around, etc).
        //TODO: fill in your own logic here!
        player.cx += player.dx;
        player.cy += player.dy;

        //slow down
        player.dx *= 0.99;
        player.dy *= 0.99;

        /* hit detection */
        if(player.cx + player.radius > viewWidth) { //left bound
            player.cx = viewWidth - player.radius;
        }
        else if(player.cx - player.radius < 0) { //right bound
            player.cx = player.radius;
        }
        else if(player.cy + player.radius > viewHeight) { //bottom bound
            player.cy = viewHeight - player.radius;
        }
        else if(player.cy - player.radius < 0) { //top bound
            player.cy = player.radius;
        }

        for (int i = 0; i < ballArray.size(); i++) {
            int a = (int) Math.ceil(player.cx); //+ (int) Math.ceil(player.radius) ;
            int b = (int) Math.ceil(ballArray.get(i).cx); //+ (int) Math.ceil(ballArray.get(i).radius);
            int c = (int) Math.ceil(player.cy); //+ (int) Math.ceil(player.radius);
            int d = (int) Math.ceil(ballArray.get(i).cy); //+ (int) Math.ceil(ballArray.get(i).radius);
            if (a + 3 > b) {
                Log.v(TAG, "First");
                if (a - 3 <= b) {
                    Log.v(TAG, "Second");
                    if (c + 3 > d) {
                        Log.v(TAG, "Third");
                        if (c - 3 <= d) {
                            Log.v(TAG, "Fourth");
                        }
                    }
                }
            }
        }
    }


    /**
     * Helper method for the "render loop"
     * @param canvas The canvas to draw on
     */
    public void render(Canvas canvas){

        if(canvas == null) return; //if we didn't get a valid canvas for whatever reason

        //TODO: replace the below example with your own rendering
        canvas.drawColor(Color.BLACK); //black out the background
        canvas.drawCircle(player.cx, player.cy, player.radius, bluePaint); //we can draw directly onto the canvas
        for(int i = 0; i < ballArray.size(); i++) {
            canvas.drawCircle(ballArray.get(i).cx, ballArray.get(i).cy, ballArray.get(i).radius, redPaint);
        }
        canvas.drawBitmap(bmp, 0, 0, null);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // create thread only; it's started in surfaceCreated()
        Log.v(TAG, "making new thread");
        mThread = new Thread(mRunnable);
        mRunnable.setRunning(true); //turn on the runner
        mThread.start(); //start up the thread when surface is created
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (mHolder) { //synchronized to keep this stuff atomic
            viewWidth = width;
            viewHeight = height;
            bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888); //new buffer to draw on

            player = new Ball(viewWidth / 2, viewHeight, 10);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        mRunnable.setRunning(false); //turn off
        boolean retry = true;
        while(retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //will try again...
            }
        }
        Log.d(TAG, "Drawing thread shut down.");
    }

    /**
     * An inner class representing a runnable that does the drawing. Animation timing could go in here.
     * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
     */
    public class DrawingRunnable implements Runnable {

        private boolean isRunning; //whether we're running or not (so we can "stop" the thread)

        public void setRunning(boolean running){
            this.isRunning = running;
        }

        public void run() {
            Canvas canvas;
            while(isRunning)
            {
                canvas = null;
                try {
                    canvas = mHolder.lockCanvas(); //grab the current canvas
                    synchronized (mHolder) {
                        update(); //update the game
                        render(canvas); //redraw the screen
                    }
                }
                finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}