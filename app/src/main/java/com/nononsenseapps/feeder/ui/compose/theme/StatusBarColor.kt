package com.nononsenseapps.feeder.ui.compose.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nononsenseapps.feeder.ui.compose.utils.LocalWindowSize
import com.nononsenseapps.feeder.ui.compose.utils.WindowSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetStatusBarColorToMatchScrollableTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceScrolledColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

    val colorTransitionFraction = scrollBehavior.state.overlappedFraction
    val fraction = if (colorTransitionFraction > 0.01f) 1f else 0f

    val appBarContainerColor by when (LocalWindowSize()) {
        WindowSize.CompactTall -> {
            rememberUpdatedState(
                lerp(
                    surfaceColor,
                    surfaceScrolledColor,
                    FastOutLinearInEasing.transform(scrollBehavior.state.collapsedFraction)
                )
            )
        }
        else -> {
            animateColorAsState(
                targetValue = lerp(
                    surfaceColor,
                    surfaceScrolledColor,
                    FastOutLinearInEasing.transform(fraction)
                ),
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )
        }
    }

    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, appBarContainerColor) {
        systemUiController.setStatusBarColor(
            appBarContainerColor,
        )
        onDispose {}
    }
}
