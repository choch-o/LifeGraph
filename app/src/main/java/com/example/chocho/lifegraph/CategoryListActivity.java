package com.example.chocho.lifegraph;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
public class CategoryListActivity extends Activity implements View.OnClickListener {
    int tPos = 0;
    int itemSize = 0;

    final Context context = this;
    DatabaseHandler db = new DatabaseHandler(this);
    ArrayList<Category> categoryArry;
    CategoryListAdapter adapter;
    List<Category> categories;

    int isCheck = 0;
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
        adapter = new CategoryListAdapter(this, R.layout.event_list, categoryArry);
        categoryList.setAdapter(adapter);
        categoryList.setOnItemClickListener(mItemClickListener);
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tPos = (position + 1) % itemSize;
            Log.w("eventID - ", toString().valueOf(categories.get(tPos).getID()));

            if(tPos != 0) {
                if (isCheck == 2) {
                    /////////////////////////////////////////////////////// edit
                } else if (isCheck == 1) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setTitle("정말 삭제하시겠습니까?");

                    alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            db.deleteCategory(categories.get(tPos).getID());
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
            }
            else
            {
                /////////////////////////////////////////////기타 수정
            }
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initializeList();
    }

    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.cDelButton:
                isCheck = 1;
                break;
            case R.id.cEditButton:
                isCheck = 2;
                break;
            case R.id.addBox:
                //////////////////////////////////////////////////////// add!!!
                break;
        }
    }
}