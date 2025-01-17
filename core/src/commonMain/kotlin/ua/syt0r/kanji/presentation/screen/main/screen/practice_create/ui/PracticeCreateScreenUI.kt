package ua.syt0r.kanji.presentation.screen.main.screen.practice_create.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.syt0r.kanji.presentation.common.MultiplatformDialog
import ua.syt0r.kanji.presentation.common.resources.icon.ExtraIcons
import ua.syt0r.kanji.presentation.common.resources.icon.Restore
import ua.syt0r.kanji.presentation.common.resources.icon.Save
import ua.syt0r.kanji.presentation.common.resources.string.resolveString
import ua.syt0r.kanji.presentation.common.trackItemPosition
import ua.syt0r.kanji.presentation.common.ui.MultiplatformPopup
import ua.syt0r.kanji.presentation.common.ui.PopupContentItem
import ua.syt0r.kanji.presentation.screen.main.MainDestination
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.DataAction
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.PracticeCreateScreenContract.ScreenState
import ua.syt0r.kanji.presentation.screen.main.screen.practice_create.data.ValidationResult

@Composable
fun PracticeCreateScreenUI(
    configuration: MainDestination.CreatePractice,
    state: State<ScreenState>,
    onUpClick: () -> Unit = {},
    onPracticeDeleteClick: () -> Unit = {},
    onDeleteAnimationCompleted: () -> Unit = {},
    onCharacterInfoClick: (String) -> Unit = {},
    onCharacterDeleteClick: (String) -> Unit = {},
    onCharacterRemovalCancel: (String) -> Unit = {},
    onSaveConfirmed: (title: String) -> Unit = {},
    onSaveAnimationCompleted: () -> Unit = {},
    submitKanjiInput: suspend (input: String) -> ValidationResult = { TODO() }
) {

    var showTitleInputDialog by remember { mutableStateOf(false) }
    if (showTitleInputDialog) {
        val saveWritingPracticeDialogData = remember {
            derivedStateOf {
                val currentState = state.value as ScreenState.Loaded
                SaveWritingPracticeDialogData(
                    initialTitle = currentState.initialPracticeTitle,
                    dataAction = currentState.currentDataAction
                )
            }
        }
        SaveWritingPracticeDialog(
            state = saveWritingPracticeDialogData,
            onInputSubmitted = onSaveConfirmed,
            onDismissRequest = { showTitleInputDialog = false },
            onSaveAnimationCompleted = onSaveAnimationCompleted
        )
    }

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    if (showDeleteConfirmationDialog) {
        val deleteConfirmationDialogData = remember {
            derivedStateOf {
                val currentState = state.value as ScreenState.Loaded
                DeleteWritingPracticeDialogData(
                    practiceTitle = currentState.initialPracticeTitle!!,
                    currentAction = currentState.currentDataAction
                )
            }
        }
        DeleteWritingPracticeDialog(
            state = deleteConfirmationDialogData,
            onDismissRequest = { showDeleteConfirmationDialog = false },
            onDeleteConfirmed = onPracticeDeleteClick,
            onDeleteAnimationCompleted = onDeleteAnimationCompleted
        )
    }

    var unknownEnteredCharacters: Set<String> by remember { mutableStateOf(emptySet()) }
    if (unknownEnteredCharacters.isNotEmpty()) {
        UnknownCharactersDialog(
            characters = unknownEnteredCharacters,
            onDismissRequest = { unknownEnteredCharacters = emptySet() }
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val contentPadding = remember { mutableStateOf(16.dp) }

    Scaffold(
        topBar = {
            Toolbar(
                configuration = configuration,
                state = state,
                onUpClick = onUpClick,
                onDeleteClick = { showDeleteConfirmationDialog = true }
            )
        },
        floatingActionButton = {
            val shouldShow = remember {
                derivedStateOf { state.value.let { it is ScreenState.Loaded && it.currentDataAction == DataAction.Loaded } }
            }
            AnimatedVisibility(
                visible = shouldShow.value,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    modifier = Modifier.trackItemPosition {
                        contentPadding.value = it.heightFromScreenBottom + 16.dp
                    },
                    onClick = { showTitleInputDialog = true },
                    content = { Icon(ExtraIcons.Save, null) }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        val transition = updateTransition(
            targetState = state.value,
            label = "State Update Transition"
        )
        transition.AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            contentKey = { it::class }
        ) { screenState ->

            when (screenState) {
                ScreenState.Loading -> {
                    LoadingState()
                }

                is ScreenState.Loaded -> {
                    LoadedState(
                        screenState = screenState,
                        onInputSubmit = {
                            coroutineScope.launch {
                                unknownEnteredCharacters = submitKanjiInput(it).unknownCharacters
                            }
                        },
                        onInfoClick = onCharacterInfoClick,
                        onDeleteClick = onCharacterDeleteClick,
                        onDeleteCancel = onCharacterRemovalCancel,
                        contentPadding = contentPadding
                    )
                }
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Toolbar(
    configuration: MainDestination.CreatePractice,
    state: State<ScreenState>,
    onUpClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = resolveString {
                    if (configuration is MainDestination.CreatePractice.EditExisting) {
                        practiceCreate.ediTitle
                    } else {
                        practiceCreate.newTitle
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onUpClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (configuration is MainDestination.CreatePractice.EditExisting) {
                val isEditEnabled = remember {
                    derivedStateOf {
                        val currentState = state.value
                        currentState is ScreenState.Loaded &&
                                currentState.currentDataAction == DataAction.Loaded
                    }
                }
                IconButton(
                    onClick = onDeleteClick,
                    enabled = isEditEnabled.value
                ) {
                    Icon(Icons.Default.Delete, null)
                }
            }
        }
    )
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun LoadedState(
    screenState: ScreenState.Loaded,
    onInputSubmit: (String) -> Unit,
    onInfoClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDeleteCancel: (String) -> Unit,
    contentPadding: State<Dp>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        CharacterInputField(
            isEnabled = screenState.currentDataAction == DataAction.Loaded,
            onInputSubmit = onInputSubmit
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(50.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        ) {

            items(screenState.characters.toList()) {
                Character(
                    character = it,
                    isPendingRemoval = screenState.charactersPendingForRemoval
                        .contains(it),
                    modifier = Modifier,
                    onInfoClick = onInfoClick,
                    onDeleteClick = onDeleteClick,
                    onDeleteCancel = onDeleteCancel
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(contentPadding.value))
            }

        }

    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CharacterInputField(
    isEnabled: Boolean,
    onInputSubmit: (String) -> Unit
) {

    var enteredText by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val handleSubmit: () -> Unit = {
        onInputSubmit(enteredText)
        enteredText = ""
        softwareKeyboardController?.hide()
    }

    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = { enteredText = "" }
        ) {
            Icon(Icons.Default.Close, null)
        }

        Box(modifier = Modifier.weight(1f)) {

            var isInputFocused by remember { mutableStateOf(false) }

            BasicTextField(
                value = enteredText,
                onValueChange = { enteredText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isInputFocused = it.isFocused },
                maxLines = 1,
                singleLine = true,
                interactionSource = interactionSource,
                cursorBrush = SolidColor(color),
                textStyle = TextStyle.Default.copy(color),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions { handleSubmit() }
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = !isInputFocused && enteredText.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = resolveString { practiceCreate.searchHint },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

        }

        IconButton(
            onClick = handleSubmit,
            enabled = isEnabled
        ) {
            Icon(Icons.Default.Search, null)
        }

    }

}

@Composable
private fun Character(
    character: String,
    isPendingRemoval: Boolean,
    modifier: Modifier = Modifier,
    onInfoClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDeleteCancel: (String) -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { isExpanded = true }
    ) {

        Text(
            text = character,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 32.sp
        )

        AnimatedVisibility(
            visible = isPendingRemoval,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.matchParentSize()
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        MultiplatformPopup(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {

            PopupContentItem(
                onClick = {
                    isExpanded = false
                    onInfoClick(character)
                }
            ) {
                Icon(Icons.Default.Info, null, modifier = Modifier.padding(end = 10.dp))
                Text(text = resolveString { practiceCreate.infoAction })
            }

            if (isPendingRemoval) {
                PopupContentItem(
                    onClick = {
                        isExpanded = false
                        onDeleteCancel(character)
                    }
                ) {
                    Icon(ExtraIcons.Restore, null, modifier = Modifier.padding(end = 10.dp))
                    Text(text = resolveString { practiceCreate.returnAction })
                }
            } else {
                PopupContentItem(
                    onClick = {
                        isExpanded = false
                        onDeleteClick(character)
                    }
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.padding(end = 10.dp))
                    Text(text = resolveString { practiceCreate.removeAction })
                }
            }

        }

    }


}

@Composable
private fun UnknownCharactersDialog(
    characters: Set<String>,
    onDismissRequest: () -> Unit = {}
) {

    MultiplatformDialog(
        onDismissRequest = onDismissRequest
    ) {
        Column {
            Text(text = resolveString { practiceCreate.unknownTitle })
            Text(text = resolveString { practiceCreate.unknownMessage(characters.toList()) })
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = resolveString { practiceCreate.unknownButton })
            }
        }
    }

}
