package ua.syt0r.kanji.presentation.screen.settings

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import ua.syt0r.kanji.core.analytics.AnalyticsManager
import ua.syt0r.kanji.core.notification.ReminderNotificationConfiguration
import ua.syt0r.kanji.core.notification.ReminderNotificationContract
import ua.syt0r.kanji.core.user_data.UserPreferencesRepository
import ua.syt0r.kanji.presentation.screen.settings.FdroidSettingsScreenContract.ScreenState

class FdroidSettingsViewModel(
    private val viewModelScope: CoroutineScope,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsManager: AnalyticsManager,
    private val reminderScheduler: ReminderNotificationContract.Scheduler
) : FdroidSettingsScreenContract.ViewModel {

    override val state = mutableStateOf<ScreenState>(ScreenState.Loading)

    override fun refresh() {
        viewModelScope.launch {
            state.value = ScreenState.Loaded(
                reminderConfiguration = ReminderNotificationConfiguration(
                    enabled = userPreferencesRepository.getReminderEnabled()
                        ?: false,
                    time = userPreferencesRepository.getReminderTime()
                        ?: LocalTime(9, 0)
                )
            )
        }
    }

    override fun reportScreenShown() {
        analyticsManager.setScreen("settings")
    }

    override fun updateReminder(configuration: ReminderNotificationConfiguration) {
        viewModelScope.launch {
            state.value = ScreenState.Loaded(configuration)
            userPreferencesRepository.setReminderEnabled(configuration.enabled)
            userPreferencesRepository.setReminderTime(configuration.time)
            if (configuration.enabled) {
                reminderScheduler.scheduleNotification(configuration.time)
            } else {
                reminderScheduler.unscheduleNotification()
            }
        }
    }

}
