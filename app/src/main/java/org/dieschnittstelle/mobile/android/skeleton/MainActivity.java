package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.dieschnittstelle.mobile.android.skeleton.util.Api;
import org.dieschnittstelle.mobile.android.skeleton.util.ApiInterface;
import org.dieschnittstelle.mobile.android.skeleton.util.Connection;
import org.dieschnittstelle.mobile.android.skeleton.util.DatabaseHelper;
import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;
import org.dieschnittstelle.mobile.android.skeleton.classes.TodoListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Todo> todos;
    private TodoListAdapter todoAdapter;
    private ListView listView;
    private FloatingActionButton addTaskBtn;
    private Context context;
    private DatabaseHelper helper = new DatabaseHelper(this);
    private ApiInterface apiInterface;
    private TextView errorMessage;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupGUIElements();
        AsyncTask<String, Boolean, Boolean> isConnected = new Connection().execute();
        try {
            if (!isConnected.get()){
                errorMessage = findViewById(R.id.errorMessage);
                errorMessage.setVisibility(View.VISIBLE);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        todos = new ArrayList<Todo>();
        todoAdapter = new TodoListAdapter(this, todos);
        listView.setAdapter(todoAdapter);

        context = getApplicationContext();
        setUpListViewListener();
        Intent intent = getIntent();
        apiInterface = Api.getClient();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        Cursor cursor = helper.getAllTodos();
        if (cursor.moveToFirst()) {
            do {
                Todo databaseTodo = new Todo();
                databaseTodo.setId(cursor.getInt(0));
                databaseTodo.setName(cursor.getString(1));
                databaseTodo.setDescription((cursor.getString(2)));
                databaseTodo.setDone (cursor.getInt(3) == 1);
                databaseTodo.setExpiry(Long.parseLong(cursor.getString(4)));
                databaseTodo.setFavourite(cursor.getInt(5) == 1);
                databaseTodo.setContacts(new Gson().fromJson(cursor.getString(6), ArrayList.class));
                todos.add(databaseTodo);

            } while (cursor.moveToNext());
        }

        System.out.println(todos.isEmpty());
        if (!todos.isEmpty()) {
            deleteAllTodosRemote();
        } else {
            getTodosRemote();
        }



        if (isIntentSet(intent)) {
            if ("update".equals(intent.getStringExtra("action"))) {
                Log.e("TAG", "onCreate: ", new Exception());
                updateTodo(intent);
                todoAdapter.notifyDataSetChanged();

            } else {
                addTodo(intent);
                todoAdapter.notifyDataSetChanged();
                todoAdapter.modifyData(todos);
            }
        }

        defaultSort();

    }

    private void getTodosRemote() {
        Call<Todo[]> call = apiInterface.getTodos();
        call.enqueue(new Callback<Todo[]>() {
            @Override
            public void onResponse(Call<Todo[]> call, Response<Todo[]> response) {
                Log.d("TAG", response.code() + "");
                System.out.println(response.body());
                for (Todo t : response.body()) {
                    helper.insert(t);
                    todos.add(t);
                    todoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Todo[]> call, Throwable t) {
                Log.d("TAG",   t.getMessage() + "##############");
                call.cancel();
            }


        });
    }

    private void deleteAllTodosRemote() {
        Call<Todo> delteAll = apiInterface.delteAll();
        delteAll.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {


            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                System.out.println(t);
            }
        });
        for (Todo t : todos) {
            createTodosRemote(t);
        }
    }

    private void createTodosRemote(Todo todo) {
        Call<Todo> create = apiInterface.createTodos(todo);
        create.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                System.out.println("RESPONSE" + response.body());
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                System.out.println("Fail " + t);
                call.cancel();
            }
        });
    }

    private void updateTodo(Intent intent) {
        String intentTodo = intent.getSerializableExtra("todo").toString();
        Todo newTodo = (new Gson()).fromJson(String.valueOf(intentTodo), Todo.class);
        boolean deleted = helper.deleteTodo(newTodo.getId()+ "");
        if (deleted) {
            boolean insertSuccess = helper.insert(newTodo);
            if (insertSuccess) {
                updateTodoRemote(newTodo.getId() + "", newTodo);
                for (Todo todo : todos) {
                    if (todo.getId() == newTodo.getId()) {
                        todos.set(todos.indexOf(todo), newTodo);
                    }
                }
                todoAdapter.modifyData(todos);
            }
        }

    }

    private void updateTodoRemote(String id, Todo todo) {
        Call<Todo> update = apiInterface.updateTodo(id, todo);
        update.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                System.out.println("Fail " + t);
                call.cancel();
            }
        });
    }

    private void defaultSort() {
        sortFavourite();
        sortDate();
        sortDone();
        todoAdapter.modifyData(todos);
        todoAdapter.notifyDataSetChanged();
    }

    private void reverseSort() {
        sortDate();
        sortFavourite();
        sortDone();
        todoAdapter.notifyDataSetChanged();

    }



    private void sortDate() {
        Collections.sort(todos, new Comparator<Todo>() {
            @Override
            public int compare(Todo o1, Todo o2) {
                int x =  (int) o1.getExpiry();
                int y =  (int) o2.getExpiry();
                System.out.println("compare: " + (x-y));
                return x - y;
            }
        });
        todoAdapter.modifyData(todos);
        todoAdapter.notifyDataSetChanged();

    }

    private void sortFavourite() {
        Collections.sort(todos, new Comparator<Todo>(){
            @Override
            public int compare(Todo o1, Todo o2) {
                boolean b1 = o1.isFavourite();
                boolean b2 = o2.isFavourite();

                return (b1 == b2) ? 0 : b1 ? -1 : 1;
            }
        });
        todoAdapter.modifyData(todos);
        todoAdapter.notifyDataSetChanged();

    }

    private void sortDone() {
        Collections.sort(todos, new Comparator<Todo>(){
            @Override
            public int compare(Todo o1, Todo o2) {
                boolean b2 = o1.isDone();
                boolean b1 = o2.isDone();

                return (b1 == b2) ? 0 : b1 ? -1 : 1;
            }
        });
        todoAdapter.modifyData(todos);
        todoAdapter.notifyDataSetChanged();

    }




    private void addTodo(Intent intent) {
        String intentTodo = intent.getSerializableExtra("todo").toString();
        Todo newTodo = (new Gson()).fromJson(String.valueOf(intentTodo), Todo.class);
        if (!newTodo.getName().equals("")) {
            boolean insertSuccess = helper.insert(newTodo);
            if (insertSuccess) {
                createTodosRemote(newTodo);
                todos.add(newTodo);
                todoAdapter.modifyData(todos);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
        MenuInflater inflater = new MenuInflater(context);
        inflater.inflate(R.menu.menu_context, menu);
        menu.add("Sort by Date and Importance").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                defaultSort();
                return false;
            }
        });
        menu.add("Sort by Importance and Date").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                reverseSort();
                return false;
            }
        });

        menu.add("Delete local todos").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                for (Todo todo : todos) {
                    helper.deleteTodo(todo.getId()+ "");
                }
                todos = new ArrayList<Todo>();
                todoAdapter.modifyData(todos);
                todoAdapter.notifyDataSetChanged();
                return false;
            }
        });

        menu.add("Delete remote todos").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Call<Todo> delteAll = apiInterface.delteAll();
                delteAll.enqueue(new Callback<Todo>() {
                    @Override
                    public void onResponse(Call<Todo> call, Response<Todo> response) {

                    }

                    @Override
                    public void onFailure(Call<Todo> call, Throwable t) {
                        System.out.println(t);
                    }
                });
                return false;
            }
        });

        menu.add("Sync").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Cursor cursor = helper.getAllTodos();
                if (cursor.moveToFirst()) {
                    do {
                        Todo databaseTodo = new Todo();
                        databaseTodo.setId(cursor.getInt(0));
                        databaseTodo.setName(cursor.getString(1));
                        databaseTodo.setDescription((cursor.getString(2)));
                        databaseTodo.setDone (cursor.getInt(3) == 1);
                        databaseTodo.setExpiry(Long.parseLong(cursor.getString(4)));
                        databaseTodo.setFavourite(cursor.getInt(5) == 1);
                        databaseTodo.setContacts(new Gson().fromJson(cursor.getString(6), ArrayList.class));
                        todos.add(databaseTodo);

                    } while (cursor.moveToNext());
                }

                System.out.println(todos.isEmpty());
                if (!todos.isEmpty()) {
                    deleteAllTodosRemote();
                } else {
                    getTodosRemote();
                }
                return false;
            }
        });
        return true;
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

    public void onDoneButtonClick(View view) {
        View v = (View) view.getParent();
        CheckBox status = (CheckBox) v.findViewById(R.id.status);
        //cBox.setTag(Integer.valueOf(position)); // set the tag so we can identify the correct row in the listener
        //https://stackoverflow.com/questions/12647001/listview-with-custom-adapter-containing-checkboxes
        for (Todo t : todos) {
            System.out.println("Long" + status.getTag() + t.getId());
            if (t.getId() == (Long) status.getTag()) {

                t.setDone(!t.isDone());
                helper.updateStatus(t.isDone(), t.getId());

            }
        }
        System.out.println("Long" );
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
