package com.johnson.utils;

import android.util.Pair;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by johnson on 9/15/14.
 */
public class ShakeData {
    Queue<Pair<Long, Float>> queue = new LinkedList<Pair<Long, Float>>();
    int threshold = 100;
    int timeWindow = 700;
    long lastUpdate = 0;
    float sum = 0;

    public void add(long time, float a) {
        lastUpdate = time;
        sum += a;
        queue.add(new Pair<Long, Float>(time, a));
        autoDelete();
    }

    void autoDelete() {
        Pair<Long, Float> pair = queue.peek();
        while (pair.first + timeWindow < lastUpdate) {
            queue.poll();
            sum -= pair.second;
            pair = queue.peek();
        }
    }

    float getAverage() {
        return sum / queue.size();
    }

    public boolean triggered() {
        return getAverage() >= threshold;
    }
}
