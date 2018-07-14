package com.konaire.teamwork.ui.projects

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.konaire.teamwork.MockApp
import com.konaire.teamwork.R
import com.konaire.teamwork.di.DaggerMockAppComponent
import com.konaire.teamwork.espresso.RecyclerViewItemCountAssertion
import com.konaire.teamwork.models.Project
import com.konaire.teamwork.models.ProjectsResponse
import com.konaire.teamwork.network.Api

import io.reactivex.Single

import org.hamcrest.Matchers.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.Mockito.*

import javax.inject.Inject

/**
 * Created by Evgeny Eliseyev on 26/04/2018.
 */
@RunWith(AndroidJUnit4::class)
class ProjectActivityTest {
    @Rule @JvmField val activityRule: ActivityTestRule<ProjectActivity> = ActivityTestRule(ProjectActivity::class.java, true, false)

    @Inject lateinit var api: Api

    @Before
    fun setUp() {
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MockApp
        (app.component as DaggerMockAppComponent).inject(this)
    }

    @Test
    fun checkWhenEverythingIsFine() {
        val networkResult = ProjectsResponse(listOf(
            Project(), Project(), Project(), Project(), Project(), Project()
        ).toMutableList())

        mockNetwork(networkResult)
        activityRule.launchActivity(Intent())
        onView(withId(R.id.emptyView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.list)).check(RecyclerViewItemCountAssertion.withItemCount(6))
    }

    @Test
    fun checkWhenNetworkCrashes() {
        `when`(api.getProjects()).thenReturn(Single.error(Exception()))

        activityRule.launchActivity(Intent())
        onView(withId(R.id.emptyView)).check(matches(isDisplayed()))
        onView(withId(R.id.list)).check(RecyclerViewItemCountAssertion.withItemCount(0))
    }

    private fun mockNetwork(response: ProjectsResponse) {
        `when`(api.getProjects()).thenReturn(Single.just(response))
    }
}