package com.clickretina.skillforge.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SkillforgeResponse(
    @Json(name = "meta") val meta: Meta,
    @Json(name = "categories") val categories: List<Category>
)

@JsonClass(generateAdapter = true)
data class Meta(
    @Json(name = "app") val app: String,
    @Json(name = "version") val version: String,
    @Json(name = "generatedAt") val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "iconColor") val iconColor: String,
    @Json(name = "courseCount") val courseCount: Int,
    @Json(name = "courses") val courses: List<Course>
)

@JsonClass(generateAdapter = true)
data class Course(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "subtitle") val subtitle: String,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String,
    @Json(name = "level") val level: String,
    @Json(name = "durationHours") val durationHours: Double,
    @Json(name = "rating") val rating: Double,
    @Json(name = "studentsEnrolled") val studentsEnrolled: Int,
    @Json(name = "language") val language: String,
    @Json(name = "lastUpdated") val lastUpdated: String,
    @Json(name = "tags") val tags: List<String>,
    @Json(name = "instructor") val instructor: Instructor,
    @Json(name = "description") val description: String,
    @Json(name = "lessons") val lessons: List<Lesson>
)

@JsonClass(generateAdapter = true)
data class Instructor(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "title") val title: String,
    @Json(name = "avatarUrl") val avatarUrl: String,
    @Json(name = "bio") val bio: String
)

@JsonClass(generateAdapter = true)
data class Lesson(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "durationMinutes") val durationMinutes: Int,
    @Json(name = "isFree") val isFree: Boolean,
    @Json(name = "videoUrl") val videoUrl: String,
    @Json(name = "content") val content: String
)

