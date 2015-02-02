package com.example.chocho.lifegraph;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chocho on 2015-01-25.
 */
public class ListActivity extends Activity implements View.OnClickListener {
    int tPos = -1;

    final Context context = this;
    DatabaseHandler db = new DatabaseHandler(this);
    ArrayList<Event> eventArry;
    EventListAdapter adapter;
    List<Event> events;

    long graphID;
    ImageButton delButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        delButton = (ImageButton) findViewById(R.id.delButton);
        editButton = (ImageButton) findViewById(R.id.editButton);
        delButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        delButton.setBackground(null);
        editButton.setBackground(null);

        graphID = (getIntent().getLongExtra("graphID", 0));
        Log.w("graphId - ", toString().valueOf(graphID));

        initializeList();
    }

    void initializeList()
    {
        eventArry = new ArrayList<Event>();

        events = db.getAllEventsByGraph(db.getGraph((int)graphID).getName());
        for(Event event : events) eventArry.add(event);

        ListView eventList = (ListView) findViewById(R.id.eventListView);
        adapter = new EventListAdapter(this, R.layout.event_list, eventArry, tPos);
        eventList.setAdapter(adapter);
        eventList.setOnItemClickListener(mItemClickListener);
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tPos = position;
            initializeList();

            Log.w("eventID - ", toString().valueOf(events.get(tPos).getID()));;
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        tPos = -1;
        initializeList();
    }

    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.delButton:
                if(tPos != -1)
                {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle("정말 삭제하시겠습니까?");

                    alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            int chk = 0;
                            Event tEvent = null;
                            db.deleteEvent(events.get(tPos).getID());
                            tPos = -1;

                            events = db.getAllEvents();
                            if(events.size()==0){}
                            else if(events.size()==1) db.updateEvent2(events.get(0));
                            else {
                                for (Event event : events) {
                                    if(tEvent != null)
                                    {
                                        if((event.getID() - 1) != tEvent.getID()) chk = 1;
                                        if(chk == 1) db.updateEvent2(event);
                                    }
                                    tEvent = event;
                                }
                            }
                            initializeList();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    alertDialogBuilder.create().show();
                }
                break;
            case R.id.editButton:
                if(tPos != -1) {
                    Intent intent = new Intent(context, EventActivity.class);
                    intent.putExtra("eventID", (long)events.get(tPos).getID());
                    startActivityForResult(intent, 1);
                }
                break;
        }
    }
}