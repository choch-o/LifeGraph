package com.example.chocho.lifegraph;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hwang on 2015-01-31.
 */
public class CategoryListAdapter extends ArrayAdapter<Category> {
    Context context;
    int layoutResourceId;
    ArrayList<Category> data = new ArrayList<Category>();

    public CategoryListAdapter(Context context, int layoutResourceId, ArrayList<Category> data)
    {
        super(context, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TextHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new TextHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.eventNameText);
            row.setTag(holder);
        }
        else holder = (TextHolder)row.getTag();

        Category category = data.get(position);
        holder.txtTitle.setText(category.getName());

        return row;
    }

    static class TextHolder
    {
        TextView txtTitle;
    }
}