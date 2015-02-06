package com.twodevs.chocho.lifegraph;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
    int tPos, layoutResourceId;
    ArrayList<Event> data=new ArrayList<Event>();

    public EventListAdapter(Context context, int layoutResourceId, ArrayList<Event> data, int tPos)
    {
        super(context, layoutResourceId, data);

        //Log.w("adapter: ", "reached adapter");
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.tPos = tPos;
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

        if(tPos != -1 && position == tPos)
        {
            holder.txtTitle.setBackgroundColor(Color.parseColor("#777777"));
            holder.txtTitle.setTextColor(Color.WHITE);
        }
        else
        {
            holder.txtTitle.setBackgroundColor(Color.WHITE);
            holder.txtTitle.setTextColor(Color.BLACK);
        }

        Event event = data.get(position);
        holder.txtTitle.setText(event.getEventName());

        return row;
    }

    static class TextHolder
    {
        TextView txtTitle;
    }
}