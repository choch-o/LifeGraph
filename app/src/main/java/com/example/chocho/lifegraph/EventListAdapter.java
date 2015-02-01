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
public class EventListAdapter extends ArrayAdapter<Event> {
    Context context;
    int layoutResourceId;
    ArrayList<Event> data=new ArrayList<Event>();

    public EventListAdapter(Context context, int layoutResourceId, ArrayList<Event> data)
    {
        super(context, layoutResourceId, data);

        //Log.w("adapter: ", "reached adapter");
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

        Event event = data.get(position);
        holder.txtTitle.setText(event.getEventName());

        return row;
    }

    static class TextHolder
    {
        TextView txtTitle;
    }
}