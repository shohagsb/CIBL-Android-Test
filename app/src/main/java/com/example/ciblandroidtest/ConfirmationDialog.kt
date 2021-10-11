package com.example.ciblandroidtest

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.ciblandroidtest.databinding.DialogConfirmationBinding
import com.example.ciblandroidtest.model.Address
import com.example.ciblandroidtest.model.PaymentModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "ConfirmationDialog"

class ConfirmationDialog(private val payment: PaymentModel) : DialogFragment() {
    private lateinit var alertDialog: AlertDialog
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var binding: DialogConfirmationBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        binding = DialogConfirmationBinding.inflate(layoutInflater)
        builder.setView(binding.root)
        binding.payment = payment

        // Switch payment icon
        switchGatewayIcon()

        getCurrentDate()
        checkPermissions()


        binding.downloadBtn.setOnClickListener {

        }
        alertDialog = builder.create()
        return alertDialog
    }

    private fun getCurrentDate() {
        val sdf = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.dateText.text = String.format(Locale.getDefault(), "Date: %s", currentDate)
    }

    private fun switchGatewayIcon() {
        if (payment.paymentType == "Bkash") {
            binding.gatewayIcon.setImageResource(R.drawable.ic_bkash)
        } else {
            binding.gatewayIcon.setImageResource(R.drawable.ic_nagad)
        }
    }

    private fun generatePDF() {
        val document = PdfDocument()
        val paint = Paint()
        val pageInfo = PageInfo.Builder(100, 100, 1).create()
        // start a page
        val page: PdfDocument.Page = document.startPage(pageInfo)

        val canvas = page.canvas
        canvas.drawText("Payment Invoice", 40F, 50F, paint)

        document.finishPage(page)
        val file = File(Environment.getExternalStorageDirectory(), "GFG.pdf")
//        val file = File(Environment.getExternalStorageState(), "/report.pdf")

        try {
            document.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDf saved!", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d(TAG, "${e.message}")
        }

        document.close()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                        getLocation()
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                        getLocation()
                    }
                    else -> {
                        // No location access granted.
                        Toast.makeText(
                            context,
                            "Permission required for location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun checkPermissions() {
        if (isPermissionApproved()) {
            getLocation()
        } else {
            requestPermissions()
        }
    }

    private fun isPermissionApproved(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (isPermissionApproved())
            return
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geoAddress = geoCodeLocation(location)
                    binding.locationText.text = String.format(
                        Locale.getDefault(),
                        "%s, %s",
                        geoAddress.line1,
                        geoAddress.city
                    )
                } else {
                    Toast.makeText(context, "Please Turn on Location", Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }
}