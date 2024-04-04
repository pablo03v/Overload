package cloud.pablos.overload.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import cloud.pablos.overload.R
import cloud.pablos.overload.data.item.ItemDatabase
import cloud.pablos.overload.data.item.ItemViewModel
import cloud.pablos.overload.ui.tabs.configurations.handleIntent
import cloud.pablos.overload.ui.tabs.configurations.importJsonFile
import cloud.pablos.overload.ui.tabs.configurations.showImportFailedToast
import cloud.pablos.overload.ui.theme.OverloadTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ItemDatabase::class.java,
            "items",
        ).build()
    }

    private val viewModel by viewModels<ItemViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ItemViewModel(db.itemDao()) as T
                }
            }
        },
    )

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    lifecycleScope.launch {
                        importJsonFile(it, contentResolver, applicationContext, db, lifecycleScope)
                    }
                } ?: run {
                    showImportFailedToast(applicationContext)
                }
            } else {
                showImportFailedToast(applicationContext)
            }
        }

    @SuppressLint("SourceLockedOrientationActivity") // The text of dialogs does not fit the screen when not in portrait
    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Overload)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val screenLayoutSize =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_SMALL || screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }

        setContent {
            OverloadTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)

                val state by viewModel.state.collectAsState()
                val onEvent = viewModel::onEvent

                OverloadApp(
                    windowSize = windowSize,
                    displayFeatures = displayFeatures,
                    state = state,
                    onEvent = onEvent,
                    filePickerLauncher = filePickerLauncher,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        handleIntent(intent, lifecycleScope, db, this, contentResolver)
        intent = null
    }
}
