package com.example.skillforge.data.remote

import com.example.skillforge.data.model.SkillforgeResponse
import retrofit2.http.GET

interface SkillforgeApi {

    @GET("android-assesment/notes/refs/heads/main/data.json")
    suspend fun getCourseCatalog(): SkillforgeResponse
}
