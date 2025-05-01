package com.csci448.bam.conspirators.ui.createboard

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp

suspend fun PointerInputScope.customDetectTransformGestures(
    onGesture: (
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        fingerCount: Int,
        pointerPositions: List<Offset>
    ) -> Unit,
    onTap: (Offset) -> Unit
) {
    awaitPointerEventScope {
        while (true) {
            val down = awaitFirstDown()

            val event = awaitPointerEvent(PointerEventPass.Main)
            val touchPoints = event.changes.filter { it.pressed }
            if (touchPoints.size < 2 && touchPoints.all { it.changedToUp() }) {
                break
            }
            if (touchPoints.isEmpty()) continue

            // Compute centroid and pan
            val positions = touchPoints.map { it.position }
            val previousPositions = touchPoints.map { it.previousPosition }

            val centroid = positions.reduce { acc, offset -> acc + offset } / positions.size.toFloat()
            val previousCentroid =
                if (previousPositions.size == positions.size)
                    previousPositions.reduce { acc, offset -> acc + offset } / previousPositions.size.toFloat()
                else centroid

            val pan = centroid - previousCentroid

            // zoom
            val zoom = if (positions.size >= 2 && previousPositions.size == positions.size) {
                val currentDist = (positions[0] - positions[1]).getDistance()
                val prevDist = (previousPositions[0] - previousPositions[1]).getDistance()
                if (prevDist != 0f) currentDist / prevDist else 1f
            } else 1f

            // rotation
            val rotation = 0f
            onGesture(
                centroid,
                pan,
                zoom,
                rotation,
                positions.size,
                positions
            )

            // Consume all changes
            event.changes.forEach { it.consume() }

        }
    }
}

suspend fun PointerInputScope.customDetectTransformGestures2(
    onGesture: (
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        fingerCount: Int,
        pointerPositions: List<Offset>
    ) -> Unit,
    onTap: (Offset) -> Unit,
    onGestureEnd: () -> Unit
) {
    awaitPointerEventScope {
        while (true) {
            val down = awaitFirstDown()
            val downTime = System.currentTimeMillis()
            var pastTouchSlop = false
            var pan = Offset.Zero
            var zoom = 1f

            do {
                val event = awaitPointerEvent()
                val changes = event.changes
                val pressed = changes.filter { it.pressed }
                val previous = changes.mapNotNull { it.previousPosition }

                if (pressed.size == 2 && previous.size == 2) {
                    val currentDist = (pressed[0].position - pressed[1].position).getDistance()
                    val prevDist = (previous[0] - previous[1]).getDistance()
                    zoom = if (prevDist != 0f) currentDist / prevDist else 1f
                    pan = Offset.Zero
                    pastTouchSlop = true
                } else if (pressed.size == 1 && previous.size == 1) {
                    pan = pressed[0].position - previous[0]
                    if (pan.getDistance() > viewConfiguration.touchSlop) {
                        pastTouchSlop = true
                    }
                }

                val centroid = pressed.map { it.position }
                    .reduceOrNull { acc, offset -> acc + offset }
                    ?.div(pressed.size.toFloat()) ?: Offset.Zero

                if (pastTouchSlop) {
                    onGesture(centroid, pan, zoom, 0f, pressed.size, pressed.map { it.position })
                }

                if (changes.all { !it.pressed }) {
                    onGestureEnd()
                }

                changes.forEach { it.consume() }
            } while (changes.any { it.pressed })

            val tapDuration = System.currentTimeMillis() - downTime
            if (!pastTouchSlop && tapDuration < 200) {
                onTap(down.position)
            }
        }
    }
}


suspend fun PointerInputScope.customDetectTransformGestures3(
    onGesture: (
        centroid: Offset,
        pan: Offset,
        zoom: Float,
        rotation: Float,
        fingerCount: Int,
        pointerPositions: List<Offset>,
    ) -> Unit,
    onTap: (Offset) -> Unit
) {
    awaitPointerEventScope {
        while (true) {
            val down = awaitFirstDown()
            val downTime = System.currentTimeMillis()
            var pointerId = down.id

            var pastTouchSlop = false
            var zoom = 1f
            var pan = Offset.Zero

            var event: PointerEvent

            do {
                event = awaitPointerEvent()
                val changes = event.changes

                val positions = changes.filter { it.pressed }.map { it.position }
                val previous = changes.mapNotNull { it.previousPosition }
                if (positions.isEmpty()) {

                }
                if (positions.size == 2 && previous.size == 2) {
                    val currentDist = (positions[0] - positions[1]).getDistance()
                    val prevDist = (previous[0] - previous[1]).getDistance()
                    zoom = if (prevDist != 0f) currentDist / prevDist else 1f
                    pan = Offset.Zero
                    pastTouchSlop = true
                } else if (positions.size == 1 && previous.size == 1) {
                    pan = positions[0] - previous[0]
                    if (pan.getDistance() > viewConfiguration.touchSlop) {
                        pastTouchSlop = true
                    }
                }

                val centroid = positions.reduceOrNull { acc, p -> acc + p }?.div(positions.size.toFloat())
                    ?: Offset.Zero

                if (pastTouchSlop) {
                    onGesture(centroid, pan, zoom, 0f, positions.size, positions)
                }

                changes.forEach { it.consume() }
            } while (event.changes.any { it.pressed })

            val tapDuration = System.currentTimeMillis() - downTime
            if (!pastTouchSlop && tapDuration < 200) {
                onTap(down.position)
            }
        }
    }
}