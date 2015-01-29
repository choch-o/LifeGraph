package com.example.chocho.lifegraph;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chocho on 2015-01-25.
 */
public class GraphActivity extends Activity implements View.OnClickListener
{
    Intent intent;
    axis view = null;
    axis tView = null;
    Bitmap allBitmap;
    ImageView imageView;
    FrameLayout framelayout;
    ImageButton graphAddButton, moveToListButton, categoryListButton;

    int itemSize = 5;
    int width, height;
    boolean isCheck = false;
    boolean[] myCheck = new boolean[itemSize];
    CharSequence[] asdf = new CharSequence[itemSize];
    List<Integer> mSelectedItmes = new ArrayList<Integer>();
    List<Integer> mTempSelectedItmes = new ArrayList<Integer>();

    //Fade in, out
    Animation animFadeIn;
    Animation animFadeOut;

    float rTop = 0, rLeft = 0;
    float rRight = -1, rBottom = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_graph);

        //Active Button
        graphAddButton = (ImageButton) findViewById(R.id.graphAddButton);
        moveToListButton = (ImageButton) findViewById(R.id.moveToListButton);
        categoryListButton = (ImageButton) findViewById(R.id.categoryListButton);
        graphAddButton.setBackground(null);
        moveToListButton.setBackground(null);
        categoryListButton.setBackground(null);
        graphAddButton.setOnClickListener(this);
        moveToListButton.setOnClickListener(this);
        categoryListButton.setOnClickListener(this);

        //Draw Point, Line
        framelayout = (FrameLayout) findViewById(R.id.graphContainer);
        view = new axis(getApplicationContext());
        framelayout.addView(view);

        //Category List
        asdf[0] = "학업";
        asdf[1] = "여행";
        asdf[2] = "만남";
        asdf[3] = "기타";
        asdf[4] = "기타1";
    }

    @Override
    public void onBackPressed() {
        Paint paint = new Paint();

        //background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FFFFFF"));

        tView = new axis(getApplicationContext());
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0,0,width,height,paint);
        tView.draw(canvas);

        //Save Image
        try {
            FileOutputStream fos = new FileOutputStream(new File("/mnt/sdcard/DCIM/Camera/", "test.png"));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) { e.printStackTrace(); }


        finish();
    }

    /*public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch(action) {
            case MotionEvent.ACTION_DOWN :
                isCheck = !isCheck;

                if(isCheck == false) {
                    animFadeIn = AnimationUtils.loadAnimation(GraphActivity.this, android.R.anim.fade_in);
                    graphAddButton.setAnimation(animFadeIn);
                    moveToListButton.setAnimation(animFadeIn);
                    categoryListButton.setAnimation(animFadeIn);
                    graphAddButton.setVisibility(View.VISIBLE);
                    moveToListButton.setVisibility(View.VISIBLE);
                    categoryListButton.setVisibility(View.VISIBLE);
                }
                else {
                    animFadeOut = AnimationUtils.loadAnimation(GraphActivity.this, android.R.anim.fade_out);
                    graphAddButton.setAnimation(animFadeOut);
                    moveToListButton.setAnimation(animFadeOut);
                    categoryListButton.setAnimation(animFadeOut);
                    graphAddButton.setVisibility(View.INVISIBLE);
                    moveToListButton.setVisibility(View.INVISIBLE);
                    categoryListButton.setVisibility(View.INVISIBLE);
                }

                break;
        }

        return super.onTouchEvent(event);
    }*/

    @Override
    public void onClick(View v) {

        Log.w("Click - ", "in");
        if(isCheck == true) {
            graphAddButton.setVisibility(View.VISIBLE);
            moveToListButton.setVisibility(View.VISIBLE);
            categoryListButton.setVisibility(View.VISIBLE);
            isCheck = !isCheck;

            switch (v.getId()) {
                case R.id.graphAddButton:
                    intent = new Intent(this, EventActivity.class);
                    startActivity(intent);

                    break;
                case R.id.moveToListButton:
                    String items[] ={"사건", "종류"};
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(GraphActivity.this);

                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = (View) inflater.inflate(R.layout.graph_editdialog, null);
                    alertDialog.setView(convertView);
                    alertDialog.setTitle("수정");
                    ListView lv = (ListView) convertView.findViewById(R.id.graphListView);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
                    lv.setAdapter(adapter);
                    alertDialog.show();

                    lv.setOnItemClickListener(new ListViewItemClickListener() );

                    break;
                case R.id.categoryListButton:
                    AlertDialog.Builder builder = new AlertDialog.Builder(GraphActivity.this);

                    for (int i = 0; i < itemSize; i++) {
                        if (mSelectedItmes.contains(i)) myCheck[i] = true;
                        else myCheck[i] = false;
                    }
                    mTempSelectedItmes = mSelectedItmes;

                    builder.setTitle(R.string.category)
                            .setMultiChoiceItems(asdf, myCheck,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which,
                                                            boolean isChecked) {
                                            if (isChecked) mTempSelectedItmes.add(which);
                                            else if (mTempSelectedItmes.contains(which))
                                                mTempSelectedItmes.remove(Integer.valueOf(which));
                                        }
                                    })
                            .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    mSelectedItmes = mTempSelectedItmes;
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //
                                }
                            });

                    builder.create().show();
                    break;
            }
        }
        Log.w("Click - ", "out");
    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if(position == 0)
            {
                intent = new Intent(GraphActivity.this, ListActivity.class);
                startActivity(intent);
            }
            else if(position == 1)
            {
                intent = new Intent(GraphActivity.this, CategoryListActivity.class);
                startActivity(intent);
            }
        }
    }

    private class axis extends View {

        private static final int INVALID_POINTER_ID = -1;

        private float mPosX = 0;
        private float mPosY = 0;

        private float mLastTouchX;
        private float mLastTouchY;
        private float mLastGestureX;
        private float mLastGestureY;
        private int mActivePointerId = INVALID_POINTER_ID;

        private ScaleGestureDetector mScaleDetector;
        private float mScaleFactor = 1.f;

        int px, py;
        int circleRadius = 10;
        Paint pLine = new Paint();
        Paint pCircle = new Paint();
        float linePoint[] = new float[4];

        public axis(Context context)
        {
            super(context);

            mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        }

        @Override
        public void onDraw(Canvas canvas)
        {
            canvas.save();

            canvas.translate(mPosX, mPosY);

            if (mScaleDetector.isInProgress()) {
                canvas.scale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
            } else {
                canvas.scale(mScaleFactor, mScaleFactor, mLastGestureX, mLastGestureY);
            }

            width = framelayout.getMeasuredWidth();
            height = framelayout.getMeasuredHeight();

            if(rRight == -1 && rBottom == -1) {
                rRight = width;
                rBottom = height;
            }

            px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,  15, getApplicationContext().getResources().getDisplayMetrics());
            py = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,  15, getApplicationContext().getResources().getDisplayMetrics());

            pLine.setColor(Color.parseColor("#d9d9d9"));
            pLine.setStrokeWidth(3);

            //x-axis, y-axis
            canvas.drawLine(px, py, px, height - py, pLine);
            canvas.drawLine(px, (int)(height / 2), width - px, (int)(height / 2), pLine);

            //point
            int x, y, x2, y2;

            x = 500; y = 200;
            x2 = 350; y2 = 450;
            pCircle.setStyle(Paint.Style.STROKE);
            pCircle.setStrokeWidth(circleRadius / 2);
            pCircle.setColor(Color.parseColor("#FF3399"));
            canvas.drawCircle(x, y, circleRadius, pCircle);
            canvas.drawCircle(x2, y2, circleRadius, pCircle);

            pLine.setColor(Color.parseColor("#FF3399"));
            pLine.setStrokeWidth(circleRadius / 2);

            getPoint(x, y, x2, y2);
            canvas.drawLine(linePoint[0], linePoint[1], linePoint[2], linePoint[3], pLine);


            super.onDraw(canvas);
            canvas.restore();
        }

        void getPoint(int x, int y, int x2, int y2) {
            float dist;

            dist = (x2 - x) * (x2 - x) + (y2 - y) * (y2 - y);
            dist = (float) Math.sqrt(dist);

            linePoint[0] = x + ((x2 - x) * circleRadius / dist);
            linePoint[1] = y + ((y2 - y) * circleRadius / dist);
            linePoint[2] = x2 - ((x2 - x) * circleRadius / dist);
            linePoint[3] = y2 - ((y2 - y) * circleRadius / dist);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            int action = ev.getAction();

            //Log.w("Touch - ", "in");
            mScaleDetector.onTouchEvent(ev);

            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {

                    //Fade in, out
                    isCheck = !isCheck;

                    if(isCheck == false) {
                        animFadeIn = AnimationUtils.loadAnimation(GraphActivity.this, android.R.anim.fade_in);
                        graphAddButton.setAnimation(animFadeIn);
                        moveToListButton.setAnimation(animFadeIn);
                        categoryListButton.setAnimation(animFadeIn);
                        graphAddButton.setVisibility(View.VISIBLE);
                        moveToListButton.setVisibility(View.VISIBLE);
                        categoryListButton.setVisibility(View.VISIBLE);
                    }
                    else {
                        animFadeOut = AnimationUtils.loadAnimation(GraphActivity.this, android.R.anim.fade_out);
                        graphAddButton.setAnimation(animFadeOut);
                        moveToListButton.setAnimation(animFadeOut);
                        categoryListButton.setAnimation(animFadeOut);
                        graphAddButton.setVisibility(View.INVISIBLE);
                        moveToListButton.setVisibility(View.INVISIBLE);
                        categoryListButton.setVisibility(View.INVISIBLE);
                    }

                    //zoom in, out
                    if (!mScaleDetector.isInProgress())
                    {
                        final float x = ev.getX();
                        final float y = ev.getY();

                        mLastTouchX = x;
                        mLastTouchY = y;

                        mActivePointerId = ev.getPointerId(0);
                    }

                    break;
                }
                case MotionEvent.ACTION_POINTER_1_DOWN: {
                    if (mScaleDetector.isInProgress())
                    {
                        final float gx = mScaleDetector.getFocusX();
                        final float gy = mScaleDetector.getFocusY();

                        mLastGestureX = gx;
                        mLastGestureY = gy;
                    }

                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    Log.w("in", " ");

                    // Only move if the ScaleGestureDetector isn't processing a gesture.
                    final boolean isIn = mScaleDetector.isInProgress();

                    if(!isIn)
                    {
                        Log.w("move", " ");
                        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                        final float x = ev.getX(pointerIndex);
                        final float y = ev.getY(pointerIndex);

                        final float dx = x - mLastTouchX;
                        final float dy = y - mLastTouchY;

                        mPosX += dx;
                        mPosY += dy;

                        rLeft += dx;
                        rRight += dx;
                        rTop += dy;
                        rBottom += dy;

                        invalidate();

                        mLastTouchX = x;
                        mLastTouchY = y;
                    }
                    else
                    {
                        Log.w("zoom", " ");
                        final float gx = mScaleDetector.getFocusX();
                        final float gy = mScaleDetector.getFocusY();

                        final float gdx = gx - mLastGestureX;
                        final float gdy = gy - mLastGestureY;

                        mPosX += gdx;
                        mPosY += gdy;

                        rLeft += gdx;
                        rRight += gdx;
                        rTop += gdy;
                        rBottom += gdy;

                        invalidate();

                        mLastGestureX = gx;
                        mLastGestureY = gy;
                    }

                    break;
                }

                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                                                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = ev.getPointerId(pointerIndex);

                    if (pointerId == mActivePointerId)
                    {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = (pointerIndex == 0 ? 1 : 0);

                        mLastTouchX = ev.getX(newPointerIndex);
                        mLastTouchY = ev.getY(newPointerIndex);
                        mActivePointerId = ev.getPointerId(newPointerIndex);
                    }
                    else
                    {
                        final int tempPointerIndex = ev.findPointerIndex(mActivePointerId);

                        mLastTouchX = ev.getX(tempPointerIndex);
                        mLastTouchY = ev.getY(tempPointerIndex);
                    }

                    break;
                }

            }

            //Log.w("Touch - ", "out");
            final float posX = ev.getX();
            final float posY = ev.getY();

            if(posX >= 970 && posX <= width && posY >= 0 && posY <= 90) return false;
            else if(posX >= 1060 && posX <= width && posY >= 550 && posY <= height) return false;
            else return true;
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScaleFactor *= detector.getScaleFactor();

                // Don't let the object get too small or too large.
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

                invalidate();
                return true;
            }
        }
    }
}