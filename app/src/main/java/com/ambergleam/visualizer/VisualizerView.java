package com.ambergleam.visualizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.ambergleam.visualizer.utils.RandomUtils;

public class VisualizerView extends View {

    private Context mContext;

    private Paint mForePaint = new Paint();
    private Rect mRect = new Rect();
    private byte[] mBytes;

    private float[] mPoints;

    public VisualizerView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        mBytes = null;
        mForePaint.setAntiAlias(true);
        mForePaint.setStrokeWidth(RandomUtils.getRandomLineWidth());
        mForePaint.setColor(RandomUtils.getRandomFadedColor());
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground();

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

//        for (int i = 1; i < 8; i++) {
//            drawLines(canvas, i);
//        }
        drawLines(canvas, 2);

    }

    private void drawLines(Canvas canvas, int scale) {
        mRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mBytes.length - 1; i++) {
            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
            mPoints[i * 4 + 1] = mRect.height() / scale + ((byte) (mBytes[i] + 128)) * (mRect.height() / scale) / 128;
            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i * 4 + 3] = mRect.height() / scale + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / scale) / 128;
        }

        canvas.drawLines(mPoints, mForePaint);
    }

    private void drawBackground() {
        setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
    }

}
