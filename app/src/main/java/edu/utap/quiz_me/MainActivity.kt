package edu.utap.quiz_me

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    private val SAVE_KEY = "savedHighscores"
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //https://stackoverflow.com/questions/36236181/how-to-remove-title-bar-from-the-android-activity
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        loadHighscore()
    }

    //How to save data with shared preferences:
    //https://www.youtube.com/watch?v=S5uLAGnBvUY
    fun saveHighscore(newScore: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putInt(SAVE_KEY, newScore)
        }.apply()
        //Log.d("HELP!", "SAVED!!!!!!!!!!!!")
    }

    private fun loadHighscore() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val savedScore: Int = sharedPreferences.getInt(SAVE_KEY, 0)
        viewModel.loadOldHighscore(savedScore)
        //Log.d("HELP!", "LoadHighscore Called!")
    }
}