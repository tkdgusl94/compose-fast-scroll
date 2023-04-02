package com.leveloper.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leveloper.compose.fastscroller.FastScrollableLazyColumn
import com.leveloper.compose.fastscroller.rememberFastScrollbarState
import com.leveloper.sample.ui.theme.ComposefastscrollTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposefastscrollTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Sample(
                        items = remember { getSampleList() }
                    )
                }
            }
        }
    }

    private fun getSampleList(): List<String> = List(20000) { "${it}st item" }
}

@Composable
fun Sample(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    FastScrollableLazyColumn(
        state = rememberFastScrollbarState(),
        knobContent = {
            Box(
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .size(width = 20.dp, height = 40.dp)
                    .background(color = Color.Blue)
            )
        }
    ) {
        items(items) { item ->
            Text(
                text = item,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        }
    }
}
