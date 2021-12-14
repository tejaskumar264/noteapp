package com.vino.gradom.notepad.db;

import com.vino.gradom.notepad.model.Note;

import java.util.LinkedList;

public interface OnDataReceived {
    void onReceived(LinkedList<Note> noteList);
}
