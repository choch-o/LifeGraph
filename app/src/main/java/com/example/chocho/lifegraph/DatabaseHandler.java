package com.example.chocho.lifegraph;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by chocho on 2015-01-27.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = DatabaseHandler.class.getName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LifeManager";

    // Table Names
    private static final String TABLE_GRAPH = "graph";
    private static final String TABLE_EVENT = "event";
    private static final String TABLE_EVENT_GRAPH = "event_graph";

    // Common Columns names
    private static final String KEY_ID = "id";

    // GRAPH Table column names
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_EVENT_NUM = "event_num";
    private static final String KEY_IMAGE = "image";

    // EVENT Table column names
    private static final String KEY_EVENT_NAME = "event_name";
    private static final String KEY_AGE = "age";
    private static final String KEY_SCORE = "score";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_COLOR= "color";

    // GRAPH_EVENT Table column names
    private static final String KEY_GRAPH_ID = "graph_id";
    private static final String KEY_EVENT_ID = "event_id";

    //Table Create Statements
    // Graph table create statement
    private static final String CREATE_TABLE_GRAPH = "CREATE TABLE " + TABLE_GRAPH + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT," + KEY_DATE + " DATETIME," + KEY_EVENT_NUM + " INTEGER," + KEY_IMAGE + " TEXT" + ")";

    // Event table create statement
    private static final String CREATE_TABLE_EVENT = "CREATE TABLE " + TABLE_EVENT + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_EVENT_NAME + " TEXT," + KEY_AGE + " INTEGER," + KEY_SCORE + " INTEGER," + KEY_CATEGORY + " TEXT" + KEY_COLOR + " TEXT" + ")";

    // Graph_Event table create statement
    private static final String CREATE_TABLE_EVENT_GRAPH = "CREATE TABLE "
            + TABLE_EVENT_GRAPH + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_GRAPH_ID + " INTEGER," + KEY_EVENT_ID + " INTEGER," + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GRAPH);
        db.execSQL(CREATE_TABLE_EVENT);
        db.execSQL(CREATE_TABLE_EVENT_GRAPH);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRAPH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_GRAPH);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
