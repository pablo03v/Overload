package cloud.pablos.overload.ui.tabs.configurations

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cloud.pablos.overload.R
import cloud.pablos.overload.ui.views.TextView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationsTabTopAppBar() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        TopAppBar(
            {
                TextView(
                    stringResource(R.string.configurations),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
            },
        )
    }
}

@Composable
@Preview
fun ConfigurationsTabTopAppBarPreview() {
    ConfigurationsTabTopAppBar()
}
