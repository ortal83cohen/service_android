package com.hpe.sb.mobile.app.common.uiComponents.gifView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class CustomGifView extends View {

    private InputStream gifInputStream;
    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long movieDuration;
    private long mMovieStart;

    public CustomGifView(Context context) {
        super(context);
        init(context);
    }

    public CustomGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomGifView(Context context, AttributeSet attrs,
                         int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        if(isInEditMode()) {
            return;
        }

        setFocusable(true);
        try {
            gifInputStream = context.getAssets().open("loader_snake.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }

        gifMovie = Movie.decodeStream(gifInputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

    public int getMovieWidth(){
        return movieWidth;
    }

    public int getMovieHeight(){
        return movieHeight;
    }

    public long getMovieDuration(){
        return movieDuration;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //get current time
        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) {   // first time
            mMovieStart = now;
        }

        //check the gif is defined
        if (gifMovie != null) {

            int dur = gifMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            //get the relative time of the gif
            int relTime = (int)((now - mMovieStart) % dur);

            //set the time of the gif
            gifMovie.setTime(relTime);

            //go to that time
            gifMovie.draw(canvas, 0, 0);
            invalidate();

        }

    }

}