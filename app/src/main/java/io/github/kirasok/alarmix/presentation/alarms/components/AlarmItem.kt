package io.github.kirasok.alarmix.presentation.alarms.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kirasok.alarmix.domain.model.Alarm
import io.github.kirasok.alarmix.ui.theme.AlarmixTheme
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun AlarmItem(
  alarm: Alarm,
  modifier: Modifier = Modifier,
  onDeleteClick: () -> Unit,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(if (alarm.enabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
      .padding(16.dp)
  ) {
    val color =
      if (alarm.enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.inverseSurface
    val date = ZonedDateTime.of(
      LocalDateTime.ofEpochSecond(alarm.timestamp, 0, ZoneOffset.UTC),
      ZoneId.systemDefault()
    ).format(
      DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
    )
    Text(text = date, color = color)
    Text(text = alarm.label, color = color)
    Row {
      DayIcon(day = 'M', enabled = alarm.days.monday)
    }
  }
}

@Preview
@Composable
fun AlarmItemPreviewDisabled() {
  AlarmixTheme(dynamicColor = false) {
    AlarmItem(
      alarm = Alarm(
        timestamp = ZonedDateTime.now().toEpochSecond(),
        enabled = false,
        label = "ABC"
      )
    ) {

    }
  }
}

@Preview
@Composable
fun AlarmItemPreviewEnabled() {
  AlarmixTheme(dynamicColor = false) {
    AlarmItem(
      alarm = Alarm(
        timestamp = ZonedDateTime.now().toEpochSecond(),
        enabled = true,
        label = "ABC"
      )
    ) {

    }
  }
}