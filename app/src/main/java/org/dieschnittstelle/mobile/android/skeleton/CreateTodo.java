package org.dieschnittstelle.mobile.android.skeleton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CreateTodo extends AppCompatActivity {

    private Button backBtn;
    private Context context;
    private EditText taskname;
    private EditText description;
    private CheckBox status, favourite;
    private Button createTodo;
    private Button datepickBtn;
    TextView dateView;

    private long timeInMili;
    private int day;
    private int month;
    private int year;
    private int min;
    private int hour;
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
        datepickBtn = findViewById(R.id.selectDate);
        dateView = findViewById(R.id.dateView);
        favourite = findViewById(R.id.favourite);
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker().setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        materialDateBuilder.setTitleText("SELECT A DATE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        Date currentTime = Calendar.getInstance().getTime();
        MaterialTimePicker.Builder materialTimeBuilder = new MaterialTimePicker.Builder();

        materialTimeBuilder.setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(currentTime.getHours())
                .setMinute(currentTime.getMinutes());
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
                timeInMili = t;

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

                // if the user clicks on the positive
                // button that is ok button update the
                // selected date
               timeInMili = selection;

                materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
                // in the above statement, getHeaderText
                // will return selected date preview from the
                // dialog
            }
        });

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
            newTodo.setName(taskname.getText().toString());
            newTodo.setDescription(description.getText().toString());
            newTodo.setDone(status.isChecked());
            newTodo.setExpiry(timeInMili);
            newTodo.setFavourite(favourite.isChecked());
            newTodo.setContacts(new ArrayList<>());
            taskname.setText("");
            Toast.makeText(context, "Todo " + newTodo.getName() + " successfully created.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("todo", (new Gson()).toJson(newTodo));
            startActivity(intent);
        } else {
            Toast.makeText(context, "Please enter some text first!", Toast.LENGTH_LONG).show();
        }
    }
}