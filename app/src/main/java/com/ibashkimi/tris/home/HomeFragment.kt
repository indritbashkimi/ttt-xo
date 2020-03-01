package com.ibashkimi.tris.home

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ibashkimi.tris.R
import com.ibashkimi.tris.databinding.FragmentHomeBinding
import com.ibashkimi.tris.game.GameFragment

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return FragmentHomeBinding.inflate(inflater, container, false).run {
            playerLevelView.setOnClickListener(this@HomeFragment)
            singlePlayer.setOnClickListener(this@HomeFragment)
            multiPlayer.setOnClickListener(this@HomeFragment)
            about.setOnClickListener(this@HomeFragment)
            root
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.playerLevelView -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.difficulty)
                    .setSingleChoiceItems(R.array.difficulty_array, playerLevel) { dialog, which ->
                        playerLevel = which
                        dialog.dismiss()
                    }
                    .show()
            }
            R.id.singlePlayer -> {
                findNavController(v).navigate(
                    HomeFragmentDirections.actionHomeToGame(
                        GameFragment.SINGLE_PLAYER, playerLevel
                    )
                )
            }
            R.id.multiPlayer -> {
                findNavController(v).navigate(
                    HomeFragmentDirections.actionHomeToGame(
                        GameFragment.MULTI_PLAYER, playerLevel
                    )
                )
            }
            R.id.about -> findNavController(v).navigate(R.id.action_home_to_about)
        }
    }

    private var playerLevel: Int
        get() = sharedPreferences.getInt("level", GameFragment.LEVEL_MINIMAX)
        set(level) = sharedPreferences.edit().putInt("level", level).apply()
}