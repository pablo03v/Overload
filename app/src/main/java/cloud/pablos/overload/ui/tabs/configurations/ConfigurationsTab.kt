package cloud.pablos.overload.ui.tabs.configurations

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Copyright
import androidx.compose.material.icons.rounded.EmojiNature
import androidx.compose.material.icons.rounded.PestControl
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Unarchive
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.content.edit
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
import cloud.pablos.overload.ui.views.TextView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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
    val workGoalDialogState = remember { mutableStateOf(false) }
    val pauseGoalDialogState = remember { mutableStateOf(false) }

    val color = MaterialTheme.colorScheme.onSurfaceVariant.toString()

    val categories = categoryState.categories
    LaunchedEffect(categories) {
        if (categories.isEmpty()) {
            categoryEvent(CategoryEvent.SetId(0))
            categoryEvent(CategoryEvent.SetName("Default"))
            categoryEvent(CategoryEvent.SetColor(color))
            categoryEvent(CategoryEvent.SetEmoji("🕣"))
            categoryEvent(CategoryEvent.SetIsDefault(true))
            categoryEvent(CategoryEvent.SaveCategory)
        }
    }

    Scaffold(
        topBar = {
            OverloadTopAppBar(
                selectedDestination = OverloadRoute.CONFIGURATIONS,
                categoryState = categoryState,
                itemState = itemState,
                itemEvent = itemEvent,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            /*
            // Goals Title
            item {
                ConfigurationsTabItem(title = stringResource(id = R.string.goals))
            }

            // Work Goal
            item {
                val itemLabel =
                    stringResource(id = R.string.work) + ": " + stringResource(id = R.string.work_goal_descr)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .clickable {
                                workGoalDialogState.value = true
                            }
                            .clearAndSetSemantics {
                                contentDescription = itemLabel
                            },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Work,
                        contentDescription = stringResource(id = R.string.work),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            ConfigurationTitle(stringResource(id = R.string.work))
                            ConfigurationDescription(stringResource(id = R.string.work_goal_descr))
                        }
                    }
                }
            }

            // Pause Goal
            item {
                val itemLabel =
                    stringResource(id = R.string.pause) + ": " + stringResource(id = R.string.pause_goal_descr)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .clickable {
                                pauseGoalDialogState.value = true
                            }
                            .clearAndSetSemantics {
                                contentDescription = itemLabel
                            },
                ) {
                    Icon(
                        imageVector = Icons.Filled.DarkMode,
                        contentDescription = stringResource(id = R.string.pause),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            ConfigurationTitle(stringResource(id = R.string.pause))
                            ConfigurationDescription(stringResource(id = R.string.pause_goal_descr))
                        }
                    }
                }
            }

            // Goals Divider
            item {
                Row {
                    HorizontalDivider()
                }
            }*/

            // Categories Title
            item {
                ConfigurationsTabItem(title = stringResource(id = R.string.categories))
            }

            categoryState.categories.forEach { category ->
                item {
                    ConfigurationsTabItem(
                        title = category.emoji + " " + category.name,
                        action = {
                            categoryEvent(CategoryEvent.SetSelectedCategoryConfigurations(category.id))
                            navController.navigate(OverloadRoute.CATEGORY)
                        },
                        background = true,
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                ) {
                    FilledTonalButton(onClick = {
                        if (categoryState.categories.isEmpty()) {
                            categoryEvent(CategoryEvent.SetId(1))
                            categoryEvent(CategoryEvent.SetName("Default"))
                            categoryEvent(CategoryEvent.SetColor(color))
                            categoryEvent(CategoryEvent.SetEmoji("🕣"))
                            categoryEvent(CategoryEvent.SetIsDefault(true))
                            categoryEvent(CategoryEvent.SaveCategory)
                        } else {
                            createCategoryDialog.value = true
                        }
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = stringResource(id = R.string.select_year),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            TextView(
                                text = "add category",
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }

            // Categories Divider
            item {
                Row {
                    HorizontalDivider()
                }
            }

            // Analytics Title
            item {
                ConfigurationsTabItem(title = stringResource(id = R.string.analytics))
            }

            // Analytics Crash Reports
            item {
                ConfigurationsTabItem(
                    title = stringResource(id = R.string.crash_reports),
                    description = stringResource(id = R.string.crash_reports_descr),
                    preferenceKey = acraEnabledKey,
                    switchState = acraEnabledState,
                    icon = Icons.Rounded.BugReport,
                )
            }

            // Analytics System Logs
            item {
                AnimatedVisibility(
                    visible = acraEnabledState.value,
                    enter = expandIn(),
                    exit = shrinkOut(),
                ) {
                    ConfigurationsTabItem(
                        title = stringResource(id = R.string.system_logs),
                        description = stringResource(id = R.string.system_logs_descr),
                        preferenceKey = acraSysLogsEnabledKey,
                        switchState = acraSysLogsEnabledState,
                        icon = Icons.Rounded.PestControl,
                    )
                }
            }

            // Analytics Divider
            item {
                Row {
                    HorizontalDivider()
                }
            }

            // Storage Title
            item {
                ConfigurationsTabItem(title = stringResource(id = R.string.storage))
            }

            item {
                ConfigurationsTabItem(
                    title = stringResource(id = R.string.backup),
                    description = stringResource(id = R.string.backup_descr),
                    icon = Icons.Rounded.Archive,
                    action = { backup(categoryState, itemState, context) },
                )
            }

            item {
                ConfigurationsTabItem(
                    title = stringResource(id = R.string.import_ol),
                    description = stringResource(id = R.string.import_descr),
                    icon = Icons.Rounded.Unarchive,
                    action = { launchFilePicker(filePickerLauncher) },
                )
            }

            // Storage Divider
            item {
                Row {
                    HorizontalDivider()
                }
            }

            // About Title
            item {
                ConfigurationsTabItem(title = stringResource(id = R.string.about))
            }

            // About Source Code
            item {
                ConfigurationsTabItem(
                    title = stringResource(id = R.string.sourcecode),
                    description = stringResource(id = R.string.sourcecode_descr),
                    link = "https://codeberg.org/pabloscloud/Overload".toUri(),
                    icon = Icons.Rounded.Code,
                )
            }

            // About ITS
            item {
                ConfigurationsTabItem(
                    title = stringResource(id = R.string.issue_reports),
                    description = stringResource(id = R.string.issue_reports_descr),
                    link = "https://codeberg.org/pabloscloud/Overload/issues".toUri(),
                    icon = Icons.Rounded.EmojiNature,
                )
            }

            // About Translations
            item {
                ConfigurationsTabItem(
                    title = stringResource(id = R.string.translate),
                    description = stringResource(id = R.string.translate_descr),
                    link = "https://translate.codeberg.org/engage/overload".toUri(),
                    icon = Icons.Rounded.Translate,
                )
            }

            // About License
            item {
                ConfigurationsTabItem(
                    title = stringResource(id = R.string.license),
                    description = stringResource(id = R.string.license_descr),
                    link = "https://codeberg.org/pabloscloud/Overload/raw/branch/main/LICENSE".toUri(),
                    icon = Icons.Rounded.Copyright,
                )
            }

            // About Divider
            item {
                Row {
                    HorizontalDivider()
                }
            }

            // Footer
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                ) {
                    ConfigurationDescription(stringResource(id = R.string.footer))
                }
            }
        }

        if (createCategoryDialog.value) {
            ConfigurationsTabCreateCategoryDialog(
                onClose = { createCategoryDialog.value = false },
                categoryEvent = categoryEvent,
            )
        }

        if (workGoalDialogState.value) {
            ConfigurationsTabGoalDialog(
                onClose = { workGoalDialogState.value = false },
                isPause = false,
            )
        }

        if (pauseGoalDialogState.value) {
            ConfigurationsTabGoalDialog(
                onClose = { pauseGoalDialogState.value = false },
                isPause = true,
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

        val sendIntent: Intent =
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "application/json"
            }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.backup_failed), Toast.LENGTH_SHORT)
            .show()
        e.printStackTrace()
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
        if (intent.getStringExtra(Intent.EXTRA_TEXT)?.isBlank() == false) {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (sharedText != null) {
                importCsvData(
                    sharedText,
                    lifecycleScope,
                    db,
                    context,
                )
            }
        } else if (intent.getStringExtra(Intent.EXTRA_STREAM)?.isBlank() == false) {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            uri?.let {
                importCsvFile(uri, contentResolver, context, db, lifecycleScope)
            }
        } else if (intent.clipData != null) {
            val uri = intent.clipData?.getItemAt(0)?.uri
            if (uri != null) {
                importCsvFile(uri, contentResolver, context, db, lifecycleScope)
            } else {
                showImportFailedToast(context)
            }
        } else {
            showImportFailedToast(context)
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

        var allImportsSucceeded = true

        db.withTransaction {
            parsedData.drop(1).forEach { row ->
                if (row.size >= 4) {
                    val startTime = row[1].trim()
                    val endTime = row[2].trim()
                    val ongoing = row[3].trim()
                    val pause = row[4].trim()
                    val categoryId = row[5].trim()

                    val item =
                        Item(
                            startTime = startTime,
                            endTime = endTime,
                            ongoing = ongoing.toBoolean(),
                            pause = pause.toBoolean(),
                            categoryId = categoryId.toInt(),
                        )

                    val importResult = itemDao.upsertItem(item)
                    if (importResult != Unit) {
                        allImportsSucceeded = false
                    }
                }
            }
        }

        withContext(Dispatchers.Main) {
            if (allImportsSucceeded) {
                showImportSuccessToast(context)
                restartApp(context)
            } else {
                showImportFailedToast(context)
            }
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

                    var allImportsSucceeded = true

                    db.withTransaction {
                        Log.d("import", "Importing items")
                        val itemsTable = databaseBackup.data["items"] ?: emptyList()
                        itemsTable.forEach { itemData ->
                            val item =
                                Item(
                                    id = (itemData["id"] as? Double)?.toInt() ?: 0,
                                    startTime = itemData["startTime"] as String,
                                    endTime = itemData["endTime"] as String,
                                    ongoing = itemData["ongoing"] as Boolean,
                                    pause = itemData["pause"] as Boolean,
                                    categoryId = (itemData["categoryId"] as? Double)?.toInt() ?: 0,
                                )

                            Log.d("import", "Importing Item: $item")

                            val importResult = itemDao.upsertItem(item)
                            if (importResult != Unit) {
                                allImportsSucceeded = false
                            }
                        }

                        Log.d("import", "Importing categories")
                        val categoriesTable = databaseBackup.data["categories"] ?: emptyList()
                        categoriesTable.forEach { categoriesData ->
                            val category =
                                Category(
                                    id = (categoriesData["id"] as? Double)?.toInt() ?: 0,
                                    color = categoriesData["color"] as String,
                                    emoji = categoriesData["emoji"] as String,
                                    isDefault = categoriesData["isDefault"] as Boolean,
                                    name = categoriesData["name"] as String,
                                )

                            Log.d("import", "Importing Category: $category")

                            val importResult = categoryDao.upsertCategory(category)
                            if (importResult != Unit) {
                                allImportsSucceeded = false
                            }
                        }
                        Log.d("import", "Import done")
                    }

                    withContext(Dispatchers.Main) {
                        if (allImportsSucceeded) {
                            showImportSuccessToast(context)
                            restartApp(context)
                        } else {
                            showImportFailedToast(context)
                        }
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
    uri.let {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val sharedData = inputStream.bufferedReader().readText()

            importCsvData(sharedData, lifecycleScope, db, context)
        }
    }
}

fun importJsonFile(
    uri: Uri,
    contentResolver: ContentResolver,
    context: Context,
    db: OverloadDatabase,
    lifecycleScope: LifecycleCoroutineScope,
) {
    uri.let {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val sharedData = inputStream.bufferedReader().readText()

            importJsonData(sharedData, lifecycleScope, db, context)
        }
    }
}

@Composable
fun ConfigurationLabel(text: String) {
    TextView(
        text = text,
        fontSize = MaterialTheme.typography.titleLarge.fontSize,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(vertical = 15.dp),
    )
}

@Composable
fun ConfigurationTitle(
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    TextView(
        text = text,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        color = color,
    )
}

@Composable
fun ConfigurationDescription(text: String) {
    Text(
        text = text,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

class OlSharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("ol_prefs", Context.MODE_PRIVATE)

    fun saveWorkGoal(goal: Int) {
        sharedPreferences.edit { putInt("workGoal", goal) }
    }

    fun savePauseGoal(goal: Int) {
        sharedPreferences.edit { putInt("pauseGoal", goal) }
    }

    fun getWorkGoal(): Int = sharedPreferences.getInt("workGoal", 0)

    fun getPauseGoal(): Int = sharedPreferences.getInt("pauseGoal", 0)
}

fun restartApp(context: Context) {
    val intent = Intent(context.applicationContext, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
    if (context is Activity) {
        context.finish()
    }
}
