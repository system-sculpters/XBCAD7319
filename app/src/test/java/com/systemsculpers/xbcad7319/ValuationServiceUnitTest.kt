package com.systemsculpers.xbcad7319


import com.systemsculpers.xbcad7319.data.api.service.ValuationService
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

class ValuationServiceUnitTest {
    @Mock
    private lateinit var valuationService: ValuationService

    @Mock
    private lateinit var getValuationsCallMock: Call<List<Valuation>>

    @Mock
    private lateinit var getUserValuationsCallMock: Call<List<Valuation>>

    @Mock
    private lateinit var createValuationCallMock: Call<Valuation>

    @Mock
    private lateinit var updateValuationCallMock: Call<Valuation>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getValuations should return a list of valuations for agent`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "agent123"
        val valuationList = listOf(Valuation(id = "1"), Valuation(id = "2"))

        `when`(valuationService.getValuations(token, userId)).thenReturn(getValuationsCallMock)
        `when`(getValuationsCallMock.execute()).thenReturn(Response.success(valuationList))

        // Act
        val response = valuationService.getValuations(token, userId).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(valuationList, response.body())
    }

    @Test
    fun `getUserValuations should return a list of valuations for user`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val valuationList = listOf(Valuation(id = "3"), Valuation(id = "4"))

        `when`(valuationService.getUserValuations(token, userId)).thenReturn(getUserValuationsCallMock)
        `when`(getUserValuationsCallMock.execute()).thenReturn(Response.success(valuationList))

        // Act
        val response = valuationService.getUserValuations(token, userId).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(valuationList, response.body())
    }

    @Test
    fun `createValuation should return the created valuation`() {
        // Arrange
        val token = "Bearer valid_token"
        val newValuation = Valuation(id = "5", status = "pending")

        `when`(valuationService.createValuation(token, newValuation)).thenReturn(createValuationCallMock)
        `when`(createValuationCallMock.execute()).thenReturn(Response.success(newValuation))

        // Act
        val response = valuationService.createValuation(token, newValuation).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(newValuation, response.body())
    }

    @Test
    fun `updateValuation should return the updated valuation`() {
        // Arrange
        val token = "Bearer valid_token"
        val valuationId = "5"
        val updatedValuation = Valuation(id = valuationId, status = "approved")

        `when`(valuationService.updateValuation(token, valuationId, updatedValuation)).thenReturn(updateValuationCallMock)
        `when`(updateValuationCallMock.execute()).thenReturn(Response.success(updatedValuation))

        // Act
        val response = valuationService.updateValuation(token, valuationId, updatedValuation).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(updatedValuation, response.body())
    }

    @Test
    fun `getValuations should handle unauthorized error`() {
        // Arrange
        val token = "Bearer invalid_token"
        val userId = "agent123"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Unauthorized")

        `when`(valuationService.getValuations(token, userId)).thenReturn(getValuationsCallMock)
        `when`(getValuationsCallMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = valuationService.getValuations(token, userId).execute()

        // Assert
        assertEquals(401, response.code())
        assertEquals("Unauthorized", response.errorBody()?.string())
    }

    @Test
    fun `createValuation should handle conflict error`() {
        // Arrange
        val token = "Bearer valid_token"
        val newValuation = Valuation(id = "5", status = "pending")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Conflict")

        `when`(valuationService.createValuation(token, newValuation)).thenReturn(createValuationCallMock)
        `when`(createValuationCallMock.execute()).thenReturn(Response.error(409, errorBody))

        // Act
        val response = valuationService.createValuation(token, newValuation).execute()

        // Assert
        assertEquals(409, response.code())
        assertEquals("Conflict", response.errorBody()?.string())
    }

    @Test
    fun `updateValuation should handle not found error`() {
        // Arrange
        val token = "Bearer valid_token"
        val valuationId = "invalid_id"
        val updatedValuation = Valuation(id = valuationId, status = "approved")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Not Found")

        `when`(valuationService.updateValuation(token, valuationId, updatedValuation)).thenReturn(updateValuationCallMock)
        `when`(updateValuationCallMock.execute()).thenReturn(Response.error(404, errorBody))

        // Act
        val response = valuationService.updateValuation(token, valuationId, updatedValuation).execute()

        // Assert
        assertEquals(404, response.code())
        assertEquals("Not Found", response.errorBody()?.string())
    }
}