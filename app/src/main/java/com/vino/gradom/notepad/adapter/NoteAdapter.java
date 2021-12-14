package com.vino.gradom.notepad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vino.gradom.notepad.R;
import com.vino.gradom.notepad.db.MySqlManager;
import com.vino.gradom.notepad.model.Note;
import com.vino.gradom.notepad.viewholder.NoteViewHolder;

import java.util.ArrayList;
import java.util.LinkedList;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private final Context context;
    private final ArrayList<Note> noteList;

    public NoteAdapter(Context context) {
        this.context = context;
        noteList = new ArrayList<>();
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_card_view, parent, false);
        return new NoteViewHolder(view, context, noteList);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setData(noteList.get(position));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void updateAdapter(LinkedList<Note> newNoteList){
        noteList.clear();
        noteList.addAll(newNoteList);
        notifyDataSetChanged();
    }

    public void removeItem(int position, MySqlManager sqlManager){
        sqlManager.deleteNoteById(noteList.get(position).getId());
        noteList.remove(position);
        notifyItemRangeChanged(0,noteList.size());
        notifyItemRemoved(position);
    }
}
