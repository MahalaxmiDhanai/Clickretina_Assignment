package com.example.skillforge.data.repository

import com.example.skillforge.data.model.SkillforgeResponse
import com.example.skillforge.data.remote.SkillforgeApi

interface SkillforgeRepository {
    suspend fun getCatalog(): Result<SkillforgeResponse>
}

class SkillforgeRepositoryImpl(
    private val api: SkillforgeApi
) : SkillforgeRepository {

    // Simple in-memory cache — fetch once per session
    private var cache: SkillforgeResponse? = null

    override suspend fun getCatalog(): Result<SkillforgeResponse> {
        cache?.let { return Result.success(it) }
        return runCatching {
            val response = api.getCourseCatalog()
            cache = response
            response
        }
    }
}
