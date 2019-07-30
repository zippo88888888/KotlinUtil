package com.zp.rx_java_t.http.observer

import io.reactivex.disposables.Disposable

interface SubscriberBack<T> {

    fun doOnComplete()

    fun doOnSubscribe(d: Disposable)

    fun doOnNext(bean: T)

    fun doOnError(errorMsg: String?, errorCode: Int)

}