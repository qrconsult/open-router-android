package com.nullo.openrouterclient.domain.usecases.settings

import com.nullo.openrouterclient.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetBraveApiKeyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(key: String) {
        settingsRepository.setBraveApiKey(key)
    }
}
