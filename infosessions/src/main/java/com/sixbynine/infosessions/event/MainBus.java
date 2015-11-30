package com.sixbynine.infosessions.event;

import com.squareup.otto.Bus;

/**
 * @author curtiskroetsch
 */
public final class MainBus {

    private static Bus sBus = new Bus();

    public static Bus get() {
        return sBus;
    }

}
