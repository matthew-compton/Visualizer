package com.ambergleam.visualizer;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer extends MediaPlayer {

    private MediaPlayer mPlayer;

    public enum STATE {
        PLAYING,
        PAUSED,
        STOPPED
    }

    private int mLength;
    private STATE mState;

    public AudioPlayer() {
        mState = STATE.STOPPED;
    }

    public void play(Context c, int id) {
        stop();

        mState = STATE.PLAYING;
        mPlayer = MediaPlayer.create(c, id);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });

        mPlayer.start();
    }

    public void pause() {
        mState = STATE.PAUSED;
        mPlayer.pause();
        mLength = mPlayer.getCurrentPosition();
    }

    public void stop() {
        mState = STATE.STOPPED;
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void resume() {
        mState = STATE.PLAYING;
        mPlayer.seekTo(mLength);
        mPlayer.start();
    }

    public STATE getState() {
        return mState;
    }

}