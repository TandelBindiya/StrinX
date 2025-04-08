package com.bindiya.strinx.presentation.home

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.bindiya.strinx.R
import com.bindiya.strinx.data.model.RandomStringData
import com.bindiya.strinx.presentation.ui.theme.Purple40
import com.bindiya.strinx.utils.ContentProviderConstants.PERMISSION_ARRAY
import com.bindiya.strinx.utils.ContentProviderConstants.READ_PERMISSION
import com.bindiya.strinx.utils.ContentProviderConstants.WRITE_PERMISSION
import com.bindiya.strinx.utils.EMPTY_STRING
import com.bindiya.strinx.utils.isProviderPermissionsGranted

@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()) {
    val randomStrings = viewModel.randomStrings.observeAsState(emptyList()).value
    val loadState = viewModel.loadState.observeAsState(false).value
    val context = LocalContext.current
    var hasContentProviderPermission by remember {
        mutableStateOf(
            context.isProviderPermissionsGranted()
        )
    }
    context.HandleContentProviderPermissions(hasContentProviderPermission) {
        hasContentProviderPermission = it
    }
    ErrorToastHandler(viewModel)
    if (hasContentProviderPermission) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                InputLayout(viewModel)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp)
                RandomStringList(randomStrings = randomStrings, viewModel)
            }
            if (loadState) LoadingIndicator()
        }
    }
}


@Composable
fun Context.HandleContentProviderPermissions(
    hasContentProviderPermission: Boolean,
    onPermissionGranted: (Boolean) -> Unit
) {
    var showRationaleDialog by remember { mutableStateOf(false) }
    var permissionAlreadyRequested by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { map ->
            onPermissionGranted(map[READ_PERMISSION] == true && map[WRITE_PERMISSION] == true)
        }
    )
    if (!hasContentProviderPermission) {
        Column {
            if (showRationaleDialog) {
                AlertDialog(
                    onDismissRequest = { showRationaleDialog = false },
                    title = { Text(stringResource(R.string.label_contentprovider_permission_needed)) },
                    text = { Text(stringResource(R.string.note_permission_required)) },
                    confirmButton = {
                        Button(onClick = {
                            showRationaleDialog = false
                            launcher.launch(PERMISSION_ARRAY)
                        }) {
                            Text(stringResource(R.string.label_request))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showRationaleDialog = false }) {
                            Text(stringResource(R.string.label_cancel))
                        }
                    }
                )
            }
            Button(onClick = {
                if (shouldShowPermissionRational()) {
                    showRationaleDialog = true
                } else if (!permissionAlreadyRequested) {
                    launcher.launch(PERMISSION_ARRAY)
                    permissionAlreadyRequested = true
                }
            }) {
                Text(stringResource(R.string.label_request_content_provider_permission))
            }
        }

    }
}

private fun Context.shouldShowPermissionRational(): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(
        this as Activity,
        READ_PERMISSION
    ) || ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        WRITE_PERMISSION
    )
}

@Composable
fun InputLayout(viewModel: HomeViewModel) {
    var length by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(stringResource(R.string.label_enter_length_of_string))
        OutlinedTextField(
            value = length.toString(),
            onValueChange = {
                length = it.toIntOrNull() ?: 0
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppButton(
                stringResource(R.string.label_generate_random_string),
                onButtonClick = { viewModel.generateRandomString(length) })
            Spacer(modifier = Modifier.width(16.dp))
            AppButton(
                stringResource(R.string.label_delete_all_strings),
                onButtonClick = { viewModel.deleteAllStrings() })
        }
    }
}

@Composable
fun AppButton(text: String, onButtonClick: (() -> Unit)? = null) {
    Button(
        onClick = { onButtonClick?.invoke() },
        colors = ButtonDefaults.buttonColors(containerColor = Purple40)
    ) {
        Text(text)
    }
}

@Composable
fun RandomStringItem(data: RandomStringData, onDelete: () -> Unit) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Text(stringResource(R.string.label_value, data.value))
        Text(stringResource(R.string.label_length, data.length))
        Text(stringResource(R.string.label_created, data.created))
        AppButton(stringResource(R.string.label_delete), onButtonClick = onDelete)
    }
}

@Composable
fun ErrorToastHandler(viewModel: HomeViewModel) {
    val errorMessage = viewModel.errorData.observeAsState(EMPTY_STRING)
    val context = LocalContext.current
    errorMessage.value.let {
        LaunchedEffect(it) {
            if (it.isNotEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

}

@Composable
fun LoadingIndicator() {
    CircularProgressIndicator(modifier = Modifier)
}

@Composable
fun RandomStringList(randomStrings: List<RandomStringData>, viewModel: HomeViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(randomStrings) { item ->
            RandomStringItem(data = item) {
                viewModel.deleteRandomString(item)
            }
        }
    }
}



