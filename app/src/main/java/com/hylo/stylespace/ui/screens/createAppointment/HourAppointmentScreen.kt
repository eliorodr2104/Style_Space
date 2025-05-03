package com.hylo.stylespace.ui.screens.createAppointment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HourAppointmentScreen(
    openAndCloseHourEstablishment: String,
    dateAppointment: LocalDate,
    dateFormatted: String,
    hourSelected: String,
    onHourSelected: (String) -> Unit
) {

    val openAndClose = openAndCloseHourEstablishment.split("-")

    Column(

        modifier = Modifier
            .fillMaxSize(),

        verticalArrangement = Arrangement.spacedBy(
            space = 15.dp
        )

    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 4.dp
            )
        ) {
            Text(
                text = "Scegli un orario",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = dateFormatted,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),

            modifier = Modifier
                .fillMaxSize(),

            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp
            ),

            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp
            )
        ) {

            val start = openAndClose.first().toInt()
            val end = openAndClose.last().toInt()

            val today = LocalDate.now()
            val now = LocalTime.now()

            items((end - start) * 2) { index ->
                val hour = start + (index / 2)
                val minute = if (index % 2 == 0) 0 else 30

                val hourFormatted = "%02d:%02d".format(hour, minute)

                val slotTime = LocalTime.of(hour, minute)

                val isDisable = if (dateAppointment.isEqual(today)) {
                    slotTime.isBefore(now)

                } else {
                    false
                }

                HourItem(
                    hour = hourFormatted,
                    isSelected = hourSelected == hourFormatted,
                    isDisable = isDisable,
                    onHourSelected = {
                        if (!isDisable) onHourSelected(it)
                    }
                )
            }

        }
    }
}

@Composable
private fun HourItem(
    hour: String,
    isSelected: Boolean,
    isDisable: Boolean,
    onHourSelected: (String) -> Unit
) {

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isDisable -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isDisable -> MaterialTheme.colorScheme.outline
        else -> Color.Unspecified
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth(),

        shape = ShapeDefaults.Medium,

        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),

        border = CardDefaults.outlinedCardBorder(
            enabled = false
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onHourSelected(hour)
                }
                .padding(
                    all = 8.dp
                ),

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = hour,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,

                color = textColor
            )
        }

    }
}