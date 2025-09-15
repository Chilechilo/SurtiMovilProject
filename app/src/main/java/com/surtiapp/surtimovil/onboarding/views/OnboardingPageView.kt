package com.surtiapp.surtimovil.onboarding.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.surtiapp.surtimovil.onboarding.model.OnboardingPageModel

@Composable
fun OnboardingPageView(pageModel: OnboardingPageModel) {
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Marco cuadrado + recorte para llenar
        Image(
            painter = painterResource(id = pageModel.imageRes),
            contentDescription = stringResource(pageModel.titleRes),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(240.dp)
                .clip(shape)
                .border(2.dp, MaterialTheme.colorScheme.primary, shape)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(pageModel.titleRes),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(pageModel.descriptionRes),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
