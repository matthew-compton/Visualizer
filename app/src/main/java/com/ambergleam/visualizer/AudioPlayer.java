package com.ambergleam.visualizer;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer extends MediaPlayer {

    private Context mContext;
    private int mAudioId;

    private MediaPlayer mPlayer;

    public enum STATE {
        PLAYING,
        PAUSED,
        STOPPED
    }

    private int mLength;
    private STATE mState;

    public AudioPlayer(Context context, int audioId) {
        mState = STATE.STOPPED;
        mContext = context;
        mAudioId = audioId;
    }

    public void play() {
        stop();
        mState = STATE.PLAYING;
        mPlayer = MediaPlayer.create(mContext, mAudioId);
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