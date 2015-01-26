package com.example.chocho.lifegraph;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by chocho on 2015-01-25.
 */
public class GraphActivity extends Activity {
    Spinner graphSpinner;
    FrameLayout framelayout;
    ImageButton graphAddButton, moveToListButton;
    ArrayList<String> graphCategoriesList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_graph);

        graphAddButton = (ImageButton)findViewById(R.id.graphAddButton);
        moveToListButton = (ImageButton)findViewById(R.id.moveToListButton);
        graphAddButton.setBackground(null);
        moveToListButton.setBackground(null);

        framelayout = (FrameLayout) findViewById(R.id.graphContainer);
        framelayout.addView(new axis(getApplicationContext()));

        graphSpinner = (Spinner)findViewById(R.id.graphList);
        graphCategoriesList.add("학업");
        graphCategoriesList.add("여행");
        graphCategoriesList.add("만남");

        ArrayAdapter<String> graphAdapter = new ArrayAdapter<String>(GraphActivity.this, android.R.layout.simple_spinner_item, graphCategoriesList);
        graphSpinner.setAdapter(graphAdapter);

        graphSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ///////////////////////
            }
        });
    }

    private class axis extends View {
        int px, py;
        int width, height;
        Paint paint = new Paint();

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
                    TypedValue.COMPLEX_UNIT_DIP,  R.dimen.activity_horizontal_margin, getApplicationContext().getResources().getDisplayMetrics());
            py = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,  R.dimen.activity_graph_vertical_margin, getApplicationContext().getResources().getDisplayMetrics());

            paint.setColor(R.color.background);
            paint.setStrokeWidth(3);

            //x-axis, y-axis
            canvas.drawLine(px, py, px, height - py, paint);
            canvas.drawLine(px, (int)(height / 2), width - px, (int)(height / 2), paint);

            //point
            paint.setColor(android.R.color.holo_purple);
            canvas.drawCircle(500, 300, 5, paint);
        }
    }
}
