package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.cos
import kotlin.math.sin

// Representation of a point in 3D Space
data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun rotateY(angleRad: Float): Point3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Point3D(
            x = x * cosA + z * sinA,
            y = y,
            z = -x * sinA + z * cosA
        )
    }

    fun rotateX(angleRad: Float): Point3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Point3D(
            x = x,
            y = y * cosA - z * sinA,
            z = y * sinA + z * cosA
        )
    }
}

// Maps DNA nucleotides into 3D Bloch sphere vector endpoints
fun getDnaBlochVector(base: Char): Point3D {
    return when (base.uppercaseChar()) {
        'A' -> Point3D(0f, 0f, 1f)     // +Z: Pure state |0⟩
        'T' -> Point3D(0f, 0f, -1f)    // -Z: Pure state |1⟩
        'C' -> Point3D(1f, 0f, 0f)     // +X: Superposition |+⟩
        'G' -> Point3D(0f, 1f, 0f)     // +Y: Superposition |+i⟩
        else -> Point3D(0f, 0f, 0f)
    }
}

fun getDnaBaseColor(base: Char): Color {
    return when (base.uppercaseChar()) {
        'A' -> Color(0xFF00FF88) // Vivid Emerald
        'T' -> Color(0xFFFF00FF) // Cyber Magenta
        'C' -> Color(0xFF44AAFF) // Electric Blue
        'G' -> Color(0xFFFFAA00) // Neon Orange
        else -> Color.Gray
    }
}

@Composable
fun BlochSphereView(
    dnaSequence: String,
    modifier: Modifier = Modifier
) {
    // Rotation states
    var dragAngleX by remember { mutableFloatStateOf(0f) }
    var dragAngleY by remember { mutableFloatStateOf(0f) }

    // Auto-rotation animation loop for background movement
    val infiniteTransition = rememberInfiniteTransition(label = "BlochSphereRotation")
    val autoRotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * kotlin.math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AutoRotation"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // Drag Y affects rotation around X-axis, Drag X affects rotation around Y-axis
                    dragAngleX += dragAmount.y * 0.007f
                    dragAngleY += dragAmount.x * 0.007f
                }
            }
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        // Draw fitting sphere bounding radius
        val radius = (size.width.coerceAtMost(size.height) / 2f) * 0.72f

        // Combined angles (auto rotation + manual user dragging)
        val currentAngleY = autoRotationAngle + dragAngleY
        val currentAngleX = dragAngleX + 0.4f // subtle static pitch tilt

        // Function to project 3D coordinate on 2D Screen
        fun project(point: Point3D): Offset {
            val rotated = point.rotateY(currentAngleY).rotateX(currentAngleX)
            // Orthographic projection
            val xOffset = centerX + rotated.x * radius
            val yOffset = centerY - rotated.z * radius // Draw Z pointing up vertically
            return Offset(xOffset, yOffset)
        }

        // 1. Draw outer boundary ring representing the sphere profile
        drawCircle(
            color = Color(0xFF00D9FF).copy(alpha = 0.2f),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )

        // 2. Draw latitude / equatorial grid line ellipses (reconstructed with projected points)
        val equatorPath = Path()
        val steps = 48
        for (i in 0..steps) {
            val theta = (i * 2 * Math.PI / steps).toFloat()
            // Flat equatorial ring (Z = 0)
            val p = Point3D(cos(theta), sin(theta), 0f)
            val screenPos = project(p)
            if (i == 0) {
                equatorPath.moveTo(screenPos.x, screenPos.y)
            } else {
                equatorPath.lineTo(screenPos.x, screenPos.y)
            }
        }
        drawPath(
            path = equatorPath,
            color = Color(0xFF00D9FF).copy(alpha = 0.15f),
            style = Stroke(width = 1f)
        )

        // Draw vertical meridian ring (Y = 0)
        val meridianPath = Path()
        for (i in 0..steps) {
            val theta = (i * 2 * Math.PI / steps).toFloat()
            val p = Point3D(cos(theta), 0f, sin(theta))
            val screenPos = project(p)
            if (i == 0) {
                meridianPath.moveTo(screenPos.x, screenPos.y)
            } else {
                meridianPath.lineTo(screenPos.x, screenPos.y)
            }
        }
        drawPath(
            path = meridianPath,
            color = Color(0xFF00D9FF).copy(alpha = 0.1f),
            style = Stroke(width = 1f)
        )

        // 3. Draw standard cartesian reference axes (X, Y, Z)
        val origin = Point3D(0f, 0f, 0f)
        val xAxis = Point3D(1.2f, 0f, 0f)
        val yAxis = Point3D(0f, 1.2f, 0f)
        val zAxis = Point3D(0f, 0f, 1.2f)

        val screenOrigin = project(origin)
        val screenX = project(xAxis)
        val screenY = project(yAxis)
        val screenZ = project(zAxis)

        // Z-Axis (Vertical)
        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = screenOrigin,
            end = screenZ,
            strokeWidth = 2f
        )
        // X-Axis
        drawLine(
            color = Color(0xFFFF5555).copy(alpha = 0.35f),
            start = screenOrigin,
            end = screenX,
            strokeWidth = 1.5f
        )
        // Y-Axis
        drawLine(
            color = Color(0xFF55FF55).copy(alpha = 0.35f),
            start = screenOrigin,
            end = screenY,
            strokeWidth = 1.5f
        )

        // 4. Draw DNA Mapped State Vectors
        // Process each character to display its corresponding vector path
        val bases = dnaSequence.uppercase().filter { it in "ATCG" }
        bases.forEachIndexed { index, char ->
            val vectorEnd = getDnaBlochVector(char)
            if (vectorEnd != Point3D(0f, 0f, 0f)) {
                val baseColor = getDnaBaseColor(char)
                val screenEnd = project(vectorEnd)

                // Render vector line
                drawLine(
                    color = baseColor.copy(alpha = 0.85f),
                    start = screenOrigin,
                    end = screenEnd,
                    strokeWidth = 4f
                )

                // Render coordinate point glow sphere
                drawCircle(
                    color = baseColor,
                    radius = 8f,
                    center = screenEnd
                )

                // Draw secondary smaller dotted orbit connection lines
                drawLine(
                    color = baseColor.copy(alpha = 0.25f),
                    start = screenEnd,
                    end = Offset(screenEnd.x, centerY), // shadow onto equator plane
                    strokeWidth = 1.5f
                )
            }
        }
    }
}
