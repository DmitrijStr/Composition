package com.strezh.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.strezh.composition.R
import com.strezh.composition.data.GameRepositoryImpl
import com.strezh.composition.domain.entity.GameResult
import com.strezh.composition.domain.entity.GameSettings
import com.strezh.composition.domain.entity.Level
import com.strezh.composition.domain.entity.Question
import com.strezh.composition.domain.usecases.GenerateQuestionUseCase
import com.strezh.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var gameSettings: GameSettings

    private val context = application
    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private var timer: CountDownTimer? = null

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var totalAnswersCount = 0
    private var rightAnswersCount = 0

    private fun getGameSettings(level: Level) {
        gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentageOfRightAnswers
    }

    fun startGame(level: Level) {
        getGameSettings(level)
        updateProgress()
        startTimer()
        generateNewQuestion()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun checkAnswer(number: Int) {
        if (number == _question.value?.rightAnswer) {
            rightAnswersCount++
        }
        totalAnswersCount++
    }

    private fun updateProgress() {
        val percent = calcPercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            context.resources.getString(R.string.progress_answers),
            rightAnswersCount,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCount.value = rightAnswersCount >= gameSettings.minCountOfRightAnswers
        _enoughPercent.value = percent >= gameSettings.minPercentageOfRightAnswers
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * 1000L, 1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                endGame()
            }
        }
        timer?.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / 1000
        val minutes = seconds / 60
        val left = seconds - (minutes * 60)

        return String.format("%02d:%02d", minutes, left)
    }

    private fun calcPercentOfRightAnswers(): Int {
        if (totalAnswersCount == 0) {
            return 0
        }

        return ((rightAnswersCount / totalAnswersCount.toDouble()) * 100).toInt()
    }

    private fun endGame() {
        _gameResult.value = GameResult(
            _enoughCount.value == true && _enoughPercent.value == true,
            rightAnswersCount,
            totalAnswersCount,
            gameSettings
        )
    }
}