package com.stepbeatgo.app.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

/** A number that smoothly counts up/down to its target value — used for the
 * result screen so the headline figure feels alive rather than just popping
 * in, echoing the tempo/counting theme of the app. */
@Composable
fun AnimatedCounter(targetValue: Int, style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.displayLarge) {
    val animated by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 700),
        label = "counter"
    )
    Text(text = animated.toString(), style = style)
}

@Composable
fun Pill(text: String, containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    )
}
