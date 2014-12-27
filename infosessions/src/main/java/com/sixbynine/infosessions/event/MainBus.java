package com.sixbynine.infosessions.event;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * @author curtiskroetsch
 */
public final class MainBus {

    private static Bus sBus = new Bus(ThreadEnforcer.MAIN);

    public static Bus get() {
        return sBus;
    }

}
