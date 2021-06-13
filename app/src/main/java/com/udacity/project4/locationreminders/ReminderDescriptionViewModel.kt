package com.udacity.project4.locationreminders

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch

class ReminderDescriptionViewModel(
        app: Application,
        private val dataSource: ReminderDataSource
) : BaseViewModel(app) {

    val reminderRemoved = MutableLiveData<Boolean>()
    fun removeReminder(reminderDataItem: ReminderDataItem) {
        viewModelScope.launch {
            dataSource.deleteReminder(reminderDataItem.id)
            reminderRemoved.value = true
        }
    }

    fun geofenceClientRemoved() {
        reminderRemoved.value = false
    }
}