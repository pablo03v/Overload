package cloud.pablos.overload.ui.tabs.configurations


import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Copyright
import androidx.compose.material.icons.rounded.EmojiNature
import androidx.compose.material.icons.rounded.PestControl
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Unarchive
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavHostController
import androidx.room.withTransaction
import cloud.pablos.overload.R
import cloud.pablos.overload.data.Backup
import cloud.pablos.overload.data.OverloadDatabase
import cloud.pablos.overload.data.category.Category
import cloud.pablos.overload.data.category.CategoryEvent
import cloud.pablos.overload.data.category.CategoryState
import cloud.pablos.overload.data.item.Item
import cloud.pablos.overload.data.item.ItemEvent
import cloud.pablos.overload.data.item.ItemState
import cloud.pablos.overload.ui.MainActivity
import cloud.pablos.overload.ui.navigation.OverloadRoute
import cloud.pablos.overload.ui.navigation.OverloadTopAppBar
import cloud.pablos.overload.ui.views.AddCategoryButton
import cloud.pablos.overload.ui.views.TextView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ConfigurationsTab(
    categoryState: CategoryState,
    categoryEvent: (CategoryEvent) -> Unit,
    itemState: ItemState,
    itemEvent: (ItemEvent) -> Unit,
    filePickerLauncher: ActivityResultLauncher<Intent>,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val acraEnabledKey = "acra.enable"
    val acraSysLogsEnabledKey = "acra.syslog.enable"

    val acraEnabled = sharedPreferences.getBoolean(acraEnabledKey, true)
    val acraSysLogsEnabled = sharedPreferences.getBoolean(acraSysLogsEnabledKey, true)

    val acraEnabledState =
        remember(acraEnabled) {
            mutableStateOf(sharedPreferences.getBoolean(acraEnabledKey, true))
        }

    val acraSysLogsEnabledState =
        remember(acraSysLogsEnabled) {
            mutableStateOf(sharedPreferences.getBoolean(acraSysLogsEnabledKey, true))
        }

    val createCategoryDialog = remember { mutableStateOf(false) }
    val backupDialog = remember { mutableStateOf(false) }

    val saveFileLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json"),
        ) { uri: Uri? ->
            uri?.let {
                saveBackupToUri(it, context, categoryState, itemState)
            }
        }

    Scaffold(
        topBar = { OverloadTopAppBar(OverloadRoute.CONFIGURATIONS, categoryState, categoryEvent, itemState, itemEvent) },
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Categories Title
            item {
                ConfigurationsTabItem(stringResource(R.string.categories))
            }

            categoryState.categories.forEach { category ->
                item {
                    ConfigurationsTabItem(
                        category.emoji + " " + category.name,
                        action = {
                            categoryEvent(CategoryEvent.SetSelectedCategoryConfigurations(category.id))
                            navController.navigate(OverloadRoute.CATEGORY)
                        },
                        background = true,
                    )
                }
            }

            item {
                AddCategoryButton { createCategoryDialog.value = true }
            }

            // Categories Divider
            item {
                HoDivider()
            }

            // Analytics Title
            item {
                ConfigurationsTabItem(stringResource(R.string.analytics))
            }

            // Analytics Crash Reports
            item {
                ConfigurationsTabItem(
                    stringResource(R.string.crash_reports),
                    stringResource(R.string.crash_reports_descr),
                    Icons.Rounded.BugReport,
                    preferenceKey = acraEnabledKey,
                    switchState = acraEnabledState,
                )
            }

            // Analytics System Logs
            item {
                AnimatedVisibility(
                    acraEnabledState.value,
                    enter = expandIn(),
                    exit = shrinkOut(),
                ) {
                    ConfigurationsTabItem(
                        stringResource(R.string.system_logs),
                        stringResource(R.string.system_logs_descr),
                        Icons.Rounded.PestControl,
                        preferenceKey = acraSysLogsEnabledKey,
                        switchState = acraSysLogsEnabledState,
                    )
                }
            }

            // Analytics Divider
            item {
                HoDivider()
            }

            // Storage Title
            item {
                ConfigurationsTabItem(stringResource(R.string.storage))
            }

            item {
                ConfigurationsTabItem(
                    stringResource(R.string.backup),
                    stringResource(R.string.backup_descr),
                    Icons.Rounded.Archive,
                    action = { backupDialog.value = true },
                )
            }

            item {
                ConfigurationsTabItem(
                    stringResource(R.string.import_ol),
                    stringResource(R.string.import_descr),
                    Icons.Rounded.Unarchive,
                    action = { launchFilePicker(filePickerLauncher) },
                )
            }

            // Storage Divider
            item {
                HoDivider()
            }

            // About Title
            item {
                ConfigurationsTabItem(stringResource(R.string.about))
            }

            // About Source Code
            item {
                ConfigurationsTabItem(
                    stringResource(R.string.sourcecode),
                    stringResource(R.string.sourcecode_descr),
                    Icons.Rounded.Code,
                    "https://github.com/pabloscloud/Overload".toUri(),
                )
            }

            // About ITS
            item {
                ConfigurationsTabItem(
                    stringResource(R.string.issue_reports),
                    stringResource(R.string.issue_reports_descr),
                    Icons.Rounded.EmojiNature,
                    "https://github.com/pabloscloud/Overload/issues".toUri(),
                )
            }

            // About Translations
            item {
                ConfigurationsTabItem(
                    stringResource(R.string.translate),
                    stringResource(R.string.translate_descr),
                    Icons.Rounded.Translate,
                    "https://crowdin.com/project/overload".toUri(),
                )
            }

            // About License
            item {
                ConfigurationsTabItem(
                    stringResource(R.string.license),
                    stringResource(R.string.license_descr),
                    Icons.Rounded.Copyright,
                    "https://github.com/pabloscloud/Overload/blob/main/LICENSE".toUri(),
                )
            }

            // About Divider
            item {
                HoDivider()
            }

            // Footer
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    Arrangement.Center,
                ) {
                    ConfigurationDescription(stringResource(R.string.footer))
                }
            }
        }

        if (createCategoryDialog.value) {
            ConfigurationsTabCreateCategoryDialog({ createCategoryDialog.value = false }, categoryEvent)
        }

        if (backupDialog.value) {
            ConfigurationsTabBackupDialog(
                onDismiss = { backupDialog.value = false },
                onSaveToDevice = { saveFileLauncher.launch("backup.json") },
                onShare = { backup(categoryState, itemState, context) },
            )
        }
    }
}

