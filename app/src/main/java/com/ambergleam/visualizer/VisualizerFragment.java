package com.ambergleam.visualizer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ambergleam.visualizer.AudioPlayer.STATE;

public class VisualizerFragment extends Fragment {

    private AudioPlayer mAudioPlayer;
    private Visualizer mVisualizer;

    private VisualizerView mVisualizerView;
    private LinearLayout mLinearLayout;

    private ImageView mPlayButton;
    private ImageView mPauseButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualizer, container, false);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mVisualizerView = new VisualizerView(getActivity());
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mLinearLayout = (LinearLayout) view.findViewById(R.id.frame);
        mLinearLayout.addView(mVisualizerView);

        mAudioPlayer = new AudioPlayer(getActivity(), R.raw.test_audio);
        mAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mAudioPlayer.stop();
                mVisualizer.setEnabled(false);
            }
        });

        mVisualizer = new Visualizer(mAudioPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(mOnDataCaptureListener, Visualizer.getMaxCaptureRate() / 2, true, false);

        mPlayButton = (ImageView) view.findViewById(R.id.play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioPlayer.getState() == STATE.PAUSED) {
                    mVisualizer.setEnabled(true);
                    mAudioPlayer.resume();
                } else {
                    mVisualizer.setEnabled(true);
                    mAudioPlayer.play();
                }
                mPlayButton.setVisibility(View.GONE);
                mPauseButton.setVisibility(View.VISIBLE);
            }
        });

        mPauseButton = (ImageView) view.findViewById(R.id.pause);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVisualizer.setEnabled(false);
                mAudioPlayer.pause();
                mPlayButton.setVisibility(View.VISIBLE);
                mPauseButton.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVisualizer != null) {
            mVisualizer.release();
        }
        mAudioPlayer.stop();
    }

    private Visualizer.OnDataCaptureListener mOnDataCaptureListener = new Visualizer.OnDataCaptureListener() {

        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            mVisualizerView.updateVisualizer(bytes);
        }

        public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
        }

    };

}
