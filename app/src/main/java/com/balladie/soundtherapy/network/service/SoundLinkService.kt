package com.balladie.soundtherapy.network.service

import com.balladie.soundtherapy.network.model.SoundInfo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface SoundLinkService {

    @GET("sound")
    fun getSoundLink(
        @Query("mode") mode: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Single<List<SoundInfo>>
}