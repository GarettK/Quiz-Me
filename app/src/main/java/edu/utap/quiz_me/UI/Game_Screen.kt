package edu.utap.quiz_me.UI

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.airbnb.lottie.LottieAnimationView
import edu.utap.quiz_me.MainViewModel
import edu.utap.quiz_me.R
import edu.utap.quiz_me.api.TriviaQuestion
import kotlinx.coroutines.*
import java.util.*

class Game_Screen : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var scoreAmount: Int = 10
    private lateinit var triviaQuestion: TriviaQuestion
    private val pointIncreaseColor = Color.rgb(0,200,0)
    private val correctAnimationSpeed = 1f
    private val incorrectAnimationSpeed = 1.5f
    private val pointIncreaseAnimationSpeed = 1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_screen, container, false)
    }

    private fun correctAnimation() {
        //https://medium.com/comparethemarket/lottie-on-android-part-2-animation-listeners-59f54d702285
        val lottieCheckMark = requireActivity().findViewById<LottieAnimationView>(R.id.correct_animation)
        lottieCheckMark.speed = correctAnimationSpeed
        lottieCheckMark.playAnimation()
        lottieCheckMark.visibility = View.VISIBLE
        lottieCheckMark.addAnimatorUpdateListener {
            val progress = (it.animatedValue as Float * 100).toInt()
            Log.d("HELP!", "Progress: $progress")
            if (progress >= 90) {
                lottieCheckMark.visibility = View.GONE
                lottieCheckMark.progress = 0F
            }
        }
    }

    private fun incorrectAnimation() {
        val lottieXMark = requireActivity().findViewById<LottieAnimationView>(R.id.incorrect_animation)
        lottieXMark.speed = incorrectAnimationSpeed
        lottieXMark.playAnimation()
    }

    private fun pointIncreaseAnimation() {
        val pointText = requireActivity().findViewById<TextView>(R.id.GameScreen_Highscore_Amount)
        val oldTextColor = pointText.currentTextColor
        val colorToGreen : Animator = ValueAnimator
                .ofObject(ArgbEvaluator(), oldTextColor, pointIncreaseColor)
                .apply{duration = 150 / pointIncreaseAnimationSpeed} // milliseconds
                .apply{addUpdateListener { animator -> pointText.setTextColor(animator.animatedValue as Int) }}
        val colorFromGreen = ValueAnimator
                .ofObject(ArgbEvaluator(), pointIncreaseColor, oldTextColor)
                .apply{duration = 100 / pointIncreaseAnimationSpeed}
                .apply{addUpdateListener { animator -> pointText.setTextColor(animator.animatedValue as Int) }}
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
                colorToGreen,
                colorFromGreen
        )
        animatorSet.start()
    }

    private fun setupButtons() {
        val trueButton = requireActivity().findViewById<Button>(R.id.True_Button)
        val falseButton = requireActivity().findViewById<Button>(R.id.False_Button)
        trueButton.isClickable = true
        falseButton.isClickable = true

        val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (triviaQuestion.correctAnswer) {
            trueButton.setOnClickListener {
                trueButton.isClickable = false
                correctAnimation()
                viewModel.incrementCurrentHighscore(scoreAmount)
                pointIncreaseAnimation()
                GlobalScope.launch(Dispatchers.IO) {
                    delay(500L)
                    viewModel.getQuestion()
                }
            }
            falseButton.setOnClickListener {
                falseButton.isClickable = false
                //End round
                incorrectAnimation()
                GlobalScope.launch(Dispatchers.IO) {
                    delay((1000 * incorrectAnimationSpeed).toLong())
                    navController.navigate(R.id.action_game_Screen_to_gameOver)
                }
            }
        } else {
            trueButton.setOnClickListener {
                trueButton.isClickable = false
                //End round
                incorrectAnimation()
                GlobalScope.launch(Dispatchers.IO) {
                    delay((1000 * incorrectAnimationSpeed).toLong())
                    navController.navigate(R.id.action_game_Screen_to_gameOver)
                }
            }
            falseButton.setOnClickListener {
                falseButton.isClickable = false
                correctAnimation()
                viewModel.incrementCurrentHighscore(scoreAmount)
                pointIncreaseAnimation()
                GlobalScope.launch(Dispatchers.IO) {
                    delay(500L)
                    viewModel.getQuestion()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        clearQuestion()
        Log.d("HELP!", "Called!")
    }

    private fun clearQuestion() {
        val questionText = requireActivity().findViewById<TextView>(R.id.Question_Text)
        questionText.text = ""
    }

    private fun fromHtml(encodedQuestion: String): String {
        return Html.fromHtml(encodedQuestion, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    private fun initObserver() {
        viewModel.observeQuestion().observe(viewLifecycleOwner, {
            if (it != null) {
                triviaQuestion = it
                val cleanedQuestion = fromHtml(triviaQuestion.question)
                val questionText = requireActivity().findViewById<TextView>(R.id.Question_Text)
                questionText.text = cleanedQuestion

                setupButtons()
            }
        })

        viewModel.observeDifficulty().observe(viewLifecycleOwner, {
            if(it != null) {
                val difficultyText = requireActivity().findViewById<TextView>(R.id.GameScreen_Difficulty)
                when(it.toLowerCase(Locale.getDefault())) {
                    "easy" -> {
                        scoreAmount = 10
                        difficultyText.text = "Easy"
                    }
                    "medium" -> {
                        scoreAmount = 20
                        difficultyText.text = "Medium"
                    }
                    "hard" -> {
                        scoreAmount = 30
                        difficultyText.text = "Hard"
                    }
                    else -> {
                        scoreAmount = 20
                        difficultyText.text = "Medium"
                    }
                }
            }
        })

        viewModel.observeCurrentHighscore().observe(viewLifecycleOwner, {
            if (it != null) {
                val currentHighscoreText = requireActivity().findViewById<TextView>(R.id.GameScreen_Highscore_Amount)
                currentHighscoreText.text = it.toString()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
    }
}