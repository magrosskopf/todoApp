package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;
import org.dieschnittstelle.mobile.android.skeleton.util.DatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;

public class EditTodo extends AppCompatActivity {
    Todo todo;
    Intent intent;
    EditText name;
    EditText description;
    CheckBox status;
    Button backButton;
    Button updateButton;
    Button deleteBtn;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);
        intent = getIntent();
            String intentTodo = intent.getSerializableExtra("todo").toString();
            todo = (new Gson()).fromJson(String.valueOf(intentTodo), Todo.class);

        System.out.println("#####"+todo);
        name = findViewById(R.id.nameField);
        description = findViewById(R.id.descriptionField);
        status = findViewById(R.id.status);
        backButton = findViewById(R.id.backbtn);
        updateButton = findViewById(R.id.updateTodo);
        context = getApplicationContext();
        deleteBtn = findViewById(R.id.deleteBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTodo();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(context);
                db.deleteTitle(todo.id + "");
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
        setTodoData(todo);
    }

    private void updateTodo() {
            if (!(name.getText().equals(""))){
                Todo newTodo = new Todo();
                newTodo.name = name.getText().toString();
                newTodo.description = description.getText().toString();
                newTodo.status = status.isChecked();
                Toast.makeText(context, "Todo " + newTodo.name + " successfully updated.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("todo", (new Gson()).toJson(newTodo));
                startActivity(intent);
            } else {
                Toast.makeText(context, "Please enter some text first!", Toast.LENGTH_LONG).show();
            }
    }

    private void back() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setTodoData(Todo todo) {
        name.setText(todo.name);
        description.setText(todo.description);
        status.setActivated(todo.status);
    }
}
