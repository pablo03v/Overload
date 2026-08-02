package cloud.pablos.overload.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import cloud.pablos.overload.data.OverloadDatabase
import cloud.pablos.overload.data.category.CategoryViewModel
import cloud.pablos.overload.data.item.ItemViewModel
import cloud.pablos.overload.ui.tabs.configurations.handleIntent
import cloud.pablos.overload.ui.tabs.configurations.importJsonFile
import cloud.pablos.overload.ui.tabs.configurations.showImportFailedToast
import cloud.pablos.overload.ui.theme.OverloadTheme
import com.google.accompanist.adaptive.calculateDisplayFeatures
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.activity.result.contract.ActivityResultContracts



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var db: OverloadDatabase

    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val itemViewModel by viewModels<ItemViewModel>()



    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
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
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        window.isNavigationBarContrastEnforced = false



        val screenLayoutSize =
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if (screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_SMALL || screenLayoutSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }

        setContent {
            OverloadTheme {
                val windowSize = calculateWindowSizeClass(this)
                val displayFeatures = calculateDisplayFeatures(this)

                val categoryState by categoryViewModel.state.collectAsState()
                val categoryEvent = categoryViewModel::categoryEvent

                val itemState by itemViewModel.state.collectAsState()
                val itemEvent = itemViewModel::itemEvent

                OverloadApp(
                    windowSize,
                    displayFeatures,
                    categoryState,
                    categoryEvent,
                    itemState,
                    itemEvent,
                    filePickerLauncher,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        handleIntent(intent, lifecycleScope, db, this, contentResolver)
        intent = null
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
