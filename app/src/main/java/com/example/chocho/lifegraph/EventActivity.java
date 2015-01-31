package com.example.chocho.lifegraph;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chocho on 2015-01-25.
 */
public class EventActivity extends Activity {
    DatabaseHandler db = new DatabaseHandler(this);

    ArrayList<String> ageList = new ArrayList<String>();
    ArrayList<String> scoreList = new ArrayList<String>();
    ArrayList<String> categoryList = new ArrayList<String>();

    EditText event_edit;
    ImageButton event_add_cate;
    Button event_done;
    Button event_cancel;

    long graphID;
    Event event;
    String event_name;
    int age;
    int score;
    String category;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        graphID = getIntent().getLongExtra("graphID", 0);
        Log.w("Event graph ID", String.valueOf(graphID));
        event_edit = (EditText) findViewById(R.id.event_edittext);

        final Spinner age_spinner = (Spinner) findViewById(R.id.age_spinner);
        final Spinner score_spinner = (Spinner) findViewById(R.id.score_spinner);
        final Spinner category_spinner = (Spinner) findViewById(R.id.category_spinner);

        for (int i = 0; i<=100; i++) {
            ageList.add(Integer.toString(i));
        }

        for (int i = 10; i>=-10; i--) {
            scoreList.add(Integer.toString(i));
        }
        /*
        for (int i = 1; i<=10; i++) {
            scoreList.add("-"+Integer.toString(i));
        }*/

        List<Category> cates = db.getAllCategory();

        Log.w("Read: ", "Read all categories");
        for (Category cate : cates) {
            String log = "ID: "+cate.getID()+" ,Name: " + cate.getName();
            Log.w("Name: ", log);

            categoryList.add(cate.getName());

        }
        //categoryList.add("학업");
        //categoryList.add("여행");
        //categoryList.add("만남");

        //Create the ArrayAdapter
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(EventActivity.this, android.R.layout.simple_spinner_item, ageList);
        ArrayAdapter<String> scoreAdapter = new ArrayAdapter<String>(EventActivity.this, android.R.layout.simple_spinner_item, scoreList);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(EventActivity.this, android.R.layout.simple_spinner_item, categoryList);

        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set the Adapter
        age_spinner.setAdapter(ageAdapter);
        score_spinner.setAdapter(scoreAdapter);
        category_spinner.setAdapter(categoryAdapter);

        //Set the ClickListener for Spinner
        /*
        age_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(EventActivity.this, "You Selected : " + ageList.get(position)+" Level ",Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        score_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(EventActivity.this, "You Selected : " + ageList.get(position)+" Level ",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(EventActivity.this, "You Selected : " + ageList.get(position)+" Level ",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        */
        event_add_cate = (ImageButton) findViewById(R.id.event_add_cate);
        event_add_cate.setBackground(null);
        event_add_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        event_done = (Button) findViewById(R.id.event_done);
        event_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_name = event_edit.getText().toString();
                age = Integer.valueOf(age_spinner.getSelectedItem().toString());
                score = Integer.valueOf(score_spinner.getSelectedItem().toString());
                category = category_spinner.getSelectedItem().toString();

                event = new Event(event_name, age, score, category);
                Log.w("Event name", event.getEventName());
                db.createEvent(event, graphID);
                
                finish();
            }
        });
        event_cancel = (Button) findViewById(R.id.event_cancel);
        event_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
