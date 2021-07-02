package org.dieschnittstelle.mobile.android.skeleton.classes;

import android.location.Location;
import android.text.Editable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Todo implements Serializable {

    //private static final long serialVersionUID = -6410064189686738560L;
    @SerializedName("id")
    private long id;

    // name and decription
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;

    // expirydate as long value
    @SerializedName("expiry")
    private long expiry;

    // whether the todo is done
    @SerializedName("done")
    private boolean done;

    // whether it is a favourite
    @SerializedName("favourite")
    private boolean favourite;

    @SerializedName("contacts")
    private ArrayList<String> contacts;

    public Todo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // a default constructor
    public Todo() {
        this.contacts = new ArrayList<>();
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object other) {
        return this.getId() == ((Todo) other).getId();
    }

    public String toString() {
        return "{Todo " + this.id + " " + this.name + ", " + this.description + this.expiry
                + "}";
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void addContact(String id) {
        contacts.add(id);
    }

    public void setContacts (ArrayList<String> c) {
        contacts = c;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }

    }


