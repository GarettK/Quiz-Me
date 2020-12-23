package edu.utap.quiz_me.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import edu.utap.quiz_me.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner = requireActivity().findViewById<Spinner>(R.id.Difficulty_Spinner)
        val spinnerItems = resources.getStringArray(R.array.spinner_items)
        val Adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, spinnerItems)
        spinner.adapter = Adapter

        val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val startButton = requireActivity().findViewById<Button>(R.id.Start_Button)
        startButton.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_game_Screen)
        }
    }
}