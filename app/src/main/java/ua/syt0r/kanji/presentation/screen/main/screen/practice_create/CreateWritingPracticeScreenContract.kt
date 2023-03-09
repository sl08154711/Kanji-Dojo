package ua.syt0r.kanji.presentation.screen.main.screen.practice_create

import androidx.compose.runtime.State
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.InputProcessingResult

interface CreateWritingPracticeScreenContract {

    interface ViewModel {

        val state: State<ScreenState>

        fun initialize(configuration: MainDestination.CreatePractice)
        suspend fun submitUserInput(input: String): InputProcessingResult

        fun remove(character: String)
        fun cancelRemoval(character: String)

        fun savePractice(title: String)
        fun deletePractice()

        fun reportScreenShown(configuration: MainDestination.CreatePractice)

    }

    enum class DataAction {
        ProcessingInput, Loaded,
        Saving, SaveCompleted,
        Deleting, DeleteCompleted
    }

    sealed class ScreenState {

        object Loading : ScreenState()

        data class Loaded(
            val initialPracticeTitle: String?,
            val characters: Set<String>,
            val charactersPendingForRemoval: Set<String>,
            val currentDataAction: DataAction
        ) : ScreenState()

    }

}

