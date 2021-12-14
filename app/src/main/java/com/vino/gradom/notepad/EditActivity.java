package com.vino.gradom.notepad;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.vino.gradom.notepad.db.MyConstants;
import com.vino.gradom.notepad.db.MySqlManager;
import com.vino.gradom.notepad.db.OnNoteSaved;
import com.vino.gradom.notepad.model.Note;
import com.vino.gradom.notepad.threading.AppExecutor;

public class EditActivity extends AppCompatActivity implements OnNoteSaved {

    private final int PICK_IMAGE_CODE = 200;

    private String imagePath = "empty";
    private boolean isNewNote;
    private int noteId;

    private ConstraintLayout imageLayout;
    private ImageView articleImage;
    private MySqlManager sqlManager;
    private EditText edTitle, edDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
    }

    private void init(){
        sqlManager = new MySqlManager(this);
        edTitle = findViewById(R.id.etTitle);
        edDescription = findViewById(R.id.etDescription);
        imageLayout = findViewById(R.id.imageLayout);
        articleImage = findViewById(R.id.articleImage);
        getInfoFromIntent();
    }

    private void getInfoFromIntent(){
        Intent intent = getIntent();
        isNewNote = intent.getBooleanExtra(MyConstants.IS_NEW_NOTE_INTENT, true);

        if(!isNewNote){
            Note note = (Note) intent.getSerializableExtra(MyConstants.NOTE_INTENT);

            if(note == null){
                return;
            }

            noteId = note.getId();

            String title = note.getTitle();
            edTitle.setText(title);

            String description = note.getDescription();
            edDescription.setText(description);

            imagePath = note.getImageURI();
            if(!imagePath.equals("empty")){
                articleImage.setImageURI(Uri.parse(imagePath));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sqlManager.openDb();
    }

    public void onSaveButton(View view) {
        final String title = edTitle.getText().toString();
        final String description = edDescription.getText().toString();
        if(title.equals("") || description.equals("")){
            Toast.makeText(this, R.string.incorrect_title_or_description, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if(isNewNote){
            AppExecutor.getInstance().getSubThread().execute(() -> sqlManager.
                    insertToDb(title, description, imagePath, EditActivity.this));
        }else {
            AppExecutor.getInstance().getSubThread().execute(() -> sqlManager
                    .updateNoteById(noteId, title, description, imagePath, EditActivity.this));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlManager.closeDb();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public void onClickChangeImage(View view) {
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.setType("image/*");
        startActivityForResult(chooser, PICK_IMAGE_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE_CODE && data != null){
            imagePath = data.getData().toString();
            articleImage.setImageURI(data.getData());
            getContentResolver().takePersistableUriPermission(data.getData(),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return;
        }

        Toast.makeText(this, "Selected image is invalid",Toast.LENGTH_LONG).show();

    }

    public void onClickCloseImage(View view) {
        handleImageLayout();
    }

    public void onClickDeleteImage(View view) {
        imagePath = "empty";
        articleImage.setImageResource(R.drawable.ic_image);
    }

    public void onClickSetImage(View view) {
        handleImageLayout();
    }

    private void handleImageLayout(){
        if(imageLayout.getVisibility() == View.VISIBLE){
            imageLayout.setVisibility(View.GONE);
        }else{
            imageLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaved(int msgResId) {
        AppExecutor.getInstance().getMainThread().execute(() -> {
            Toast.makeText(EditActivity.this, msgResId, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}