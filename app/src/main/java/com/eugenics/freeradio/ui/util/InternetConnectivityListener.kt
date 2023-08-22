package com.eugenics.freeradio.ui.util

import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InternetConnectivityListener(private val connectionManager: ConnectivityManager) {

    private val _isActive: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isActive: StateFlow<Boolean> = _isActive
    private val networks: MutableSet<Int> = mutableSetOf()

    val networkCallback: ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networks.add(network.hashCode())
                checkActive()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networks.remove(network.hashCode())
                checkActive()
            }

            private fun checkActive() {
                _isActive.value = networks.isNotEmpty()
            }
        }

    companion object {
        private const val TAG = "InternetListener"
    }
}