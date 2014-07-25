package com.ambergleam.visualizer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import java.util.Random;

public class VisualizerFragment extends Fragment {

    private static Random sRandom = new Random();
    private AnimatorSet mAnimatorSet;

    private View mOrbView;
    private int orb_x;
    private int orb_y;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mOrbView = view.findViewById(R.id.orb);

        view.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                startVisualizer();
            }
        });

        orb_x = view.getWidth() / 2;
        orb_y = view.getHeight() / 2;

        mOrbView.setX(orb_x);
        mOrbView.setX(orb_y);

        return view;
    }

    private void startVisualizer() {
        ObjectAnimator orbAnimatorX = ObjectAnimator.ofFloat(mOrbView, "x", orb_x, orb_x + sRandom.nextInt(400)).setDuration(1000);
        orbAnimatorX.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator orbAnimatorY = ObjectAnimator.ofFloat(mOrbView, "y", orb_y, orb_y + sRandom.nextInt(400)).setDuration(1000);
        orbAnimatorY.setInterpolator(new AccelerateInterpolator());

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(orbAnimatorX).with(orbAnimatorY);
        mAnimatorSet.start();
    }

}
