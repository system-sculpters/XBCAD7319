package com.systemsculpers.xbcad7319

import com.systemsculpers.xbcad7319.data.api.service.AnalyticsService
import com.systemsculpers.xbcad7319.data.model.analytics.AnalyticsResponse
import com.systemsculpers.xbcad7319.data.model.analytics.PropertyStats
import com.systemsculpers.xbcad7319.data.model.analytics.UserStats
import com.systemsculpers.xbcad7319.data.model.analytics.ValuationStats
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Response

class AnalyticsServiceUnitTest {
    @Mock
    private lateinit var analyticsService: AnalyticsService

    @Mock
    private lateinit var callMock: Call<AnalyticsResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getAnalytics should return a successful response`() {
        // Arrange
        val token = "Bearer valid_token"
        val analyticsResponse = AnalyticsResponse(
            totalRevenueAmount = 0.0,
            users = UserStats(2, 2),
            properties = PropertyStats(4, 3, 2),
            valuations = ValuationStats(2, 2),
            listOf()
        )

        `when`(analyticsService.getAnalytics(token)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.success(analyticsResponse))

        // Act
        val response = analyticsService.getAnalytics(token).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(analyticsResponse, response.body())
    }

    @Test
    fun `getAnalytics should handle unauthorized error`() {
        // Arrange
        val token = "Bearer invalid_token"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Unauthorized access")

        `when`(analyticsService.getAnalytics(token)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = analyticsService.getAnalytics(token).execute()

        // Assert
        assertEquals(401, response.code())
        assertTrue(response.errorBody() != null)
    }

    @Test
    fun `getAnalytics should handle server error`() {
        // Arrange
        val token = "Bearer valid_token"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Internal Server Error")

        `when`(analyticsService.getAnalytics(token)).thenReturn(callMock)
        `when`(callMock.execute()).thenReturn(Response.error(500, errorBody))

        // Act
        val response = analyticsService.getAnalytics(token).execute()

        // Assert
        assertEquals(500, response.code())
        assertTrue(response.errorBody() != null)
    }
}