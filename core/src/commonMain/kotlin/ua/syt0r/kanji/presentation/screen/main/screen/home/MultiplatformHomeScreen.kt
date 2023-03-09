package ua.syt0r.kanji.presentation.screen.main.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.home.data.HomeScreenTab

@Composable
fun MultiplatformHomeScreen(
    viewModel: HomeScreenContract.ViewModel,
    mainNavigationState: MainNavigationState,
    homeNavigationState: NewHomeNavigationState
) {

    val tabContent = remember {
        movableContentOf {
            NewHomeNavigationContent(homeNavigationState, mainNavigationState)
        }
    }

    HomeScreenUI(
        availableTabs = HomeScreenTab.values().toList(),
        selectedTabState = homeNavigationState.selectedTab,
        onTabSelected = { homeNavigationState.navigate(it) }
    ) {

        tabContent()

    }

}