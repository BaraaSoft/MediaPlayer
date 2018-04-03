package com.baraa.bsoft.mediaplayer.Services;

/**
 * Created by baraa on 28/03/2018.
 */

public class Constants {
    public interface ACTION{
        public static final String ACTION_MAIN = "com.baraa.bsoft.mediaplayer.action.main";
        public static final String ACTION_PLAY = "com.baraa.bsoft.mediaplayer.action.play";
        public static final String ACTION_PREV = "com.baraa.bsoft.mediaplayer.action.prev";
        public static final String ACTION_NEXT = "com.baraa.bsoft.mediaplayer.action.next";
        public static final String ACTION_PAUSE = "com.baraa.bsoft.mediaplayer.action.pause";
        public static final String ACTION_START_FOREGROUND = "com.baraa.bsoft.mediaplayer.action.START_FOREGROUND";
        public static final String ACTION_STOP_FOREGROUND = "com.baraa.bsoft.mediaplayer.action.STOP_FOREGROUND ";

    }

    public interface NOTIFICATION_ID{
        public static final int FOREGROUND_SERVICE = 1;
        public static final String IMG = "img";
        public static final String TITLE = "title";
        public static final String TITLE_AR = "title_ar";
        public static final String SUD_TEXT = "sub_text";
    }
}
