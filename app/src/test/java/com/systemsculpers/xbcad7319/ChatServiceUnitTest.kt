package com.systemsculpers.xbcad7319

import com.systemsculpers.xbcad7319.data.api.service.ChatService
import com.systemsculpers.xbcad7319.data.model.Chat
import com.systemsculpers.xbcad7319.data.model.Message
import com.systemsculpers.xbcad7319.data.model.SendMessage
import com.systemsculpers.xbcad7319.data.model.MessageResponse
import com.systemsculpers.xbcad7319.data.model.User
import com.systemsculpers.xbcad7319.data.model.Valuation
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response

class ChatServiceUnitTest {
    @Mock
    private lateinit var chatService: ChatService

    @Mock
    private lateinit var callMock: Call<List<Chat>>

    @Mock
    private lateinit var messageCallMock: Call<List<Message>>

    @Mock
    private lateinit var sendMessageCallMock: Call<MessageResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getChats should return a list of chats`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val chatList = listOf(Chat(id = "1", participants = listOf(), messages = listOf(), createdAt = 0L, valuationId = "", participantsDetails = User()),
            Chat(id = "2", participants = listOf(), messages = listOf(), createdAt = 0L, valuationId = "", participantsDetails = User()))
        `when`(chatService.getChats(token, userId)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.success(chatList))

        // Act
        val response = chatService.getChats(token, userId).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(chatList, response.body())
    }

    @Test
    fun `getChats should handle unauthorized error`() {
        // Arrange
        val token = "Bearer invalid_token"
        val userId = "user123"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Unauthorized")

        `when`(chatService.getChats(token, userId)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = chatService.getChats(token, userId).execute()

        // Assert
        assertEquals(401, response.code())
        assertTrue(response.errorBody() != null)
        assertEquals("Unauthorized", response.errorBody()?.string())
    }

    @Test
    fun `getMessages should return a list of messages`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val messageList = listOf(Message(id = "1"), Message(id = "2"))
        `when`(chatService.getMessages(token, userId)).thenReturn(messageCallMock)
        `when`(messageCallMock.execute()).thenReturn(Response.success(messageList))

        // Act
        val response = chatService.getMessages(token, userId).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(messageList, response.body())
    }

    @Test
    fun `sendMessage should send a message and return response`() {
        // Arrange
        val token = "Bearer valid_token"
        val sendMessage = SendMessage(text = "Hello")
        val messageResponse = MessageResponse(messageId = "messageId", messages = listOf())
        `when`(chatService.sendMessage(token, sendMessage)).thenReturn(sendMessageCallMock)
        `when`(sendMessageCallMock.execute()).thenReturn(Response.success(messageResponse))

        // Act
        val response = chatService.sendMessage(token, sendMessage).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(messageResponse, response.body())
    }

    @Test
    fun `sendNewMessage should send a new message and return response`() {
        // Arrange
        val token = "Bearer valid_token"
        val sendMessage = SendMessage(text = "Hello")
        val messageResponse = MessageResponse(messageId = "messageId", messages = listOf())
        `when`(chatService.sendNewMessage(token, sendMessage)).thenReturn(sendMessageCallMock)
        `when`(sendMessageCallMock.execute()).thenReturn(Response.success(messageResponse))

        // Act
        val response = chatService.sendNewMessage(token, sendMessage).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(messageResponse, response.body())
    }

    @Test
    fun `sendMessage should handle server error`() {
        // Arrange
        val token = "Bearer valid_token"
        val sendMessage = SendMessage(text = "Hello")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Internal Server Error")

        `when`(chatService.sendMessage(token, sendMessage)).thenReturn(sendMessageCallMock)
        `when`(sendMessageCallMock.execute()).thenReturn(Response.error(500, errorBody))

        // Act
        val response = chatService.sendMessage(token, sendMessage).execute()

        // Assert
        assertEquals(500, response.code())
        assertTrue(response.errorBody() != null)
        assertEquals("Internal Server Error", response.errorBody()?.string())
    }
}