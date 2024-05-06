package com.strezh.composition.domain.usecases

import com.strezh.composition.domain.entity.GameSettings
import com.strezh.composition.domain.entity.Level
import com.strezh.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(private val repository: GameRepository) {
    operator fun invoke(level: Level) : GameSettings {
        return repository.getGameSettings(level)
    }
}