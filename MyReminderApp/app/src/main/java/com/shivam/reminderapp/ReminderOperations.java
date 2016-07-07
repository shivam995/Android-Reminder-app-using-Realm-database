package com.shivam.reminderapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ReminderOperations {

    static String TAG = "Log";

    public static void addReminder(String date, String time, long currentTimeStamp, String task) {
        Realm realm = Realm.getInstance(MyApplication.getInstance());
        realm.beginTransaction();
        Reminder u = realm.createObject(Reminder.class);
        u.setId(UUID.randomUUID().toString());
        u.setDate(date);
        u.setTime(time);
        u.setValid(true);
        u.setTask(task);
        u.setCurrentTimeStamp(currentTimeStamp);
        realm.commitTransaction();
    }

    public static void viewReminder() {
        Realm realm = Realm.getInstance(MyApplication.getInstance());
        RealmQuery query = realm.where(Reminder.class);
        RealmResults<Reminder> results = query.findAll();
        if (results != null && !results.isEmpty()) {
            for (int i = 0; i < results.size(); i++) {
                Reminder reminder = results.get(i);
            }
        }
    }

    public static void checkFoReminderList(Context con) {
        Realm realm = Realm.getInstance(MyApplication.getInstance());
        RealmQuery query = realm.where(Reminder.class);
        RealmResults<Reminder> results = query.findAll();
        if (results != null && !results.isEmpty()) {
            for (int i = 0; i < results.size(); i++) {
                Reminder reminder = results.get(i);
                if (reminder.isValid()) {
                    long diff = reminder.getCurrentTimeStamp() - System.currentTimeMillis();
                    if (diff > 0 && diff < AppConstant.REMINDER_TIME_DIFF) {
                        setNotificationAlarm(con, reminder, realm, diff);
                    } else if (diff < 0) {
                        setNotificationAlarm(con, reminder, realm, 0);
                    }
                }
            }
        } else {
            Log.i(TAG, "No results found: ");
        }
    }

    private static void setNotificationAlarm(Context con, Reminder reminder, Realm realm, long timestamp){
        Random random = new Random();
        Intent intent = new Intent(con, NotificationReceiver.class);
        intent.putExtra(AppConstant.TASK_NAME_KEY, reminder.getTask());
        AlarmManager alarmMgr = (AlarmManager) con.getSystemService(Activity.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(con, random.nextInt(200), intent, 0);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        timestamp, pendingIntent);

        updateReminder(reminder, realm);
    }

    public static void deleteInvalidReminder(Context con) {
        Realm realm = Realm.getInstance(MyApplication.getInstance());
        RealmQuery query = realm.where(Reminder.class);
        RealmResults<Reminder> results = query.findAll();
        if (results != null && !results.isEmpty()) {
            for (int i = 0; i < results.size(); i++) {
                Reminder reminder = results.get(i);
                if (!reminder.isValid()) {
                    realm.beginTransaction();
                    results.get(i).removeFromRealm();
                    realm.commitTransaction();
                }
            }
        } else {
            Log.i(TAG, "No results found: ");
        }
    }

    private static void updateReminder(Reminder reminder, Realm realm) {
        realm.beginTransaction();
        reminder.setValid(false);
        realm.commitTransaction();
    }

    public static void deleteAllReminder() {
        Realm realm = Realm.getInstance(MyApplication.getInstance());
        realm.beginTransaction();
        realm.clear(Reminder.class);
        realm.commitTransaction();
    }
}
