package com.vino.gradom.notepad.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vino.gradom.notepad.R;
import com.vino.gradom.notepad.model.Note;

import java.util.LinkedList;

public class MySqlManager {
    private final MySqlHelper mySqlHelper;
    private SQLiteDatabase db;

    public MySqlManager(Context context) {
        mySqlHelper = new MySqlHelper(context);
    }

    public void openDb(){
        db = mySqlHelper.getReadableDatabase();
    }

    public void insertToDb(String title, String description, String imageURI, OnNoteSaved onNoteSaved){
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.TITLE, title);
        cv.put(MyConstants.DESCRIPTION, description);
        cv.put(MyConstants.IMAGE_URI, imageURI);
        db.insert(MyConstants.TABLE_NAME, null, cv);
        int successfulAddingMsgId = R.string.successful_adding;
        onNoteSaved.onSaved(successfulAddingMsgId);
    }

    public void getNotesFromDbByTextFragment(String searchText, OnDataReceived onDataReceived){
        LinkedList<Note> resultList = new LinkedList<>();
        String selection = MyConstants.TITLE + " LIKE ?";
        Cursor cursor = db.query(
                MyConstants.TABLE_NAME,
                null, selection,
                new String[]{"%"+searchText+"%"}, null,
                null, null);

        while (cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(MyConstants.TITLE));
            String description = cursor.getString(cursor.getColumnIndex(MyConstants.DESCRIPTION));
            String imageURI = cursor.getString(cursor.getColumnIndex(MyConstants.IMAGE_URI));
            int id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));

            Note note = new Note();
            note.setTitle(title);
            note.setDescription(description);
            note.setImageURI(imageURI);
            note.setId(id);

            resultList.add(note);
        }

        cursor.close();
        onDataReceived.onReceived(resultList);
    }

    public void deleteNoteById(int id){
        String selection = MyConstants._ID + "=" + id;
        db.delete(MyConstants.TABLE_NAME, selection, null);
    }

    public void updateNoteById(int id, String title, String description, String imageURI, OnNoteSaved onNoteSaved){
        String selection = MyConstants._ID + "=" + id;
        ContentValues cv = new ContentValues();
        cv.put(MyConstants.TITLE, title);
        cv.put(MyConstants.DESCRIPTION, description);
        cv.put(MyConstants.IMAGE_URI, imageURI);
        db.update(MyConstants.TABLE_NAME,cv, selection, null);
        int successfulUpdatingMsgId = R.string.successful_updating;
        onNoteSaved.onSaved(successfulUpdatingMsgId);
    }

    public void closeDb(){
        mySqlHelper.close();
    }

}
