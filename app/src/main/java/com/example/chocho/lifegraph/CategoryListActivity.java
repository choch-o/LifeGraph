package com.example.chocho.lifegraph;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

/**
 * Created by Hwang on 2015-01-30.
 */
public class CategoryListActivity extends Activity
{
    ImageButton delButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorylist);

        delButton = (ImageButton) findViewById(R.id.cDelButton);
        editButton = (ImageButton) findViewById(R.id.cEditButton);
        delButton.setBackground(null);
        editButton.setBackground(null);
    }
}
