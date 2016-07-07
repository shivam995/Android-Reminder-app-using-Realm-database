package com.shivam.reminderapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .name("default0")
                .schemaVersion(1)
                .migration(new RealmMigration() {
                    @Override
                    public long execute(Realm realm, long version) {
                        return 0;
                    }
                })
                .setModules(new SimpleRealmModule()).build();

        Realm.setDefaultConfiguration(config);

    }

    public static MyApplication getInstance() {
        return instance;
    }
}
