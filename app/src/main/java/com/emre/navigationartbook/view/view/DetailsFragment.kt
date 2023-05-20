package com.emre.navigationartbook.view.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.room.Room
import com.emre.navigationartbook.R
import com.emre.navigationartbook.databinding.FragmentDetailsBinding
import com.emre.navigationartbook.view.model.Arts
import com.emre.navigationartbook.view.roomdb.ArtDB
import com.emre.navigationartbook.view.roomdb.ArtDao
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var db: ArtDB
    private lateinit var artDao: ArtDao
    private val compositeDisposable = CompositeDisposable()
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null
    var chosenBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = MainActivity()
        registerLauncher()

        db =
            Room.databaseBuilder(requireContext(), ArtDB::class.java, "Arts").build()
        artDao = db.artDao()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener { save() }
        binding.imageView.setOnClickListener { selectImage() }


        arguments?.let {
            val info = DetailsFragmentArgs.fromBundle(it).info
            if (info.equals("new")) {
                binding.button2.visibility = View.GONE
                binding.yearText.setText("")
                binding.artNameText.setText("")
                binding.artistNameText.setText("")
                val baseImage = BitmapFactory.decodeResource(context?.resources,R.drawable.select)
                binding.imageView.setImageBitmap(baseImage)
            } else {
                binding.button.visibility = View.GONE
                val id = DetailsFragmentArgs.fromBundle(it).id

                compositeDisposable.add(
                    artDao.getItemWithId(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseOldInfo)
                )
            }
        }

    }

    private fun save() {
        val name = binding.artNameText.text.toString()
        val artist = binding.artistNameText.text.toString()
        val year = binding.yearText.text.toString()

        if (selectedBitmap != null) {
            val smallImage = makeBitmapToSmaller(selectedBitmap!!, 300)

            val outputStream = ByteArrayOutputStream()
            smallImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val byteArray = outputStream.toByteArray()

            val art = Arts(name, artist, year, byteArray)

            compositeDisposable.add(
                artDao.insert(art)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }

    }
    private fun handleResponseOldInfo(dataWithId: List<Arts>) {
        binding.artNameText.setText(dataWithId[0].name)
        binding.artistNameText.setText(dataWithId[0].artist)
        binding.yearText.setText(dataWithId[0].year)

        chosenBitmap = BitmapFactory.decodeByteArray(dataWithId[0].image, 0, dataWithId[0].image.size)
        binding.imageView.setImageBitmap(chosenBitmap)

    }

    private fun handleResponse() {
        val action = DetailsFragmentDirections.actionDetailsFragmentToListFragment()
        Navigation.findNavController(binding.root).navigate(action)
    }

    private fun makeBitmapToSmaller(image: Bitmap, maxSize: Int) : Bitmap {

        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            // Landscape
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else if (bitmapRatio < 1) {
            // Potrait
            height = maxSize
            width = (height * bitmapRatio).toInt()
        } else {
            // Square
            width = maxSize
            height = maxSize
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun selectImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // SDK 33+

            if (ContextCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    // Onaylı izin ister
                    Snackbar.make(binding.root,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                } else {
                    // İzin ister
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                // İzin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
                activityLauncher.launch(intentToGallery)

            }
        } else {
            // SDK 32-
            if (ContextCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Onaylı izin ister
                    Snackbar.make(binding.root,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                } else {
                    // İzin ister
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                // İzin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
                activityLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher() {
        activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == AppCompatActivity.RESULT_OK) {

                    if (result.data != null) {
                        val imageData = result.data!!.data // -> Uri
                        try {
                            val source = ImageDecoder.createSource(requireActivity().contentResolver, imageData!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
            if (result) {
                // İzin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI)
                activityLauncher.launch(intentToGallery)
            } else {
                // İzin verilmedi
                Toast.makeText(requireContext(),"Permission needed", Toast.LENGTH_LONG).show()
            }

        }
    }

}