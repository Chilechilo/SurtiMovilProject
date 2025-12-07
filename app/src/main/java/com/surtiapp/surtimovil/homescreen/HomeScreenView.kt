package com.surtiapp.surtimovil.homescreen

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
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
import com.surtiapp.surtimovil.core.orders.model.Order
import com.surtiapp.surtimovil.core.orders.model.OrderStatus
import com.surtiapp.surtimovil.core.orders.viewmodel.OrdersViewModel
import com.surtiapp.surtimovil.homescreen.home.HomeViewModel
import com.surtiapp.surtimovil.homescreen.home.login.HomeViewModelFactory
import com.surtiapp.surtimovil.addcart.viewmodel.CartViewModel
import com.surtiapp.surtimovil.home.views.HomeViewProducts
import com.surtiapp.surtimovil.core.offers.views.OffersView
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.surtiapp.surtimovil.addcart.model.ProductDetailModal
import com.surtiapp.surtimovil.addcart.model.Producto
import com.surtiapp.surtimovil.core.homescreen.model.network.HomeApi
import com.surtiapp.surtimovil.core.offers.viewmodel.OffersViewModel
import com.surtiapp.surtimovil.core.offers.viewmodel.OffersViewModelFactory
import com.surtiapp.surtimovil.homescreen.model.dto.Product
import com.surtiapp.surtimovil.homescreen.repository.OffersRepository
import com.surtiapp.surtimovil.login.model.network.RetrofitProvider
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/* ======= Bottom navigation setup ======= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(
    navController: NavController,
    homeViewModelFactory: HomeViewModelFactory
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val cartViewModel: CartViewModel = viewModel()
    val ordersViewModel: OrdersViewModel = viewModel()

    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory)

    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var userName by rememberSaveable { mutableStateOf("Usuario") }

    var showHelpInsideAccount by rememberSaveable { mutableStateOf(false) }

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
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = {
                            selectedIndex = 3
                            showHelpInsideAccount = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = stringResource(R.string.profile_icon_cd),
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
                        icon = { Icon(Icons.Default.List, stringResource(R.string.catalog_icon_cd)) },
                        label = { Text(stringResource(R.string.catalog_tab_label)) }
                    )

                    NavigationBarItem(
                        selected = selectedIndex == 1,
                        onClick = { selectedIndex = 1 },
                        icon = { Icon(Icons.Default.QrCode, stringResource(R.string.orders_icon_cd)) },
                        label = { Text(stringResource(R.string.orders_tab_label)) }
                    )

                    Spacer(modifier = Modifier.width(56.dp))

                    NavigationBarItem(
                        selected = selectedIndex == 2,
                        onClick = { selectedIndex = 2 },
                        icon = { Icon(Icons.Default.LocalOffer, stringResource(R.string.offers_icon_cd)) },
                        label = { Text(stringResource(R.string.offers_tab_label)) }
                    )

                    NavigationBarItem(
                        selected = selectedIndex == 3,
                        onClick = {
                            selectedIndex = 3
                            showHelpInsideAccount = false
                        },
                        icon = { Icon(Icons.Default.Person, stringResource(R.string.account_icon_cd)) },
                        label = { Text(stringResource(R.string.account_tab_label)) }
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
                        contentDescription = stringResource(R.string.search_cd),
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
                SearchBar(
                    homeViewModel = homeViewModel,
                    cartViewModel = cartViewModel,
                    snackbarHostState = snackbarHostState,
                    onClose = {
                        showSearchBar = false
                        selectedIndex = 0
                    }
                )
            }

            when (selectedIndex) {
                0 -> CatalogoScreen(homeViewModelFactory, snackbarHostState, cartViewModel, ordersViewModel)
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
    cartViewModel: CartViewModel,
    ordersViewModel: OrdersViewModel
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

    HomeViewProducts(uiState, viewModel, cartViewModel, ordersViewModel)
}

/* ======= Pedidos (Lista + QR Scanner + Generador) ======= */
@Composable
fun PedidosScreen() {
    var showQRSection by rememberSaveable { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showScanner by rememberSaveable { mutableStateOf(false) }

    val deliveryViewModel = remember { DeliveryViewModel() }
    val deliveryUiState by deliveryViewModel.ui.collectAsState()

    val ordersViewModel: OrdersViewModel = viewModel()
    val ordersUiState by ordersViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Título y botones QR
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.orders_management_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val orderId = "order_12345"
                            qrBitmap = generateQRCode(orderId)
                            showQRSection = true
                            showScanner = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.QrCode, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.generate_qr_button))
                    }

                    Button(
                        onClick = {
                            showScanner = true
                            showQRSection = true
                            qrBitmap = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.scan_qr_button))
                    }
                }

                // Sección QR (colapsable)
                AnimatedVisibility(visible = showQRSection) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!showScanner && qrBitmap != null) {
                            Card(
                                modifier = Modifier.size(220.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        bitmap = qrBitmap!!.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        if (showScanner) {
                            RequestCameraPermission()
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                QRScannerView { code ->
                                    showScanner = false
                                    showQRSection = false
                                    deliveryViewModel.confirmDelivery(code)
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Estado de entrega
                        when {
                            deliveryUiState.loading -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        stringResource(R.string.verifying_delivery),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            deliveryUiState.success == true -> {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            deliveryUiState.message ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            deliveryUiState.success == false -> {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Error,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            deliveryUiState.message ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }

                        if (showQRSection) {
                            TextButton(onClick = {
                                showQRSection = false
                                showScanner = false
                                qrBitmap = null
                            }) {
                                Text(stringResource(R.string.close_icon_cd))
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Lista de pedidos
        OrdersList(orders = ordersUiState.orders)
    }
}

/* ======= Lista de Pedidos ======= */
@Composable
private fun OrdersList(orders: List<Order>) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.no_orders_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.my_orders_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(orders, key = { it.id }) { order ->
            OrderCard(order = order)
        }
    }
}

/* ======= Tarjeta de Pedido ======= */
@Composable
private fun OrderCard(order: Order) {
    var expanded by remember { mutableStateOf(false) }

    val containerColor = when (order.status) {
        OrderStatus.DELIVERED -> Color(0xFFE8F5E9) // Verde claro
        OrderStatus.PENDING -> Color(0xFFFFF9C4) // Amarillo claro
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) // Rojo claro
    }

    val borderColor = when (order.status) {
        OrderStatus.DELIVERED -> Color(0xFF4CAF50) // Verde
        OrderStatus.PENDING -> Color(0xFFFFC107) // Amarillo
        OrderStatus.CANCELLED -> Color(0xFFF44336) // Rojo
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado del pedido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ID: ${order.id}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stringResource(R.string.order_date_label)} ${order.getFormattedDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = borderColor
                )
            }

            Spacer(Modifier.height(8.dp))

            // Total y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.order_total_label)} ${order.getFormattedTotal()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = borderColor,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = getStatusText(order.status),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Lista de productos (expandible)
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = borderColor.copy(alpha = 0.3f)
                    )

                    Text(
                        text = stringResource(R.string.order_products_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    order.products.forEach { product ->
                        ProductItem(product = product)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            if (!expanded) {
                Text(
                    text = stringResource(R.string.tap_to_see_details),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/* ======= Item de Producto ======= */
@Composable
private fun ProductItem(product: com.surtiapp.surtimovil.core.orders.model.OrderProduct) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.product_quantity_format, product.quantity),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = format.format(product.unitPrice),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = format.format(product.subtotal),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/* ======= Utilidad para traducir estados ======= */
@Composable
private fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.DELIVERED -> stringResource(R.string.order_status_delivered)
        OrderStatus.PENDING -> stringResource(R.string.order_status_pending)
        OrderStatus.CANCELLED -> stringResource(R.string.order_status_cancelled)
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

    AndroidView(
        factory = { ctx ->
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
        },
        modifier = Modifier.fillMaxSize()
    )
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
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
        title = stringResource(R.string.help_center_title),
        body = stringResource(R.string.help_center_body)
    )
}

@Composable
private fun OfertasScreen() {
    val homeApi = RetrofitProvider.retrofit.create(HomeApi::class.java)
    val offersRepository = OffersRepository(homeApi)
    val viewModelFactory = OffersViewModelFactory(offersRepository)
    val viewModel: OffersViewModel = viewModel(factory = viewModelFactory)

    // Obtener CartViewModel y SnackbarHostState del scope superior
    val cartViewModel: CartViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    // Scaffold para manejar el snackbar
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            com.surtiapp.surtimovil.core.offers.views.OffersView(
                viewModel = viewModel,
                cartViewModel = cartViewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.back_button_cd),
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
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoggedIn) {

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
            )

            Text(
                text = stringResource(R.string.welcome_user, userName),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )

            OutlinedButton(
                onClick = { onHelpClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Help, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.help_center_button))
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onLogout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.logout_button))
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("☹️", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.not_logged_in_message),
                    style = MaterialTheme.typography.titleMedium
                )
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
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.opening_login))
                    } else {
                        Text(stringResource(R.string.login_action))
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
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
        if (!isGranted) {
            Toast.makeText(
                context,
                context.getString(R.string.camera_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) { launcher.launch(permission) }
}

/* ======= Search Bar ======= */
@Composable
fun SearchBar(
    homeViewModel: HomeViewModel,
    cartViewModel: CartViewModel,
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val uiState by homeViewModel.ui.collectAsState()
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Estado para el modal de producto
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    // Filtrar productos basados en la búsqueda
    val searchResults = remember(query, uiState.categorias) {
        if (query.isEmpty()) {
            emptyList()
        } else {
            uiState.categorias.flatMap { category ->
                category.productos.filter { product ->
                    product.nombre.contains(query, ignoreCase = true)
                }
            }
        }
    }

    // Función para agregar al carrito con cantidad
    val addToCartWithQuantity: (Product, Int) -> Unit = { product, quantity ->
        // Agregar la cantidad especificada
        repeat(quantity) {
            val productoParaCarrito = Producto(
                id = product.id.toString(),
                nombre = product.nombre,
                descripcion = "",
                precio = product.precio,
                imageUrl = product.imagen,
                cantidadEnCarrito = 1
            )
            cartViewModel.addCarrito(productoParaCarrito)
        }

        // Mostrar snackbar
        coroutineScope.launch {
            val mensaje = if (quantity == 1) {
                "¡${product.nombre} agregado con éxito!"
            } else {
                "¡$quantity unidades de ${product.nombre} agregadas!"
            }
            snackbarHostState.showSnackbar(
                message = mensaje,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Mostrar modal si hay un producto seleccionado
    selectedProduct?.let { product ->
        ProductDetailModal(
            product = product,
            onDismiss = { selectedProduct = null },
            onAddToCart = { prod, qty ->
                addToCartWithQuantity(prod, qty)
                selectedProduct = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Barra de búsqueda
        Surface(
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar productos...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { focusManager.clearFocus() }
                    ),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    }
                )

                Spacer(Modifier.width(8.dp))

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        }

        // Resultados de búsqueda
        when {
            query.isEmpty() -> {
                // Estado inicial - sin búsqueda
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Buscar productos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Escribe el nombre del producto que buscas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            searchResults.isEmpty() -> {
                // No se encontraron resultados
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No se encontraron productos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Intenta con otro término de búsqueda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                // Mostrar resultados
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "${searchResults.size} resultado${if (searchResults.size != 1) "s" else ""} encontrado${if (searchResults.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(searchResults) { product ->
                        SearchResultItem(
                            product = product,
                            onAddToCart = { productToAdd ->
                                // Abrir el modal en lugar de agregar directamente
                                selectedProduct = productToAdd
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    product: Product,
    onAddToCart: (Product) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            AsyncImage(
                model = product.imagen,
                contentDescription = product.nombre,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatPrice(product.precio),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(8.dp))

            // Botón de agregar al carrito
            FilledTonalButton(
                onClick = { onAddToCart(product) },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    contentDescription = "Agregar al carrito",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Agregar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// Formateo de precios
private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return format.format(price)
}