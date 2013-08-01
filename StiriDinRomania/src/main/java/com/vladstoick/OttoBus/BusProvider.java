package com.vladstoick.OttoBus;

import com.squareup.otto.Bus;

/**
 * Created by vlad on 7/19/13.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}