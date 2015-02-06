package com.twodevs.chocho.lifegraph;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Created by chocho on 2015-01-30.
 */
public class GraphImageAdapter extends ArrayAdapter<Graph> {
    Context context;
    int layoutResourceId;
    ArrayList<Graph> data=new ArrayList<Graph>();
    public GraphImageAdapter(Context context, int layoutResourceId, ArrayList<Graph> data) {
        super(context, layoutResourceId, data);
        Log.w("adapter: ", "reached adapter");
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ImageHolder holder = null;
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ImageHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.mainGraphImage);
            holder.txtTitle = (TextView)row.findViewById(R.id.mainGraphTitle);
            row.setTag(holder);
        }
        else
        {
            holder = (ImageHolder)row.getTag();
        }
        Graph picture = data.get(position);
        holder.txtTitle.setText(picture._name);
        Log.w("Name: ", "Graph name "+picture.getName());
//convert byte to bitmap take from Graph class
        byte[] outImage=picture._image;
        if (outImage != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            holder.imgIcon.setImageBitmap(theImage);
        }
        else {
            holder.imgIcon.setImageResource(android.R.color.transparent);
        }
        return row;
    }
    static class ImageHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}