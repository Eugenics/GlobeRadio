package com.eugenics.freeradio.util

import android.net.ConnectivityManager
import com.eugenics.freeradio.ui.util.InternetConnectivityListener

fun ConnectivityManager.createInternetConnectivityListener():InternetConnectivityListener =
    InternetConnectivityListener(connectionManager = this)