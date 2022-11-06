package com.vmobile.astronomypics.utils

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 10,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    maxCountDown: Int = 1,
    afterObserve: () -> Unit = {},
): T {

    val data: MutableList<T?> = mutableListOf()
    val latch = CountDownLatch(maxCountDown)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data.add(o)
            latch.countDown()
            if (latch.count == 0L) {
                this@getOrAwaitValue.removeObserver(this)
            }
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    return data as T
}