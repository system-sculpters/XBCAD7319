package com.systemsculpers.xbcad7319

import com.systemsculpers.xbcad7319.data.api.service.UserService
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

class UserServiceUnitTest {
    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var getAllUsersCallMock: Call<List<User>>

    @Mock
    private lateinit var getUsersByRoleCallMock: Call<List<User>>

    @Mock
    private lateinit var updateUserRoleCallMock: Call<Void>

    @Mock
    private lateinit var updateUserDetailsCallMock: Call<Void>

    @Mock
    private lateinit var updatePasswordCallMock: Call<Void>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getAllUsers should return a list of users`() {
        // Arrange
        val token = "Bearer valid_token"
        val userList = listOf(User(id = "1"), User(id = "2"))

        `when`(userService.getAllUsers(token)).thenReturn(getAllUsersCallMock)
        `when`(getAllUsersCallMock.execute()).thenReturn(Response.success(userList))

        // Act
        val response = userService.getAllUsers(token).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(userList, response.body())
    }

    @Test
    fun `getUsersByRole should return a list of users with specific role`() {
        // Arrange
        val token = "Bearer valid_token"
        val userList = listOf(User(id = "1", role = "user"), User(id = "2", role = "user"))

        `when`(userService.getUsersByRole(token)).thenReturn(getUsersByRoleCallMock)
        `when`(getUsersByRoleCallMock.execute()).thenReturn(Response.success(userList))

        // Act
        val response = userService.getUsersByRole(token).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(userList, response.body())
    }

    @Test
    fun `updateUserRole should return success when role is updated`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val user = User(id = userId, role = "agent")

        `when`(userService.updateUserRole(token, userId, user)).thenReturn(updateUserRoleCallMock)
        `when`(updateUserRoleCallMock.execute()).thenReturn(Response.success(null))

        // Act
        val response = userService.updateUserRole(token, userId, user).execute()

        // Assert
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `updateUserDetails should return success when user details are updated`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val user = User(id = userId, fullName = "New Name", email = "new@example.com")

        `when`(userService.updateUserDetails(token, userId, user)).thenReturn(updateUserDetailsCallMock)
        `when`(updateUserDetailsCallMock.execute()).thenReturn(Response.success(null))

        // Act
        val response = userService.updateUserDetails(token, userId, user).execute()

        // Assert
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `updatePassword should return success when password is updated`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val user = User(id = userId, password = "newPassword123")

        `when`(userService.updatePassword(token, userId, user)).thenReturn(updatePasswordCallMock)
        `when`(updatePasswordCallMock.execute()).thenReturn(Response.success(null))

        // Act
        val response = userService.updatePassword(token, userId, user).execute()

        // Assert
        assertTrue(response.isSuccessful)
    }

    @Test
    fun `getAllUsers should handle unauthorized error`() {
        // Arrange
        val token = "Bearer invalid_token"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Unauthorized")

        `when`(userService.getAllUsers(token)).thenReturn(getAllUsersCallMock)
        `when`(getAllUsersCallMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = userService.getAllUsers(token).execute()

        // Assert
        assertEquals(401, response.code())
        assertEquals("Unauthorized", response.errorBody()?.string())
    }

    @Test
    fun `updateUserRole should handle forbidden error`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val user = User(id = userId, role = "agent")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Forbidden")

        `when`(userService.updateUserRole(token, userId, user)).thenReturn(updateUserRoleCallMock)
        `when`(updateUserRoleCallMock.execute()).thenReturn(Response.error(403, errorBody))

        // Act
        val response = userService.updateUserRole(token, userId, user).execute()

        // Assert
        assertEquals(403, response.code())
        assertEquals("Forbidden", response.errorBody()?.string())
    }
}