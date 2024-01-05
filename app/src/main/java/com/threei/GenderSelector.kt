package com.threei

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun GenderSelector(
    modifier: Modifier = Modifier,
    maleGradient: List<Color> = listOf(Color.White, Color(0xffADD8E6), Color.Blue),
    femaleGradient: List<Color> = listOf(Color.White, Color(0xffff80ff), Color.Magenta),
    distanceBetweenGender: Dp = 25.dp,
    scaleFactor: Float = 7f,
    onGenderSelected: (Gender) -> Unit
) {

    val maleString = stringResource(id = R.string.male_path)
    val femaleString = stringResource(id = R.string.female_path)

    val malePath = remember {
        PathParser().parsePathString(maleString).toPath()
    }
    val femalePath = remember {
        PathParser().parsePathString(femaleString).toPath()
    }

    var selectedGender by remember {
        mutableStateOf<Gender>(Gender.Female)
    }

    val maleBounds = remember {
        malePath.getBounds()
    }

    val femaleBounds = remember {
        femalePath.getBounds()
    }

    var maleTransformationCoordinate by remember {
        mutableStateOf(Offset.Zero)
    }

    var femaleTransformationCoordinate by remember {
        mutableStateOf(Offset.Zero)
    }

    var currentOffset by remember {
        mutableStateOf(Offset.Zero)
    }

    val maleSelectionRadius =
        animateFloatAsState(
            targetValue = if (selectedGender is Gender.Male) 80f else 0f,
            label = "maleRadius",
            animationSpec = tween(500)
        )

    val femaleSelectionRadius =
        animateFloatAsState(
            targetValue = if (selectedGender is Gender.Female) 80f else 0f,
            label = "femaleRadius",
            animationSpec = tween(500)
        )

    Canvas(modifier = modifier.pointerInput(true) {
        detectTapGestures {
            val transformedMaleRect = Rect(
                offset = maleTransformationCoordinate,
                size = maleBounds.size * scaleFactor
            )

            val transformedFemaleRect = Rect(
                offset = femaleTransformationCoordinate,
                size = femaleBounds.size * scaleFactor
            )

            if (selectedGender !is Gender.Male && transformedMaleRect.contains(it)) {
                currentOffset = it
                selectedGender = Gender.Male
                onGenderSelected(Gender.Male)
            } else if (selectedGender !is Gender.Female && transformedFemaleRect.contains(it)) {
                currentOffset = it
                selectedGender = Gender.Female
                onGenderSelected(Gender.Female)
            }
        }

    }) {

        maleTransformationCoordinate = Offset(
            x = center.x - maleBounds.width * scaleFactor - distanceBetweenGender.toPx() / 2f,
            y = center.y - maleBounds.height * scaleFactor / 2f
        )

        femaleTransformationCoordinate = Offset(
            x = center.x + distanceBetweenGender.toPx() / 2f,
            y = center.y - femaleBounds.height * scaleFactor / 2f
        )

        val untransformedMaleCoordinates =
            if (currentOffset == Offset.Zero) maleBounds.center else ((currentOffset - maleTransformationCoordinate) / scaleFactor)

        val untransformedFemaleCoordinates =
            if (currentOffset == Offset.Zero) femaleBounds.center else ((currentOffset - femaleTransformationCoordinate) / scaleFactor)

        translate(
            top = maleTransformationCoordinate.y,
            left = maleTransformationCoordinate.x
        ) {
            scale(scale = scaleFactor, pivot = maleBounds.topLeft) {
                drawPath(
                    path = malePath,
                    color = Color.LightGray
                )
                clipPath(path = malePath) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = maleGradient,
                            radius = maleSelectionRadius.value + 1f,
                            center = untransformedMaleCoordinates
                        ),
                        radius = maleSelectionRadius.value,
                        center = untransformedMaleCoordinates
                    )
                }
            }

        }

        translate(
            top = femaleTransformationCoordinate.y,
            left = femaleTransformationCoordinate.x
        ) {
            scale(scale = scaleFactor, pivot = femaleBounds.topLeft) {
                drawPath(
                    path = femalePath,
                    color = Color.LightGray
                )
                clipPath(path = femalePath) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = femaleGradient,
                            radius = femaleSelectionRadius.value + 1f,
                            center = untransformedFemaleCoordinates
                        ),
                        radius = femaleSelectionRadius.value,
                        center = untransformedFemaleCoordinates
                    )
                }
            }
        }
    }
}

