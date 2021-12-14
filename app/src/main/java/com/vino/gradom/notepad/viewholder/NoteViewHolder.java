package com.vino.gradom.notepad.viewholder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vino.gradom.notepad.EditActivity;
import com.vino.gradom.notepad.R;
import com.vino.gradom.notepad.db.MyConstants;
import com.vino.gradom.notepad.model.Note;

import java.util.ArrayList;

public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvDescription;
    private ImageView ivImage;

    private final Context context;
    private final ArrayList<Note> noteList;

    public NoteViewHolder(@NonNull View itemView, Context context, ArrayList<Note> noteList) {
        super(itemView);
        this.context = context;
        this.noteList = noteList;
        init();
    }

    private void init(){
        tvTitle = itemView.findViewById(R.id.tvTitle_main);
        tvDescription = itemView.findViewById(R.id.tvDescription_main);
        ivImage = itemView.findViewById(R.id.articlePreviewImage);
        itemView.setOnClickListener(this);
    }

    public void setData(Note note){
        tvTitle.setText(note.getTitle());
        tvDescription.setText(note.getDescription());
        String imageUri = note.getImageURI();
        if(!imageUri.equals("empty")) {
            ivImage.setImageURI(Uri.parse(imageUri));
            return;
        }
        ivImage.setImageResource(R.drawable.ic_article);
    }

    @Override
    public void onClick(View v) {
        int id = getAdapterPosition();
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(MyConstants.NOTE_INTENT, noteList.get(id));
        intent.putExtra(MyConstants.IS_NEW_NOTE_INTENT, false);
        context.startActivity(intent);
    }
}