package com.baraa.bsoft.mediaplayer.DataAccess;

import android.content.Context;

import com.baraa.bsoft.mediaplayer.Model.Artist;
import com.baraa.bsoft.mediaplayer.Model.Surah;
import com.baraa.bsoft.mediaplayer.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by baraa on 24/03/2018.
 */

public class DataBuilder {
    private Context mContext;
    private ArrayList<Artist> mArtists;

    public DataBuilder(Context ctx) {
        mContext = ctx;
        mArtists = new ArrayList<>();
        DAL.getInstance().setContext(mContext).insertListArtistToDB(buildArtistData());
    }

    private ArrayList<Artist> buildArtistData(){
        mArtists.add(new Artist("1", R.drawable.shk_muhammad_abdulkareem_bezzy,mContext.getString(R.string.shk_muhammad_abdulkareem),
                "https://quran.islamway.net/quran3/324/","",mContext.getString(R.string.shk_ar_muhammad_abdulkareem)));
        mArtists.add(new Artist("2",R.drawable.shk_maher_almueaqly,mContext.getString(R.string.shk_maher_almueaqly),
                "https://quran.islamway.net/quran3/115/","",mContext.getString(R.string.shk_ar_maher_almueaqly)));

        mArtists.add(new Artist("3",R.drawable.shk_mishary_rashed_alafasy,mContext.getString(R.string.shk_alafasy),
                "https://quran.islamway.net/quran3/732/","",mContext.getString(R.string.shk_ar_alafasy)));

        mArtists.add(new Artist("4",R.drawable.shk_ahmad_bin_ali_alajmy,mContext.getString(R.string.shk_ahmad_bin_ali_alajmy),
                "https://quran.islamway.net/quran3/21/","",mContext.getString(R.string.shk_ar_ahmad_bin_ali_alajmy)));

        mArtists.add(new Artist("5",R.drawable.shk_ali_hothifi,mContext.getString(R.string.shk_ali_huthaify),
                "https://quran.islamway.net/quran3/14691/96/","",mContext.getString(R.string.shk_ar_ali_huthaify)));

        mArtists.add(new Artist("6",R.drawable.skh_saad_alghamdy,mContext.getString(R.string.shk_saad_al_ghamidi),
                "https://quran.islamway.net/quran3/45/","",mContext.getString(R.string.shk_ar_saad_al_ghamidi)));

        mArtists.add(new Artist("7",R.drawable.shk_al_sudais,mContext.getString(R.string.shk_al_sudais),
                "https://quran.islamway.net/quran3/82/","",mContext.getString(R.string.shk_ar_al_sudais)));

        mArtists.add(new Artist("8",R.drawable.shk_saud_al_shuraim,mContext.getString(R.string.shk_saud_al_shuraim),
                "https://quran.islamway.net/quran3/101/12097/128/","",mContext.getString(R.string.shk_ar_saud_al_shuraim)));

        mArtists.add(new Artist("9",R.drawable.shk_siddiq_el_minshawi,mContext.getString(R.string.shk_siddiq_el_minshawi),
                "https://quran.islamway.net/quran3/1032/9925/32/","",mContext.getString(R.string.shk_ar_siddiq_el_minshawi)));

        mArtists.add(new Artist("10",R.drawable.shk_alzain_mohamed,mContext.getString(R.string.shk_alzain_mohamed),
                "https://quran.islamway.net/quran3/687/","",mContext.getString(R.string.shk_ar_alzain_mohamed)));

        mArtists.add(new Artist("11",R.drawable.shk_mohamed_samad,mContext.getString(R.string.shk_mohamed_samad),
                "https://quran.islamway.net/quran3/956/14642/128/","",mContext.getString(R.string.shk_ar_mohamed_samad)));

        mArtists.add(new Artist("12",R.drawable.shk_abdul_rashid_sufi,mContext.getString(R.string.shk_abdul_rashid_sufi),
                "https://quran.islamway.net/quran3/391/","",mContext.getString(R.string.shk_ar_abdul_rashid_sufi)));
        return mArtists;
    }

    public ArrayList<Artist> getAllArtists(){
        return mArtists;
    }

    public ArrayList<Surah> builSurahList(Artist artist){
        ArrayList<Surah> surahs = new ArrayList<>();
        String baseUrl = artist.getBasUrl1();
        String[] surahArry = mContext.getResources().getStringArray(R.array.surahs);
        String[] surahsArabic = mContext.getResources().getStringArray(R.array.surahs_arabic);
        String decm = String.format(Locale.US,"%03d",0);
        for (int i=0;i<surahArry.length;i++){
            StringBuilder strBuilder = new StringBuilder(baseUrl);
            String b = String.format(Locale.US,"%03d",i+1);
            String x = strBuilder.append(b).append(".mp3").toString();
            Surah item = new Surah(surahArry[i],x,i,artist.getKey(),surahsArabic[i]);
            surahs.add(item);
        }
        DAL.getInstance().setContext(mContext).insertListToDB(surahs);
        return surahs;
    }

}
