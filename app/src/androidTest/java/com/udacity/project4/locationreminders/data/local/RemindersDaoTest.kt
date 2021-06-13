package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.runner.RunWith;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        stopKoin()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        // Given a reminder saved in the db
        val reminderDTO = ReminderDTO("Title1", "Desc1", "Loc1", 1.0, 0.0)
        database.reminderDao().saveReminder(reminderDTO)

        // When getting the reminder by id from the database
        val loaded = database.reminderDao().getReminderById(reminderDTO.id)

        // Then the loaded reminder contains the expected values
        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminderDTO.id))
        assertThat(loaded.title, `is`(reminderDTO.title))
        assertThat(loaded.description, `is`(reminderDTO.description))
        assertThat(loaded.location, `is`(reminderDTO.location))
        assertThat(loaded.latitude, `is`(reminderDTO.latitude))
        assertThat(loaded.longitude, `is`(reminderDTO.longitude))
    }
}