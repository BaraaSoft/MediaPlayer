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

import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.R;

import io.realm.RealmResults;
import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by baraa on 01/01/2018.
 */

public class SurahAdapter extends ArrayAdapter {
    private RealmResults<Surah> surahslst;
    private Context context;
    private Surah surah;
    private int resource;
    private LayoutInflater layoutInflater;
    private PlayListListener mPlayListListener;
    public interface PlayListListener{
        void onItemListClicked(Surah surah,int index,View view);
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
    }

    @Override
    public int getCount() {
        return surahslst.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(resource,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Surah surah = surahslst.get(position);
        viewHolder.getTvTitle().setText(surah.getTitle());
        viewHolder.getBtnPlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayListListener.onItemListClicked(surah,position,viewHolder.getBtnPlay());

            }
        });

        return convertView;
    }

    public class ViewHolder{
        public TextView tvTitle;
        public FabButton btnPlay;
        private CardView cvMain;
        private View view;

        public View getView() {
            return view;
        }

        public ViewHolder(View view) {
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.btnPlay = (FabButton) view.findViewById(R.id.btnPlayLst);
            this.cvMain = (CardView)view.findViewById(R.id.cvMain);
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
    }
}
