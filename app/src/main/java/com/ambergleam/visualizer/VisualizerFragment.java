package com.ambergleam.visualizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import java.lang.reflect.Field;

public class VisualizerFragment extends Fragment {

    private static final String TAG = VisualizerFragment.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;

    private VisualizerView mVisualizerView;
    private FrameLayout mFrameLayout;

    private boolean mIsPlaying;
    private Field mAudioField;
    private int mAudioId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualizer, container, false);
        setHasOptionsMenu(true);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mFrameLayout = (FrameLayout) view.findViewById(R.id.frame);
        mIsPlaying = false;
        mAudioField = null;

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVisualizer != null) {
            mVisualizer.release();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        if (mAudioField == null) {
            MenuItem item = menu.findItem(R.id.action_feedback);
            item.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feedback:
                if (mIsPlaying) {
                    mIsPlaying = false;
                    item.setIcon(android.R.drawable.ic_media_play);
                    teardown();
                } else {
                    mIsPlaying = true;
                    item.setIcon(android.R.drawable.ic_media_pause);
                    setup();
                }
                return true;
            case R.id.action_search:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAudio(Field field) {
        mAudioField = field;
        try {
            mAudioId = field.getInt(field);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: " + e.getStackTrace());
        }
        getActivity().getActionBar().setTitle(field.getName());
        getActivity().invalidateOptionsMenu();
    }

    private void setup() {
        mVisualizerView = new VisualizerView(getActivity());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mVisualizerView.setLayoutParams(params);
        mFrameLayout.addView(mVisualizerView);

        mMediaPlayer = MediaPlayer.create(getActivity(), mAudioId);
        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(mOnDataCaptureListener, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);

        mMediaPlayer.start();
    }

    private void teardown() {
        mMediaPlayer.stop();
        mVisualizer.setEnabled(false);
        mFrameLayout.removeView(mVisualizerView);
    }

    private void showDialog() {
        final ArrayAdapter<Field> adapter = new ArrayAdapter<Field>(getActivity(), android.R.layout.select_dialog_singlechoice);
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            adapter.add(field);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Audio:");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        setAudio(adapter.getItem(position));
                    }
                }
        );
        builder.show();
    }

    private Visualizer.OnDataCaptureListener mOnDataCaptureListener = new Visualizer.OnDataCaptureListener() {

        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            mVisualizerView.updateVisualizer(bytes);
        }

        public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
        }

    };

}
