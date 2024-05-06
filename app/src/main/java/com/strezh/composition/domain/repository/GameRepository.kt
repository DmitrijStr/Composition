package com.strezh.composition.domain.repository

import com.strezh.composition.domain.entity.GameSettings
import com.strezh.composition.domain.entity.Level
import com.strezh.composition.domain.entity.Question

interface GameRepository {
    fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question

    fun getGameSettings(level: Level): GameSettings
}