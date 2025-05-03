package com.hylo.stylespace.ui.screens.createAppointment

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hylo.stylespace.model.Services
import com.hylo.stylespace.model.enums.ProgressCreateAppointment
import kotlinx.coroutines.delay

@Composable
fun ServicesList(
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