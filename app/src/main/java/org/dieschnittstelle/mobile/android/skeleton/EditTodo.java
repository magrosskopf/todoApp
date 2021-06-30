package org.dieschnittstelle.mobile.android.skeleton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;
import org.dieschnittstelle.mobile.android.skeleton.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditTodo extends AppCompatActivity {
    Todo todo;
    Intent intent;
    EditText name;
    EditText description;
    CheckBox favourite;
    CheckBox status;
    Button backButton;
    Button updateButton;
    Button deleteBtn;
    Button addContact;
    Context context;
    TextView dateView;
    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> names;
    private Button datepickBtn;
    private long timeInMili;
    private int day;
    private int month;
    private int year;
    private int min;
    private int hour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);
        intent = getIntent();
        String intentTodo = intent.getSerializableExtra("todo").toString();
        todo = (new Gson()).fromJson(String.valueOf(intentTodo), Todo.class);
        names = new ArrayList<>();
        adapter = new ArrayAdapter(this, R.layout.simple_list_item, names);
        System.out.println("#####"+todo);
        name = findViewById(R.id.nameField);
        description = findViewById(R.id.descriptionField);
        status = findViewById(R.id.status);
        backButton = findViewById(R.id.backbtn);
        updateButton = findViewById(R.id.updateTodo);
        context = getApplicationContext();
        deleteBtn = findViewById(R.id.deleteBtn);
        datepickBtn = findViewById(R.id.selectDate);
        favourite = findViewById(R.id.favourite);
        dateView = findViewById(R.id.dateView);
        addContact = findViewById(R.id.addContact);
        listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);
        Calendar timedate = Calendar.getInstance();

        timedate.setTimeInMillis(todo.getExpiry());
        String d = DateFormat.format("dd-MM-yyyy hh:mm", timedate).toString();
        dateView.setText(d);
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker().setSelection(todo.getExpiry());
        materialDateBuilder.setTitleText("SELECT A DATE");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(todo.getExpiry());

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();

        materialTimeBuilder.setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE));
        final MaterialTimePicker materialTimePicker = materialTimeBuilder.build();


        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = materialTimePicker.getHour();
                min = materialTimePicker.getMinute();
                Calendar timedate = Calendar.getInstance();
                timedate.setTimeInMillis(timeInMili);
                timedate.set(Calendar.HOUR_OF_DAY, hour);
                timedate.set(Calendar.MINUTE, min);
                long t = (long) timedate.getTimeInMillis();;
                timeInMili =  t;
                String date = DateFormat.format("dd-MM-yyyy hh:mm", timedate).toString();
                dateView.setText(date);
            }
        });


        datepickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener( new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Long selection) {
                timeInMili = selection;
                materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
            }
        });

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
                new MaterialAlertDialogBuilder(EditTodo.this, R.style.AppTheme)
                        .setMessage("Do you really want your Todo to be deleted?")
                        .setNegativeButton("Cancle", (dialog, which) ->
                            dialog.cancel())
                        .setPositiveButton("Delete Forever", (dialog, which) ->
                                    delete())
                        .show();
            }
        });

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });

        setTodoData(todo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            System.out.println(uri);
            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                        ContactsContract.Profile.DISPLAY_NAME,

                            },
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String displayname = c.getString(1);
                        todo.addContact(uri.getLastPathSegment());
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    public void showSelectedNumber(String type, Uri number) {
        Toast.makeText(this, type + ": " + number, Toast.LENGTH_LONG).show();
    }

    private void delete() {
        DatabaseHelper db = new DatabaseHelper(context);
        db.deleteTitle(todo.getId() + "");
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }

    private void updateTodo() {
            if (!(name.getText().equals(""))){
                Todo newTodo = new Todo();
                newTodo.setId(todo.getId());
                newTodo.setName(name.getText().toString());
                newTodo.setDescription(description.getText().toString());
                newTodo.setDone(status.isChecked());
                newTodo.setExpiry(timeInMili );
                newTodo.setFavourite(favourite.isChecked());
                Toast.makeText(context, "Todo " + newTodo.getName() + " successfully updated.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("todo", (new Gson()).toJson(newTodo));
                intent.putExtra("action", "update");
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
        name.setText(todo.getName());
        description.setText(todo.getDescription());
        if (todo.isDone()) {
            status.isChecked();
        }
        if (todo.isFavourite()) {
            favourite.isChecked();
        }

    }
}
