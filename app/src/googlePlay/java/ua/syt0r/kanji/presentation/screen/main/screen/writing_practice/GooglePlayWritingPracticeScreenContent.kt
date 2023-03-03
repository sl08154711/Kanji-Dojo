package ua.syt0r.kanji.presentation.screen.main.screen.writing_practice

import androidx.compose.runtime.Composable
import ua.syt0r.kanji.presentation.screen.main.MainNavigationState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_preview.data.PracticeScreenConfiguration

object GooglePlayWritingPracticeScreenContent : WritingPracticeScreenContract.ScreenContent {

    @Composable
    override fun Draw(
        configuration: PracticeScreenConfiguration.Writing,
        mainNavigationState: MainNavigationState,
    ) {
        GooglePlayWritingPracticeScreen(configuration, mainNavigationState)
    }

}