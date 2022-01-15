package com.khairy.user_list.view

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.khairy.user_list.R
import kotlinx.coroutines.InternalCoroutinesApi
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@InternalCoroutinesApi
class UserListFragmentTest {

    @Test
    fun test_UserListIsVisible() {
        val launchFragmentInContainer =
            launchFragmentInContainer<UserListFragment>()
        onView(withId(R.id.userListRecycleView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_openDetails() {
        val launchFragmentInContainer =
            launchFragmentInContainer<UserListFragment>()

        onView(withId(R.id.userListRecycleView))
            .perform(actionOnItemAtPosition<UserListAdapter.UserHolder>(1, click()))

        onView(withId(R.id.userListRecycleView))
            .check(matches(not(isDisplayed())))

    }

}