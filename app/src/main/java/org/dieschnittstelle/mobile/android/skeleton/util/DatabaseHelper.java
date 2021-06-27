package org.dieschnittstelle.mobile.android.skeleton.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "TodoListDB";
    public static final String CONTACTS_TABLE_NAME = "TodoList";
    public static final String KEY_NAME = "id";

    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table "+ CONTACTS_TABLE_NAME +"(id integer primary key, name text, description text, done integer, expiry text, favourite integer, contacts text)"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
        onCreate(db);
    }
    public boolean insert(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", todo.getName());
        contentValues.put("description", todo.getDescription());
        contentValues.put("done", todo.isDone() ? 1 : 0);
        contentValues.put("expiry", todo.getExpiry() + "" );
        contentValues.put("favourite", todo.isFavourite() ? 1 : 0);
        contentValues.put("contacts", new Gson().toJson(todo.getContacts()));
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getAllTodos() {
        // select all query
        String select_query= "SELECT *FROM " + CONTACTS_TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select_query, null);
        return cursor;
    }

    public boolean deleteTitle(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME, KEY_NAME + "=" + id, null) > 0;
    }

    public boolean deleteTodo(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME, KEY_NAME + "=" + id, null) > 0;
    }

    public boolean updateStatus(boolean s, long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE "+CONTACTS_TABLE_NAME+" SET done = "+"'"+ (s ? 1: 0)+"' "+ "WHERE id = "+"'"+id+"'");
        return true;
    }

}
