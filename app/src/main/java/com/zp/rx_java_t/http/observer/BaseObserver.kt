package com.zp.rx_java_t.http.observer

import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.http.api.ApiException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * 基类
 */
abstract class BaseObserver<T> : Observer<T>, SubscriberBack<T> {

    final override fun onComplete() {
        doOnComplete()
    }

    final override fun onSubscribe(d: Disposable) {
        doOnSubscribe(d)
    }

    final override fun onNext(bean: T) {
        doOnNext(bean)
    }

    final override fun onError(e: Throwable) {
        val exception = ApiException.formatException(e)
        doOnError(exception.msg, exception.code)
    }

    override fun doOnComplete() = Unit

    override fun doOnSubscribe(d: Disposable) = Unit

    override fun doOnNext(bean: T) = Unit

    override fun doOnError(errorMsg: String?, errorCode: Int) {
        if (!errorMsg.isNullOrEmpty()) showToast(errorMsg)
    }

}