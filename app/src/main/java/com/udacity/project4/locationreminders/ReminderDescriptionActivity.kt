package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    private val viewModel: ReminderDescriptionViewModel by viewModel()
    private lateinit var geofencingClient: GeofencingClient

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        val reminderDataItem = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem
        binding.reminderDataItem = reminderDataItem
        geofencingClient = LocationServices.getGeofencingClient(this)
        binding.buttonRemoveReminder.setOnClickListener {
            geofencingClient.removeGeofences(listOf(reminderDataItem.id))?.run {
                addOnSuccessListener {
                    viewModel.removeReminder(reminderDataItem)
                }
                addOnFailureListener {
                    Toast.makeText(this@ReminderDescriptionActivity, "Could not remove geofence",
                            Toast.LENGTH_SHORT).show()
                    if ((it.message != null)) {
                        Log.w("ReminderDescriptionAct", it.message!!)
                    }
                }
            }
        }
        viewModel.reminderRemoved.observe(this, Observer { removed ->
            if(removed) {
                viewModel.geofenceClientRemoved()
                Toast.makeText(this, "Reminder removed",
                        Toast.LENGTH_SHORT).show()
            }
        })
    }
}
