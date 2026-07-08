package com.clickretina.skillforge.data.remote

import com.clickretina.skillforge.data.model.SkillforgeResponse
import retrofit2.http.GET

interface SkillforgeApi {

    @GET("android-assesment/notes/refs/heads/main/data.json")
    suspend fun getCourseCatalog(): SkillforgeResponse
}

