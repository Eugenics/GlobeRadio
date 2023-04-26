package com.eugenics.freeradio.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.eugenics.core.enums.Theme
import com.eugenics.freeradio.R
import com.eugenics.freeradio.navigation.NavGraph
import com.eugenics.freeradio.ui.theme.FreeRadioTheme
import com.eugenics.freeradio.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

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

        WindowCompat.setDecorFitsSystemWindows(window, false)

        collectShareData()
        collectViewModelMessages()

        setContent {
            val theme = mainViewModel.settings.collectAsState().value.theme
            FreeRadioTheme(
                useDarkTheme =
                when (theme) {
                    Theme.DARK -> true
                    Theme.LIGHT -> false
                    else -> isSystemInDarkTheme()
                }
            ) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
        }
    }

    private fun collectShareData() {
        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                mainViewModel.saveData.collect { jsonData ->
                    if (jsonData.isNotBlank()) {
                        val jsonFile = File("${applicationContext.filesDir}/share.json")
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
                                    applicationContext,
                                    getString(R.string.no_data_to_share),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
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
                            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MAIN_ACTIVITY"
        private const val INTENT_FILE_TYPE = "file/json"
    }
}