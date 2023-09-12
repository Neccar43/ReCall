package com.novacodestudios.recall.data.remote

import com.novacodestudios.recall.data.remote.dto.TranslationResponseDto
import com.novacodestudios.recall.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface TranslationApi {

    @Headers(
        "content-type: application/x-www-form-urlencoded",
        "Accept-Encoding: application/gzip",
        "X-RapidAPI-Key: $API_KEY",
        "X-RapidAPI-Host: google-translate1.p.rapidapi.com"
    )
    @POST("language/translate/v2")
    @FormUrlEncoded
    suspend fun translateText(
        @Field("q") query: String,
        @Field("target") targetLang: String = "tr",
        @Field("source") sourceLang: String = "en"
    ): Response<TranslationResponseDto>
}