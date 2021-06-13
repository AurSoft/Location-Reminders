package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

     // Executes each task synchronously using Architecture Components.
    // Useful in testing LiveDatas
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource

    // Subject under test
    private lateinit var viewModel: RemindersListViewModel

    @Before
    fun setupViewModel() {
        stopKoin()
        dataSource = FakeDataSource()
        val rem1 = ReminderDTO("Title1", "Desc1", "Loc1", 1.0, 0.0)
        val rem2 = ReminderDTO("Title2", "Desc2", "Loc2", 2.0, 0.0)
        val rem3 = ReminderDTO("Title3", "Desc3", "Loc3", 3.0, 0.0)
        dataSource.addReminders(rem1, rem2, rem3)
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun loadReminders_showSnackBarWhenGetReminderResultIsError() = runBlockingTest {
        // When something goes wrong in loading reminders
        dataSource.doReturnError(true)
        viewModel.loadReminders()

        // Then showSnackBar value fills with the error that will be shown to the user
        assertThat(viewModel.showSnackBar.getOrAwaitValue(), `is`("Test exception"))
    }

    @Test
    fun loadReminders_showNoDataIfNoReminderExists() = runBlockingTest {
        // Given an empty repository
        dataSource.deleteAllReminders()

        // When loading reminders
        viewModel.loadReminders()

        // Then no data will be shown to the user
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun loadReminders_remindersListContainsThreeReminders() = runBlockingTest {
        // Given three reminders (in setupViewModel)
        // When loading these reminders
        viewModel.loadReminders()

        // Then remindersList will have a list of tree reminders as its value, each of them having a title
        assertThat(viewModel.remindersList.getOrAwaitValue().count(), `is`(3))
        assertThat(viewModel.remindersList.getOrAwaitValue()[0].title, `is`("Title1"))
        assertThat(viewModel.remindersList.getOrAwaitValue()[1].title, `is`("Title2"))
        assertThat(viewModel.remindersList.getOrAwaitValue()[2].title, `is`("Title3"))
    }

    @Test
    fun loadReminder_checkLoading() = runBlockingTest {
        // Given three reminders (in setupViewModel)

        // When loading reminders
        // (we pause the dispatcher before calling loadReminders to be sure the progress bar is showing,
        // that is showLoading is true)
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        // Then the progress bar is showing
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        // When reminders finish loading
        mainCoroutineRule.resumeDispatcher()

        // Then the progress bar isn't showing anymore
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }
}