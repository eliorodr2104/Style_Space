package com.hylo.stylespace.ui.screens.createAppointment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hylo.stylespace.model.enums.ProgressCreateAppointment
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarScreen(
    navController: NavController,
    onDateSelected: (String) -> Unit
) {
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var currentMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    Column(

        verticalArrangement = Arrangement.spacedBy(
            space = 10.dp
        )

    ) {
        Text(
            text = "Scegli una data",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedCard(
            shape = ShapeDefaults.Large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                CalendarHeader(
                    currentMonth = currentMonth,
                    onPreviousClick = { currentMonth = currentMonth.minusMonths(1) },
                    onNextClick = { currentMonth = currentMonth.plusMonths(1) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DaysOfWeekHeader()

                Spacer(modifier = Modifier.height(8.dp))

                CalendarDays(
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    today = today,
                    onDateSelected = { date ->
                        if (!date.isBefore(today)) {
                            selectedDate = date

                            onDateSelected(selectedDate.format(DateTimeFormatter.ISO_DATE))
                            navController.navigate(ProgressCreateAppointment.SelectHour.route)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedIconButton (
            onClick = onPreviousClick,
            border = BorderStroke(
                width = 0.dp,
                color = Color.Transparent
            )
        ) {

            Icon(
                painter = rememberVectorPainter(Icons.Default.ArrowBackIosNew),
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp)
            )

        }

        Text(
            text = currentMonth.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedIconButton(
            onClick = onNextClick,
            border = BorderStroke(
                width = 0.dp,
                color = Color.Transparent
            )
        ) {
            Icon(
                painter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowForwardIos),
                contentDescription = "",
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}

@Composable
private fun DaysOfWeekHeader() {
    val daysOfWeek = listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")

    Row(modifier = Modifier.fillMaxWidth()) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CalendarDays(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var day = firstOfMonth

    while (day.dayOfWeek != firstDayOfWeek) {
        day = day.minusDays(1)
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        repeat(6) {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) {
                    val date = day
                    val isFromCurrentMonth = date.month == currentMonth.month
                    val isSelected = date.equals(selectedDate)
                    val isToday = date.equals(today)
                    val isPast = date.isBefore(today)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                }
                            )
                            .alpha(if (isFromCurrentMonth) 1f else 0.3f)
                            .alpha(if (isPast && isFromCurrentMonth) 0.5f else 1f)
                            .clickable(enabled = isFromCurrentMonth && !isPast) {
                                if (isFromCurrentMonth && !isPast) {
                                    onDateSelected(date)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isToday -> MaterialTheme.colorScheme.primary
                                isPast -> MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                else -> MaterialTheme.colorScheme.onSurface
                            },
                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    day = day.plusDays(1)
                }
            }
        }
    }
}