package com.example.cantang.rxinvest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;

/**
 * Created by cantang on 10/4/16.
 */

public class RxUtil {

    /**
     * Emit default value if the original Observable doesn't emit any item in given time
     *
     * @param original     Original Observable
     * @param defaultValue value to be emitted on time out
     * @param time         int
     * @param timeUnit     {@link TimeUnit}
     * @return transformed Observable
     */
    public static <T> Observable<T> emitDefaultOnTimeout(Observable<T> original, final T defaultValue, int time, TimeUnit timeUnit) {
        return original.timeout(time, timeUnit)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        return Observable.just(defaultValue);
                    } else {
                        return Observable.error(throwable);
                    }
                });
    }

}
