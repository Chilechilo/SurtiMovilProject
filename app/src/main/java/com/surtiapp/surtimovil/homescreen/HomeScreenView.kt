package com.surtiapp.surtimovil.homescreen

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.core.delivery.viewmodel.DeliveryViewModel
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory
import com.surtiapp.surtimovil.addcart.viewmodel.CartViewModel
import com.surtiapp.surtimovil.home.views.HomeViewProducts
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.camera.core.ExperimentalGetImage

/* ======= Bottom navigation setup ======= */
private data class TabItem(val titleRes: Int, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(
    navController: NavController,
    homeViewModelFactory: HomeViewModelFactory
) {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val cartViewModel: CartViewModel = viewModel()

    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var userName by rememberSaveable { mutableStateOf("Usuario") }

    var showHelpInsideAccount by rememberSaveable { mutableStateOf(false) }

    // 🔍 ESTE es el único estado del buscador
    var showSearchBar by rememberSaveable { mutableStateOf(false) }
    val SEARCH_INDEX = 99

    // Detectar regreso del login
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collectLatest { entry ->
            val fromLogin = entry.destination.route == "home" &&
                    entry.savedStateHandle?.get<Boolean>("loggedIn") == true
            if (fromLogin) {
                isLoggedIn = true
                userName = entry.savedStateHandle?.get<String>("username") ?: "Usuario"
            }
        }
    }

    Scaffold(
        topBar = {
            if (isLoggedIn) {
                CenterAlignedTopAppBar(
                    title = { Text("SurtiMóvil") },
                    actions = {
                        IconButton(onClick = {
                            selectedIndex = 3
                            showHelpInsideAccount = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Box {
                NavigationBar(containerColor = Color.White) {

                    NavigationBarItem(
                        selected = selectedIndex == 0,
                        onClick = { selectedIndex = 0 },
                        icon = { Icon(Icons.Default.List, "Catálogo") },
                        label = { Text("Catálogo") }
                    )

                    NavigationBarItem(
                        selected = selectedIndex == 1,
                        onClick = { selectedIndex = 1 },
                        icon = { Icon(Icons.Default.QrCode, "Pedidos") },
                        label = { Text("Pedidos") }
                    )

                    Spacer(modifier = Modifier.width(56.dp))

                    NavigationBarItem(
                        selected = selectedIndex == 2,
                        onClick = { selectedIndex = 2 },
                        icon = { Icon(Icons.Default.LocalOffer, "Ofertas") },
                        label = { Text("Ofertas") }
                    )

                    NavigationBarItem(
                        selected = selectedIndex == 3,
                        onClick = {
                            selectedIndex = 3
                            showHelpInsideAccount = false
                        },
                        icon = { Icon(Icons.Default.Person, "Mi cuenta") },
                        label = { Text("Mi Cuenta") }
                    )
                }

                FloatingActionButton(
                    onClick = {
                        selectedIndex = SEARCH_INDEX
                        showSearchBar = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-10).dp)
                        .size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
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

            if (selectedIndex == SEARCH_INDEX && showSearchBar) {
                SearchBar(onClose = {
                    showSearchBar = false
                    selectedIndex = 0   // regresar al catálogo o donde tú quieras
                })
            }

            when (selectedIndex) {
                0 -> CatalogoScreen(homeViewModelFactory, snackbarHostState, cartViewModel)
                1 -> PedidosScreen()
                2 -> OfertasScreen()
                3 -> CuentasScreen(
                    navController,
                    isLoggedIn,
                    userName,
                    onLogout = {
                        isLoggedIn = false
                        userName = ""
                    },
                    showHelp = showHelpInsideAccount,
                    onHelpClick = { showHelpInsideAccount = true },
                    onCloseHelp = { showHelpInsideAccount = false }
                )
            }
        }
    }
}

/* ======= Catálogo ======= */
@Composable
private fun CatalogoScreen(
    factory: HomeViewModelFactory,
    snackbarHostState: SnackbarHostState,
    cartViewModel: CartViewModel
) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by viewModel.ui.collectAsState()
    val localUserId = remember { "local_user_001" }

    LaunchedEffect(Unit) {
        viewModel.setUserId(localUserId)
        viewModel.fetchHome()
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessage()
        }
    }

    HomeViewProducts(uiState, viewModel, cartViewModel)
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
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Gestión de pedidos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        Button({
            val orderId = "order_12345"
            qrBitmap = generateQRCode(orderId)
            showQR = true
            showScanner = false
        }) {
            Text("Generar QR")
        }

        Spacer(Modifier.height(12.dp))

        Button({
            showScanner = true
            showQR = false
        }) {
            Text("Escanear QR")
        }

        Spacer(Modifier.height(24.dp))

        if (showQR && qrBitmap != null) {
            Image(bitmap = qrBitmap!!.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
        }

        if (showScanner) {
            RequestCameraPermission()
            QRScannerView { code ->
                showScanner = false
                viewModel.confirmDelivery(code)
            }
        }

        Spacer(Modifier.height(24.dp))

        when {
            uiState.loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Verificando entrega…")
                }
            }
            uiState.success == true -> Text("✅ ${uiState.message}", color = MaterialTheme.colorScheme.primary)
            uiState.success == false -> Text("❌ ${uiState.message}", color = MaterialTheme.colorScheme.error)
        }
    }
}

/* ======= QR Generator ======= */
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

/* ======= QR Scanner ======= */
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

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
            } catch (e: Exception) {
                Log.e("CameraX", "Error: ", e)
            }
        }, executor)

        previewView
    })
}

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
                barcodes.firstOrNull()?.rawValue?.let { onQRCodeScanned(it) }
            }
            .addOnFailureListener { Log.e("QRScanner", "Error", it) }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        imageProxy.close()
    }
}

