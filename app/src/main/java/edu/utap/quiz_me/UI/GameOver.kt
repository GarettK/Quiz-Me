package edu.utap.quiz_me.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import edu.utap.quiz_me.MainActivity
import edu.utap.quiz_me.MainViewModel
import edu.utap.quiz_me.R


class GameOver : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_over, container, false)
    }

    private fun getHighscoreValues() {
        val newHighscoreText = requireActivity().findViewById<TextView>(R.id.New_Highscore_Amount)
        newHighscoreText.text = viewModel.getcurrentHighscore().toString()

        val oldHighscoreText = requireActivity().findViewById<TextView>(R.id.Old_Highscore_Amount)
        oldHighscoreText.text = viewModel.getMaxHighscore().toString()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Read score values from viewModel and put into text views
        getHighscoreValues()
        // Update max highscore if the new one is bigger. RESET current score to 0.
        viewModel.setMaxHighscore()
        viewModel.resetCurrentHighscore()
        // Save max highscore to sharedPreferences
        (activity as MainActivity).saveHighscore(viewModel.getMaxHighscore())

        val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup the buttons
        val playAgain = requireActivity().findViewById<Button>(R.id.Play_Again_Button)
        playAgain.setOnClickListener {
            viewModel.getQuestion()
            navController.navigate(R.id.action_gameOver_to_game_Screen)
        }

        val homeButton = requireActivity().findViewById<Button>(R.id.Home_Button)
        homeButton.setOnClickListener {
            navController.navigate(R.id.action_gameOver_to_navigation_home)
        }
    }
}