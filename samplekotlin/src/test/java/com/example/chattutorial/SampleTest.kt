package com.example.chattutorial

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.utils.Result
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SampleTest {

    @Test
    fun sampleTest() = runBlockingTest {
        val chatClient: ChatClient = mock()
        whenever(
            chatClient.createChannel(
                any(),
                any()
            )
        ) doReturn TestCall(Result(Channel(cid = "messaging:123")))

        val result = chatClient.createChannel("abc", emptyList()).await()

        assertEquals("messaging:123", result.data().cid)
    }
}