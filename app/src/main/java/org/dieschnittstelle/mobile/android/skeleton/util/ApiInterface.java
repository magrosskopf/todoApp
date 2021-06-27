package org.dieschnittstelle.mobile.android.skeleton.util;

import com.google.gson.JsonObject;

import org.dieschnittstelle.mobile.android.skeleton.classes.Todo;
import org.dieschnittstelle.mobile.android.skeleton.classes.User;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("/api/todos")
    Call<Todo[]> getTodos();

    @POST("/api/todos")
    Call<Todo> createTodos(@Body Todo todo);

    @PUT("/api/todos?")
    Call<Todo> updateTodo(@Query("id") String id, @Body Todo todo);

    @PUT("/api/users/auth")
    Call<Boolean> login(@Body User user);

    @GET("/api/todos?")
    Call<Todo> getSingleTodo(@Query("id") String id);

    @DELETE("/api/todos")
    Call<Todo> delteAll();

    @FormUrlEncoded
    @POST("/api/todos?")
    Call<Todo[]> doCreateUserWithField(@Field("name") String name, @Field("job") String job);
}



