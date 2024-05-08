package com.strezh.composition.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.strezh.composition.R
import com.strezh.composition.databinding.FragmentGameBinding
import com.strezh.composition.domain.entity.GameResult
import com.strezh.composition.domain.entity.Level

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            AndroidViewModelFactory(requireActivity().application)
        )[GameViewModel::class.java]
    }
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

        viewModel.startGame(level)
        observe()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observe() {
        viewModel.question.observe(viewLifecycleOwner) { question ->
            with(binding) {
                tvSum.text = question.sum.toString()
                tvLeftNumber.text = question.visibleNumber.toString()

                tvOption1.text = question.options[0].toString()
                tvOption2.text = question.options[1].toString()
                tvOption3.text = question.options[2].toString()
                tvOption4.text = question.options[3].toString()
                tvOption5.text = question.options[4].toString()
                tvOption6.text = question.options[5].toString()
            }
        }

        viewModel.percentOfRightAnswers.observe(viewLifecycleOwner) {
            binding.progressBar.setProgress(it, true)
        }

        viewModel.enoughCount.observe(viewLifecycleOwner) {
            val colorRes = if (it) android.R.color.holo_green_light else android.R.color.holo_red_light
            val color = ContextCompat.getColor(requireContext(), colorRes)

            binding.tvAnswersProgress.setTextColor(color)
        }

        viewModel.enoughPercent.observe(viewLifecycleOwner) {
            val colorRes = if (it) android.R.color.holo_green_light else android.R.color.holo_red_light
            val color = ContextCompat.getColor(requireContext(), colorRes)

            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }

        viewModel.formattedTime.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }

        viewModel.minPercent.observe(viewLifecycleOwner) {
            binding.progressBar.secondaryProgress = it
        }

        viewModel.progressAnswers.observe(viewLifecycleOwner) {
            binding.tvAnswersProgress.text = it
        }

        viewModel.enoughCount.observe(viewLifecycleOwner) { isEnough ->
            binding.tvAnswersProgress.setTextColor(
                if (isEnough) Color.GREEN else Color.RED
            )
        }

        viewModel.gameResult.observe(viewLifecycleOwner) {
            launchFinishFragment(it)
        }
    }

    private fun setupListeners() {
        with(binding) {
            tvOption1.setOnClickListener {
                viewModel.chooseAnswer(tvOption1.text.toString().toInt())
            }
            tvOption2.setOnClickListener {
                viewModel.chooseAnswer(tvOption2.text.toString().toInt())
            }
            tvOption3.setOnClickListener {
                viewModel.chooseAnswer(tvOption3.text.toString().toInt())
            }
            tvOption4.setOnClickListener {
                viewModel.chooseAnswer(tvOption4.text.toString().toInt())
            }
            tvOption5.setOnClickListener {
                viewModel.chooseAnswer(tvOption5.text.toString().toInt())
            }
            tvOption6.setOnClickListener {
                viewModel.chooseAnswer(tvOption6.text.toString().toInt())
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