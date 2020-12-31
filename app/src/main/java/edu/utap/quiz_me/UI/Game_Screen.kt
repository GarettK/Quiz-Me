package edu.utap.quiz_me.UI

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import edu.utap.quiz_me.MainViewModel
import edu.utap.quiz_me.R
import edu.utap.quiz_me.api.TriviaQuestion
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Game_Screen.newInstance] factory method to
 * create an instance of this fragment.
 */
class Game_Screen : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel: MainViewModel by activityViewModels()
    private var scoreAmount: Int = 10
    private lateinit var triviaQuestion: TriviaQuestion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_screen, container, false)
    }

    private fun setupButtons() {
        val trueButton = requireActivity().findViewById<Button>(R.id.True_Button)
        val falseButton = requireActivity().findViewById<Button>(R.id.False_Button)
        val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (triviaQuestion.correctAnswer) {
            trueButton.setOnClickListener {
                viewModel.incrementCurrentHighscore(scoreAmount)
                viewModel.getQuestion()
                //Success Animation
            }
            falseButton.setOnClickListener {
                //End round
                navController.navigate(R.id.action_game_Screen_to_gameOver)
            }
        } else {
            trueButton.setOnClickListener {
                //End round
                navController.navigate(R.id.action_game_Screen_to_gameOver)
            }
            falseButton.setOnClickListener {
                viewModel.incrementCurrentHighscore(scoreAmount)
                viewModel.getQuestion()
                //Success Animation
            }
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Game_Screen.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Game_Screen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}