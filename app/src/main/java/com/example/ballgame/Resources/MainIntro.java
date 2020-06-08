package com.example.ballgame.Resources;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.ballgame.R;

/**
 * Created by michaelwasher on 4/10/15.
 */
public class MainIntro extends DrawingView {

    Context context;

    public MainIntro(Context context) {
        super(context);
        this.context = context;

        // Set up the Paint object with some default settings
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setTextSize(50);
    }
    //Initializing Balls
    Ball ball = new Ball(new V2(100,100),50);
    Brick brick = new Brick(new V2(10,10),50,50);

    float dx = 10;
    float dy = 10;
    protected void onDraw(Canvas canvas) {
        int backgroundColor = Color.BLUE;
        int titleColor = Color.BLACK;

        //Paint BackGround
        paint.setColor(Color.BLUE);
        canvas.drawPaint(paint);
        //Draw Title
        paint.setColor(Color.BLACK);
        canvas.drawText("Welcome to mainDisplay", 100, 100, paint);

        //Move Brick Across Screen
        brick.repositionRelative((int)dx,(int)dy);


        //Draw Ball
        paint.setColor(Color.YELLOW);
        ball.draw(canvas,paint);
        brick.draw(canvas,paint);



        //Queue Redraw
        invalidate();

    }
}