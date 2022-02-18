package com.example.moneymanager.ui.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import com.example.moneymanager.R
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File

//Use CameraX?
@ExperimentalPermissionsApi
@Composable
fun Camera(tViewModel: TransactionViewModel) {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
        )
    )

    val fileName = "photo"
    val imgPath = LocalContext.current.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File.createTempFile(fileName, ".jpg", imgPath)
    imageFile.deleteOnExit()

    val photoURI: Uri = FileProvider.getUriForFile(LocalContext.current, "com.example.moneymanager.fileprovider", imageFile)
    val currentPhotoPath = imageFile.absolutePath

    val result = remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            result.value = BitmapFactory.decodeFile(currentPhotoPath)
        } else
            Log.i("DBG", "Picture not taken" )
    }

    when {
        // If the camera permission is granted, then show screen with the feature enabled
        multiplePermissionsState.allPermissionsGranted -> {
            IconButton(onClick = {
                launcher.launch(photoURI)
                tViewModel.onImagePathChange(currentPhotoPath.toString())
            }) {
                Icon(painterResource(R.drawable.ic_twotone_chevron_right_24), contentDescription = "Next month")
            }
            Column() {
                result.value?.let { image ->
                    Image(image.asImageBitmap(), null, modifier = Modifier.fillMaxSize(0.1f))
                }
            }
        }
        // If the user denied the permission but a rationale should be shown, or the user sees
        // the permission for the first time, explain why the feature is needed by the app and allow
        // the user to be presented with the permission again or to not see the rationale any more.
        multiplePermissionsState.shouldShowRationale ||
                !multiplePermissionsState.permissionRequested -> {
            Column {
                    Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                        Text("Request permission")

                }
            }
        }
    }
}


