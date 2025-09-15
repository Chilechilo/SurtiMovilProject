package com.surtiapp.surtimovil.onboarding.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.surtiapp.surtimovil.onboarding.model.OnboardingPageModel

/**
 * Vista que tiene la pagina del onboarding (Imagen, titulo, descripción)
 */
@Composable
fun OnboardingPageView(pageModel: OnboardingPageModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // 👈 centra verticalmente todo
    ) {
        Image(
            painter = painterResource(id = pageModel.imageRes),
            contentDescription = stringResource(pageModel.titleRes),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
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
