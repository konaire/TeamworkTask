package com.konaire.teamwork.network

import com.konaire.teamwork.models.ProjectResponse
import com.konaire.teamwork.models.ProjectsResponse

import io.reactivex.Single

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Evgeny Eliseyev on 23/04/2018.
 */
interface Api {
    @GET("projects.json")
    fun getProjects(@Query("status") status: String = "ALL"): Single<ProjectsResponse>

    @GET("projects/{id}.json")
    fun getProject(
        @Path("id") id: Long
    ): Single<ProjectResponse>
}