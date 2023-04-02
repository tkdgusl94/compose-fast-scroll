package com.leveloper.compose.fastscroller

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberFastScrollbarState(
    listState: LazyListState = rememberLazyListState(),
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 500,
): FastScrollbarState {
    return remember {
        FastScrollbarState(
            listState = listState,
            fadeInAnimationDurationMs = fadeInAnimationDurationMs,
            fadeOutAnimationDurationMs = fadeOutAnimationDurationMs,
            fadeOutAnimationDelayMs = fadeOutAnimationDelayMs
        )
    }
}

@Stable
class FastScrollbarState(
    val listState: LazyListState,
    val fadeInAnimationDurationMs: Int,
    val fadeOutAnimationDurationMs: Int,
    val fadeOutAnimationDelayMs: Int,
) {
    internal var viewportSize by mutableStateOf(0f)

    internal var knobSize by mutableStateOf(0f)

    private val firstItemSize by derivedStateOf {
        listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
    }

    val offsetY by derivedStateOf {
        val estimatedFullListSize = firstItemSize * listState.layoutInfo.totalItemsCount

        val viewportOffsetInFullListSpace =
            listState.firstVisibleItemIndex * firstItemSize + listState.firstVisibleItemScrollOffset

        val factor =
            viewportOffsetInFullListSpace / (estimatedFullListSize - viewportSize) * knobSize

        (viewportSize / (estimatedFullListSize - viewportSize)) * viewportOffsetInFullListSpace - factor
    }

    val alpha by derivedStateOf {
        if (listState.isScrollInProgress) {
            1f
        } else {
            0f
        }
    }

    val animationDelayMs by derivedStateOf {
        if (listState.isScrollInProgress) {
            0
        } else {
            fadeOutAnimationDelayMs
        }
    }

    val animationDurationMs by derivedStateOf {
        if (listState.isScrollInProgress) {
            fadeInAnimationDurationMs
        } else {
            fadeOutAnimationDurationMs
        }
    }
}
