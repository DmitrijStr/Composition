package com.strezh.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.strezh.composition.R
import com.strezh.composition.data.GameRepositoryImpl
import com.strezh.composition.databinding.FragmentGameBinding
import com.strezh.composition.domain.entity.GameResult
import com.strezh.composition.domain.entity.Level
import com.strezh.composition.domain.usecases.GetGameSettingsUseCase

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    private lateinit var level: Level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        with(binding) {
            tvOption1.setOnClickListener {
                launchFinishFragment(
                    GameResult(
                        false,
                        1,
                        20,
                        GetGameSettingsUseCase(GameRepositoryImpl)(level)
                    )
                )
            }
        }
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }

    private fun launchFinishFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val NAME = "GameFragment"
        private const val KEY_LEVEL = "level"
        fun newInstance(level: Level): GameFragment {
            val instance = GameFragment()
            instance.apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }

            return instance
        }
    }
}