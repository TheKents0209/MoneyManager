package com.example.moneymanager.ui.views

import android.Manifest
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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.data.database.DB
import com.example.moneymanager.data.repository.AccountRepository
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.ui.components.*
import com.example.moneymanager.ui.viewmodel.AccountViewModel
import com.example.moneymanager.ui.viewmodel.TransactionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileDescriptor

//Sample used
//https://github.com/MakeItEasyDev/Jetpack-Compose-Capture-Image-Or-Choose-from-Gallery/blob/main/app/src/main/java/com/jetpack/takecamerapicture/MainActivity.kt
@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun InsertTransaction(
    tViewModel: TransactionViewModel,
    aViewModel: AccountViewModel,
    navController: NavController,
    isUpdate: Boolean
) {
    var imgPathViewModel by rememberSaveable { mutableStateOf("") }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
    )
    var photoURI: Uri?
    val coroutineScope = rememberCoroutineScope()

    var currentPhotoPath: String? = null

    val context = LocalContext.current

    val result = remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            coroutineScope.launch(Dispatchers.IO) {
                if (uri != null) {
                    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                        uri, "r"
                    )
                    val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
                    val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                    result.value = image
                    parcelFileDescriptor.close()
                    imgPathViewModel = uri.toString()
                }
            }
        }


    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it && currentPhotoPath != null) {
            imgPathViewModel = currentPhotoPath!!
            result.value = BitmapFactory.decodeFile(currentPhotoPath)
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(
            topEnd = ContentAlpha.medium,
            topStart = ContentAlpha.medium
        ),
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background.copy(0.08f))
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.take_picture),
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
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                } else {
                                    multiplePermissionsState.launchMultiplePermissionRequest()
                                }
                            }
                            .padding(15.dp),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Divider(
                        modifier = Modifier
                            .height(0.5.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    )
                    Text(
                        text = stringResource(R.string.choose_gallery),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (multiplePermissionsState.allPermissionsGranted) {
                                    galleryLauncher.launch("image/*")
                                    coroutineScope.launch {
                                        bottomSheetScaffoldState.bottomSheetState.collapse()
                                    }
                                } else {
                                    multiplePermissionsState.launchMultiplePermissionRequest()
                                }
                            }
                            .padding(15.dp),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Divider(
                        modifier = Modifier
                            .height(0.5.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    )
                    Text(
                        text = stringResource(R.string.cancel),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                            .padding(15.dp),
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        },
    ) {
        Column(Modifier.fillMaxSize()) {
            Column {
                TransactionTypeSelector(tViewModel)
                DateAlertDialog(tViewModel)
                AccountAlertDialog(tViewModel, aViewModel)
                CategoryAlertDialog(tViewModel)
                AmountRow(tViewModel)
                DescriptionRow(tViewModel)
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth(0.4f)) {
                    result.value?.let { image ->
                        Image(image.asImageBitmap(), null)
                    }
                }
                Column(Modifier.fillMaxWidth(0.6f)) {
                    IconButton(modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.End), onClick = {
                        coroutineScope.launch {
                            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            } else {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
                            }
                        }
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_twotone_photo_camera_24),
                            contentDescription = "Take picture"
                        )
                    }
                }
            }
            Column {
                tViewModel.onImagePathChange(imgPathViewModel)
                InsertTransactionButton(tViewModel, navController, isUpdate)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun InsertTransaction(navController: NavController) {
    val tViewModel = TransactionViewModel(
        TransactionRepository(
            DB.getInstance(LocalContext.current).TransactionDao()
        )
    )
    val aViewModel =
        AccountViewModel(AccountRepository(DB.getInstance(LocalContext.current).AccountDao()))
    InsertTransaction(tViewModel, aViewModel, navController, false)
}