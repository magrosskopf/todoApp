package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;

public class CreateTodo extends AppCompatActivity {

    private Button backBtn;
    private Context context;
    private EditText taskname;
    private EditText description;
    private CheckBox status;
    private Button createTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);
        context = getApplicationContext();
        backBtn = findViewById(R.id.backbtn);
        taskname = findViewById(R.id.nameField);
        description = findViewById(R.id.descriptionField);
        status = findViewById(R.id.status);
        createTodo = findViewById(R.id.createTodo);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        createTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
    }

    private void back() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void addTask() {
        if (!(taskname.getText().equals(""))){
            Todo newTodo = new Todo();
            newTodo.name = taskname.getText().toString();
            newTodo.description = description.getText().toString();
            newTodo.status = status.isChecked();
            taskname.setText("");
            Toast.makeText(context, "Todo " + newTodo.name + " successfully created.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("todo", (new Gson()).toJson(newTodo));
            startActivity(intent);
        } else {
            Toast.makeText(context, "Please enter some text first!", Toast.LENGTH_LONG).show();
        }
    }
}