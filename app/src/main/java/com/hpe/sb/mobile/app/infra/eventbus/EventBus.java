package com.hpe.sb.mobile.app.infra.eventbus;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by malikdav on 26/04/2016.
 */
public class EventBus<T> {

    private final PublishSubject<T> bus = PublishSubject.create();

    // If multiple threads are going to emit events to this
    // then it must be made thread-safe like this instead
//    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public void send(T o) {
        bus.onNext(o);
    }

    public Observable<T> toObserverable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}
