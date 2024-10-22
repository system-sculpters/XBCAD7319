package com.systemsculpers.xbcad7319.data.api.service

import com.systemsculpers.xbcad7319.data.model.Bookmark
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookmarkService {
    // Route to bookmark a property
    @POST("bookmark/{propertyId}")
    fun bookmarkProperty(@Header("Authorization") token: String, @Path("propertyId") propertyId: String, @Body request: Bookmark): Call<Void>

    // Route to unBookmark a property
    @DELETE("bookmark")
    fun unBookmarkProperty(
        @Header("Authorization") token: String,
        @Query("propertyId") propertyId: String,
        @Query("userId") userId: String): Call<Void>
}