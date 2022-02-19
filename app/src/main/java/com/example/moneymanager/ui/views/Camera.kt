package com.example.moneymanager.ui.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileDescriptor


@ExperimentalMaterialApi
@ExperimentalPermissionsApi
@Composable
fun Camera() {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
        )
    )
    var isCameraSelected = false
    var photoURI: Uri? = null
    var bitmap: Bitmap? = null
    val coroutineScope = rememberCoroutineScope()

    var currentPhotoPath: String? = null

    val context = LocalContext.current
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val result = remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        coroutineScope.launch {
            if(uri != null) {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                    uri, "r"
                )
                val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
                val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                result.value = image
                //Log.d("StringPathUri cam", uri.toString())
                parcelFileDescriptor.close()
                //tViewModel.onImagePathChange(uri.toString())
            }
        }
    }


    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it && currentPhotoPath != null) {
            //Log.d("StringPathUri cam", currentPhotoPath!!)
            //tViewModel.onImagePathChange(currentPhotoPath.toString())
            result.value = BitmapFactory.decodeFile(currentPhotoPath)
        }
    }

//    when {
//        multiplePermissionsState.allPermissionsGranted -> {
//            if(isCameraSelected) {
//                cameraLauncher.launch()
//            } else {
//                LaunchedEffect(isCameraSelected) {
//                    galleryLauncher.launch("image/*")
//                }
//            }
//            LaunchedEffect(isCameraSelected) {
//                coroutineScope.launch {
//                    bottomSheetState.hide()
//                }
//            }
//        }
//        !multiplePermissionsState.allPermissionsGranted -> {
//            Toast.makeText(context, "Permissions denied!", Toast.LENGTH_SHORT).show()
//        }
//    }


    ModalBottomSheetLayout(
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colors.primary.copy(0.08f))
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add Photo!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        color = MaterialTheme.colors.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Divider(
                        modifier = Modifier
                            .height(1.dp)
                            .background(MaterialTheme.colors.primary)
                    )
                    Text(
                        text = "Take Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (multiplePermissionsState.allPermissionsGranted) {
                                    val fileName = "photo"
                                    val imgPath =
                                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                    val imageFile = File.createTempFile(fileName, ".jpg", imgPath)
                                    currentPhotoPath = imageFile.absolutePath

                                    photoURI = FileProvider.getUriForFile(
                                        context,
                                        "com.example.moneymanager.fileprovider",
                                        imageFile
                                    )

                                    cameraLauncher.launch(photoURI)
                                    coroutineScope.launch {
                                        bottomSheetState.hide()
                                    }
                                } else {
                                    isCameraSelected = true
                                    multiplePermissionsState.launchMultiplePermissionRequest()
                                }
                            }
                            .padding(15.dp),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Divider(
                        modifier = Modifier
                            .height(0.5.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    )
                    Text(
                        text = "Choose from Gallery",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (multiplePermissionsState.allPermissionsGranted) {
                                    galleryLauncher.launch("image/*")
                                    coroutineScope.launch {
                                        bottomSheetState.hide()
                                    }
                                } else {
                                    isCameraSelected = false
                                    multiplePermissionsState.launchMultiplePermissionRequest()
                                }
                            }
                            .padding(15.dp),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Divider(
                        modifier = Modifier
                            .height(0.5.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                    )
                    Text(
                        text = "Cancel",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                }
                            }
                            .padding(15.dp),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        },
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        modifier = Modifier
            .background(MaterialTheme.colors.background)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (!bottomSheetState.isVisible) {
                            bottomSheetState.show()
                        } else {
                            bottomSheetState.hide()
                        }
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Take Picture",
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }
        result.value?.let { image ->
            Image(image.asImageBitmap(), null, modifier = Modifier.fillMaxWidth())
        }
    }
}


