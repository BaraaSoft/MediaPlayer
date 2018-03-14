package com.baraa.bsoft.mediaplayer.Views;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.baraa.bsoft.mediaplayer.Activities.MainActivity;
import com.baraa.bsoft.mediaplayer.DataAccess.DAL;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.report.listener.DownloadManagerListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by baraa on 11/03/2018.
 */

public class ItemDownloadListener implements View.OnClickListener,DownloadManagerListener {
    private static final String TAG = "ItemDownloadListener";
    private WeakReference<FabButton> mFabButton;
    private Context mContext;
    private Surah mSurah;

    private static final int REQUEST = 101;
    private int mTaskToken;
    private DownloadManagerPro mDownloadManagerPro;
    private ProgressHelper mProgressHelper;

    private SurahAdapter mSurahAdapter;
    private KeepUiInSyncListener mKeepUiInSyncListener;

    public ItemDownloadListener(FabButton fabButton, Context context,Surah surah,SurahAdapter adapter,KeepUiInSyncListener uiInSyncListener) {
        super();
        this.mContext = context;
        this.mFabButton = new WeakReference<FabButton>(fabButton);
        this.mSurah = surah;
        mProgressHelper = new ProgressHelper(fabButton,(MainActivity)context,surah.getKey());
        this.mSurahAdapter = adapter;
        this.mKeepUiInSyncListener = uiInSyncListener;
    }

    @Override
    public void onClick(View view) {
        mDownloadManagerPro = new DownloadManagerPro(mContext);
        mDownloadManagerPro.init(getPublicAlbumStorageDir("Quran").getAbsolutePath(),12,this);
        downloadClip(mSurah);
        mKeepUiInSyncListener.onDownloadStarted(mSurah.getKey());
    }



    private void downloadClip(Surah surah){
        if (!((MainActivity)mContext).checkStoragePermissionBeforeAccess()){
            return;
        }
        mTaskToken = mDownloadManagerPro
                .addTask(surah.getKey(),surah.getUrl(),12,getPublicAlbumStorageDir("Quran").getAbsolutePath(),true,false);
        try {
            mDownloadManagerPro.startDownload(mTaskToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getPublicAlbumStorageDir(String albumName) {
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
        if(taskId == mTaskToken){
            ((MainActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressHelper.startDeterminate();
                }
            });
        }
    }

    @Override
    public void OnDownloadPaused(long taskId) {

    }

    @Override
    public void onDownloadProcess(long taskId, final double percent, long downloadedLength) {
        if(taskId == mTaskToken){
            ((MainActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DAL.getInstance().setContext(mContext).updateProgress(mSurah.getKey().toString(),percent);
                    mProgressHelper.setCurrentProgress(percent);
                    mSurahAdapter.notifyDataSetChanged();
                }
            });
            Log.d(TAG, "onDownloadProcess: currentProgress ::"+percent+"%");
        }
    }

    @Override
    public void OnDownloadFinished(long taskId) {

       mFabButton.get().setVisibility(View.INVISIBLE);
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


    public interface KeepUiInSyncListener{
        void onDownloadStarted(String key);
    }
}
