package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()

    // runBlocking is used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
    @Test
    fun saveReminder_getReminder() = runBlocking {
        // Given a new reminder saved in the db
        val reminderDTO = ReminderDTO("Title1", "Desc1", "Loc1", 1.0, 0.0)
        repository.saveReminder(reminderDTO)

        // When the reminder is retrieved by its id
        val result = repository.getReminder(reminderDTO.id)

        // Then the same reminder is returned
        result as Result.Success
        assertThat(result.data.id, `is`(reminderDTO.id))
        assertThat(result.data.title, `is`(reminderDTO.title))
        assertThat(result.data.description, `is`(reminderDTO.description))
        assertThat(result.data.location, `is`(reminderDTO.location))
        assertThat(result.data.latitude, `is`(reminderDTO.latitude))
        assertThat(result.data.longitude, `is`(reminderDTO.longitude))
    }

    @Test
    fun deleteReminder_getReminder() = runBlocking {
        // Given a new reminder saved in the db
        val reminderDTO = ReminderDTO("Title1", "Desc1", "Loc1", 1.0, 0.0)
        repository.saveReminder(reminderDTO)

        // When deleting all the reminders
        repository.deleteAllReminders()
        val result = repository.getReminder(reminderDTO.id)

        // Then retrieving the previously saved reminder will return an error
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }
}