// ------------------------ EVENTS table methods ----------------//

    /**
     * Creating a event
     */
    public long createEvent(Event event, long[] graph_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, event.getEventName());
        values.put(KEY_AGE, event.getAge());
        values.put(KEY_SCORE, event.getScore());
        values.put(KEY_CATEGORY, event.getCategory());
        values.put(KEY_COLOR, event.getColor());

        // insert row
        long event_id = db.insert(TABLE_EVENT, null, values);

        // insert tag_ids
        for (long graph_id : graph_ids) {
            createEventGraph(event_id, graph_id);
        }

        return event_id;
    }

    /**
     * get single event
     */
    public Event getEvent(long event_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_EVENT + " WHERE "
                + KEY_ID + " = " + event_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Event event = new Event();
        event.setID(c.getInt(c.getColumnIndex(KEY_ID)));
        event.setEventName((c.getString(c.getColumnIndex(KEY_EVENT_NAME))));
        event.setAge(c.getInt(c.getColumnIndex(KEY_AGE)));
        event.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
        event.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY)));
        event.setColor(c.getString(c.getColumnIndex(KEY_COLOR)));

        return event;
    }

    /**
     * getting all events
     * */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<Event>();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Event event = new Event();
                event.setID(c.getInt(c.getColumnIndex(KEY_ID)));
                event.setEventName(c.getString(c.getColumnIndex(KEY_EVENT_NAME)));
                event.setAge(c.getInt(c.getColumnIndex(KEY_AGE)));
                event.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
                event.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY)));
                event.setColor(c.getString(c.getColumnIndex(KEY_COLOR)));

                // adding to event list
                events.add(event);
            } while (c.moveToNext());
        }

        return events;
    }

    /**
     * getting all events under single graph
     * */
    public List<Event> getAllEventsByGraph(String graph_name) {
        List<Event> events = new ArrayList<Event>();

        String selectQuery = "SELECT  * FROM " + TABLE_EVENT + " td, "
                + TABLE_GRAPH + " tg, " + TABLE_EVENT_GRAPH+ " tt WHERE tg."
                + KEY_NAME + " = '" + graph_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_GRAPH_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_EVENT_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Event event = new Event();
                event.setID(c.getInt((c.getColumnIndex(KEY_ID))));
                event.setEventName((c.getString(c.getColumnIndex(KEY_EVENT_NAME))));
                event.setAge(c.getInt(c.getColumnIndex(KEY_AGE)));
                event.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));
                event.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY)));
                event.setColor(c.getString(c.getColumnIndex(KEY_COLOR)));

                // adding to event
                events.add(event);
            } while (c.moveToNext());
        }

        return events;
    }

    /**
     * getting event count
     */
    public int getEventCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EVENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /**
     * Updating a event
     */
    public int updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_NAME, event.getEventName());
        values.put(KEY_AGE, event.getAge());
        values.put(KEY_SCORE, event.getScore());
        values.put(KEY_CATEGORY, event.getCategory());
        values.put(KEY_COLOR, event.getColor());

        // updating row
        return db.update(TABLE_EVENT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(event.getID()) });
    }

    /**
     * Deleting a event
     */
    public void deleteEvent(long event_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, KEY_ID + " = ?",
                new String[] { String.valueOf(event_id) });
    }
    //-------------------- GRAPH table methods ---------------------//

    public long createGraph(Graph graph) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, graph.getName());
        values.put(KEY_DATE, graph.getDate());
        values.put(KEY_EVENT_NUM, graph.getEventNum());
        values.put(KEY_IMAGE, graph.getImage());

        // Inserting Row
        long graph_id = db.insert(TABLE_GRAPH, null, values);

        return graph_id;
        //db.close(); // Closing database connection
    }

    // Getting All Graphs
    public List<Graph> getAllGraphs() {
        List<Graph> graphs = new ArrayList<Graph>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GRAPH;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Graph graph = new Graph();
                graph.setID(c.getInt((c.getColumnIndex(KEY_ID))));
                graph.setName(c.getString((c.getColumnIndex(KEY_NAME))));
                graph.setDate(c.getString((c.getColumnIndex(KEY_DATE))));
                graph.setEventNum(c.getInt((c.getColumnIndex(KEY_EVENT_NUM))));
                graph.setImage(c.getString((c.getColumnIndex(KEY_IMAGE))));

                // Adding contact to list
                graphs.add(graph);
            } while (c.moveToNext());
        }

        // return contact list
        return graphs;
    }

    // Updating single contact
    public int updateGraph(Graph graph) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, graph.getName());
        values.put(KEY_DATE, graph.getDate());
        values.put(KEY_EVENT_NUM, graph.getEventNum());
        values.put(KEY_IMAGE, graph.getImage());

        // updating row
        return db.update(TABLE_GRAPH, values, KEY_ID + " = ?",
                new String[] { String.valueOf(graph.getID()) });
    }

    // Deleting single contact
    public void deleteGraph(Graph graph, boolean should_delete_all_events) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (should_delete_all_events) {
            List<Event> allGraphEvents = getAllEventsByGraph(graph.getName());

            for (Event event : allGraphEvents) {
                deleteEvent(event.getID());
            }
        }
        db.delete(TABLE_GRAPH, KEY_ID + " = ?",
                new String[]{String.valueOf(graph.getID())});
        //db.close();
    }

// ------------------------ Event_Graph table methods ----------------//

    /**
     * Creating event_graph
     */
    public long createEventGraph(long event_id, long graph_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, event_id);
        values.put(KEY_GRAPH_ID, graph_id);
        //values.put(KEY_CREATED_AT, getDateTime());

        long id = db.insert(TABLE_EVENT_GRAPH, null, values);

        return id;
    }

    /**
     * Updating a event graph
     */
    public int updateNoteGraph(long id, long graph_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GRAPH_ID, graph_id);

        // updating row
        return db.update(TABLE_EVENT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /**
     * Deleting a event graph
     */
    public void deleteEventGraph(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
