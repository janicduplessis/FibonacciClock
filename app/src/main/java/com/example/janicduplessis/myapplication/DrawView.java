package com.example.janicduplessis.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by francis on 2015-11-07.
 */
public class DrawView extends View {


    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void fiboDetected(boolean isFibo){
        if(isFibo)
        {
            mPaint.setColor(Color.GREEN);
        }else{
            mPaint.setColor(Color.BLUE);
        }
    }

    private Paint mPaint = new Paint();


    @Override
    public void onDraw(Canvas canvas) {

       canvas.drawColor(0x00AAAAAA);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        //then draw the square !!
        paint.setStrokeWidth(8);

        //landscape
        if( width > height) {
            canvas.drawRect((width / 7),
                    ((height / 9)),
                    ((width - (width / 7))),
                    ((height - (height / 9))),
                    paint);
        }else{
            canvas.drawRect((width / 7),
                    ((height / 3)),
                    ((width - (width / 7))),
                    ((height - (height / 3))),
                    paint);
        }

    }
}
