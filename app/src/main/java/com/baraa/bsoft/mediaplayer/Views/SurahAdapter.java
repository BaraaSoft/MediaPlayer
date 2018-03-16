package com.baraa.bsoft.mediaplayer.Views;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baraa.bsoft.mediaplayer.Activities.MainActivity;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.RealmResults;
import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by baraa on 01/01/2018.
 */

public class SurahAdapter extends ArrayAdapter {
    private static final String TAG = "SurahAdapter";
    private RealmResults<Surah> surahslst;
    private Context context;
    private Surah surah;
    private int resource;
    private LayoutInflater layoutInflater;
    private PlayListListener mPlayListListener;
    private WeakReference<MainActivity> mainActivityWeakReference;

    private ProgressHelper mProgressHelper;
    private ArrayList<String> mKeys;


    public interface PlayListListener{
        void onItemListClicked(Surah surah,int index,FabButton view);
        void onDownloadClicked(Surah surah,int index,FabButton view);
    }
    public void setmPlayListListener(PlayListListener playListListener){
        mPlayListListener = playListListener;
    }
    public SurahAdapter(@NonNull Context context, @LayoutRes int resource, RealmResults<Surah> mlist) {
        super(context, resource);
        surahslst = mlist;
        this.context = context;
        this.resource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        //mainActivityWeakReference = new WeakReference<MainActivity>(mainActivity);
        this.mKeys = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return surahslst.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null) {
            convertView = layoutInflater.inflate(resource,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Surah surah = surahslst.get(position);
        viewHolder.getTvTitle().setText(surah.getTitle());
        viewHolder.getBtnPlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayListListener.onItemListClicked(surah,position,(FabButton)viewHolder.getBtnPlay());
            }
        });
        viewHolder.getBtnDownload().setOnClickListener(new ItemDownloadListener(viewHolder.getBtnDownload(),context,surahslst.get(position)));
        return convertView;
    }

    public boolean itemInTheList(String x, ArrayList<String> list){
        for (String key:list) {
            if (x.compareTo(key) == 0){
                return true;
            }
        }
        return false;
    }

    /*** To prevent recycling ListView Item ***
     *
     *    (( disable view recycling!! ))
     *
     ****/
    @Override
    public int getViewTypeCount() {

        return surahslst.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    /*************************************/

    public class ViewHolder{
        public TextView tvTitle;
        public FabButton btnPlay;
        private FabButton btnDownload;
        private CardView cvMain;
        private View view;

        public View getView() {
            return view;
        }

        public ViewHolder(View view) {
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.btnPlay = (FabButton) view.findViewById(R.id.btnPlayLst);
            this.cvMain = (CardView)view.findViewById(R.id.cvMain);
            this.btnDownload = (FabButton)view.findViewById(R.id.btnDownloadLst);
            this.view = view;
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public FabButton getBtnPlay() {
            return btnPlay;
        }

        public CardView getCvMain() {
            return cvMain;
        }

        public FabButton getBtnDownload() {
            return btnDownload;
        }
    }
}
