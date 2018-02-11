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
    private static final DAL ourInstance = new DAL();
    private static Realm realm;
    private static Context mContext;
    public static DAL getInstance() {
        return ourInstance;
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


}
