package com.systemsculpers.xbcad7319


import com.systemsculpers.xbcad7319.data.api.service.AuthService
import com.systemsculpers.xbcad7319.data.model.User
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

class AuthServiceUnitTest {
    @Mock
    private lateinit var authService: AuthService

    @Mock
    private lateinit var callMock: Call<User>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `register should return a successful response`() {
        // Arrange
        val newUser = User(id = "123", fullName = "testuser", email = "test@example.com", password = "password123")
        `when`(authService.register(newUser)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.success(newUser))

        // Act
        val response = authService.register(newUser).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(newUser, response.body())
    }

    @Test
    fun `register should handle error response`() {
        // Arrange
        val newUser = User(id = "123", fullName = "testuser", email = "test@example.com", password = "password123")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Email already in use")

        `when`(authService.register(newUser)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.error(400, errorBody))

        // Act
        val response = authService.register(newUser).execute()

        // Assert
        assertEquals(400, response.code())
        assertTrue(response.errorBody() != null)
        assertEquals("Email already in use", response.errorBody()?.string())
    }

    @Test
    fun `login should return a successful response`() {
        // Arrange
        val loginUser = User(id = "123", fullName = "testuser", email = "test@example.com", password = "password123")
        `when`(authService.login(loginUser)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.success(loginUser))

        // Act
        val response = authService.login(loginUser).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(loginUser, response.body())
    }

    @Test
    fun `login should handle invalid credentials error`() {
        // Arrange
        val loginUser = User(id = "123", fullName = "testuser", email = "test@example.com", password = "wrongpassword")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Invalid credentials")

        `when`(authService.login(loginUser)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = authService.login(loginUser).execute()

        // Assert
        assertEquals(401, response.code())
        assertTrue(response.errorBody() != null)
        assertEquals("Invalid credentials", response.errorBody()?.string())
    }
}