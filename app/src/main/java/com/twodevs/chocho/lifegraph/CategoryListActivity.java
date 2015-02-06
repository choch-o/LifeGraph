package com.twodevs.chocho.lifegraph;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hwang on 2015-01-30.
 */
public class CategoryListActivity extends FragmentActivity implements View.OnClickListener {
    int tPos = -1;
    int realPos = -1;
    int itemSize = 0;

    final Context context = this;
    DatabaseHandler db = new DatabaseHandler(this);
    ArrayList<Category> categoryArry;
    CategoryListAdapter adapter;
    List<Category> categories;

    ImageButton cDelButton, cEditButton, cAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorylist);

        cAddButton = (ImageButton) findViewById(R.id.addBox);
        cDelButton = (ImageButton) findViewById(R.id.cDelButton);
        cEditButton = (ImageButton) findViewById(R.id.cEditButton);
        cAddButton.setOnClickListener(this);
        cDelButton.setOnClickListener(this);
        cEditButton.setOnClickListener(this);
        cAddButton.setBackground(null);
        cDelButton.setBackground(null);
        cEditButton.setBackground(null);

        initializeList();
    }

    void initializeList()
    {
        int chk = 0;
        categoryArry = new ArrayList<Category>();

        categories = db.getAllCategory();
        for(Category category : categories)
        {
            if(chk == 1) categoryArry.add(category);
            chk = 1;
        }
        categoryArry.add(categories.get(0));
        itemSize = categoryArry.size();

        ListView categoryList = (ListView) findViewById(R.id.categoryListView);
        adapter = new CategoryListAdapter(this, R.layout.event_list, categoryArry, realPos);
        categoryList.setAdapter(adapter);
        categoryList.setOnItemClickListener(mItemClickListener);
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tPos = position + 2;
            if(tPos > itemSize) tPos = 1;
            realPos = position;

            initializeList();
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        tPos = -1;
        realPos = -1;
        initializeList();
    }

    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.cDelButton:
                if(tPos > 1)
                {
                    Log.w("category delete - ", toString().valueOf(categories.get(tPos).getID()));
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle("정말 삭제하시겠습니까?");

                    alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.w("tPos - ", toString().valueOf(tPos));
                            String tmp = db.getCategory(tPos).getName();

                            db.deleteCategory(tPos);
                            List<Event> events = db.getAllEvents();
                            for(Event event : events)
                            {
                                if(event.getCategory() == tmp)
                                {
                                    Event tmp2 = new Event(event.getID(), event.getEventName(), event.getAge(), event.getScore(), db.getCategory(1).getName());
                                    db.updateEvent(tmp2);
                                }
                            }

                            int chk = 0;
                            Category tCategory = null;

                            categories = db.getAllCategory();
                            if(categories.size()==0){}
                            else if(categories.size()==1) db.updateCategory2(categories.get(0));
                            else {
                                for (Category category : categories) {
                                    if(tCategory != null)
                                    {
                                        if((category.getID() - 1) != tCategory.getID()) chk = 1;
                                        if(chk == 1) db.updateCategory2(category);
                                    }
                                    tCategory = category;
                                }
                            }
                            categories = db.getAllCategory();
                            for (Category category : categories) Log.w("category - ", toString().valueOf(category.getID()));

                            tPos = -1;
                            realPos = -1;
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
            case R.id.cEditButton:
                if(tPos > 1)
                {
                    Category tmp = db.getCategory(tPos);
                    int color = Integer.parseInt(tmp.getColor().substring(1,7), 16) | 0xff000000;
                    Log.w("Color - Blue ", toString().valueOf(Color.BLUE));
                    Log.w("color - get ", toString().valueOf(color));
                    final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                    colorPickerDialog.initialize(R.string.dialog_title, new int[]{Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW}, color, 3, 2, 2, tPos);
                    colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int color) {
                            //Toast.makeText(EventActivity.this, "selectedColor : " + color, Toast.LENGTH_SHORT).show();
                        }
                    });
                    colorPickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface $dialog) {
                            Log.w("DISMISS LISTENER", "dismissed");
                            ColorPickerDialog dialog = (ColorPickerDialog) $dialog;
                        }
                    });
                    colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");

                    tPos = -1;
                    realPos = -1;
                    initializeList();
                }
                break;
            case R.id.addBox:
                Log.w("List - ", "add click");
                final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.initialize(R.string.dialog_title, new int[]{Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW}, Color.YELLOW, 3, 2, 2, -1);
                colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        //Toast.makeText(EventActivity.this, "selectedColor : " + color, Toast.LENGTH_SHORT).show();
                    }
                });
                colorPickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface $dialog) {
                        Log.w("DISMISS LISTENER", "dismissed");
                        ColorPickerDialog dialog = (ColorPickerDialog) $dialog;
                    }
                });
                colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");

                tPos = -1;
                realPos = -1;

                initializeList();
                break;
        }
    }
}