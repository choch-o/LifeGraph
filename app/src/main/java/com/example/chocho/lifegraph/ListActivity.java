package com.example.chocho.lifegraph;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;

/**
 * Created by chocho on 2015-01-25.
 */
public class ListActivity extends Activity {
    ImageButton delButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        delButton = (ImageButton) findViewById(R.id.delButton);
        editButton = (ImageButton) findViewById(R.id.editButton);
        delButton.setBackground(null);
        editButton.setBackground(null);
    }
}


//e6e6e6