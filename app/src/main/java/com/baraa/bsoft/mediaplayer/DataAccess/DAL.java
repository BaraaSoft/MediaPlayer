package com.baraa.bsoft.mediaplayer.DataAccess;

import android.content.Context;
import android.util.Log;

import com.baraa.bsoft.mediaplayer.Model.Artist;
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
    private  Realm realm;
    private  Context mContext;
    public static DAL getInstance() {
        if(ourInstance == null){
            ourInstance = new DAL();
            return ourInstance;
        }
        return  ourInstance;
    }

    private DAL() {

    }


    public  DAL setContext(Context context){
        Realm.init(context);
        realm = Realm.getDefaultInstance();
        mContext = context;
        return ourInstance;
    }


    public  void insertListArtistToDB(final ArrayList<Artist> lst){
        Realm.init(mContext.getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
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

    public  RealmResults<Artist> getAllArtist(){
        RealmQuery<Artist> query = realm.where(Artist.class);
        return query.findAll();
    }
    public  RealmResults<Surah> getAllSurahBelongTo(String artistKey){
        RealmQuery<Surah> query = realm.where(Surah.class).equalTo("artistKey",artistKey);
        return query.findAll();
    }

    public  void insertListToDB(final ArrayList<Surah> lst){

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

    public  RealmResults<Surah> getAllSurah(){
        RealmQuery<Surah> query = realm.where(Surah.class);
        return query.findAll();
    }

    public  void updateProgress(final String id ,final int progress){
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

    public  double getProgress(final String id){
        Surah surah = realm.where(Surah.class).equalTo("key", id).findFirst();
        return surah.getProgress();
    }
    public Surah getSurah(final String id){
        Surah surah = realm.where(Surah.class).equalTo("key", id).findFirst();
        return surah;
    }


}
