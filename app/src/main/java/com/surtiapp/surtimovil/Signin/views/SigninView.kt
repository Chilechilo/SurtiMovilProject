package com.surtiapp.surtimovil.Signin.views

import android.app.Application
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.surtiapp.surtimovil.R
import com.surtiapp.surtimovil.Signin.viewmodel.SignInViewModel
import com.surtiapp.surtimovil.Signin.viewmodel.SignInViewModelFactory
import com.surtiapp.surtimovil.login.model.network.RetrofitProvider
import com.surtiapp.surtimovil.login.model.repository.AuthRepository
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SigninView(navController: NavController) {
    // Inyección simple del repositorio y el ViewModel (mismo patrón que LoginView)
    val app = LocalContext.current.applicationContext as Application
    val repo = remember { AuthRepository(RetrofitProvider.authApi, app) }
    val vm: SignInViewModel = viewModel(factory = SignInViewModelFactory(repo, app))
    val ui by vm.ui.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var pwVisible by remember { mutableStateOf(false) }
    var pw2Visible by remember { mutableStateOf(false) }

    // Toasts
    LaunchedEffect(vm) {
        vm.toastEvents.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }
    // Al registrarse con éxito, regresar a la pantalla anterior
    LaunchedEffect(vm) {
        vm.signUpSuccessEvents.collectLatest {
            navController.popBackStack()
            // navController.navigate("home") { popUpTo("signin") { inclusive = true } }
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

            // Logo
            Image(
                painter = painterResource(id = R.drawable.surtimovil_trans),
                contentDescription = stringResource(R.string.app_logo_cd),
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(16.dp))

            // Nombre
            OutlinedTextField(
                value = ui.name,
                onValueChange = vm::onNameChange,
                label = { Text(stringResource(R.string.name_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Email
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

            // Password
            OutlinedTextField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                label = { Text(stringResource(R.string.password_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        val icon = if (pwVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        val cd = if (pwVisible)
                            stringResource(R.string.hide_password_cd)
                        else stringResource(R.string.show_password_cd)
                        Icon(imageVector = icon, contentDescription = cd)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Confirmación
            OutlinedTextField(
                value = ui.confirm,
                onValueChange = vm::onConfirmChange,
                label = { Text(stringResource(R.string.confirm_password_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { vm.signUp() }
                ),
                visualTransformation = if (pw2Visible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { pw2Visible = !pw2Visible }) {
                        val icon = if (pw2Visible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        val cd = if (pw2Visible)
                            stringResource(R.string.hide_password_cd)
                        else stringResource(R.string.show_password_cd)
                        Icon(imageVector = icon, contentDescription = cd)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Aceptar términos
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = ui.acceptTerms,
                    onCheckedChange = vm::onAcceptTermsChange
                )
                Text(
                    text = stringResource(R.string.accept_terms_label),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Botón de crear cuenta
            Button(
                onClick = { vm.signUp() },
                enabled = !ui.isLoading && ui.acceptTerms,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (ui.isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.creating_account))
                } else {
                    Text(stringResource(R.string.signup_button))
                }
            }

            // Enlace para ir al login
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.already_have_account_go_login))
            }
        }
    }
}