package com.surtiapp.surtimovil.onboarding.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.surtiapp.surtimovil.R
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun BottomBarView(
    isLastPage: Boolean,
    page: Int,
    total: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        TextButton(
            enabled = page > 0,
            onClick = onPrev
        ) {
            Text(stringResource(R.string.prev))
        }

        Spacer(Modifier.weight(1f))

        Button(onClick = onNext) {
            Text(
                text = if (isLastPage)
                    stringResource(R.string.start)
                else
                    stringResource(R.string.next)
            )
        }
    }
}

