package com.ambergleam.visualizer.utils;

import android.graphics.Color;

import java.util.Random;

public class RandomUtils {

    private static Random sRandom = new Random();

    public static int getRandomColor() {
        int r = sRandom.nextInt(255);
        int g = sRandom.nextInt(255);
        int b = sRandom.nextInt(255);
        int color = Color.rgb(r, g, b);
        return color;
    }

    public static int getRandomAlphaColor() {
        int a = sRandom.nextInt(255);
        int r = sRandom.nextInt(255);
        int g = sRandom.nextInt(255);
        int b = sRandom.nextInt(255);
        int color = Color.argb(a, r, g, b);
        return color;
    }

    public static int getRandomFadedColor() {
        int a = ((int) sRandom.nextGaussian() * 50 + 128);
        int r = sRandom.nextInt(255);
        int g = sRandom.nextInt(255);
        int b = sRandom.nextInt(255);
        int color = Color.argb(a, r, g, b);
        return color;
    }

    public static float getRandomLineWidth() {
        return sRandom.nextInt(4) + 1;
    }

}
