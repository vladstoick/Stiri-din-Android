package com.vladstoick.OttoBus;

/**
 * Created by vlad on 7/20/13.
 */
public class DataModifiedEvent {
    public static String TAG_DELETEGROUP = "DELETEDGROUP";
    public static String TAG_GROUPADD = "ADDGROUP";
    public final String dataModifiedType;
    public int id;
    public String title;
    public DataModifiedEvent(String type, int id) {
        this.dataModifiedType = type;
        this.id = id;
    }
    public DataModifiedEvent(String type, String title){
        this.title = title;
        this.dataModifiedType=type;
    }
    @Override
    public String toString() {
        return this.dataModifiedType;
    }

}