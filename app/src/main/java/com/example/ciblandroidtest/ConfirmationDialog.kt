package com.example.ciblandroidtest

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.example.ciblandroidtest.databinding.DialogConfirmationBinding


class ConfirmationDialog(private val payment: PaymentModel) : DialogFragment() {
    private lateinit var alertDialog: AlertDialog
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var binding: DialogConfirmationBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        binding = DialogConfirmationBinding.inflate(layoutInflater)
        builder.setView(binding.root)

        // Switch payment icon
        switchGatewayIcon()


        binding.downloadBtn.setOnClickListener {
            generatePDF()
        }
        alertDialog = builder.create()
        return alertDialog
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

//        document.writeTo()

        document.close()
    }

    private fun checkPermissions() {
        if (isPermissionApproved()) {
//            enableMyLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun isPermissionApproved(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        if (isPermissionApproved())
            return
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}