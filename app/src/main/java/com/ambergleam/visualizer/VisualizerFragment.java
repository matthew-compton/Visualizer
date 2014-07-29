package com.ambergleam.visualizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ambergleam.visualizer.utils.StringFormat;

import java.lang.reflect.Field;

public class VisualizerFragment extends Fragment {

    private static final String TAG = VisualizerFragment.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private Visualizer mVisualizer;

    private VisualizerView mVisualizerView;
    private RelativeLayout mRelativeLayout;

    private TextView mTitleTextView;
    private TextView mCurrentTimeTextView;
    private TextView mDurationTimeTextView;

    private SeekBar mSeekBar;
    private Handler mSeekBarHandler = new Handler();

    private int mAudioId;
    private int mCurrentPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visualizer, container, false);
        setHasOptionsMenu(true);

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.frame);
        mRelativeLayout.setVisibility(View.INVISIBLE);

        mTitleTextView = (TextView) view.findViewById(R.id.title);
        mCurrentTimeTextView = (TextView) view.findViewById(R.id.time_current);
        mDurationTimeTextView = (TextView) view.findViewById(R.id.time_total);
        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);

        mAudioId = 0;

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVisualizer != null) {
            mVisualizer.release();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem menuItemFeedback = menu.findItem(R.id.action_feedback);
        MenuItem menuItemRestart = menu.findItem(R.id.action_restart);
        if (mAudioId == 0) {
            menuItemFeedback.setVisible(false);
            menuItemRestart.setVisible(false);
        } else {
            menuItemFeedback.setVisible(true);
            menuItemRestart.setVisible(true);
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                menuItemFeedback.setIcon(android.R.drawable.ic_media_pause);
            } else {
                menuItemFeedback.setIcon(android.R.drawable.ic_media_play);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feedback:
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        pause();
                    } else {
                        resume();
                    }
                }
                return true;
            case R.id.action_restart:
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        reset();
                        resume();
                    } else {
                        reset();
                    }
                }
                return true;
            case R.id.action_search:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            startTime();
        }
    };

    private void startTime() {
        updateTime();
        mCurrentPosition += 1000;
        mSeekBar.setProgress(mCurrentPosition);

        mSeekBarHandler.postDelayed(mRunnable, 1000);
    }

    private void stopTime() {
        mSeekBarHandler.removeCallbacks(mRunnable);
    }

    private void updateTime() {
        mCurrentTimeTextView.setText(StringFormat.getTimeFormatted(mCurrentPosition));
    }

    private void setAudio(String name) {
        try {
            Field field = R.raw.class.getField(name);
            int audioId = field.getInt(field);
            if (mAudioId != audioId) {
                if (mMediaPlayer != null) {
                    destroy();
                }
                mAudioId = audioId;
                setup();
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: " + e.getStackTrace().toString());
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "NoSuchFieldException: " + e.getStackTrace().toString());
        }
        updateUI(name);
        getActivity().invalidateOptionsMenu();
    }

    private void updateUI(String name) {
        mRelativeLayout.setVisibility(View.VISIBLE);
        mTitleTextView.setText(StringFormat.getFileNameFormatted(name));
        mDurationTimeTextView.setText(StringFormat.getTimeFormatted(mMediaPlayer.getDuration()));
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mMediaPlayer.isPlaying()) {
                        pause();
                        mCurrentPosition = progress;
                        updateTime();
                        resume();
                    } else {
                        mCurrentPosition = progress;
                        updateTime();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setup() {
        mVisualizerView = new VisualizerView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mVisualizerView.setLayoutParams(params);
        mRelativeLayout.addView(mVisualizerView);

        mMediaPlayer = MediaPlayer.create(getActivity(), mAudioId);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCurrentPosition = 0;
                mSeekBar.setProgress(0);
                mMediaPlayer.pause();
                stopTime();
                updateTime();
                getActivity().invalidateOptionsMenu();
            }
        });

        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(mOnDataCaptureListener, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);

        getActivity().invalidateOptionsMenu();
    }

    private void pause() {
        mMediaPlayer.pause();
        stopTime();
        getActivity().invalidateOptionsMenu();
    }

    private void reset() {
        mCurrentPosition = 0;
        mSeekBar.setProgress(0);
        mMediaPlayer.pause();
        stopTime();
        updateTime();
        getActivity().invalidateOptionsMenu();
    }

    private void resume() {
        mMediaPlayer.seekTo(mCurrentPosition);
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mMediaPlayer.start();
                startTime();
                getActivity().invalidateOptionsMenu();
            }
        });
    }

    private void destroy() {
        mCurrentPosition = 0;
        mSeekBar.setProgress(0);
        mMediaPlayer.stop();
        stopTime();
        updateTime();
        mVisualizer.setEnabled(false);
        mRelativeLayout.removeView(mVisualizerView);
    }

    private void showDialog() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            adapter.add(StringFormat.getFileNameFormatted(field.getName()));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Audio:");
        builder.setIcon(android.R.drawable.ic_menu_search);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        setAudio(StringFormat.getFileNameRaw(adapter.getItem(position)));
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
