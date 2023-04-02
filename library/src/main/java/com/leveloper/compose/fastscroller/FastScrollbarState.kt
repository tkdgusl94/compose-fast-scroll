package com.leveloper.compose.fastscroller

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberFastScrollbarState(
    listState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    fadeInAnimationDurationMs: Int = 150,
    fadeOutAnimationDurationMs: Int = 500,
    fadeOutAnimationDelayMs: Int = 1000,
): FastScrollbarState {
    return remember {
        FastScrollbarState(
            listState = listState,
            coroutineScope = coroutineScope,
            fadeInAnimationDurationMs = fadeInAnimationDurationMs,
            fadeOutAnimationDurationMs = fadeOutAnimationDurationMs,
            fadeOutAnimationDelayMs = fadeOutAnimationDelayMs
        )
    }
}

@Stable
class FastScrollbarState(
    val listState: LazyListState,
    val coroutineScope: CoroutineScope,
    val fadeInAnimationDurationMs: Int,
    val fadeOutAnimationDurationMs: Int,
    val fadeOutAnimationDelayMs: Int,
) {
    internal var viewportSize by mutableStateOf(0f)
    internal var knobSize by mutableStateOf(0f)

    internal var showKnob by mutableStateOf(false)

    private val firstItemSize by derivedStateOf {
        listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
    }

    private val estimatedFullListSize by derivedStateOf {
        firstItemSize * listState.layoutInfo.totalItemsCount
    }

    val offsetY by derivedStateOf {
        val viewportOffsetInFullListSpace =
            listState.firstVisibleItemIndex * firstItemSize + listState.firstVisibleItemScrollOffset

        val factor =
            viewportOffsetInFullListSpace / (estimatedFullListSize - viewportSize) * knobSize

        (viewportSize / (estimatedFullListSize - viewportSize)) * viewportOffsetInFullListSpace - factor
    }

    private var isDragging by mutableStateOf(false)

    val alpha by derivedStateOf {
        if (listState.isScrollInProgress || isDragging) {
            1f
        } else {
            0f
        }
    }

    val animationDelayMs by derivedStateOf {
        if (listState.isScrollInProgress || isDragging) {
            0
        } else {
            fadeOutAnimationDelayMs
        }
    }

    val animationDurationMs by derivedStateOf {
        if (listState.isScrollInProgress || isDragging) {
            fadeInAnimationDurationMs
        } else {
            fadeOutAnimationDurationMs
        }
    }

    fun onDragStart(offset: Offset) {
        if (showKnob) {
            isDragging = offset.y in (offsetY..offsetY + knobSize)
        }
    }

    fun onDrag(offset: Offset) {
        if (isDragging) {
            coroutineScope.launch {
                listState.scrollBy(offset.y * estimatedFullListSize / viewportSize)
            }
        }
    }

    fun onDragInterrupted() {
        isDragging = false
    }
}
