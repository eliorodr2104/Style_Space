package com.hylo.stylespace.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hylo.stylespace.R
import com.hylo.stylespace.model.TypeServices
import com.hylo.stylespace.model.enums.Screen
import com.hylo.stylespace.viewmodel.AppointmentViewModel
import com.hylo.stylespace.viewmodel.ServicesViewModel
import com.hylo.stylespace.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    mainScreenNavController: NavController,
    userViewModel: UserViewModel,
    servicesViewModel: ServicesViewModel,
    appointmentViewModel: AppointmentViewModel,

    changeServicesSelected: (TypeServices) -> Unit
) {

    val user by userViewModel.user.collectAsState()
    val services by servicesViewModel.services.collectAsState()
    val typeServices by servicesViewModel.typeServices.collectAsState()
    val nextAppointment by appointmentViewModel.nextAppointment.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(
                start = 15.dp,
                end = 15.dp,
                bottom = 15.dp
            )
            .fillMaxSize(),

        verticalArrangement = Arrangement.spacedBy(
            space = 25.dp
        )
    ) {

        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.outline,
                    disabledContentColor = MaterialTheme.colorScheme.outline
                )

            ) {

                Column(
                    modifier = Modifier
                        .padding(
                            all = 15.dp
                        ),

                    verticalArrangement = Arrangement.spacedBy(
                        space = 15.dp
                    )
                ) {

                    Column {
                        Text(
                            text = "${stringResource(R.string.welcome_home)} ${user?.name}!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.hello_home),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (nextAppointment == null) {
                        ShimmerAppointmentCard()

                    } else {
                        AnimatedVisibility(
                            visible = nextAppointment != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            val servicesUse = services.find { it.id == nextAppointment?.servicesId }

                            Column (
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 8.dp
                                )
                            ){

                                Text(
                                    text = stringResource(R.string.next_appointment),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                OutlinedCard (
                                    modifier = Modifier
                                        .fillMaxWidth(),

                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                ) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()

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

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    space = 15.dp
                                                ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {

                                                Box(
                                                    modifier = Modifier
                                                        .clip(
                                                            shape = CircleShape
                                                        )
                                                        .background(
                                                            color = MaterialTheme.colorScheme.primaryContainer
                                                        )
                                                        .width(50.dp)
                                                        .aspectRatio(1f)
                                                )


                                                Column {
                                                    Text(
                                                        text = servicesUse?.name.toString(),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )

                                                    Text(
                                                        text = "Con MIAO",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = MaterialTheme.colorScheme.outline
                                                    )

                                                }
                                            }

                                            Column {
                                                Text(
                                                    text = "€${servicesUse?.price.toString()}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )

                                                Text(
                                                    text =  "${servicesUse?.durationInMinutes.toString()} min",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }


                                        Row(
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

                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically

                                        ) {
                                            nextAppointment?.dataOra?.let { timestamp ->
                                                val date = timestamp.toDate()

                                                val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ITALY)
                                                val hourFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

                                                Text(
                                                    text = dateFormatter.format(date),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )

                                                Text(
                                                    text = hourFormatter.format(date),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {

            AnimatedVisibility(
                visible = typeServices.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text = stringResource(R.string.service_header),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    key(typeServices.size) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            flingBehavior = rememberSnapFlingBehavior(
                                lazyListState = rememberLazyListState()
                            )
                        ) {
                            itemsIndexed(
                                items = typeServices,
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
                                        delay(index * 100L)
                                        animatedProgress.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(
                                                durationMillis = 200,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    }

                                    CardServices(
                                        mainScreenNavController = mainScreenNavController,
                                        item = item,
                                        changeServicesSelected = changeServicesSelected,
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
        }
    }
}

@Composable
private fun CardServices(
    mainScreenNavController: NavController,
    item: TypeServices,
    changeServicesSelected: (TypeServices) -> Unit,
    modifier: Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemSize = screenWidth / 3

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .size(itemSize)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        changeServicesSelected(item)
                        mainScreenNavController.navigate(Screen.CreateAppointment.route)
                    }
            ) {
                Icon(
                    imageVector = when (item.name) {
                        "taglio" -> Icons.Default.ContentCut
                        "tinta" -> Icons.Default.Palette
                        "piega" -> Icons.Default.Spa
                        else -> Icons.Default.Star
                    },
                    contentDescription = item.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ShimmerAppointmentCard() {

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition()

    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = stringResource(R.string.next_appointment),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(brush)
                        )

                        Column {

                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(brush)
                            )
                        }
                    }

                    Column {

                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )

                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                    }
                }
            }
        }
    }
}