package com.leveloper.compose.fastscroller

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged

@Composable
fun FastScrollableLazyColumn(
    knobContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    state: FastScrollbarState = rememberFastScrollbarState(),
    content: LazyListScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .onSizeChanged {
                state.viewportSize = it.height.toFloat()
            }
    ) {
        LazyColumn(
            content = content,
            state = state.listState
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .onSizeChanged { state.knobSize = it.height.toFloat() }
                .graphicsLayer { translationY = state.offsetY }
                .composed {
                    val alpha by animateFloatAsState(
                        targetValue = state.alpha,
                        animationSpec = tween(
                            delayMillis = state.animationDelayMs,
                            durationMillis = state.animationDurationMs
                        )
                    )
                    alpha(alpha)
                }
        ) {
            knobContent()
        }
    }
}
