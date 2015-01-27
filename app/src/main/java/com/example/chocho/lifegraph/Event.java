package com.example.chocho.lifegraph;

/**
 * Created by chocho on 2015-01-27.
 */
public class Event {

    //private variables
    int _id;
    String _event_name;
    int _age;
    int _score;
    String _category;
    String _color;

    // Empty constructor
    public Event(){

    }
    // constructor
    public Event(int id, String event_name, int age, int score, String category, String color){
        this._id = id;
        this._event_name= event_name;
        this._age = age;
        this._score = score;
        this._category = category;
        this._color = color;
    }

    // constructor
    public Event(String event_name, int age, int score, String category, String color){
        this._event_name= event_name;
        this._age = age;
        this._score = score;
        this._category = category;
        this._color = color;
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
    public String getEventName(){
        return this._event_name;
    }

    // setting name
    public void setEventName(String event_name){
        this._event_name = event_name;
    }

    public int getAge(){
        return this._age;
    }

    public void setAge(int age){
        this._age = age;
    }

    public int getScore(){
        return this._score;
    }

    public void setScore(int score){
        this._score = score;
    }

    public String getCategory(){
        return this._category;
    }

    public void setCategory(String category){
        this._category = category;
    }

    public String getColor(){
        return this._color;
    }

    public void setColor(String color){
        this._color = color;
    }
}
