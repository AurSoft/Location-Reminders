package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {
    var reminderData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private var returnErr = false

    fun doReturnError(returnError: Boolean) {
        returnErr = returnError
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (returnErr) {
            return Result.Error("Test exception")
        }
        return Result.Success(reminderData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (returnErr) {
            return Result.Error("Test exception")
        }
        reminderData[id]?.let {
            return Result.Success(it)
        }
        return Result.Error("Reminder not found!")
    }

    override suspend fun deleteAllReminders() {
        reminderData.clear()
    }

    override suspend fun deleteReminder(id: String) {
        TODO("Not yet implemented")
    }

    fun addReminders(vararg reminders: ReminderDTO) {
        for (reminder in reminders){
            reminderData[reminder.id] = reminder
        }
    }
}