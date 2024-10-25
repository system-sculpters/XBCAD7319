package com.systemsculpers.xbcad7319

import com.systemsculpers.xbcad7319.data.api.service.PurchaseService
import com.systemsculpers.xbcad7319.data.model.Purchase
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

class PurchaseServiceUnitTest {
    @Mock
    private lateinit var purchaseService: PurchaseService

    @Mock
    private lateinit var getPurchasesCallMock: Call<List<Purchase>>

    @Mock
    private lateinit var createPurchaseCallMock: Call<Purchase>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getPurchases should return a list of purchases for a specific user`() {
        // Arrange
        val token = "Bearer valid_token"
        val userId = "user123"
        val purchaseList = listOf(Purchase(id = "1"), Purchase(id = "2"))

        `when`(purchaseService.getPurchases(token, userId)).thenReturn(getPurchasesCallMock)
        `when`(getPurchasesCallMock.execute()).thenReturn(Response.success(purchaseList))

        // Act
        val response = purchaseService.getPurchases(token, userId).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(purchaseList, response.body())
    }

    @Test
    fun `getPurchases should handle unauthorized error`() {
        // Arrange
        val token = "Bearer invalid_token"
        val userId = "user123"
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Unauthorized")

        `when`(purchaseService.getPurchases(token, userId)).thenReturn(getPurchasesCallMock)
        `when`(getPurchasesCallMock.execute()).thenReturn(Response.error(401, errorBody))

        // Act
        val response = purchaseService.getPurchases(token, userId).execute()

        // Assert
        assertEquals(401, response.code())
        assertEquals("Unauthorized", response.errorBody()?.string())
    }

    @Test
    fun `createPurchase should create a purchase and return it`() {
        // Arrange
        val token = "Bearer valid_token"
        val purchase = Purchase(id = "123", userId = "userId", propertyId = "propertyId", amount = 400000.0, purchaseDate = "19 November 2024")

        `when`(purchaseService.createPurchase(token, purchase)).thenReturn(createPurchaseCallMock)
        `when`(createPurchaseCallMock.execute()).thenReturn(Response.success(purchase))

        // Act
        val response = purchaseService.createPurchase(token, purchase).execute()

        // Assert
        assertTrue(response.isSuccessful)
        assertEquals(purchase, response.body())
    }

    @Test
    fun `createPurchase should handle bad request error`() {
        // Arrange
        val token = "Bearer valid_token"
        val purchase = Purchase(id = "123", userId = "userId", propertyId = "propertyId", amount = 400000.0, purchaseDate = "19 November 2024")
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Bad Request")

        `when`(purchaseService.createPurchase(token, purchase)).thenReturn(createPurchaseCallMock)
        `when`(createPurchaseCallMock.execute()).thenReturn(Response.error(400, errorBody))

        // Act
        val response = purchaseService.createPurchase(token, purchase).execute()

        // Assert
        assertEquals(400, response.code())
        assertEquals("Bad Request", response.errorBody()?.string())
    }
}