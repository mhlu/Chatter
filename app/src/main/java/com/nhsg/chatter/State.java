package com.nhsg.chatter;

import android.app.Application;

/**
 * Created by Ron on 2015-10-18.
 */
public class State extends Application {

    private Long last_poll_time;

    public Long getLastPollTime() {
        return last_poll_time;
    }

    public void setLastPollTime(Long someVariable) {
        this.last_poll_time = someVariable;
    }
}
