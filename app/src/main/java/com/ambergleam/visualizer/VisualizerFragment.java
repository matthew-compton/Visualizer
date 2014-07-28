package com.ambergleam.visualizer;

import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ambergleam.visualizer.AudioPlayer.STATE;

public class VisualizerFragment extends Fragment {

    private static final float VISUALIZER_HEIGHT_DIP = 50f;

    private AudioPlayer mAudioPlayer;
    private Visualizer mVisualizer;

    private LinearLayout mFrame;
    private VisualizerView mVisualizerView;
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualizer, container, false);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAudioPlayer = new AudioPlayer();

        mPlayButton = (ImageButton) view.findViewById(R.id.play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAudioPlayer.getState() == STATE.PAUSED) {
                    mAudioPlayer.resume();
                } else {
                    mAudioPlayer.play(getActivity(), R.raw.test_audio);
                    mVisualizer = new Visualizer(mAudioPlayer.getAudioSessionId());
                    mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
                    mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {

                        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                            mVisualizerView.updateVisualizer(bytes);
                        }

                        public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        }

                    }, Visualizer.getMaxCaptureRate() / 2, true, false);
                }
                updateUI();
            }
        });

        mPauseButton = (ImageButton) view.findViewById(R.id.pause);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioPlayer.pause();
                updateUI();
            }
        });

        mFrame = (LinearLayout) view.findViewById(R.id.frame);
        mVisualizerView = new VisualizerView(getActivity());
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mFrame.addView(mVisualizerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVisualizer != null) {
            mVisualizer.release();
        }
        mAudioPlayer.stop();
    }

    private void updateUI() {
        switch (mAudioPlayer.getState()) {
            case PAUSED:
            case STOPPED:
                if (mVisualizer != null) {
                    mVisualizerView.setEnabled(false);
                }
                mPauseButton.setEnabled(false);
                mPlayButton.setEnabled(true);
                break;
            case PLAYING:
                if (mVisualizer != null) {
                    mVisualizerView.setEnabled(true);
                }
                mPauseButton.setEnabled(true);
                mPlayButton.setEnabled(false);
                break;
            default:
                break;
        }
    }

}
