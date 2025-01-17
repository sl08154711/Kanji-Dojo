package ua.syt0r.kanji.presentation.preview.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.tooling.preview.Preview
import ua.syt0r.kanji.presentation.common.PaginatableJapaneseWordList
import ua.syt0r.kanji.presentation.common.theme.AppTheme
import ua.syt0r.kanji.presentation.common.ui.kanji.PreviewKanji
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.SearchScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.data.RadicalSearchState
import ua.syt0r.kanji.presentation.screen.main.screen.home.screen.search.ui.SearchScreenUI

@Preview
@Composable
private fun EmptyStatePreview() {
    AppTheme {
        SearchScreenUI(
            state = rememberUpdatedState(
                ScreenState(
                    isLoading = true,
                    characters = emptyList(),
                    words = rememberUpdatedState(PaginatableJapaneseWordList(0, emptyList())),
                    query = ""
                )
            ),
            radicalsState = rememberUpdatedState(RadicalSearchState.random()),
            onSubmitInput = {},
            onRadicalsSectionExpanded = {},
            onRadicalsSelected = {},
            onCharacterClick = {},
            onScrolledToEnd = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadedStatePreview() {
    AppTheme(useDarkTheme = false) {
        SearchScreenUI(
            state = ScreenState(
                isLoading = false,
                characters = (0 until 10).map { PreviewKanji.randomKanji() },
                words = rememberUpdatedState(
                    PaginatableJapaneseWordList(200, PreviewKanji.randomWords(20))
                ),
                query = ""
            ).run { rememberUpdatedState(newValue = this) },
            radicalsState = rememberUpdatedState(RadicalSearchState.random()),
            onSubmitInput = {},
            onRadicalsSectionExpanded = {},
            onRadicalsSelected = {},
            onCharacterClick = {},
            onScrolledToEnd = {}
        )
    }
}