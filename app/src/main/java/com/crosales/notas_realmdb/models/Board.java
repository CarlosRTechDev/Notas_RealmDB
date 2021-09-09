package com.crosales.notas_realmdb.models;

import com.crosales.notas_realmdb.app.MyApplication;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Board extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String title;
    @Required
    private Date createdAt;

    //esto crea relacion con la otra tabla
    private RealmList<Notas> notas;

    public Board(){

    }

    public Board(String title){
        //generador de Id empieza en 1
        this.id = MyApplication.BoardId.incrementAndGet();
        this.title = title;
        this.createdAt = new Date();
        this.notas = new RealmList<Notas>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RealmList<Notas> getNotas() {
        return notas;
    }

}
