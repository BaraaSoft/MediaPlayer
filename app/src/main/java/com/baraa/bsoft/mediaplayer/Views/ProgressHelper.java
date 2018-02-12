package com.baraa.bsoft.mediaplayer.Views;

import android.app.Activity;
import android.os.Handler;

import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by baraa on 14/01/2018.
 */

public class ProgressHelper {
    private final FabButton button;
    private final Activity activity;
    private int currentProgress = 0;
    private Handler handle=new Handler();
    private  boolean resume;

    public ProgressHelper(FabButton button, Activity activity) {
        this.button = button;
        this.activity = activity;
    }

    private Runnable getRunnable(final Activity activity){
        return new Runnable() {
            @Override
            public void run() {
                // currentProgress += 1;  Increment the progress each time in loop
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setProgress(currentProgress);
                        if(currentProgress <= 100){
                            handle.postDelayed(getRunnable(activity),50);
                        }
                    }
                });
            }
        };
    }

    private Runnable getRunnableIndeterminate(final Activity activity){
        return new Runnable() {
            @Override
            public void run() {

                while (resume){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        };
    }

    public void startIndeterminate() {
        button.resetIcon();
        button.showShadow(false);
        currentProgress = 0;
        resume = true;
        button.showProgress(true);
        button.setProgress(currentProgress);
        mythread my= new mythread();
        my.start();
    }

    public void stopIndeterminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resume = false;
                button.showProgress(false);
            }
        });
    }

    public void startDeterminate() {
        button.resetIcon();
        button.showShadow(false);
        currentProgress = 0;
        button.showProgress(true);
        button.setProgress(currentProgress);
        getRunnable(activity).run();
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    class  mythread extends  Thread{
        public void run() {
            while(resume){
                currentProgress += 25;
                try {
                    Thread.sleep(50);

                }  catch (Exception e) {}
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setProgress(currentProgress);
                        if(currentProgress >= 100){
                            button.setProgress(0);
                            currentProgress = 0;
                        }
                    }
                });

            }
        }}
}