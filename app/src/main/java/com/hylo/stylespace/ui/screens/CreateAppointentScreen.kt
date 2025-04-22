package com.hylo.stylespace.ui.screens

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.hylo.stylespace.viewmodel.ServicesViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CreateAppointmentScreen(
    mainScreenNavController: NavController,
    servicesViewModel: ServicesViewModel,
    typeServiceSelected: TypeServices
) {
    var navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val services by servicesViewModel.services.collectAsState()
    val servicesTypeSelected = services.filter { it.type == typeServiceSelected.name }

    var serviceSelected by remember {
        mutableStateOf<Services?>(null)
    }

    var dateSelected by remember {
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
                Column {  }
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

@Composable
private fun ServicesList(
    navController: NavController,
    listServices: List<Services>,

    changeServiceSelected: (Services) -> Unit
) {
    AnimatedVisibility(
        visible = listServices.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column(

            verticalArrangement = Arrangement.spacedBy(
                space = 10.dp
            )

        ) {

            Text(
                text = "Servizi disponibili",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn (
                modifier = Modifier
                    .fillMaxSize(),

                verticalArrangement = Arrangement.spacedBy(
                    space = 15.dp
                ),
                flingBehavior = rememberSnapFlingBehavior(
                    lazyListState = rememberLazyListState()
                )

            ) {

                itemsIndexed(
                    items = listServices,
                    key = { _, item -> item.id }

                ) { index, item ->

                    Box(
                        modifier = Modifier.animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null,
                            placementSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    ) {
                        val animatedProgress = remember(index) {
                            Animatable(initialValue = 0f)
                        }

                        LaunchedEffect(Unit) {
                            delay(index * 50L)
                            animatedProgress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(
                                    durationMillis = 200,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }

                        CardServices(
                            navController = navController,
                            service = item,
                            changeServiceSelected = changeServiceSelected,
                            modifier = Modifier
                                .graphicsLayer {
                                    alpha = animatedProgress.value
                                    translationY = (1f - animatedProgress.value) * 20f
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardServices(
    navController: NavController,
    service: Services,
    changeServiceSelected: (Services) -> Unit,
    modifier: Modifier
) {

    ElevatedCard (
        modifier = modifier
            .fillMaxWidth(),

        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        )

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    changeServiceSelected(service)
                    navController.navigate(ProgressCreateAppointment.SelectDate.route)
                }

        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        all = 15.dp
                    ),

                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = service.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline
                    )

                }

                Column {
                    Text(
                        text = "€${service.price}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text =  "${service.durationInMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer
                    )
                    .padding(
                        start = 15.dp,
                        end = 15.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    ),

                contentAlignment = Alignment.CenterStart
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 10.dp
                    )

                ) {

                    Icon(
                        painter = rememberVectorPainter(Icons.Outlined.CalendarToday),
                        contentDescription = "",

                        modifier = Modifier
                            .size(
                                size = 18.dp
                            )
                    )

                    Text(
                        text = "Prenota",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }
    }
}

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
fun CalendarHeader(
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
fun DaysOfWeekHeader() {
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
fun CalendarDays(
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