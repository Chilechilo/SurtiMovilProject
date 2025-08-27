package com.surtiapp.surtimovil.onboarding.views

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.surtiapp.surtimovil.onboarding.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

@Composable
fun OnboardingView(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit = {} // <-- NUEVO
) {
    val pages by viewModel.pages.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { pages.size }
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Sync VM <-> Pager
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setPage(pagerState.currentPage)
    }
    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.scrollToPage(currentPage)
        }
    }

    Scaffold(
        bottomBar = {
            BottomBarView(
                isLastPage = viewModel.isLastPage(),
                page = currentPage,
                total = pages.size,
                onPrev = {
                    if (currentPage > 0) {
                        scope.launch { pagerState.animateScrollToPage(currentPage - 1) }
                    }
                },
                onNext = {
                    if (!viewModel.isLastPage()) {
                        scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                    } else {
                        onFinish() // <-- GUARDAR FLAG (se hará desde Main)
                        Toast.makeText(context, "Onboarding finished", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingPageView(pageModel = pages[page])
            }

            DotsIndicatorView(
                totalDots = pages.size,
                selectedIndex = currentPage,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 16.dp)
            )
        }
    }
}