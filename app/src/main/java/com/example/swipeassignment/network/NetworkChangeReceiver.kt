package com.example.swipeassignment.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val isConnected = isNetworkConnected(context)
            val connectivityIntent = Intent("network_status")
            connectivityIntent.putExtra("is_connected", isConnected)
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(connectivityIntent)
        }
    }

    private fun isNetworkConnected(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
