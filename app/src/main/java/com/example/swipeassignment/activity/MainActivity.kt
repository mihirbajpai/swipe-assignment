package com.example.swipeassignment.activity
//Mihir Bajpai

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.swipeassignment.network.NetworkChangeReceiver
import com.example.swipeassignment.R
import com.example.swipeassignment.fragments.GetData

class MainActivity : AppCompatActivity() {
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var connectivityStatusReceiver: BroadcastReceiver
    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        networkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(
            networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        connectivityStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isConnected = intent?.getBooleanExtra("is_connected", false) ?: false
                if (!isConnected) {
                    Toast.makeText(this@MainActivity, "You are offline.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "You are online.", Toast.LENGTH_SHORT).show()
                    loadFragment()
                }
            }
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(connectivityStatusReceiver, IntentFilter("network_status"))
    }

    private fun loadFragment() {
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GetData())
                .commit()
        }
    }
}
