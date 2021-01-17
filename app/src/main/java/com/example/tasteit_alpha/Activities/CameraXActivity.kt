@file:Suppress("DEPRECATION")

package com.example.tasteit_alpha.Activities

import android.Manifest
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.tasteit_alpha.DataBase.Room.ImageRoomRepo
import com.example.tasteit_alpha.DataBase.UploadManager
import com.example.tasteit_alpha.DataBase.UploadStrategies.FireBaseStorageStrategy
import com.example.tasteit_alpha.DataBase.UploadStrategies.FireBasefireStoreStrategy
import com.example.tasteit_alpha.Model.Data.DataClasses.ImageDatum
import com.example.tasteit_alpha.Model.DataRepository
import com.example.tasteit_alpha.R
import com.example.tasteit_alpha.Services.LocationService
import com.example.tasteit_alpha.Utils.AppUtils.*
import com.example.tasteit_alpha.Utils.Gestures.OnSwipeTouchListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS
import kotlinx.android.synthetic.main.activity_camera_x.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//todo: save the image smaller in first place into database

class CameraXActivity : AppCompatActivity() {
    private var uploadManager = UploadManager(null)
    private var imageCapture: ImageCapture? = null
    private lateinit var mProgressDialog:ProgressDialog
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var photoFile: File
    private lateinit var preview: Preview
    private lateinit var cameraProvider: ProcessCameraProvider
    private val imgOK : ImageView by lazy {
        findViewById<ImageView>(R.id.imgcheck_ok)
    }
    private val imgClear : ImageView by lazy {
        findViewById<ImageView>(R.id.imgcheck_cancel)
    }

    private val captureImgButton:FloatingActionButton by lazy {
        findViewById<FloatingActionButton>(R.id.camera_capture_button)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_x)
        imgSetVisibility(View.INVISIBLE)
        configureSwipeListener()
        // Request camera permissions
        if (allPermissionsGranted()) {
                startCamera()
        } else {
            this.let {
                ActivityCompat.requestPermissions(
                    it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }
        }
        imgOK.setOnClickListener{
            imgSetVisibility(View.INVISIBLE)
            captureImgButton.visibility=View.VISIBLE
            onPositiveClick()
        }
        imgClear.setOnClickListener{
            imgSetVisibility(View.INVISIBLE)
            captureImgButton.visibility=View.VISIBLE
            startCamera()
        }

       captureImgButton.setOnClickListener {
           it.visibility=View.INVISIBLE
           takePhoto()
       }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    fun imgSetVisibility(visibility: Int){
        imgOK.visibility=visibility
        imgClear.visibility=visibility
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(
                    this,
                    "please enable location service if you want to use this feature",
                    Toast.LENGTH_LONG
                ).show()
                onBackPressed()
                }else takePhoto()
            }
        }

    private fun configureSwipeListener() {
        findViewById<View>(R.id.cam_container).setOnTouchListener(object :
            OnSwipeTouchListener(this) {
            override fun onSwipeRight() {
                onBackPressed()
            }
        })
    }


    private fun getOutputDirectory(): File {
        val mediaDir = this.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else this.filesDir!!
    }

    private fun takePhoto() {
        if(!checkLocSetting(this)){
            showLocationDialog(this)
            return
        }
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
         photoFile = File(
             outputDirectory,
             SimpleDateFormat(
                 FILENAME_FORMAT, Locale.US
             ).format(System.currentTimeMillis()) + ".jpg"
         )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
         // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imgSetVisibility(View.VISIBLE)
                    cameraProvider.unbind(preview)

                }
            })

    }



    private fun startCamera() {
        val cameraProviderFuture = this.let { ProcessCameraProvider.getInstance(it) }
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

        imageCapture = ImageCapture.Builder()
            .build()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        this.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it
            )
        } == PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //start listening:
        registerReceiver(mReceiver, IntentFilter(ACTION_GET_ADDRESS))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, LocationService::class.java))
        cameraExecutor.shutdown()
    }


    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }




// save data methods
    private fun savingInGallery() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(photoFile)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
    }


    private fun uploadIMGtoStorage() {
       uploadManager.setDbStrategy(
           FireBaseStorageStrategy(
               photoFile, PHOTO_CHILD_PATH + photoFile.name
           )
       )
           uploadManager.uploadToDb(null)
    }

    private fun updateImageDatumToFb(adress: String, locality: String, long: Double, lat: Double) {
        val imageDatum =
                FirebaseAuth.getInstance().currentUser?.uid?.let { userID ->
                    ImageDatum(
                        photoFile.name,
                        userID, 0, long, lat, adress, locality
                    )
                }
        finishInitAndUpload(imageDatum)
    }

    private fun finishInitAndUpload(imageDatum: ImageDatum?) {
        if(imageDatum==null)return
        lifecycleScope.launch {
            val details = DataRepository.fetchDetails(imageDatum.address)
            imageDatum.relatedPlaceDetails = details
            uploadManager.setDbStrategy(FireBasefireStoreStrategy(IMAGES_COLLECTION_PATH))
            uploadManager.uploadToDb(imageDatum)
        }.invokeOnCompletion {
            mProgressDialog.dismiss()
            startCamera()
        }
        uploadImageToRoom(imageDatum)
    }



    private fun uploadImageToRoom(imageDatum: ImageDatum) {
        ImageRoomRepo.getInstance(this).uploadToDb(imageDatum)
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val address: String? = intent.getStringExtra(ADDRESS_EXTRA)
            val locality:String?=intent.getStringExtra(CITY_EXTRA)
            if(address==null||locality==null)return
             updateImageDatumToFb(
                 adress = address,
                 locality = locality,
                 long = intent.getDoubleExtra(LONG_EXTRA, 0.0),
                 lat = intent.getDoubleExtra(LAT_EXTRA, 0.0)
             )

        }
    }



     private fun onPositiveClick() {
        if(!checkLocSetting(this)){
            Toast.makeText(this, "please enable location to use this feature", Toast.LENGTH_LONG).show()
            onBackPressed()
        }
        savingInGallery()
        mProgressDialog = ProgressDialog.show(
            this@CameraXActivity,
            "upload data",
            "Please wait while we uploading your photo..."
        );
        uploadIMGtoStorage()
        initLocService()

    }

    private fun initLocService() {
        SmartLocation.with(this).location(LocationGooglePlayServicesProvider()).oneFix().start {
            //explicit intent:
            val geoIntent = Intent(this, LocationService::class.java)
            geoIntent.putExtra(LOC_KEY, LatLng(it.latitude, it.longitude))
            //startService:
            this.startService(geoIntent)
        }

    }





/* optional: add an image analytics according to your algorithem*/
}