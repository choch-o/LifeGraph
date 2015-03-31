package com.twodevs.chocho.lifegraphKEDI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    Dialog dialogList;
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
                            db.deleteEvent(events.get(tPos).getID());
                            tPos = -1;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("도움말")
                    .setMessage(R.string.helpMessage)
                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            dialogList = builder.create();
            dialogList.setCanceledOnTouchOutside(true);
            dialogList.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}