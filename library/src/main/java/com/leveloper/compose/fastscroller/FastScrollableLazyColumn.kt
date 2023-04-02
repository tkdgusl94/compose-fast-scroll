package com.leveloper.compose.fastscroller

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged

@Composable
fun FastScrollableLazyColumn(
    state: FastScrollbarState,
    knobContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .onSizeChanged { state.viewportSize = it.height.toFloat() }
    ) {
        LazyColumn(
            state = state.listState,
            content = content
        )
        FastScroller(
            state = state,
            knobContent = knobContent,
            modifier = Modifier
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun FastScroller(
    state: FastScrollbarState,
    knobContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, offset ->
                        change.consume()
                        state.onDrag(offset)
                    },
                    onDragStart = state::onDragStart,
                    onDragCancel = state::onDragInterrupted,
                    onDragEnd = state::onDragInterrupted
                )
            }
    ) {
        Box(
            modifier = Modifier
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
                    val showKnob by derivedStateOf { alpha == 1f }
                    state.showKnob = showKnob

                    alpha(alpha)
                }
        ) {
            knobContent()
        }
    }
}
