package com.twodevs.chocho.lifegraph;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chocho on 2015-01-25.
 */
public class EventActivity extends FragmentActivity{
    Context context = this;
    DatabaseHandler db = new DatabaseHandler(this);

    EditText event_edit;
    ImageButton event_add_cate;
    Button event_done;
    Button event_cancel;

    long graphID;
    long eventID;
    Event event;
    String event_name;
    int age;
    int score;
    String category;
    Spinner age_spinner;
    Spinner score_spinner;
    Spinner category_spinner;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        event_edit = (EditText) findViewById(R.id.event_edittext);
        graphID = getIntent().getLongExtra("graphID", -1);
        eventID = getIntent().getLongExtra("eventID", -1);
        Log.w("Event graph ID", String.valueOf(graphID));

        age_spinner = (Spinner) findViewById(R.id.age_spinner);
        score_spinner = (Spinner) findViewById(R.id.score_spinner);
        category_spinner = (Spinner) findViewById(R.id.category_spinner);
        fillSpinners();

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
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.dialog_title, new int[]{Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW}, Color.YELLOW, 3, 2, 1, -1);
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                //Toast.makeText(EventActivity.this, "selectedColor : " + color, Toast.LENGTH_SHORT).show();
            }
        });
        event_add_cate = (ImageButton) findViewById(R.id.event_add_cate);
        event_add_cate.setBackground(null);
        event_add_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface $dialog) {
                        Log.w("DISMISS LISTENER", "dismissed");
                        ColorPickerDialog dialog = (ColorPickerDialog) $dialog;
                    }
                });
                colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
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

                if (graphID != -1) {
                    event = new Event(event_name, age, score, category);
                    Log.w("NEW EVENT", event.getEventName());
                    db.createEvent(event, graphID);
                }
                else {
                    Log.w("UPDATE", "update");
                    event = new Event((int) eventID, event_name, age, score, category);
                    //event = db.getEvent(eventID);
                    db.updateEvent(event);
                }
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

    public void fillSpinners() {
        ArrayList<String> ageList = new ArrayList<String>();
        ArrayList<String> scoreList = new ArrayList<String>();
        ArrayList<String> categoryList = new ArrayList<String>();
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
            if (!cate.getName().equals("기타"))
                categoryList.add(cate.getName());

        }
        categoryList.add("기타");

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

        if (eventID != -1) {
            Event prev_event = db.getEvent(eventID);
            String prev_event_name = prev_event.getEventName();
            int prev_age = prev_event.getAge();
            int prev_score = prev_event.getScore();
            String prev_cate = prev_event.getCategory();

            event_edit.setText(prev_event_name);
            int age_position = ageAdapter.getPosition(String.valueOf(prev_age));
            int score_position = scoreAdapter.getPosition(String.valueOf(prev_score));
            int cate_position = categoryAdapter.getPosition(prev_cate);

            age_spinner.setSelection(age_position);
            score_spinner.setSelection(score_position);
            category_spinner.setSelection(cate_position);
        }
    }

    public void updateCategorySpinner() {
        ArrayList<String> categoryList = new ArrayList<String>();
        List<Category> cates = db.getAllCategory();

        Log.w("Read: ", "Read all categories");
        for (Category cate : cates) {

            if (!cate.getName().equals("기타"))
                categoryList.add(cate.getName());

        }
        categoryList.add("기타");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(EventActivity.this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(categoryAdapter);
    }
}