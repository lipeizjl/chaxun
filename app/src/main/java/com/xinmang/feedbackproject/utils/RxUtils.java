package com.xinmang.feedbackproject.utils;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lipei on 2018/1/29.
 */

public class RxUtils {
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private RxUtils() {
    }

    public static RxUtils getInstance() {
        return new RxUtils();
    }

    public void clearSubscription() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.clear();
        }
    }

    public void unSubscription() {
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }

    public void addSubscription(Subscription subscription) {
        if (compositeSubscription != null) {
            compositeSubscription.add(subscription);
        }
    }
}
