package com.nhsg.chatter;

/**
 * Created by Ron on 2015-10-18.
 */
public class Semaphore {
    private boolean signal = false;

    public synchronized void take() {
        this.signal = true;
        this.notify();
    }

    public synchronized void release() throws InterruptedException{
        while(!this.signal) wait();
        this.signal = false;
    }

}
