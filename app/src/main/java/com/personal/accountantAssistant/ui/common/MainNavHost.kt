package com.personal.accountantAssistant.ui.common

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.personal.accountantAssistant.domain.enums.TabPositions
import kotlin.enums.EnumEntries

@Composable
internal fun MainNavHost(
    paddingValues: PaddingValues,
    navController: NavHostController,
    entries: EnumEntries<TabPositions>,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = entries.first().route,
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        enterTransition = {
            val from = entries.indexOfFirst { it.route == initialState.destination.route }
            val to = entries.indexOfFirst { it.route == targetState.destination.route }
            slideInHorizontally { if (to >= from) it else -it } + fadeIn()
        },
        exitTransition = {
            val from = entries.indexOfFirst { it.route == initialState.destination.route }
            val to = entries.indexOfFirst { it.route == targetState.destination.route }
            slideOutHorizontally { if (to >= from) -it else it } + fadeOut()
        },
        popEnterTransition = {
            val from = entries.indexOfFirst { it.route == initialState.destination.route }
            val to = entries.indexOfFirst { it.route == targetState.destination.route }
            slideInHorizontally { if (to >= from) it else -it } + fadeIn()
        },
        popExitTransition = {
            val from = entries.indexOfFirst { it.route == initialState.destination.route }
            val to = entries.indexOfFirst { it.route == targetState.destination.route }
            slideOutHorizontally { if (to >= from) -it else it } + fadeOut()
        },
        builder = builder
    )
}