package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.ui.theme.TicTacToeTheme

enum class Win{
    Player1,
    Player2,
    Draw,
    None
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TTTScreen()
                }
            }
        }
    }
}

@Composable
@Preview
fun TTTScreen() {
    val turn = remember { mutableStateOf(true) }
    val moves = remember { mutableStateListOf<Boolean?>(null, null, null, null, null, null, null, null, null) }
    val win = remember { mutableStateOf<Win?>(Win.None) }
    val player1Score = remember { mutableStateOf(0) }
    val player2Score = remember { mutableStateOf(0) }

    val onTap: (Offset) -> Unit = { offset ->
        if (win.value == Win.None) {
            val x = (offset.x / 333).toInt()
            val y = (offset.y / 333).toInt()
            val posInMoves = y * 3 + x
            if (moves[posInMoves] == null) {
                moves[posInMoves] = turn.value
                turn.value = !turn.value
                win.value = checkEndGame(moves)
                when (win.value) {
                    Win.Player1 -> player1Score.value += 1
                    Win.Player2 -> player2Score.value += 1
                    else -> {}
                }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))
        Header(turn.value, player1Score.value, player2Score.value)
        Board(moves, onTap)
        when (win.value) {
            Win.Player1 -> Text(text = "Player 1 has won \ud83c\uDF89", fontSize = 25.sp)
            Win.Player2 -> Text(text = "Player 2 has won \uD83C\uDF89", fontSize = 25.sp)
            Win.Draw -> Text(text = "Match draw \uD83C\uDF33", fontSize = 25.sp)
            else -> {}
        }
        Button(onClick = {
            turn.value = true
            win.value = Win.None
            for (i in 0..8) {
                moves[i] = null
            }
        }) {
            Text(text = "Reset Board")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            moves.fill(null)
            win.value = Win.None
            turn.value = true
            player1Score.value = 0
            player2Score.value = 0
        }) {
            Text(text = "Reset Scores")
        }
    }

}

fun checkEndGame(m: List<Boolean?>): Win {
    // Define all possible winning positions
    val winningPositions = listOf(
        listOf(0, 1, 2), // Top row
        listOf(3, 4, 5), // Middle row
        listOf(6, 7, 8), // Bottom row
        listOf(0, 3, 6), // Left column
        listOf(1, 4, 7), // Middle column
        listOf(2, 5, 8), // Right column
        listOf(0, 4, 8), // Diagonal from top-left to bottom-right
        listOf(2, 4, 6)  // Diagonal from top-right to bottom-left
    )

    // Check for Player1 (true) win
    for (positions in winningPositions) {
        if (positions.all { m[it] == true }) {
            return Win.Player1
        }
    }

    // Check for Player2 (false) win
    for (positions in winningPositions) {
        if (positions.all { m[it] == false }) {
            return Win.Player2
        }
    }

    // Check for draw (all cells are filled and no winner)
    if (m.all { it != null }) {
        return Win.Draw
    }

    return Win.None
}

@Composable
fun Header(turn: Boolean, player1Score: Int, player2Score: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val player1Color = if (turn) Color.Blue else Color.LightGray
        val player2Color = if (turn) Color.LightGray else Color.Red


        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .background(player1Color)
            ) {
                Text(
                    text = "Player1", modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center)
                )
            }
            Text(text = "Score: $player1Score", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(50.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .background(player2Color)
            ) {
                Text(
                    text = "Player2", modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Center)
                )
            }
            Text(text = "Score: $player2Score", fontSize = 20.sp)
        }
    }
}

@Composable
fun Board(moves: List<Boolean?>, onTap: (Offset) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(32.dp)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(onTap = onTap)
            }
    ) {
        Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f)) {
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(1f)
                    .background(Color.Black)
            ) {}
            Row(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(1f)
                    .background(Color.Black)
            ) {}


        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxSize(1f)) {
            Column(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(1f)
                    .background(Color.Black)
            ) {}
            Column(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight(1f)
                    .background(Color.Black)
            ) {}

        }
        Column(modifier = Modifier.fillMaxSize(1f)) {
            for (i in 0..2){
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..2){
                        Column(modifier = Modifier.weight(1f)) {
                            getComposableForMoves(move = moves[i*3+j])

                        }
                    }

                }
            }

        }

    }
}

@Composable
fun getComposableForMoves(move: Boolean?) {
    when (move) {
        true -> Image(
            painter = painterResource(id = R.drawable.ic_x),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            colorFilter = ColorFilter.tint(Color.Blue)
        )

        false -> Image(
            painter = painterResource(id = R.drawable.ic_o),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            colorFilter = ColorFilter.tint(Color.Red)
        )

        null -> Image(
            painter = painterResource(id = R.drawable.ic_null),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f)
        )

    }

}