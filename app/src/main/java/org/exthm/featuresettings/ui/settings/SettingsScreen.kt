package org.exthm.featuresettings.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.exthm.featuresettings.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureSettingsScreen(viewModel: SettingsViewModel) {
    val lockscreenDimEnabled by viewModel.lockscreenDimEnabled.collectAsStateWithLifecycle()
    val launcherBlurEnabled by viewModel.launcherBlurEnabled.collectAsStateWithLifecycle()
    val screenOcrEnabled by viewModel.screenOcrEnabled.collectAsStateWithLifecycle()
    val screenOcrHighValue by viewModel.screenOcrHighValue.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.top_bar_title),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_features_page),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp) 
                    .padding(16.dp)
            )
            Text(
                text = stringResource(R.string.main_hint),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.main_ps),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingItem(
                title = stringResource(R.string.lockscreen_dim_title),
                description = stringResource(R.string.lockscreen_dim_summary),
                isChecked = lockscreenDimEnabled,
                onCheckedChange = { viewModel.onLockscreenDimChanged(it) }
            )

            SettingItem(
                title = stringResource(R.string.launcher_blur_title),
                description = stringResource(R.string.launcher_blur_summary),
                isChecked = launcherBlurEnabled,
                onCheckedChange = { viewModel.onLauncherBlurChanged(it) }
            )

            SettingItem(
                title = stringResource(R.string.screen_ocr_title),
                description = stringResource(R.string.screen_ocr_summary),
                isChecked = screenOcrEnabled,
                onCheckedChange = { viewModel.onScreenOcrChanged(it) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SliderSettingItem(
                title = stringResource(R.string.screen_ocr_high_title),
                value = screenOcrHighValue,
                onValueChange = { viewModel.onScreenOcrHighChanged(it) },
                onValueChangeFinished = { viewModel.onScreenOcrHighChangeFinished(screenOcrHighValue) },
                valueRange = 4f..10f,
                steps = 5,
                enabled = screenOcrEnabled
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp)) 
            .clickable(enabled = enabled) {
                onCheckedChange(!isChecked)
            }
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .alpha(if (enabled) 1f else 0.5f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 3, 
                overflow = TextOverflow.Ellipsis
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}


@Composable
fun SliderSettingItem(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value.roundToInt().toString(), 
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled
        )
    }
}