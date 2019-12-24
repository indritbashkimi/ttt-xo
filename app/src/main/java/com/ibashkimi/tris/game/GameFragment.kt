package com.ibashkimi.tris.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ibashkimi.tris.R
import com.ibashkimi.tris.databinding.FragmentGameBinding
import com.ibashkimi.tris.model.Board
import com.ibashkimi.tris.model.ButtonFieldChannel
import com.ibashkimi.tris.model.Game
import com.ibashkimi.tris.model.player.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    private val viewModel: GameViewModel by viewModels()

    private val args: GameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGameBinding.inflate(inflater, container, false)

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.ic_back_nav)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        binding.boardView.clicks().onEach {
            ButtonFieldChannel.onButtonPressed(it)
        }.launchIn(lifecycleScope)

        val game = if (savedInstanceState != null) {
            GameSessionManager.restoreGame(savedInstanceState)
                ?: throw IllegalStateException("Cannot restore game.")
        } else {
            createNewGame(args.mode, args.level)
        }

        binding.playAgain.setOnClickListener {
            viewModel.game.value?.let {
                val newGame = Game(
                    Board(),
                    Pair(it.players.second, it.players.first),
                    0,
                    it.stats
                )
                viewModel.game.value = newGame
            }
        }

        viewModel.gameEvents.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            onGameEvent(it)
        })

        viewModel.game.value = game

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.game.value?.let {
            GameSessionManager.saveGame(it, outState)
        }
    }

    private fun onGameEvent(event: Game.Event) {
        when (event) {
            is Game.Event.GameCreated -> {
                binding.boardView.reset(event.game.board.array)
                showTurn(event.game.activePlayer.name)
                updateScore(event.game)
            }
            is Game.Event.GameStarted -> {
                hidePlayAgain()
            }
            is Game.Event.MoveMade -> {
                binding.boardView.setOwner(event.move, event.player.seed)
            }
            is Game.Event.TurnChanged -> {
                showTurn(event.player.name)
            }
            is Game.Event.GameOver.Draw -> {
                binding.boardView.disable()
                showDrawResult()
                showPlayAgain()
                updateScore(event.game)
            }
            is Game.Event.GameOver.WithWinner -> {
                showWinner(event.player.name)
                binding.boardView.disable()
                binding.boardView.highlight(event.moves)
                showPlayAgain()
                updateScore(event.game)
            }
        }
    }

    private fun showTurn(turn: String) {
        binding.toolbar.title = getString(R.string.turn, turn)
    }

    private fun showDrawResult() {
        binding.toolbar.title = getString(R.string.draw)
        binding.boardView.disable()
        showPlayAgain()
    }

    private fun showPlayAgain() {
        binding.playAgain.visibility = View.VISIBLE
    }

    private fun hidePlayAgain() {
        binding.playAgain.visibility = View.GONE
    }

    private fun showWinner(winner: String) {
        binding.toolbar.title = getString(R.string.winner, winner)
    }

    private fun updateScore(game: Game) {
        val xScore: Long
        val oScore: Long
        if (game.players.first.name == "X") {
            xScore = game.players.first.score
            oScore = game.players.second.score
        } else {
            xScore = game.players.second.score
            oScore = game.players.first.score
        }
        updateScore(xScore, oScore, game.stats.drawGames)
    }

    private fun updateScore(xScore: Long, oScore: Long, draw: Long) {
        binding.apply {
            xWinsView.text = String.format(Locale.ENGLISH, "%d", xScore)
            oWinsView.text = String.format(Locale.ENGLISH, "%d", oScore)
            drawView.text = String.format(Locale.ENGLISH, "%d", draw)
        }
    }

    private fun createNewGame(mode: Int, level: Int = 0): Game {
        val board = Board()
        val player1 = HumanPlayer("X", Board.PLAYER_1, 0)
        val player2 = when (mode) {
            SINGLE_PLAYER -> when (level) {
                LEVEL_EASY -> EasyPlayer("0", Board.PLAYER_2, 0)
                LEVEL_NORMAL -> NormalPlayer("0", Board.PLAYER_2, 0)
                LEVEL_HARD -> HardPlayer("0", Board.PLAYER_2, 0)
                LEVEL_MINIMAX -> MinimaxPlayer("0", Board.PLAYER_2, 0)
                else -> throw Exception("Invalid player level: $level.")
            }
            MULTI_PLAYER -> HumanPlayer("O", Board.PLAYER_2, 0)
            else -> throw Exception("Invalid game mode extra: $mode.")
        }
        return Game(board, Pair(player1, player2), 0, Game.GameStats())
    }

    companion object {
        const val SINGLE_PLAYER = 0
        const val MULTI_PLAYER = 1

        const val LEVEL_EASY = 0
        const val LEVEL_NORMAL = 1
        const val LEVEL_HARD = 2
        const val LEVEL_MINIMAX = 3
    }
}