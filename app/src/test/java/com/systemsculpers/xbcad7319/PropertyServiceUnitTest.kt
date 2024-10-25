package com.systemsculpers.xbcad7319

import com.systemsculpers.xbcad7319.data.api.service.PropertyService
import com.systemsculpers.xbcad7319.data.model.Property
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

class PropertyServiceUnitTest {
    @Mock
    private lateinit var propertyService: PropertyService

    @Mock
    private lateinit var propertiesCallMock: Call<List<Property>>

    @Mock
    private lateinit var createPropertyCallMock: Call<Property>

    @Mock
    private lateinit var updatePropertyCallMock: Call<Property>

    @Mock
    private lateinit var deletePropertyCallMock: Call<Void>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getProperties should return a list of properties`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val propertyList = listOf(Property(id = "1"), Property(id = "2"))
        `when`(propertyService.getProperties(token, userId)).thenReturn(propertiesCallMock)
        `when`(propertiesCallMock.execute()).thenReturn(Response.success(propertyList))

        // Act
        val response = propertyService.getProperties(token, userId).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(propertyList, response.body())
    }

    @Test
    fun `getProperties should handle unauthorized error`() {
        // Arrange
        val token = "Bearer invalid_token"
        val userId = "user123"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Unauthorized")

        `when`(propertyService.getProperties(token, userId)).thenReturn(propertiesCallMock)
        `when`(propertiesCallMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = propertyService.getProperties(token, userId).execute()

        // Assert
        assertEquals(401, response.code())
        assertEquals("Unauthorized", response.errorBody()?.string())
    }

    @Test
    fun `createProperty should create a property and return it`() {
        // Arrange
        val token = "Bearer valid_token"
        val property = Property(id = "123", title = "Test Property")
        val propertyBody = RequestBody.create("application/json".toMediaTypeOrNull(), "Test Property")
        val images = listOf<MultipartBody.Part>() // Replace with mock images if needed

        `when`(propertyService.createProperty(token, propertyBody, images)).thenReturn(createPropertyCallMock)
        `when`(createPropertyCallMock.execute()).thenReturn(Response.success(property))

        // Act
        val response = propertyService.createProperty(token, propertyBody, images).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(property, response.body())
    }

    @Test
    fun `createProperty should handle server error`() {
        // Arrange
        val token = "Bearer valid_token"
        val propertyBody = RequestBody.create("application/json".toMediaTypeOrNull(), "Test Property")
        val images = listOf<MultipartBody.Part>()
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Server Error")

        `when`(propertyService.createProperty(token, propertyBody, images)).thenReturn(createPropertyCallMock)
        `when`(createPropertyCallMock.execute()).thenReturn(Response.error(500, errorBody))

        // Act
        val response = propertyService.createProperty(token, propertyBody, images).execute()

        // Assert
        assertEquals(500, response.code())
        assertEquals("Server Error", response.errorBody()?.string())
    }

    @Test
    fun `updateProperty should update a property and return it`() {
        // Arrange
        val token = "Bearer valid_token"
        val propertyId = "123"
        val updatedProperty = Property(id = propertyId, title = "Updated Property")

        val propertyBody = RequestBody.create("application/json".toMediaTypeOrNull(), "Test Property")
        val images = listOf<MultipartBody.Part>()

        `when`(propertyService.updateProperty(token, propertyId, propertyBody ,images)).thenReturn(updatePropertyCallMock)
        `when`(updatePropertyCallMock.execute()).thenReturn(Response.success(updatedProperty))

        // Act
        val response = propertyService.updateProperty(token, propertyId, propertyBody, images).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(updatedProperty, response.body())
    }

    @Test
    fun `updateProperty should handle not found error`() {
        // Arrange
        val token = "Bearer valid_token"
        val propertyId = "non_existing_id"
        val updatedProperty = Property(id = propertyId, title = "Updated Property")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Not Found")

        val propertyBody = RequestBody.create("application/json".toMediaTypeOrNull(), "Test Property")
        val images = listOf<MultipartBody.Part>()


        `when`(propertyService.updateProperty(token, propertyId, propertyBody,images)).thenReturn(updatePropertyCallMock)
        `when`(updatePropertyCallMock.execute()).thenReturn(Response.error(404, errorBody))

        // Act
        val response = propertyService.updateProperty(token, propertyId, propertyBody, images).execute()

        // Assert
        assertEquals(404, response.code())
        assertEquals("Not Found", response.errorBody()?.string())
    }

    @Test
    fun `deleteProperty should delete a property and return success`() {
        // Arrange
        val token = "Bearer valid_token"
        val propertyId = "123"
        `when`(propertyService.deleteProperty(token, propertyId)).thenReturn(deletePropertyCallMock)
        `when`(deletePropertyCallMock.execute()).thenReturn(Response.success(null))

        // Act
        val response = propertyService.deleteProperty(token, propertyId).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(null, response.body())
    }

    @Test
    fun `deleteProperty should handle unauthorized error`() {
        // Arrange
        val token = "Bearer invalid_token"
        val propertyId = "123"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Unauthorized")

        `when`(propertyService.deleteProperty(token, propertyId)).thenReturn(deletePropertyCallMock)
        `when`(deletePropertyCallMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = propertyService.deleteProperty(token, propertyId).execute()

        // Assert
        assertEquals(401, response.code())
        assertEquals("Unauthorized", response.errorBody()?.string())
    }
}