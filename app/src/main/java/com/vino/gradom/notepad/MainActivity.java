package com.vino.gradom.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vino.gradom.notepad.adapter.NoteAdapter;
import com.vino.gradom.notepad.db.MyConstants;
import com.vino.gradom.notepad.db.MySqlManager;
import com.vino.gradom.notepad.db.OnDataReceived;
import com.vino.gradom.notepad.model.Note;
import com.vino.gradom.notepad.threading.AppExecutor;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements OnDataReceived {

    private MySqlManager sqlManager;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.id_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                readNotesFromDb(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void init(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sqlManager = new MySqlManager(this);
        RecyclerView noteListView = findViewById(R.id.noteListView);
        noteAdapter = new NoteAdapter(this);
        noteListView.setLayoutManager(new LinearLayoutManager(this));
        getItemTouchHelper().attachToRecyclerView(noteListView);
        noteListView.setAdapter(noteAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sqlManager.openDb();
        readNotesFromDb("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlManager.closeDb();
    }

    public void onClickAdd(View view) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(MyConstants.IS_NEW_NOTE_INTENT, true);
        startActivity(intent);
    }

    private ItemTouchHelper getItemTouchHelper(){
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int notePosition = viewHolder.getAdapterPosition();
                noteAdapter.removeItem(notePosition, sqlManager);
            }
        });
    }

    @Override
    public void onReceived(LinkedList<Note> noteList) {
        AppExecutor.getInstance().getMainThread().execute(() -> noteAdapter.updateAdapter(noteList));
    }

    private void readNotesFromDb(final String newText){
        AppExecutor.getInstance().getSubThread().execute(() ->
                sqlManager.getNotesFromDbByTextFragment(newText, MainActivity.this));
    }
}