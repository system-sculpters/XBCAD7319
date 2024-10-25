package com.systemsculpers.xbcad7319

import com.systemsculpers.xbcad7319.data.api.service.BookmarkService
import com.systemsculpers.xbcad7319.data.model.Bookmark
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

class BookmarkServiceUnitTest {
    @Mock
    private lateinit var bookmarkService: BookmarkService

    @Mock
    private lateinit var callMock: Call<Void>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `bookmarkProperty should return a successful response`() {
        // Arrange
        val token = "Bearer valid_token"
        val propertyId = "property123"
        val bookmarkRequest = Bookmark(userId = "user123")
        `when`(bookmarkService.bookmarkProperty(token, propertyId, bookmarkRequest)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.success(null))

        // Act
        val response = bookmarkService.bookmarkProperty(token, propertyId, bookmarkRequest).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
    }

    @Test
    fun `bookmarkProperty should handle unauthorized error`() {
        // Arrange
        val token = "Bearer invalid_token"
        val propertyId = "property123"
        val bookmarkRequest = Bookmark(userId = "user123")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Unauthorized")

        `when`(bookmarkService.bookmarkProperty(token, propertyId, bookmarkRequest)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = bookmarkService.bookmarkProperty(token, propertyId, bookmarkRequest).execute()

        // Assert
        assertEquals(401, response.code())
        assertTrue(response.errorBody() != null)
        assertEquals("Unauthorized", response.errorBody()?.string())
    }

    @Test
    fun `unBookmarkProperty should return a successful response`() {
        // Arrange
        val token = "Bearer valid_token"
        val propertyId = "property123"
        val userId = "user123"
        `when`(bookmarkService.unBookmarkProperty(token, propertyId, userId)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.success(null))

        // Act
        val response = bookmarkService.unBookmarkProperty(token, propertyId, userId).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
    }

    @Test
    fun `unBookmarkProperty should handle server error`() {
        // Arrange
        val token = "Bearer valid_token"
        val propertyId = "property123"
        val userId = "user123"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Internal Server Error")

        `when`(bookmarkService.unBookmarkProperty(token, propertyId, userId)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.error(500, errorBody))

        // Act
        val response = bookmarkService.unBookmarkProperty(token, propertyId, userId).execute()

        // Assert
        assertEquals(500, response.code())
        assertTrue(response.errorBody() != null)
        assertEquals("Internal Server Error", response.errorBody()?.string())
    }
}