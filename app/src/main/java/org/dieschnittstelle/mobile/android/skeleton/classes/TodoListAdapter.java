package org.dieschnittstelle.mobile.android.skeleton.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.CreateTodo;
import org.dieschnittstelle.mobile.android.skeleton.EditTodo;
import org.dieschnittstelle.mobile.android.skeleton.R;
import org.dieschnittstelle.mobile.android.skeleton.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateFormat;

import static org.dieschnittstelle.mobile.android.skeleton.R.drawable.expired;

public class TodoListAdapter extends BaseAdapter implements View.OnClickListener{
    Activity activity;
    ArrayList todoDataModeList = new ArrayList<Todo>();
    LayoutInflater layoutInflater = null;
    Button optionButton;
    View vi;
    CheckBox status;
    Calendar calendar = Calendar.getInstance();


    public TodoListAdapter(Activity activity, ArrayList<Todo> customListDataModelArray){
        this.activity=activity;
        this.todoDataModeList = customListDataModelArray;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return todoDataModeList.size();
    }

    @Override
    public Object getItem(int i) {
        return todoDataModeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void onClick(View v) {
        viewHolder = new ViewHolder();
        // inflate list_rowcell for each row
        vi = layoutInflater.inflate(R.layout.list_item,null);
        viewHolder.optionButton = (Button) vi.findViewById(R.id.optionbutton);
        viewHolder.optionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(activity, "Clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(activity, EditTodo.class);
                activity.startActivity(intent);

            }
        });
    }

    public void modifyData(ArrayList<Todo> listData) {
        todoDataModeList = listData;
    }

    private static class ViewHolder{
        CheckBox checkBox;
        TextView todoName, todoDescription, expiry;
        Button optionButton;
        ImageView favIcon;

    }
    ViewHolder viewHolder = null;

    // this method  is called each time for arraylist data size.
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        vi=view;
        final int pos = position;

        if(vi == null){

            // create  viewholder object for list_rowcell View.
            viewHolder = new ViewHolder();
            // inflate list_rowcell for each row
            vi = layoutInflater.inflate(R.layout.list_item,null);
            viewHolder.checkBox = (CheckBox) vi.findViewById(R.id.status);
            viewHolder.todoName = (TextView) vi.findViewById(R.id.todoName);
            viewHolder.todoDescription = (TextView) vi.findViewById(R.id.todoDescription);
            viewHolder.optionButton = (Button) vi.findViewById(R.id.optionbutton);
            viewHolder.expiry = (TextView) vi.findViewById(R.id.expiry);
            viewHolder.favIcon = (ImageView) vi.findViewById(R.id.favIcon);
            Todo todo = (Todo) getItem(pos);
            todoIsExpired(todo, vi);
            viewHolder.checkBox.setTag(Long.valueOf(todo.getId()));

            /*We can use setTag() and getTag() to set and get custom objects as per our requirement.
            The setTag() method takes an argument of type Object, and getTag() returns an Object.*/
            vi.setTag(viewHolder);
        }else {

            /* We recycle a View that already exists */
            viewHolder= (ViewHolder) vi.getTag();
        }
        Todo tempTodo = (Todo) todoDataModeList.get(pos);
        viewHolder.checkBox.setChecked(tempTodo.isDone());
        viewHolder.todoName.setText(tempTodo.getName());
        viewHolder.todoDescription.setText(tempTodo.getDescription());
        if (tempTodo.isFavourite()) {
            viewHolder.favIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.favIcon.setVisibility(View.INVISIBLE);
        }
        calendar.setTimeInMillis((long) (tempTodo.getExpiry()));
        String date = DateFormat.format("dd-MM-yyyy hh:mm", calendar).toString();

        viewHolder.expiry.setText("" + date);
        viewHolder.optionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(activity, "Clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(activity, EditTodo.class);
                intent.putExtra("todo", (new Gson()).toJson(tempTodo));
                activity.startActivity(intent);

            }
        });
        todoIsExpired(tempTodo, vi);
        return vi;
    }

    public boolean todoIsExpired(Todo todo, View vi) {
        Calendar calendar = Calendar.getInstance();
        if (todo.getExpiry() < calendar.getTimeInMillis()) {
            Drawable drawable = activity.getResources().getDrawable(expired);
            vi.setBackground(drawable);
        }
        return true;
    }
}


