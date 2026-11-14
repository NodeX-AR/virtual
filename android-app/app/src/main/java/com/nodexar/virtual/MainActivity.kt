package com.nodexar.virtual

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.sceneview.ar.PlacementScene
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CarArScreen() }
    }
}

@Composable
private fun CarArScreen() {
    var placed by remember { mutableStateOf(false) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var showControls by remember { mutableStateOf(false) }
    var placementVersion by remember { mutableIntStateOf(0) }
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    Box(modifier = Modifier.fillMaxSize()) {
        key(placementVersion) {
            PlacementScene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,
                planeRenderer = true,
                onPlaced = {
                    placed = true
                    val car = rememberModelInstance(modelLoader, "models/car.glb")
                    car?.let {
                        ModelNode(
                            modelInstance = it,
                            scaleToUnits = 4.5f,
                            autoAnimate = true,
                            centerOrigin = io.github.sceneview.math.Position(y = -1f),
                            rotation = Rotation(y = rotation),
                            scale = Scale(scale)
                        )
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xDD17191C),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (!placed) "Move your phone to find the ground"
                    else "Car anchored to the detected floor",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xDD17191C))
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showControls) {
                Text("Rotate", color = Color.White)
                Slider(value = rotation, onValueChange = { rotation = it }, valueRange = -180f..180f)
                Text("Size", color = Color.White)
                Slider(value = scale, onValueChange = { scale = it }, valueRange = .3f..2f)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { showControls = !showControls }) {
                    Text(if (showControls) "Hide controls" else "Model")
                }
                Button(onClick = { placed = false; placementVersion += 1 }) { Text("Re-place car") }
            }
        }
    }
}
