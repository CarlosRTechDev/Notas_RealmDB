package com.crosales.notas_realmdb.app;

import android.app.Application;

import com.crosales.notas_realmdb.models.Board;
import com.crosales.notas_realmdb.models.Notas;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApplication extends Application {

    //AtomicInteger tiene un metodo implementado (get), recupera el ultimo id y agrega +1
    public static AtomicInteger BoardId = new AtomicInteger();
    public static AtomicInteger NotasId = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();
        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        BoardId = getIdByTable(realm, Board.class);
        NotasId = getIdByTable(realm, Notas.class);
        realm.close();
    }

    //configuracion Realm
    private void setUpRealmConfig(){
        // initialize Realm
        Realm.init(getApplicationContext());

        // create your Realm configuration
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }
}