/* ======= Pantallas ======= */
@Composable
private fun AyudaScreen() {
    CenterCard(
        title = "Centro de Ayuda",
        body = "Aquí encontrarás respuestas a preguntas frecuentes, información de contacto y soporte para resolver cualquier problema con tu cuenta o pedidos."
    )
}

@Composable
private fun OfertasScreen() {
    CenterCard(
        title = "Ofertas y Promociones",
        body = "Descubre nuestras promociones actuales y descuentos especiales. ¡Aprovecha y haz tu pedido ahora!"
    )
}

/* ======= Cuenta ======= */
@Composable
private fun CuentasScreen(
    navController: NavController,
    isLoggedIn: Boolean,
    userName: String,
    onLogout: () -> Unit,
    showHelp: Boolean,
    onHelpClick: () -> Unit,
    onCloseHelp: () -> Unit
) {
    if (showHelp) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Regresar",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onCloseHelp() }
            )

            Spacer(Modifier.height(16.dp))

            AyudaScreen()
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoggedIn) {

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(90.dp).clip(CircleShape)
            )

            Text(
                "Bienvenido, $userName",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )

            OutlinedButton(
                onClick = { onHelpClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Help, null)
                Spacer(Modifier.width(8.dp))
                Text("Centro de Ayuda")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onLogout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }

        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("☹️", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(16.dp))
                Text("No has iniciado sesión", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(24.dp))

                var isNavigating by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        if (!isNavigating) {
                            isNavigating = true
                            navController.navigate("login")
                        }
                    },
                    enabled = !isNavigating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isNavigating) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp).padding(end = 8.dp)
                        )
                        Text("Abriendo...")
                    } else {
                        Text("Iniciar sesión")
                    }
                }
            }
        }
    }
}

/* ======= Tarjeta genérica ======= */
@Composable
private fun CenterCard(title: String, body: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/* ======= Permiso de cámara ======= */
@Composable
fun RequestCameraPermission() {
    val context = LocalContext.current
    val permission = android.Manifest.permission.CAMERA
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) { launcher.launch(permission) }
}

/* ======= Search Bar ======= */
@Composable
fun SearchBar(onClose: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {

            Icon(Icons.Default.Search, "Buscar", tint = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.width(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Buscar productos…") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Spacer(Modifier.width(8.dp))

            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "Cerrar")
            }
        }
    }
}
