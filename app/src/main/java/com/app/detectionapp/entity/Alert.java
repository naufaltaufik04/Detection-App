package com.app.detectionapp.entity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.app.detectionapp.R;

public class Alert extends AsyncTask<Integer, Void, String>{
    static final int WITHOUT_MASK = 0;
    static final int INCORRECT_MASK = 1;
    static final int WITH_MASK = 2;

    Context context;

    public Alert(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Integer... status) {
        this.run(status[0]);
        return "Success";
    }
    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
    }

    public void run(int detectionStatus){
        MediaPlayer mediaPlayer = MediaPlayer.create(this.context, alertingAssets(detectionStatus));
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }

    public int alertingAssets(int detectionStatus){
        int assets = 0;
        switch (detectionStatus){
            case WITHOUT_MASK:{
                assets = R.raw.without_mask;
                break;
            }
            case INCORRECT_MASK:{
                assets = R.raw.incorrect_mask;
                break;
            }
            case WITH_MASK:{
                assets = R.raw.with_mask;
                break;
            }
        }

        return assets;
    }
}