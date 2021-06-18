package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.util.DatabaseHelper;
import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;
import org.dieschnittstelle.mobile.android.skeleton.classes.TodoListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Todo> todos;
    private TodoListAdapter todoAdapter;
    private ListView listView;
    private FloatingActionButton addTaskBtn;
    private Context context;
    private DatabaseHelper helper = new DatabaseHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupGUIElements();
        todos = new ArrayList<Todo>();
        todoAdapter = new TodoListAdapter(this, todos);
        listView.setAdapter(todoAdapter);
        context = getApplicationContext();
        setUpListViewListener();
        Intent intent = getIntent();
        if (isIntentSet(intent)) {
            addTodo(intent);
        }
        Cursor cursor = helper.getAllTodos();
        if (cursor.moveToFirst()) {
            do {
                Todo databaseTodo = new Todo();
                databaseTodo.id = cursor.getInt(0);
                databaseTodo.name = cursor.getString(1);
                databaseTodo.description = (cursor.getString(2));
                databaseTodo.status = (cursor.getInt(3) == 0);
                todos.add(databaseTodo);
            }while (cursor.moveToNext());
        }

    }

    private void addTodo(Intent intent) {
        String intentTodo = intent.getSerializableExtra("todo").toString();
        Todo newTodo = (new Gson()).fromJson(String.valueOf(intentTodo), Todo.class);
        if (!newTodo.name.equals("")) {
            // todos.add(newTodo);
            helper.insert(newTodo.name, newTodo.description, newTodo.status);
        }
    }

    private void setUpListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return removeItem(position);
            }
        });
    }

    public boolean removeItem(int i) {
        Toast.makeText(context, "Task removed", Toast.LENGTH_LONG).show();
        todos.remove(i);
        todoAdapter.notifyDataSetChanged();
        return true;
    }

    public void setupGUIElements() {
        listView = findViewById(R.id.todolist);
        addTaskBtn = findViewById(R.id.openDialogBtn);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private boolean isIntentSet(Intent intent) {
        return intent.getSerializableExtra("todo") != null;
    }

    private void openDialog() {
        Intent intent = new Intent(this, CreateTodo.class);
        startActivity(intent);
    }




}
