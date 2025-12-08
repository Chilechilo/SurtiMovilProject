package com.surtiapp.surtimovil.login.views

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.login.model.network.RetrofitProvider
import com.surtiapp.surtimovil.login.model.repository.AuthRepository
import com.surtiapp.surtimovil.login.util.BiometricHelper
import com.surtiapp.surtimovil.login.viewmodel.LoginViewModel
import com.surtiapp.surtimovil.login.viewmodel.LoginViewModelFactory
import com.surtiapp.surtimovil.ui.theme.SurtiMovilTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginView(navController: NavController, activity: FragmentActivity) {
    Log.d("BiometricCheck", "🚀 Entrando a LoginView composable")

    // ✅ Bloquea el botón físico "Atrás" del sistema
    BackHandler(enabled = true) {
        // Evita que el usuario regrese al Home sin autenticarse
        Log.d("BackHandler", "Botón atrás desactivado en pantalla de login 🚫")
    }

    // --- ViewModel + Repository ---
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { AuthRepository(RetrofitProvider.authApi, app) }
    val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(repo, app))
    val ui by vm.ui.collectAsState()

    val appContext = LocalContext.current.applicationContext
    val snackbarHostState = remember { SnackbarHostState() }
    var passwordVisible by remember { mutableStateOf(false) }

    // --- Toast helper ---
    fun showToastSafe(text: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- Observadores de eventos ---
    LaunchedEffect(vm) {
        vm.toastEvents.collectLatest { msg ->
            snackbarHostState.showSnackbar(message = msg)
        }
    }

    LaunchedEffect(vm) {
        vm.loginSuccessEvents.collectLatest {
            Log.d("LoginView", "✅ Login exitoso detectado")

            val token = vm.ui.value.authToken

            // Marca el login como exitoso para el Home
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("loggedIn", true)

            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("username", vm.ui.value.email)

            // 🔥 Guardamos también el token
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("token", token)

            // Volver al Home
            navController.popBackStack()
        }
    }

    // --- Pantalla principal ---
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

            // --- Logo ---
            Image(
                painter = painterResource(id = R.drawable.surtimovil_trans),
                contentDescription = stringResource(R.string.app_logo_cd),
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(16.dp))

            // --- Campo Email ---
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

            // --- Campo Password ---
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text(stringResource(R.string.password_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { vm.login() }),
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

            // --- Botón Login Normal ---
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

            Spacer(Modifier.height(8.dp))

            // --- Botón Login con Biometría (Face ID / Huella) ---
            Log.d("BiometricCheck", "🧩 Activity encontrada: ${activity != null}")

            val context = LocalContext.current
            val biometricHelper = remember {
                BiometricHelper(
                    context = context,
                    activity = activity,
                    onSuccess = {
                        Log.d("BiometricCheck", "Autenticación biométrica EXITOSA ✅")
                        vm.loginWithBiometric()
                    },
                    onError = { msg ->
                        Log.e("BiometricCheck", "Error en biometría: $msg")
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Diagnóstico al abrir la vista
            LaunchedEffect(Unit) {
                val canAuth = biometricHelper.canAuthenticate()
                Log.d("BiometricCheck", "🔍 canAuthenticate() devuelve: $canAuth")
            }

            if (biometricHelper.canAuthenticate()) {
                OutlinedButton(
                    onClick = { biometricHelper.showPrompt() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Face, contentDescription = "Face ID")
                    Spacer(Modifier.width(8.dp))
                    Text("Iniciar sesión con Face ID")
                }
            } else {
                Log.w("BiometricCheck", "🚫 El dispositivo no tiene Face ID ni huella registrada.")
            }

            Spacer(Modifier.height(8.dp))

            // --- Botón para registrarse ---
            TextButton(
                onClick = {
                    navController.navigate("signin") {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.signup_button_in_login))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    SurtiMovilTheme { }
}

/* ✅ Extensión auxiliar */
fun Context.findActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is android.content.ContextWrapper) {
        if (currentContext is FragmentActivity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}
