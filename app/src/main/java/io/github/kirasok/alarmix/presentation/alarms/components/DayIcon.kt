package io.github.kirasok.alarmix.presentation.alarms.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kirasok.alarmix.ui.theme.AlarmixTheme

@Composable
fun DayIcon(
  day: Char,
  enabled: Boolean,
  modifier: Modifier = Modifier,
) {
  if (enabled) {
    Box(
      modifier = modifier
        .width(24.dp)
        .height(24.dp)
        .border(1.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.inverseSurface, MaterialTheme.shapes.medium),
      contentAlignment = Alignment.Center,
    )
    {
      Text(text = day.toString(), color = MaterialTheme.colorScheme.inverseOnSurface)
    }
  } else {
    Box(
      modifier = modifier
        .width(24.dp)
        .height(24.dp)
        .border(2.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.medium),
      contentAlignment = Alignment.Center,
    )
    {
      Text(text = day.toString())
    }
  }
}

@Preview
@Composable
fun DayIconPreviewEnabled() {
  AlarmixTheme(dynamicColor = false) {
    DayIcon(day = 'M', enabled = true)
  }
}

@Preview
@Composable
fun DayIconPreviewDisabled() {
  AlarmixTheme(dynamicColor = false) {
    DayIcon(day = 'T', enabled = false)
  }
}
