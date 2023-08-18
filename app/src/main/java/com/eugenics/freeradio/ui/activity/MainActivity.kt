package com.eugenics.freeradio.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eugenics.freeradio.BuildConfig
import com.eugenics.freeradio.R
import com.eugenics.freeradio.ui.application.Application
import com.eugenics.freeradio.ui.util.UICommands
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val permissionsRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            for (permission in permissions) {
                if (!permission.value) {
                    Log.d(TAG, "${permission.key} has denied...")
                }
            }
        }

    private val filePickLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { uri ->
                Log.d(TAG, uri.toString())
                val file = contentResolver.openInputStream(uri)
                file?.let { inputStream ->
                    val json = String(inputStream.readBytes())
                    mainViewModel.restoreFavorites(favoritesJsonString = json)
                    Log.d(TAG, json)
                    file.close()
                }
            }
        }

    private val startShare =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null) {
                when (result.resultCode) {
                    Activity.RESULT_OK -> Toast.makeText(
                        applicationContext,
                        getString(R.string.saved_text),
                        Toast.LENGTH_LONG
                    ).show()

                    else -> Toast.makeText(
                        applicationContext,
                        getString(R.string.not_saved_text),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isFits = this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        Log.d(TAG, "Landscape orientation=$isFits")
        WindowCompat.setDecorFitsSystemWindows(window, isFits)

        checkPostNotificationPermission()
        checkIntentData()
        collectUICommands()
        collectViewModelMessages()

        mainViewModel.start()
        mainViewModel.getTagsList(context = applicationContext)

        setContent {
            Application(viewModel = mainViewModel)
        }
    }

    private fun collectUICommands() {
        lifecycleScope.launch(Dispatchers.Default) {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                mainViewModel.uiCommands.collect { command ->
                    when (command) {
                        UICommands.UI_COMMAND_BACKUP_FAVORITES -> backUpFavorites()
                        UICommands.UI_COMMAND_RESTORE_FAVORITES -> filePickLauncher.launch("*/*")
                        else -> {}
                    }
                    mainViewModel.setUICommand(UICommands.UI_COMMAND_IDL)
                }
            }
        }
    }

    private suspend fun backUpFavorites() {
        val jsonData = mainViewModel.backUpData.value
        if (jsonData.isNotBlank()) {
            val jsonFile = File("${applicationContext.cacheDir}/share.json")
            try {
                jsonFile.writeBytes(jsonData.toByteArray(Charsets.UTF_8))
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
            }
            if (jsonFile.exists()) {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val jsonFileUri = FileProvider.getUriForFile(
                        applicationContext,
                        getString(R.string.authority),
                        jsonFile
                    )
                    putExtra(
                        Intent.EXTRA_STREAM,
                        jsonFileUri
                    )
                    type = INTENT_FILE_TYPE
                }

                val shareIntent =
                    Intent.createChooser(
                        sendIntent,
                        getString(R.string.share_dialog_title)
                    )
                startShare.launch(shareIntent)
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.no_data_to_share),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun collectViewModelMessages() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.message.collect { message ->
                    if (message.isNotBlank()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                                .show()
                            mainViewModel.clearMessage()
                        }
                    }
                }
            }
        }
    }

    private fun hasExtStoragePermissions(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            false
        }

    private fun callExtStoragePermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                val appUri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")

                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        appUri
                    )
                )
            }

            else -> {
                permissionsRequestLauncher.launch(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun checkIntentData() {
        if (intent.data != null) {
            if (hasExtStoragePermissions()) {
                intent.data?.let {
                    Log.d(TAG, it.path ?: "No path...")
                }
            } else {
                Log.d(TAG, "Grant permission and try again...")
                callExtStoragePermissions()
            }
        }
    }

    private fun checkPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsRequestLauncher.launch(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            )
        }
    }

    companion object {
        private const val TAG = "MAIN_ACTIVITY"
        private const val INTENT_FILE_TYPE = "file/json"
    }
}