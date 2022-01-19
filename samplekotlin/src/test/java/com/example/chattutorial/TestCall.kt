package com.example.chattutorial

import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.utils.Result

class TestCall<T : Any>(val result: Result<T>) : Call<T> {
    var cancelled: Boolean = false

    override fun cancel() {
        cancelled = true
    }

    override fun enqueue(callback: Call.Callback<T>) {
        callback.onResult(result)
    }

    override fun execute(): Result<T> {
        return result
    }
}

fun <T : Any> callFrom(valueProvider: () -> T): Call<T> = TestCall(Result(valueProvider()))

fun <T : Any> T.asCall(): Call<T> = TestCall(Result(this))

inline fun <reified T : Any> failedCall(message: String = "", cause: Throwable? = null): Call<T> {
    return object : Call<T> {
        public var cancelled: Boolean = false

        override fun execute(): Result<T> = Result(ChatError(message, cause))

        override fun enqueue(callback: Call.Callback<T>) {
            callback.onResult(Result(ChatError(message, cause)))
        }

        override fun cancel() {
            cancelled = true
        }
    }
}
