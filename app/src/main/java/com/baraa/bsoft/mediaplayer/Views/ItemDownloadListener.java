package com.baraa.bsoft.mediaplayer.Views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.baraa.bsoft.mediaplayer.Activities.MainActivity;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.Services.DownloadService;

import java.lang.ref.WeakReference;

import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by baraa on 11/03/2018.
 */

public class ItemDownloadListener implements View.OnClickListener {
    private static final String TAG = "ItemDownloadListener";
    private WeakReference<FabButton> mFabButton;
    private Context mContext;
    private Surah mSurah;

    private static final int REQUEST = 101;
    private Integer mTaskToken = null;
    private Double mProgress = null;
    private ProgressHelper mProgressHelper;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long tk = intent.getLongExtra(DownloadService.TOKEN_DOWNLOAD,0);
            if(intent.getAction().equals(DownloadService.TOKEN_DOWNLOAD)){
                mTaskToken =  intent.getIntExtra(DownloadService.TOKEN_DOWNLOAD,0);
            }
            if(intent.getAction().equals(DownloadService.ACTION_PROGRESS)){
                if(tk == mTaskToken){
                    mProgress = intent.getDoubleExtra(DownloadService.ACTION_PROGRESS,0);
                    if(mProgress <= 0 && mProgress != null){
                        mFabButton.get().setProgress(mProgress.floatValue());
                    }
                }
            }

            if(intent.getAction().equals(DownloadService.ACTION_FINISHED)){
                if(tk == mTaskToken){
                    mFabButton.get().setVisibility(View.INVISIBLE);
                }
            }

        }

    };

    public ItemDownloadListener(FabButton fabButton, Context context,Surah surah) {
        super();
        this.mContext = context;
        this.mFabButton = new WeakReference<FabButton>(fabButton);
        this.mSurah = surah;
        mProgressHelper = new ProgressHelper(fabButton,(MainActivity)context,surah.getKey());

        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mBroadcastReceiver,new IntentFilter(DownloadService.TOKEN_DOWNLOAD));
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mBroadcastReceiver,new IntentFilter(DownloadService.ACTION_PROGRESS));
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mBroadcastReceiver,new IntentFilter(DownloadService.ACTION_FINISHED));

    }

    @Override
    public void onClick(View view) {
        downloadClip(mSurah);
    }


    private void downloadClip(Surah surah){
        if (!((MainActivity)mContext).checkStoragePermissionBeforeAccess()) return;
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.setAction(DownloadService.ACTION_START_DOWNLOAD);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DownloadService.DATA_SURAH,surah);
        intent.putExtras(bundle);
        mContext.startService(intent);
    }




}
