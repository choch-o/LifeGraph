package com.twodevs.chocho.lifegraph;

/**
 * Created by chocho on 2015-01-31.
 */
public class Category {
    //private variables
    int _id;
    String _cate_name;
    String _color;

    // Empty constructor
    public Category(){

    }
    // constructor
    public Category(int id, String cate_name, String color){
        this._id = id;
        this._cate_name = cate_name;
        this._color = color;
    }

    // constructor
    public Category(String cate_name, String color){
        this._cate_name = cate_name;
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
    public String getName(){
        return this._cate_name;
    }

    // setting name
    public void setName(String cate_name){
        this._cate_name = cate_name;
    }
    public String getColor(){
        return this._color;
    }

    public void setColor(String color){
        this._color = color;
    }
}
