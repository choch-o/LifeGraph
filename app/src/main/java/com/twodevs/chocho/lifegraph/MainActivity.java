package com.twodevs.chocho.lifegraph;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    Dialog dialogList;

    final Context context = this;
    DatabaseHandler db = new DatabaseHandler(this);

    GraphImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w("enter","entered main");

        ImageButton addButton = (ImageButton)findViewById(R.id.addButton);
        addButton.setBackground(null);
        addButton.setOnClickListener(this);

        showGraphs();
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.w("onClick: ", "ID " + id);
            Intent intent = new Intent(context, GraphActivity.class);
            intent.putExtra("graphID", id+1);
            startActivityForResult(intent, 1);
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                LayoutInflater li = MainActivity.this.getLayoutInflater();
                View dialogView = li.inflate(R.layout.main_dialog, null);

                final EditText dialogEdit = (EditText) dialogView.findViewById(R.id.main_dialog_edit);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setView(dialogView);

                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String newTitle = dialogEdit.getText().toString();
                        Intent addIntent = new Intent(context, GraphActivity.class);
                        long newGraphID = db.createGraph(new Graph(newTitle, null, 0, null));
                        Log.w("newGraphID", String.valueOf(newGraphID));
                        addIntent.putExtra("graphID", newGraphID);
                        startActivityForResult(addIntent, 1);
                        showGraphs();
                    }
                });

                alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
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

    public void showGraphs() {
        ArrayList<Graph> imageArry = new ArrayList<Graph>();
        Log.w("Reading: ", "Reading all graphs..");
        List<Graph> graphs = db.getAllGraphs();

        Log.w("Read: ", "Read all graphs");
        if (graphs == null) {
            Log.w("GRAPHS NULL", "graphs is null");
        }
        else {
            for (Graph graph : graphs) {
                String log = "ID: " + graph.getID() + " ,Name: " + graph.getName();
                Log.w("Name: ", log);

                imageArry.add(graph);

            }
            adapter = new GraphImageAdapter(this, R.layout.main_list, imageArry);
            ListView graphList = (ListView) findViewById(R.id.main_listview);
            graphList.setAdapter(adapter);
            graphList.setOnItemClickListener(mItemClickListener);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == 2) {
            Log.w("GRAPH FINISHED", "entered");
            showGraphs();
        }
    }
}
