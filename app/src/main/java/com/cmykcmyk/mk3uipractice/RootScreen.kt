package com.cmykcmyk.mk3uipractice

import Intro1Screen
import Intro2Screen
import StartScreen
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cmykcmyk.mk3uipractice.ui.theme.MK3UIPracticeTheme
import kotlinx.coroutines.delay

enum class ScreenId {
    Intro1, Intro2, StartScreen, CharacterSelection, Biography
}

@Composable
fun RootScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ScreenId.Intro1.name) {
        composable(
            route = ScreenId.Intro1.name
        ) {
            BackHandler(true, {})
            Intro1Screen()

            LaunchedEffect(Unit) {
                delay(7000)
                navController.navigate(ScreenId.Intro2.name)
            }
        }

        composable(
            route = ScreenId.Intro2.name
        ) {
            BackHandler(true, {})
            Intro2Screen()

            LaunchedEffect(Unit) {
                delay(5000)
                navController.navigate(ScreenId.StartScreen.name)
            }
        }

        composable(
            route = ScreenId.StartScreen.name
        ) {
            BackHandler(true, {})
            StartScreen {
                navController.navigate(ScreenId.CharacterSelection.name)
            }
        }

        composable(
            route = ScreenId.CharacterSelection.name,
            enterTransition = { fadeIn(tween(300, 2000, LinearEasing)) },
            exitTransition = { fadeOut(tween(1000, 0, LinearEasing)) }
        ) {
            BackHandler(true, {})
            CharacterSelectionScreen(onCharacterSelected = { selection ->
                navController.navigate("${ScreenId.Biography.name}/${selection.ordinal}")
            })
        }

        composable(
            route = "${ScreenId.Biography.name}/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType }),
            enterTransition = { fadeIn(tween(2000, 2000, LinearEasing)) },
            exitTransition = { fadeOut(tween(0)) }
        ) { navBackStackEntry ->
            BackHandler(true, {})

            val characterId = CharacterData.CharacterId.entries[navBackStackEntry.arguments?.getInt("characterId") ?: 0]
            BiographyScreen(
                characterId,
                onExit = { navController.navigate(ScreenId.CharacterSelection.name) }
            )
        }
    }
}

@Preview(device = "spec:width=1280px,height=800px,orientation=landscape")
@Composable
fun RootScreenPreview() {
    MK3UIPracticeTheme {
        RootScreen()
    }
}
