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
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
    axis view = null;
    Intent intent;
    FrameLayout framelayout;
    ImageButton graphAddButton, moveToListButton, categoryListButton;

    int itemSize = 5;
    int width, height;
    boolean isCheck = false;
    boolean[] myCheck = new boolean[itemSize];
    CharSequence[] asdf = new CharSequence[itemSize];
    List<Integer> mSelectedItmes = new ArrayList<Integer>();
    List<Integer> mTempSelectedItmes = new ArrayList<Integer>();

    Animation animFadeIn;
    Animation animFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_graph);

        graphAddButton = (ImageButton) findViewById(R.id.graphAddButton);
        moveToListButton = (ImageButton) findViewById(R.id.moveToListButton);
        categoryListButton = (ImageButton) findViewById(R.id.categoryListButton);
        graphAddButton.setBackground(null);
        moveToListButton.setBackground(null);
        categoryListButton.setBackground(null);

        framelayout = (FrameLayout) findViewById(R.id.graphContainer);
        view = new axis(getApplicationContext());
        framelayout.addView(view);

        graphAddButton.setOnClickListener(this);
        moveToListButton.setOnClickListener(this);
        categoryListButton.setOnClickListener(this);

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

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0,0,width,height,paint);
        view.draw(canvas);

        try {
            FileOutputStream fos = new FileOutputStream(new File("/mnt/sdcard/DCIM/Camera/", "test.png"));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.w("OK", "!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w("error", "!");
        }
        finish();
    }

    public boolean onTouchEvent(MotionEvent event) {
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
    }

    @Override
    public void onClick(View v) {
        if(isCheck == false) {
            switch (v.getId()) {
                case R.id.graphAddButton:
                    intent = new Intent(this, EventActivity.class);
                    startActivity(intent);

                    break;
                case R.id.moveToListButton:
                    intent = new Intent(this, ListActivity.class);
                    startActivity(intent);
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
    }

    private class axis extends View {
        int px, py;
        int circleRadius = 10;
        Paint pLine = new Paint();
        Paint pCircle = new Paint();
        float linePoint[] = new float[4];

        public axis(Context context)
        {
            super(context);
        }

        @Override
        public void onDraw(Canvas canvas)
        {
            width = framelayout.getMeasuredWidth();
            height = framelayout.getMeasuredHeight();

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
    }
}
