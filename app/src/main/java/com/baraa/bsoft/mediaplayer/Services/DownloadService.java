package com.baraa.bsoft.mediaplayer.Services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.baraa.bsoft.mediaplayer.DataAccess.DAL;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.report.listener.DownloadManagerListener;

import java.io.File;
import java.io.IOException;

public class DownloadService extends Service implements DownloadManagerListener {
    private static final String TAG = "DownloadService";
    private static final int REQUEST = 101;
    private int mTaskToken;

    public static final String ACTION_START_DOWNLOAD = "action.startDownload";
    public static final String ACTION_PROGRESS = "action.progress";
    public static final String ACTION_FINISHED = "action.finished";
    public static final String DATA_SURAH = "surah";
    public static final String TOKEN_DOWNLOAD = "token.download";
    private  DownloadManagerPro mDownloadManagerPro;
    public DownloadService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManagerPro = new DownloadManagerPro(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(ACTION_START_DOWNLOAD)){
            int key = intent.getIntExtra(DATA_SURAH,0);
            Surah surah = DAL.getInstance().setContext(this).getSurah(key+"");
            //downloadDataViaDownloadManager(surah);
            downloadInit(surah);
        }
        return START_STICKY; //START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //mDownloadManagerPro.dispose();
        //mDownloadManagerPro = null;

    }

    private void downloadInit(Surah surah){
        mDownloadManagerPro.init(getPublicAlbumStorageDir("Quran").getAbsolutePath(),12,this);
        this.mTaskToken = mDownloadManagerPro
                .addTask(surah.getKey(),surah.getUrl(),12,getPublicAlbumStorageDir("Quran")
                        .getAbsolutePath(),true,false);
        Intent intent = new Intent(TOKEN_DOWNLOAD);
        intent.putExtra(TOKEN_DOWNLOAD,new Long(mTaskToken));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        try {
            mDownloadManagerPro.startDownload(this.mTaskToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), albumName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
        }catch (Exception e){
            Log.e(TAG, "getPublicAlbumStorageDir: ",e );
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
        Log.d(TAG, "onDownloadProcess: "+percent+"%");
        Intent intent = new Intent(ACTION_PROGRESS);
        intent.putExtra(TOKEN_DOWNLOAD,taskId);
        intent.putExtra(ACTION_PROGRESS,percent);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void OnDownloadFinished(long taskId) {
        Intent intent = new Intent(ACTION_FINISHED);
        intent.putExtra(TOKEN_DOWNLOAD,taskId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        stopSelf();
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


    private long downloadDataViaDownloadManager (Surah surah) {

        long downloadReference;

        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(surah.getUrl()));

        request.setTitle(surah.getTitle());
        request.addRequestHeader(surah.getTitle(),surah.getTitle());
        request.setDescription("Downloading "+surah.getTitle()+" please wait...");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        //request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);



        //Set the local destination for the downloaded file to a path
        //within the application's external files directory
        // request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.mp3");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,surah.getKey().toString());
        downloadReference = downloadManager.enqueue(request);
        Log.d(TAG, "downloadData: >>>"+downloadReference);
        Log.d(TAG, "downloadData: >>>"+surah.getUrl());

        return downloadReference;
    }



}
