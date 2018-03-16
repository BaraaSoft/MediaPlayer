package com.baraa.bsoft.mediaplayer.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.report.listener.DownloadManagerListener;

import java.io.File;

public class DownloadService extends Service implements DownloadManagerListener {
    private static final String TAG = "DownloadService";
    private static final int REQUEST = 101;
    private int mTaskToken;

    public static final String ACTION_START_DOWNLOAD = "action.startDownload";
    public static final String DATA_SURAH = "surah";
    private  DownloadManagerPro mDownloadManagerPro;
    public DownloadService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(ACTION_START_DOWNLOAD)){
            Surah surah = (Surah) intent.getExtras().getSerializable(DATA_SURAH);
            downloadInit(surah);
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManagerPro.dispose();
        mDownloadManagerPro = null;

    }

    private void downloadInit(Surah surah){
        mDownloadManagerPro = new DownloadManagerPro(this);
        mDownloadManagerPro.init(getPublicAlbumStorageDir("Quran").getAbsolutePath(),12,this);
        mTaskToken = mDownloadManagerPro
                .addTask(surah.getKey(),surah.getUrl(),12,getPublicAlbumStorageDir("Quran").getAbsolutePath(),true,false);

    }

    private File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    @Override
    public void OnDownloadStarted(long taskId) {

    }

    @Override
    public void OnDownloadPaused(long taskId) {

    }

    @Override
    public void onDownloadProcess(long taskId, double percent, long downloadedLength) {

    }

    @Override
    public void OnDownloadFinished(long taskId) {

    }

    @Override
    public void OnDownloadRebuildStart(long taskId) {

    }

    @Override
    public void OnDownloadRebuildFinished(long taskId) {

    }

    @Override
    public void OnDownloadCompleted(long taskId) {

    }

    @Override
    public void connectionLost(long taskId) {

    }



}
