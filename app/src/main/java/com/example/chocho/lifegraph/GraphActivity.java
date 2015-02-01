package com.example.chocho.lifegraph;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chocho on 2015-01-25.
 */
public class GraphActivity extends Activity implements View.OnClickListener
{
    Dialog dialogList;
    boolean isDraw = false;
    int temp = 0;
    long graphID;
    final Context context = this;
    DatabaseHandler db = new DatabaseHandler(this);
    List<Event> eventArry;
    List<Category> categoryArry;
    Bitmap bitmap;

    Intent intent;
    axis view = null;
    axis tView = null;
    FrameLayout framelayout;
    ImageButton graphAddButton, moveToListButton, categoryListButton;

    int itemSize;
    int eventSize = 0;
    int width, height;
    boolean isCheck = false;
    boolean[] myCheck;
    CharSequence[] categoryList;
    List<Integer> mSelectedItmes;
    List<Integer> mTempSelectedItmes;

    //Fade in, out
    Animation animFadeIn;
    Animation animFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_graph);

        graphID = getIntent().getLongExtra("graphID", 0);

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

        initializeEvent();
        initializeCategory();

        //Draw Point, Line
        framelayout = (FrameLayout) findViewById(R.id.graphContainer);
        view = new axis(getApplicationContext());
        framelayout.addView(view);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        width = view.getWidth();
        height = view.getHeight();
        saveGraph();
    }

    public void initializeEvent() {
        eventArry = db.getAllEventsByGraph(db.getGraph((int)graphID).getName());
        eventSize = eventArry.size();
    }

    public void initializeCategory() {
        int j = 0;
        int chk = 0;
        CharSequence tmp = null;

        itemSize = db.getCategoryCount();
        categoryArry = db.getAllCategory();

        myCheck = new boolean[itemSize];
        categoryList = new CharSequence[itemSize];
        mSelectedItmes = new ArrayList<Integer>();
        mTempSelectedItmes = new ArrayList<Integer>();
        for(int i = 0 ; i < itemSize ; i ++) myCheck[i] = false;
        for (Category cate : categoryArry)
        {
            if(chk == 0)
            {
                chk = 1;
                tmp = cate.getName();
            }
            else categoryList[j ++] = cate.getName();
        }
        categoryList[j ++] = tmp;
    }

    void saveGraph()
    {
        getBitmap();

        Bitmap tBitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        String name, date;
        Graph graph;
        byte[] image;

        tBitmap = bitmap;
        name = db.getGraph((int)graphID).getName();
        date = db.getGraph((int)graphID).getDate();
        tBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        image = stream.toByteArray();
        graph = new Graph((int)graphID, name, date, eventSize, image);
        db.updateGraph(graph);
    }

    void getBitmap() {
        Paint paint = new Paint();

        //background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#FFFFFF"));

        tView = new axis(getApplicationContext());
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0,0,width,height,paint);
        tView.draw(canvas);
    }

    @Override
    public void onBackPressed() {
        Bitmap tBitmap;

        saveGraph();
        tBitmap = bitmap;

        //Save Image
        try {
            FileOutputStream fos = new FileOutputStream(new File("/mnt/sdcard/", db.getGraph((int)graphID).getName() + ".png"));
            tBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        finish();
    }

    @Override
    public void onClick(View v) {

        if(isCheck == true) {
            graphAddButton.setVisibility(View.VISIBLE);
            moveToListButton.setVisibility(View.VISIBLE);
            categoryListButton.setVisibility(View.VISIBLE);
            isCheck = !isCheck;

            switch (v.getId()) {
                case R.id.graphAddButton:
                    intent = new Intent(this, EventActivity.class);
                    intent.putExtra("graphID", graphID);
                    startActivityForResult(intent, 3);

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
                    dialogList=alertDialog.create();
                    dialogList.setCanceledOnTouchOutside(true);
                    dialogList.show();

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
                            .setMultiChoiceItems(categoryList, myCheck,
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

                                    framelayout.removeView(view);
                                    view = new axis(getApplicationContext());
                                    framelayout.addView(view);
                                    saveGraph();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    ///////////////
                                }
                            });

                    dialogList = builder.create();
                    dialogList.setCanceledOnTouchOutside(true);
                    dialogList.show();

                    break;

            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initializeEvent();
        if(requestCode == 2 || ((requestCode == 1 || requestCode == 3) && itemSize != db.getCategoryCount())) initializeCategory();

        framelayout.removeView(view);
        view = new axis(getApplicationContext());
        framelayout.addView(view);
        saveGraph();

        if(requestCode == 1 || requestCode == 2) dialogList.dismiss();
    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if(position == 0)
            {
                intent = new Intent(GraphActivity.this, ListActivity.class);
                intent.putExtra("graphID", graphID);
                startActivityForResult(intent, 1);
            }
            else if(position == 1)
            {
                intent = new Intent(GraphActivity.this, CategoryListActivity.class);
                startActivityForResult(intent, 2);
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

        int cnt = 0;
        float px, py;
        int red = 0, green = 0, blue = 0;
        int circleRadius = 10;
        Paint pText = new Paint();
        Paint pLine = new Paint();
        Paint pCircle = new Paint();
        float[] linePoint = new float[4];
        int[][] eventTable = new int[itemSize][eventSize];
        int[] cntTable = new int[itemSize];
        int[] allEventTable = new int[eventSize];

        public axis(Context context)
        {
            super(context);

            makeTable();
            mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        }

        @Override
        public void onDraw(Canvas canvas)
        {
            TextView textView;

            canvas.save();
            canvas.translate(mPosX, mPosY);

            if (mScaleDetector.isInProgress()) {
                canvas.scale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
            } else {
                canvas.scale(mScaleFactor, mScaleFactor, mLastGestureX, mLastGestureY);
            }

            px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,  20, getApplicationContext().getResources().getDisplayMetrics());
            py = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,  20, getApplicationContext().getResources().getDisplayMetrics());

            pLine.setColor(Color.parseColor("#d9d9d9"));
            pLine.setStrokeWidth(3);

            //x-axis, y-axis
            canvas.drawLine(px, py, px, height - py, pLine);
            canvas.drawLine(px, (int)(height / 2), width - px, (int)(height / 2), pLine);

            pText.setColor(Color.BLACK);
            pText.setTextSize(20);
            pText.setTextAlign(Paint.Align.CENTER);
            for(int i = 10 ; i >= -10 ; i --) canvas.drawText(String.valueOf(i), px / 2, 10 + py + (height - py * 2) / 20 * (10 - i), pText);
            for(int i = 5 ; i <= 100 ; i = i + 5) canvas.drawText(String.valueOf(i), px + (width - px * 2) / 100 * i, (int)(height / 2) + 15, pText);

            //draw Graph
            float x, y, x2 = 0, y2 = 0;
            boolean isC = false;

            pCircle.setStyle(Paint.Style.STROKE);
            pCircle.setStrokeWidth(circleRadius / 2);
            for(int i = 0 ; i < itemSize; i ++ )
            {
                if(myCheck[i])
                {
                    /////////////////////////////////////////////////////////////////////////////////////////
                    //Category Color
                    if(i == (itemSize - 1)) getRGB(db.getCategory(1).getColor());
                    else getRGB(db.getCategory(i + 2).getColor());

                    pLine.setStrokeWidth(circleRadius / 2);
                    pLine.setColor(Color.rgb(red, green, blue));
                    pCircle.setColor(Color.rgb(red, green, blue));

                    for(int j = 0 ; j < cntTable[i] ; j ++)
                    {
                        Event tEv = eventArry.get(eventTable[i][j]);
                        int age = tEv._age;
                        int score = tEv._score;

                        //Draw Circle
                        x = px + (width - px * 2) / 100 * age;
                        y = py + (height - py * 2) / 20 * (10 - score);
                        canvas.drawCircle(x, y, circleRadius, pCircle);

                        //Draw Line
                        if(j > 0)
                        {
                            getPoint(x, y, x2, y2);
                            canvas.drawLine(linePoint[0], linePoint[1], linePoint[2], linePoint[3], pLine);
                        }

                        x2 = x;
                        y2 = y;
                    }
                    isC = true;
                }
            }

            if(isC == false)
            {
                pLine.setStrokeWidth(circleRadius / 2);
                pLine.setColor(Color.rgb(0, 204, 255));
                pCircle.setColor(Color.rgb(0, 204, 255));

                for(int i = 0 ; i < cnt ; i ++)
                {
                    Event tEv = eventArry.get(allEventTable[i]);
                    int age = tEv._age;
                    int score = tEv._score;

                    //Draw Circle
                    x = px + (width - px * 2) / 100 * age;
                    y = py + (height - py * 2) / 20 * (10 - score);
                    canvas.drawCircle(x, y, circleRadius, pCircle);

                    //Draw Line
                    if(i > 0)
                    {
                        getPoint(x, y, x2, y2);
                        canvas.drawLine(linePoint[0], linePoint[1], linePoint[2], linePoint[3], pLine);
                    }

                    x2 = x;
                    y2 = y;
                }
            }

            isDraw = true;

            super.onDraw(canvas);
            canvas.restore();
        }

        void getRGB(String strC)
        {
            red = Integer.valueOf(strC.substring(1,3), 16);
            green = Integer.valueOf(strC.substring(3,5), 16);
            blue = Integer.valueOf(strC.substring(5,7), 16);
        }

        void getPoint(float x, float y, float x2, float y2) {
            float dist;

            dist = (x2 - x) * (x2 - x) + (y2 - y) * (y2 - y);
            dist = (float) Math.sqrt(dist);

            linePoint[0] = x + ((x2 - x) * circleRadius / dist);
            linePoint[1] = y + ((y2 - y) * circleRadius / dist);
            linePoint[2] = x2 - ((x2 - x) * circleRadius / dist);
            linePoint[3] = y2 - ((y2 - y) * circleRadius / dist);
        }

        int getId(String categoryName) {
            for(int i = 0 ; i < itemSize ; i ++)
            {
                if(categoryName.equals(categoryList[i].toString())) return i;
            }
            return -1;
        }

        void makeTable()
        {
            int tmp;
            Event tEv, tEv2;

            for(int i = 0 ; i < eventSize ; i ++) {
                tEv = eventArry.get(i);
                int category = getId(tEv.getCategory());

                if (category != -1)
                {
                    eventTable[category][cntTable[category] ++] = i;
                    allEventTable[cnt ++] = i;
                }
            }

            for(int i = 0 ; i < itemSize ; i ++ ) {
                for(int j = 0 ; j < cntTable[i] ; j ++)
                {
                    for(int k = j + 1 ; k < cntTable[i] ; k ++)
                    {
                        tEv = eventArry.get(eventTable[i][j]);
                        tEv2 = eventArry.get(eventTable[i][k]);

                        if(tEv._age > tEv2._age ||(tEv._age == tEv2._age && tEv._score > tEv2._score))
                        {
                            tmp = eventTable[i][j];
                            eventTable[i][j] = eventTable[i][k];
                            eventTable[i][k] = tmp;
                        }
                    }
                }
            }

            for(int j = 0 ; j < cnt ; j ++)
            {
                for(int k = j + 1 ; k < cnt ; k ++)
                {
                    tEv = eventArry.get(allEventTable[j]);
                    tEv2 = eventArry.get(allEventTable[k]);

                    if(tEv._age > tEv2._age ||(tEv._age == tEv2._age && tEv._score > tEv2._score))
                    {
                        tmp = allEventTable[j];
                        allEventTable[j] = allEventTable[k];
                        allEventTable[k] = tmp;
                    }
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            int action = ev.getAction();

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
                case MotionEvent.ACTION_POINTER_DOWN: {
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

                    // Only move if the ScaleGestureDetector isn't processing a gesture.
                    final boolean isIn = mScaleDetector.isInProgress();
                    final float tWidth = width / mScaleFactor, tHeight = height / mScaleFactor;
                    float leftTopX, leftTopY, rightBottomX, rightBottomY;

                    if(!isIn)
                    {
                        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                        final float x = ev.getX(pointerIndex);
                        final float y = ev.getY(pointerIndex);

                        final float dx = x - mLastTouchX;
                        final float dy = y - mLastTouchY;

                        leftTopX = (0 - mPosX - dx - mLastGestureX) / mScaleFactor + mLastGestureX;
                        leftTopY = (0 - mPosY - dy - mLastGestureY) / mScaleFactor + mLastGestureY;
                        rightBottomX = leftTopX + tWidth;
                        rightBottomY = leftTopY + tHeight;

                        if((leftTopX < 0 && dx < 0) || (rightBottomX > width && dx > 0) || (leftTopX >= 0 && rightBottomX <= width)) mPosX += dx;
                        if((leftTopY < 0 && dy < 0) || (rightBottomY > height && dy > 0) || (leftTopY >= 0 && rightBottomY <= height)) mPosY += dy;

                        invalidate();

                        mLastTouchX = x;
                        mLastTouchY = y;
                    }
                    else
                    {
                        final float gx = mScaleDetector.getFocusX();
                        final float gy = mScaleDetector.getFocusY();

                        final float gdx = gx - mLastGestureX;
                        final float gdy = gy - mLastGestureY;

                        leftTopX = (0 - mPosX - gdx - gx) / mScaleFactor + gx;
                        leftTopY = (0 - mPosY - gdy - gy) / mScaleFactor + gy;
                        rightBottomX = leftTopX + tWidth;
                        rightBottomY = leftTopY + tHeight;

                        if((leftTopX < 0 && gdx < 0) || (rightBottomX > width && gdx > 0) || (leftTopX >= 0 && rightBottomX <= width)) mPosX += gdx;
                        if((leftTopY < 0 && gdy < 0) || (rightBottomY > height && gdy > 0) || (leftTopY >= 0 && rightBottomY <= height)) mPosY += gdy;

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
                mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 5.0f));

                invalidate();
                return true;
            }
        }
    }
}