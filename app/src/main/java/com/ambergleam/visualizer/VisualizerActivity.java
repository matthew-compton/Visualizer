package com.ambergleam.visualizer;

import android.support.v4.app.Fragment;

public class VisualizerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new VisualizerFragment();
    }

}