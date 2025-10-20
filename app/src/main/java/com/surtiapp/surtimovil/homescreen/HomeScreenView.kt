package com.surtiapp.surtimovil.homescreen

import RequestCameraPermission
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.core.delivery.viewmodel.DeliveryViewModel
import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.core.homescreen.repository.HomeRepository
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory
import com.surtiapp.surtimovil.homescreen.home.views.HomeViewProducts
import com.surtiapp.surtimovil.Addcarrito.viewmodel.CarritoViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/* ======= Bottom navigation setup ======= */
private data class TabItem(val titleRes: Int, val icon: ImageVector)

@Composable
fun HomeScreenView(
    navController: NavController,
    homeViewModelFactory: HomeViewModelFactory
) {
    val tabs = listOf(
        TabItem(R.string.tab_catalogo, Icons.Filled.List),
        TabItem(R.string.tab_pedidos, Icons.Filled.QrCode),
        TabItem(R.string.tab_ayuda, Icons.Filled.Help),
        TabItem(R.string.tab_ofertas, Icons.Filled.LocalOffer),
        TabItem(R.string.tab_mi_cuenta, Icons.Filled.Person),
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val carritoViewModel: CarritoViewModel = viewModel()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = stringResource(item.titleRes)) },
                        label = { Text(stringResource(item.titleRes)) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedIndex) {
                0 -> CatalogoScreen(
                    factory = homeViewModelFactory,
                    snackbarHostState = snackbarHostState,
                    carritoViewModel = carritoViewModel
                )
                1 -> PedidosScreen() // mantiene el lector QR
                2 -> AyudaScreen()
                3 -> OfertasScreen()
                4 -> CuentasScreen(navController)
            }
        }
    }
}

/* ======= Catálogo ======= */
@Composable
private fun CatalogoScreen(
    factory: HomeViewModelFactory,
    snackbarHostState: SnackbarHostState,
    carritoViewModel: CarritoViewModel
) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.ui.collectAsState()
    val localUserId = remember { "local_user_001" }

    LaunchedEffect(Unit) {
        viewModel.setUserId(localUserId)
        viewModel.fetchHome()
    }

    LaunchedEffect(key1 = uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessage()
        }
    }

    HomeViewProducts(
        uiState = uiState,
        viewModel = viewModel,
        carritoViewModel = carritoViewModel
    )
}

/* ======= Pedidos (QR Scanner + Generador) ======= */
@Composable
fun PedidosScreen() {
    var showQR by rememberSaveable { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showScanner by rememberSaveable { mutableStateOf(false) }

    val viewModel = remember { DeliveryViewModel() }
    val uiState by viewModel.ui.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Gestión de pedidos",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            val orderId = "order_12345"
            qrBitmap = generateQRCode(orderId)
            showQR = true
            showScanner = false
        }) {
            Text("Generar QR")
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            showScanner = true
            showQR = false
        }) {
            Text("Escanear QR")
        }

        Spacer(Modifier.height(24.dp))

        if (showQR && qrBitmap != null) {
            Image(
                bitmap = qrBitmap!!.asImageBitmap(),
                contentDescription = "Código QR de pedido",
                modifier = Modifier.size(200.dp)
            )
        }

        if (showScanner) {
            RequestCameraPermission()
            QRScannerView(onQRCodeScanned = { code ->
                showScanner = false
                viewModel.confirmDelivery(code)
            })
        }

        Spacer(Modifier.height(24.dp))

        when {
            uiState.loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Verificando entrega...")
                }
            }
            uiState.success == true -> {
                Text(
                    text = "✅ ${uiState.message}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            uiState.success == false -> {
                Text(
                    text = "❌ ${uiState.message}",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Genera un QR bitmap */
private fun generateQRCode(text: String): Bitmap {
    val size = 512
    val bits = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp.setPixel(x, y, if (bits[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bmp
}

/** Composable QR scanner con ML Kit */
@Composable
fun QRScannerView(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx)
        val executor = ContextCompat.getMainExecutor(ctx)
        val scanner = BarcodeScanning.getClient()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor) { imageProxy ->
                        processImageProxy(scanner, imageProxy, onQRCodeScanned)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analyzer)
            } catch (e: Exception) {
                Log.e("CameraX", "Error iniciando cámara", e)
            }
        }, executor)

        previewView
    })
}

/** Procesa frames de la cámara para detectar QR */
@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onQRCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { onQRCodeScanned(it) }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QRScanner", "Error al escanear", e)
            }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        imageProxy.close()
    }
}

/* ======= Pantallas secundarias ======= */
@Composable
private fun AyudaScreen() {
    CenterCard(
        title = stringResource(R.string.tab_ayuda),
        body = stringResource(R.string.ayuda_body)
    )
}

@Composable
private fun OfertasScreen() {
    CenterCard(
        title = stringResource(R.string.tab_ofertas),
        body = stringResource(R.string.ofertas_body)
    )
}

@Composable
private fun CuentasScreen(navController: NavController) {
    CenterCard(
        title = stringResource(R.string.tab_mi_cuenta),
        body = stringResource(R.string.cuenta_body)
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { navController.navigate("login") }) {
            Text(stringResource(R.string.go_login))
        }
    }
}

/* ======= Reutilizable ======= */
@Composable
private fun CenterCard(title: String, body: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(text = body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
