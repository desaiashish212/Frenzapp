package com.rishi.frendzapp.custome.helper;

import com.quickblox.customobjects.model.QBCustomObject;
import com.rishi.frendzapp.custome.model.Note;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {

    private static DataHolder dataHolder;
    private int signInUserId;
    private List<Note> noteList;

    public static synchronized DataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new DataHolder();
        }
        return dataHolder;
    }

    public int getSignInUserId() {
        return signInUserId;
    }

    public void setSignInUserId(int signInUserId) {
        this.signInUserId = signInUserId;
    }

    public int getNoteListSize() {
        if (noteList == null) {
            noteList = new ArrayList<Note>();
        }
        return noteList.size();
    }

    public String getNoteCity(int position) {
        return noteList.get(position).getCity();
    }

    public String getNoteShopName(int position) {
        return noteList.get(position).getShop_name();
    }


    public String getNoteContactPerson(int position) {
        return noteList.get(position).getContact_person();
    }

    public String getNoteId(int position) {
        return noteList.get(position).getId();
    }

    public void setNoteToNoteList(int position, Note note) {
        noteList.set(position, note);
    }


    public void removeNoteFromList(int position) {
        noteList.remove(position);
    }

    public void clear() {
        noteList.clear();
    }

    public int size() {
        if (noteList != null) {
            return noteList.size();
        }
        return 0;
    }

    public void addNoteToList(QBCustomObject customObject) {
        if (noteList == null) {
            noteList = new ArrayList<Note>();
        }
        noteList.add(new Note(customObject));
    }
}