package com.surtiapp.surtimovil.core.user.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.surtiapp.surtimovil.core.orders.viewmodel.OrdersViewModel
import com.surtiapp.surtimovil.core.orders.viewmodel.RecommendedProduct
import com.surtiapp.surtimovil.homescreen.model.dto.ProductDto
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    ordersViewModel: OrdersViewModel,
    productsCatalog: List<ProductDto>,
    onBack: () -> Unit
) {
    val ordersUiState by ordersViewModel.uiState.collectAsState()
    val orders = ordersUiState.orders

    // Obtener productos recomendados (top productos)
    val topProducts = remember(orders) {
        ordersViewModel.getRecommendedProducts().take(10)
    }

    // Crear mapa de imágenes del catálogo
    val productImages = remember(productsCatalog) {
        productsCatalog.associate {
            it.id.toString() to it.image
        }
    }

    // Enriquecer con imágenes
    val topProductsWithImages = remember(topProducts, productImages) {
        topProducts.map { product ->
            val numericId = if (product.id.contains("_")) {
                product.id.split("_").last()
            } else {
                product.id
            }
            product.copy(imageUrl = productImages[numericId] ?: "")
        }
    }

    // Calcular estadísticas
    val totalOrders = orders.size
    val totalSpent = orders.sumOf { it.total }
    val favoriteProduct = topProductsWithImages.firstOrNull()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (totalOrders == 0) {
            EmptyDashboardState(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Estadísticas generales
                item {
                    StatsCard(
                        totalOrders = totalOrders,
                        totalSpent = totalSpent,
                        favoriteProduct = favoriteProduct
                    )
                }

                // Gráfica de pastel
                item {
                    PieChartCard(products = topProductsWithImages.take(5))
                }

                // Título de top productos
                item {
                    Text(
                        text = "🏆 Top 10 Productos Más Pedidos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Lista de top productos
                itemsIndexed(topProductsWithImages) { index, product ->
                    TopProductCard(
                        rank = index + 1,
                        product = product,
                        maxPurchases = topProductsWithImages.firstOrNull()?.timesPurchased ?: 1
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCard(
    totalOrders: Int,
    totalSpent: Double,
    favoriteProduct: RecommendedProduct?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.BarChart,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "📊 Resumen de Compras",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.height(16.dp))

            // Grid de estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.ShoppingBag,
                    label = "Pedidos",
                    value = "$totalOrders"
                )

                StatItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Total Gastado",
                    value = formatPrice(totalSpent)
                )
            }

            if (favoriteProduct != null) {
                Spacer(Modifier.height(16.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(24.dp)
                    )

                    Column {
                        Text(
                            text = "Tu favorito",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = favoriteProduct.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun PieChartCard(products: List<RecommendedProduct>) {
    if (products.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "📈 Distribución Top 5",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            PieChart(
                data = products.map { it.timesPurchased.toFloat() },
                labels = products.map { it.name }
            )

            Spacer(Modifier.height(16.dp))

            // Leyenda
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                products.forEachIndexed { index, product ->
                    LegendItem(
                        color = getChartColor(index),
                        label = product.name,
                        value = "${product.timesPurchased} veces"
                    )
                }
            }
        }
    }
}

@Composable
private fun PieChart(
    data: List<Float>,
    labels: List<String>
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "pie_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val total = data.sum()
    val proportions = data.map { it / total }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(180.dp)
        ) {
            var startAngle = -90f

            proportions.forEachIndexed { index, proportion ->
                val sweepAngle = proportion * 360f * animatedProgress

                drawArc(
                    color = getChartColor(index),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )

                // Borde blanco entre secciones
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height),
                    style = Stroke(width = 4f)
                )

                startAngle += sweepAngle
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TopProductCard(
    rank: Int,
    product: RecommendedProduct,
    maxPurchases: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ranking badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Imagen del producto
            if (product.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${product.timesPurchased} ${if (product.timesPurchased == 1) "compra" else "compras"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                // Barra de progreso
                LinearProgressIndicator(
                    progress = { product.timesPurchased.toFloat() / maxPurchases.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Gastado: ${formatPrice(product.totalSpent)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyDashboardState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.BarChart,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Sin estadísticas aún",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Realiza algunas compras para ver tus estadísticas y productos favoritos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getChartColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF6200EE),
        Color(0xFF03DAC6),
        Color(0xFFFF6B6B),
        Color(0xFF4ECDC4),
        Color(0xFFFFD93D)
    )
    return colors[index % colors.size]
}

private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return format.format(price)
}