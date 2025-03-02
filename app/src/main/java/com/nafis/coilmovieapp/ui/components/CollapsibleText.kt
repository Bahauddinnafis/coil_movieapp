package com.nafis.coilmovieapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

@Composable
fun CollapsibleText(
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 3,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    var isExpanded = remember { mutableStateOf(false) }

    val annotatedString = buildAnnotatedString {
        if (!isExpanded.value) {
            withStyle(SpanStyle(color = Color.Blue)) {
                append("See more")
            }
        } else {
            withStyle(SpanStyle(color = Color.Blue)) {
                append("See less")
            }
        }
    }

    Column {
        Text(
            text = text,
            modifier = Modifier
                .clickable { isExpanded.value = !isExpanded.value },
            maxLines = if (isExpanded.value) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            style = style
        )
        Text(
            text = annotatedString,
            modifier = Modifier
                .clickable { isExpanded.value = !isExpanded.value },
            overflow = TextOverflow.Ellipsis,
            style = style
        )
    }

}