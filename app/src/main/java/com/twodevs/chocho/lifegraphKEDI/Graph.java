package com.twodevs.chocho.lifegraphKEDI;

/**
 * Created by chocho on 2015-01-27.
 */
public class Graph {

    //private variables
    int _id;
    String _name;
    String _date;
    int _event_num;
    byte[] _image;

    // Empty constructor
    public Graph(){

    }
    // constructor
    public Graph(int id, String name, String date, int event_num, byte[] image){
        this._id = id;
        this._name = name;
        this._date = date;
        this._event_num = event_num;
        this._image = image;
    }

    // constructor
    public Graph(String name, String date, int event_num, byte[] image){
        this._name = name;
        this._date = date;
        this._event_num = event_num;
        this._image = image;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // setting name
    public void setName(String name){
        this._name = name;
    }

    public String getDate(){
        return this._date;
    }

    public void setDate(String date){
        this._date = date;
    }

    public int getEventNum(){
        return this._event_num;
    }

    public void setEventNum(int event_num){
        this._event_num = event_num;
    }

    public byte[] getImage(){
        return this._image;
    }

    public void setImage(byte[] image){
        this._image = image;
    }
}