package com.hylo.stylespace.ui.screens.createAppointment

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.hylo.stylespace.model.ProgressCreateAppointmentItem
import com.hylo.stylespace.model.Services
import com.hylo.stylespace.model.TypeServices
import com.hylo.stylespace.model.enums.ProgressCreateAppointment
import com.hylo.stylespace.viewmodel.EstablishmentViewModel
import com.hylo.stylespace.viewmodel.ServicesViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CreateAppointmentScreen(
    mainScreenNavController: NavController,
    servicesViewModel: ServicesViewModel,
    typeServiceSelected: TypeServices,
    establishmentViewModel: EstablishmentViewModel
) {
    var navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val services by servicesViewModel.services.collectAsState()
    val servicesTypeSelected = services.filter { it.type == typeServiceSelected.name }

    val establishmentViewModel by establishmentViewModel.establishmentUse.collectAsState()

    var serviceSelected by remember {
        mutableStateOf<Services?>(null)
    }

    var dateSelected by remember {
        mutableStateOf("")
    }

    var hourSelected by remember {
        mutableStateOf("")
    }

    val listItemProgress = listOf(

        ProgressCreateAppointmentItem(
            imageVector = Icons.Outlined.ContentCut,
            imageVectorSelected = Icons.Filled.ContentCut,
            text = "Servizio",
            routeStep = ProgressCreateAppointment.SelectService
        ),

        ProgressCreateAppointmentItem(
            imageVector = Icons.Outlined.DateRange,
            imageVectorSelected = Icons.Filled.DateRange,
            text = "Data",
            routeStep = ProgressCreateAppointment.SelectDate
        ),

        ProgressCreateAppointmentItem(
            imageVector = Icons.Outlined.AccessTime,
            imageVectorSelected = Icons.Filled.AccessTime,
            text = "Orario",
            routeStep = ProgressCreateAppointment.SelectHour
        ),

        ProgressCreateAppointmentItem(
            imageVector = Icons.Outlined.Person,
            imageVectorSelected = Icons.Filled.Person,
            text = "Specialista",
            routeStep = ProgressCreateAppointment.SelectEmployee
        ),

        ProgressCreateAppointmentItem(
            imageVector = Icons.Outlined.CheckCircleOutline,
            imageVectorSelected = Icons.Filled.CheckCircleOutline,
            text = "Conferma",
            routeStep = ProgressCreateAppointment.ConfirmAppointment
        )

    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 15.dp,
                end = 15.dp,
                bottom = 15.dp
            ),

        verticalArrangement = Arrangement.spacedBy(
            space = 15.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {

            listItemProgress.forEachIndexed { index, item ->

                ItemProgressAppointment(
                    imageVector = item.imageVector,
                    imageVectorSelected = item.imageVectorSelected,
                    text = item.text,
                    current = item.routeStep.route == currentRoute
                )

            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),

            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(
                alpha = 0.4f
            )
        )

        val graph = navController.createGraph(
            startDestination = ProgressCreateAppointment.SelectService.route
        ) {

            composable(route = ProgressCreateAppointment.SelectService.route) {
                ServicesList(
                    navController = navController,
                    listServices = servicesTypeSelected,
                    changeServiceSelected = { serviceSelected = it }
                )
            }

            composable(route = ProgressCreateAppointment.SelectDate.route) {
                CalendarScreen(
                    navController = navController,
                    onDateSelected = { dateSelected = it }
                )
            }

            composable(route = ProgressCreateAppointment.SelectHour.route) {
                val date: LocalDate = LocalDate.parse(dateSelected)

                val formatterAbbr = DateTimeFormatter
                    .ofPattern("EEE", Locale("it"))

                val formatterFull = DateTimeFormatter
                    .ofPattern("EEEE d MMMM", Locale("it"))

                HourAppointmentScreen(
                    openAndCloseHourEstablishment = establishmentViewModel?.openingHours?.get(date.format(formatterAbbr)).toString(),
                    dateAppointment = date,
                    dateFormatted = date.format(formatterFull),
                    hourSelected = hourSelected,
                    onHourSelected = { hourSelected = it }
                )
            }

            composable(route = ProgressCreateAppointment.SelectEmployee.route) {
                Column {  }
            }

            composable(route = ProgressCreateAppointment.ConfirmAppointment.route) {
                Column {  }
            }
        }

        NavHost(
            navController = navController,
            graph = graph
        )
    }
}

@Composable
fun ItemProgressAppointment(
    imageVector: ImageVector,
    imageVectorSelected: ImageVector,
    text: String,
    current: Boolean
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isPhone = configuration.screenWidthDp < 600

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Box(
            modifier = Modifier
                .clip(
                    shape = CircleShape
                )
                .background(
                    color = if (current)
                        MaterialTheme.colorScheme.primary else
                            MaterialTheme.colorScheme.primaryContainer
                )
                .padding(
                    all = 10.dp
                )

        ) {

            Icon(
                painter = rememberVectorPainter(
                    if (current) imageVectorSelected else imageVector
                ),
                contentDescription = "",
                tint = if (current)
                    MaterialTheme.colorScheme.onPrimary else
                        MaterialTheme.colorScheme.onPrimaryContainer
            )

        }

        if (!(isPortrait && isPhone)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}