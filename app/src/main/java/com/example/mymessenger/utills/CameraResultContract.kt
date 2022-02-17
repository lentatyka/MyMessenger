package com.example.mymessenger.utills

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import javax.inject.Inject

class CameraResultContract @Inject constructor(
    private val context: Application
):ActivityResultContract<CameraResultContract.Action, Bitmap?>() {
    @Inject
    lateinit var file: File
    private var photoURI: Uri? =null
    enum class Action{
        CAMERA, GALLERY
    }

    @Throws(IOException::class)
    override fun parseResult(resultCode: Int, intent: Intent?):Bitmap? {
        if (resultCode == Activity.RESULT_OK) {
            intent?.let { result ->
                //Gallery
                result.data?.let {
                    return createBitmap(it)
                }
                //Camera
                photoURI?.let {
                    return createBitmap(it)
                }
            }
        }
        return null
    }

    @Throws(IOException::class)
    override fun createIntent(context: Context, input: Action): Intent {
        return when(input){
            Action.CAMERA ->{
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

                    takePictureIntent.resolveActivity(context.packageManager)?.also {
                        // Create the File where the photo should go
                        val photoFile: File = file
                        // Continue only if the File was successfully created
                        photoFile.also {
                             photoURI = FileProvider.getUriForFile(
                                context,
                                "com.example.mymessenger.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        }
                    }
                }
            }
            Action.GALLERY ->{
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            }
        }
    }

    @Throws(IOException::class)
    private fun createBitmap(uri: Uri):Bitmap{
            return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    uri
                )
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
    }
}