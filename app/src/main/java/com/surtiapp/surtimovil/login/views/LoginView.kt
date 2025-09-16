package com.surtiapp.surtimovil.login.views

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.login.model.network.RetrofitProvider
import com.surtiapp.surtimovil.login.model.repository.AuthRepository
import com.surtiapp.surtimovil.login.viewmodel.LoginViewModel
import com.surtiapp.surtimovil.login.viewmodel.LoginViewModelFactory
import com.surtiapp.surtimovil.ui.theme.SurtiMovilTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginView(navController: NavController) {
    // Inyección simple del repositorio y el ViewModel
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { AuthRepository(RetrofitProvider.authApi, app) }
    val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(repo, app))
    val ui by vm.ui.collectAsState()

    val appContext = LocalContext.current.applicationContext
    val snackbarHostState = remember { SnackbarHostState() }
    var passwordVisible by remember { mutableStateOf(false) }

    fun showToastSafe(text: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Mostrar toasts y snackbars
    LaunchedEffect(vm) {
        vm.toastEvents.collectLatest { msg ->
            snackbarHostState.showSnackbar(message = msg)
        }
    }

    // Navegar de regreso a Home al iniciar sesión con éxito
    LaunchedEffect(vm) {
        vm.loginSuccessEvents.collectLatest {
            navController.popBackStack() // vuelve a la pantalla anterior (Home)
            // Si prefieres ir a una ruta específica:
            // navController.navigate("home") {
            //     popUpTo("login") { inclusive = true }
            //     launchSingleTop = true
            // }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo institucional
            Image(
                painter = painterResource(id = R.drawable.surtilogo),
                contentDescription = stringResource(R.string.app_logo_cd),
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(16.dp))

            // Campo de email
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text(stringResource(R.string.email_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Campo de password con mostrar/ocultar
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text(stringResource(R.string.password_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { vm.login() }
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val cd = if (passwordVisible)
                        stringResource(R.string.hide_password_cd)
                    else
                        stringResource(R.string.show_password_cd)

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = cd)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Botón de login
            Button(
                onClick = { vm.login() },
                enabled = !ui.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.signing_in))
                } else {
                    Text(stringResource(R.string.login_button))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    // Solo para preview; no hay NavController real aquí.
    SurtiMovilTheme {
        // Podrías usar un fake navController si lo deseas.
    }
}
