package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: AutoCloseKoinTest() {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { FakeDataSource() as ReminderDataSource }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }

        repository = get()
        //clear the data to start fresh
        runBlockingTest {
            repository.deleteAllReminders()
        }
    }

    @Test
    fun clickFAB_navigateToAddReminder() {
        // Given empty reminder list
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // When clicking on FAB
        onView(withId(R.id.addReminderFAB)).perform(click())

        //Then we naviigate to SelectLocationFragment
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun addReminder_showReminderOnList() = runBlockingTest {
        // Given a reminder
        val reminderDTO = ReminderDTO("Title1", "Desc1", "Loc1", 1.0, 0.0)
        repository.saveReminder(reminderDTO)

        // When reminder list fragment starts
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)

        // Then the reminder displays on the fragment
        onView(ViewMatchers.withText(reminderDTO.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(reminderDTO.location)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(reminderDTO.description)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyList_showNoDataMsg() {
        // Given no reminder
        // When reminder list fragment starts
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        // Then the user sees the no data icon
        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}