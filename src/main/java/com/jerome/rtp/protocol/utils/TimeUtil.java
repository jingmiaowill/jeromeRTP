package com.jerome.rtp.protocol.utils;
public class TimeUtil {

    // constructors ---------------------------------------------------------------------------------------------------

    private TimeUtil() {
    }

    // public static methods ------------------------------------------------------------------------------------------

    /**
     * Retrieve a timestamp for the current instant.
     *
     * @return Current instant.
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * Retrieve a timestamp for the current instant, in nanoseconds.
     *
     * @return Current instant.
     */
    public static long nowNanos() {
        return System.nanoTime();
    }

    /**
     * Test whether a given event has timed out (in seconds).
     *
     * @param now        Current instant.
     * @param eventTime  Instant at which the event took place.
     * @param timeBuffer The amount of time for which the event is valid (in seconds).
     *
     * @return <code>true</code> if the event has expired, <code>false</code> otherwise
     */
    public static boolean hasExpired(long now, long eventTime, long timeBuffer) {
        return hasExpiredMillis(now, eventTime, timeBuffer * 1000);
    }

    /**
     * Test whether a given event has timed out (in milliseconds).
     *
     * @param now        Current instant.
     * @param eventTime  Instant at which the event took place.
     * @param timeBuffer The amount of time for which the event is valid (in milliseconds).
     *
     * @return <code>true</code> if the event has expired, <code>false</code> otherwise
     */
    public static boolean hasExpiredMillis(long now, long eventTime, long timeBuffer) {
        return (eventTime + timeBuffer) < now;
    }
}