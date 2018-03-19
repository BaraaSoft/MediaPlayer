package com.baraa.bsoft.mediaplayer.DataAccess;

import android.content.Context;
import android.util.Log;

import com.baraa.bsoft.mediaplayer.Model.Surah;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by baraa on 11/02/2018.
 */

public class DAL {
    private static final String TAG = "DAL";
    private static DAL ourInstance = null;
    private static Realm realm;
    private static Context mContext;
    public static DAL getInstance() {
        if(ourInstance == null){
            ourInstance = new DAL();
            return ourInstance;
        }
        return  ourInstance;
    }

    private DAL() {
        realm = Realm.getDefaultInstance();
    }


    public static DAL setContext(Context context){
        mContext = context;
        return ourInstance;
    }

    public static void InsertListToDB(final ArrayList<Surah> lst){

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.insert(lst);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.d(TAG, "onError: \n"+error.toString());
            }
        });

    }

    public static RealmResults<Surah> getAllSurah(){
        RealmQuery<Surah> query = realm.where(Surah.class);
        return query.findAll();
    }

    public static void updateProgress(final String id ,final int progress){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Surah surah = bgRealm.where(Surah.class).equalTo("key", id).findFirst();
                surah.setProgress(progress);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: item Action Set!");
            }
        });
    }

    public static double getProgress(final String id){
        Surah surah = realm.where(Surah.class).equalTo("key", id).findFirst();
        return surah.getProgress();
    }
    public Surah getSurah(final String id){
        Surah surah = realm.where(Surah.class).equalTo("key", id).findFirst();
        return surah;
    }


}
