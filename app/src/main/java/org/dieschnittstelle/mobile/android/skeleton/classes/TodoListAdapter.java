package org.dieschnittstelle.mobile.android.skeleton.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.CreateTodo;
import org.dieschnittstelle.mobile.android.skeleton.EditTodo;
import org.dieschnittstelle.mobile.android.skeleton.R;

import java.util.ArrayList;

public class TodoListAdapter extends BaseAdapter implements View.OnClickListener{
    Activity activity;
    ArrayList todoDataModeList = new ArrayList<Todo>();
    LayoutInflater layoutInflater = null;
    Button optionButton;
    View vi;

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

    private static class ViewHolder{
        CheckBox checkBox;
        TextView todoName, todoDescription;
        Button optionButton;

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
            /*We can use setTag() and getTag() to set and get custom objects as per our requirement.
            The setTag() method takes an argument of type Object, and getTag() returns an Object.*/
            vi.setTag(viewHolder);
        }else {

            /* We recycle a View that already exists */
            viewHolder= (ViewHolder) vi.getTag();
        }
        Todo tempTodo = (Todo) todoDataModeList.get(pos);
        viewHolder.checkBox.setActivated(tempTodo.status);
        viewHolder.todoName.setText(tempTodo.name);
        viewHolder.todoDescription.setText(tempTodo.description);
        System.out.println((new Gson()).toJson(tempTodo));
        viewHolder.optionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(activity, "Clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(activity, EditTodo.class);
                intent.putExtra("todo", (new Gson()).toJson(tempTodo));
                activity.startActivity(intent);

            }
        });

        return vi;
    }
}
