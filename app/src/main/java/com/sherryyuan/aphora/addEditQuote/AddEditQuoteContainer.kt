package com.sherryyuan.aphora.addEditQuote

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sherryyuan.aphora.R
import com.sherryyuan.aphora.ui.common.AphoraCard
import com.sherryyuan.aphora.ui.common.VerticalSpacer
import com.sherryyuan.aphora.ui.theme.LikeIconRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditQuoteContainer(viewModel: AddEditQuoteViewModel) {
    val viewState by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    val quoteTextFieldState = rememberTextFieldState(viewState.existingQuote?.text.orEmpty())
    val noteTextFieldState = rememberTextFieldState(viewState.existingQuote?.userNote.orEmpty())
    var rating by remember {
        mutableIntStateOf(viewState.existingQuote?.rating ?: 1)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
        topBar = {
            TopAppBar(
                title = {
                    viewState.topBarTitleRes?.let { Text(stringResource(it)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            AphoraCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuoteInputField(quoteTextFieldState)
                    VerticalSpacer()
                    RatingHearts(
                        rating = rating,
                        onRatingUpdate = { updatedRating -> rating = updatedRating }
                    )
                    VerticalSpacer(height = 8.dp)
                    NotesInputField(noteTextFieldState)
                }
            }
            VerticalSpacer()
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.saveQuote(
                        text = quoteTextFieldState.text.toString(),
                    )
                }) {
                Text(stringResource(R.string.add_edit_quote_save_button))
            }
        }
    }
}

@Composable
fun QuoteInputField(textFieldState: TextFieldState, modifier: Modifier = Modifier) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        state = textFieldState,
        label = { Text(stringResource(R.string.add_edit_quote_aphorism_label)) },
        labelPosition = TextFieldLabelPosition.Attached(alwaysMinimize = true),
        placeholder = { Text(stringResource(R.string.add_edit_quote_aphorism_placeholder)) },
    )
}

@Composable
fun RatingHearts(
    modifier: Modifier = Modifier,
    rating: Int,
    onRatingUpdate: (Int) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            for (i in 1..5) {
                Image(
                    painter = painterResource(R.drawable.icon_heart),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            onRatingUpdate(i)
                        },
                    colorFilter = ColorFilter.tint(
                        if (i <= rating) LikeIconRed else Color.Gray.copy(alpha = 0.3f)
                    )
                )
            }
        }
        VerticalSpacer(8.dp)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.add_edit_quote_rating_low),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.add_edit_quote_rating_high),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun NotesInputField(textFieldState: TextFieldState, modifier: Modifier = Modifier) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        state = textFieldState,
        label = { Text(stringResource(R.string.add_edit_quote_notes_label)) },
        placeholder = { Text(stringResource(R.string.add_edit_quote_notes_placeholder)) },
    )
}