fun backup(
    categoryState: CategoryState,
    itemState: ItemState,
    context: Context,
) {
    try {
        val exportedData = Backup.backupToJson(categoryState, itemState)
        val cachePath = File(context.cacheDir, "backup.json")

        cachePath.writeText(exportedData)

        val contentUri =
            FileProvider.getUriForFile(
                context,
                context.getString(R.string.app_fileprovider),
                cachePath,
            )

        val sendIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_TITLE, "backup.json")
                clipData = ClipData.newUri(context.contentResolver, "backup.json", contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        val shareIntent =
            Intent.createChooser(sendIntent, context.getString(R.string.backup)).apply {
                putExtra("android.intent.extra.AUTO_LAUNCH_SINGLE_CHOICE", false)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.backup_failed), Toast.LENGTH_SHORT)
            .show()
        e.printStackTrace()
    }
}

fun saveBackupToUri(
    uri: Uri,
    context: Context,
    categoryState: CategoryState,
    itemState: ItemState,
) {
    val path = uri.path
    if (path != null) {
        try {
            val normalized = java.nio.file.FileSystems.getDefault().getPath(path).normalize()
            if (normalized.startsWith("/data")) {
                Toast.makeText(context, context.getString(R.string.backup_failed), Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: Exception) {
            Log.w("backup", "Failed to normalize path for URI: $uri", e)
            Toast.makeText(context, context.getString(R.string.backup_failed), Toast.LENGTH_SHORT).show()
            return
        }
    }

    try {
        val exportedData = Backup.backupToJson(categoryState, itemState)
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.bufferedWriter().use { writer ->
                writer.write(exportedData)
            }
            Toast.makeText(context, context.getString(R.string.backup_success), Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, context.getString(R.string.backup_failed), Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Log.e("backup", "Failed to save backup to URI: $uri", e)
        Toast.makeText(context, context.getString(R.string.backup_failed), Toast.LENGTH_SHORT).show()
    }
}

fun launchFilePicker(filePickerLauncher: ActivityResultLauncher<Intent>) {
    val intent =
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
    filePickerLauncher.launch(intent)
}

fun handleIntent(
    intent: Intent?,
    lifecycleScope: LifecycleCoroutineScope,
    db: OverloadDatabase,
    context: Context,
    contentResolver: ContentResolver,
) {
    if (intent != null && intent.action == Intent.ACTION_SEND && intent.type == "text/comma-separated-values") {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (!sharedText.isNullOrBlank()) {
            importCsvData(
                sharedText,
                lifecycleScope,
                db,
                context,
            )
        } else {
            val streamUri = IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
            val clipUri = intent.clipData?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.uri
            val uri = streamUri ?: clipUri
            if (uri != null) {
                importCsvFile(uri, contentResolver, context, db, lifecycleScope)
            } else {
                showImportFailedToast(context)
            }
        }
    }
}

private fun importCsvData(
    csvData: String,
    lifecycleScope: LifecycleCoroutineScope,
    db: OverloadDatabase,
    context: Context,
) {
    val parsedData = parseCsvData(csvData)

    lifecycleScope.launch(Dispatchers.IO) {
        val itemDao = db.itemDao()



        db.withTransaction {
            parsedData.drop(1).forEach { row ->
                if (row.size >= 4) {
                    val startTime = row[1].trim()
                    val endTime = row[2].trim()
                    val ongoing = row[3].trim()
                    val pause = row[4].trim()

                    val item =
                        Item(
                            startTime = startTime,
                            endTime = endTime,
                            ongoing = ongoing.toBoolean(),
                            pause = pause.toBoolean(),
                            categoryId = 1,
                        )

                    itemDao.upsertItem(item)

                }
            }
        }

        withContext(Dispatchers.Main) {
            showImportSuccessToast(context)
        }

    }
}

private fun importJsonData(
    jsonData: String,
    lifecycleScope: LifecycleCoroutineScope,
    db: OverloadDatabase,
    context: Context,
) {
    lifecycleScope.launch(Dispatchers.IO) {
        try {
            val gson = Gson()
            val databaseBackup = gson.fromJson(jsonData, Backup.DatabaseBackup::class.java)

            when (databaseBackup.backupVersion) {
                2 -> {
                    Log.d("import", "Import started")
                    val itemDao = db.itemDao()
                    val categoryDao = db.categoryDao()



                    db.withTransaction {
                        Log.d("import", "Importing items")
                        val itemsTable = databaseBackup.data["items"] ?: emptyList()
                        val itemsToImport = itemsTable.map { itemData ->
                            Item(
                                (itemData["id"] as? Double)?.toInt() ?: 0,
                                itemData["startTime"] as String,
                                itemData["endTime"] as String,
                                itemData["ongoing"] as Boolean,
                                itemData["pause"] as Boolean,
                                (itemData["categoryId"] as? Double)?.toInt() ?: 0,
                            )
                        }
                        itemDao.upsertItems(itemsToImport)

                        Log.d("import", "Importing categories")
                        val categoriesTable = databaseBackup.data["categories"] ?: emptyList()
                        val categoriesToImport = categoriesTable.map { categoriesData ->
                            Category(
                                (categoriesData["id"] as? Double)?.toInt() ?: 0,
                                (categoriesData["color"] as? Double)?.toLong() ?: 0,
                                categoriesData["emoji"] as String,
                                (categoriesData["goal1"] as? Double)?.toInt() ?: 0,
                                (categoriesData["goal2"] as? Double)?.toInt() ?: 0,
                                categoriesData["isDefault"] as Boolean,
                                categoriesData["name"] as String,
                            )
                        }
                        categoryDao.upsertCategories(categoriesToImport)
                    }

                    withContext(Dispatchers.Main) {
                        showImportSuccessToast(context)
                    }

                }
                else -> {
                    withContext(Dispatchers.Main) {
                        showImportFailedToast(context)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("import", e.toString())
            withContext(Dispatchers.Main) {
                showImportFailedToast(context)
            }
        }
    }
}

fun parseCsvData(csvData: String): List<List<String>> {
    val rows = csvData.split("\n")
    return rows.map { row ->
        val separator =
            when {
                row.contains(',') -> ","
                row.contains(';') -> ";"
                else -> ","
            }

        row.split(separator)
    }
}

fun showImportSuccessToast(context: Context) {
    Toast.makeText(context, context.getString(R.string.import_success), Toast.LENGTH_SHORT).show()
}

fun showImportFailedToast(context: Context) {
    Toast.makeText(context, context.getString(R.string.import_failure), Toast.LENGTH_SHORT).show()
}

fun importCsvFile(
    uri: Uri,
    contentResolver: ContentResolver,
    context: Context,
    db: OverloadDatabase,
    lifecycleScope: LifecycleCoroutineScope,
) {
    val path = uri.path
    if (path != null) {
        try {
            val normalized = java.nio.file.FileSystems.getDefault().getPath(path).normalize()
            if (normalized.startsWith("/data")) {
                showImportFailedToast(context)
                return
            }
        } catch (e: Exception) {
            Log.w("import", "Failed to normalize path for URI: $uri", e)
            showImportFailedToast(context)
            return
        }
    }

    val appFileProvider = context.getString(R.string.app_fileprovider)
    if (uri.authority.equals(appFileProvider, ignoreCase = true)) {
        showImportFailedToast(context)
        return
    }

    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val sharedData = inputStream.bufferedReader().readText()

            importCsvData(sharedData, lifecycleScope, db, context)
        } ?: run {
            showImportFailedToast(context)
        }
    } catch (e: Exception) {
        Log.e("import", "Failed to open input stream for URI: $uri", e)
        showImportFailedToast(context)
    }
}

fun importJsonFile(
    uri: Uri,
    contentResolver: ContentResolver,
    context: Context,
    db: OverloadDatabase,
    lifecycleScope: LifecycleCoroutineScope,
) {
    val path = uri.path
    if (path != null) {
        try {
            val normalized = java.nio.file.FileSystems.getDefault().getPath(path).normalize()
            if (normalized.startsWith("/data")) {
                showImportFailedToast(context)
                return
            }
        } catch (e: Exception) {
            Log.w("import", "Failed to normalize path for URI: $uri", e)
            showImportFailedToast(context)
            return
        }
    }

    val appFileProvider = context.getString(R.string.app_fileprovider)
    if (uri.authority.equals(appFileProvider, ignoreCase = true)) {
        showImportFailedToast(context)
        return
    }

    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val sharedData = inputStream.bufferedReader().readText()

            importJsonData(sharedData, lifecycleScope, db, context)
        } ?: run {
            showImportFailedToast(context)
        }
    } catch (e: Exception) {
        Log.e("import", "Failed to open input stream for URI: $uri", e)
        showImportFailedToast(context)
    }
}

@Composable
fun ConfigurationLabel(text: String) {
    TextView(
        text,
        Modifier.padding(vertical = 15.dp),
        MaterialTheme.typography.titleLarge.fontSize,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
fun ConfigurationTitle(
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    TextView(
        text,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        color = color,
    )
}

@Composable
fun ConfigurationDescription(text: String) {
    Text(
        text,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
fun HoDivider() {
    HorizontalDivider(Modifier.padding(top = 20.dp))
}


