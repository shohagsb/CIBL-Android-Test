package com.example.ciblandroidtest

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.ciblandroidtest.databinding.DialogConfirmationBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "ConfirmationDialog"

class ConfirmationDialog(private val payment: PaymentModel) : DialogFragment() {
    private lateinit var yourpath: String
    private lateinit var alertDialog: AlertDialog
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var binding: DialogConfirmationBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        binding = DialogConfirmationBinding.inflate(layoutInflater)
        builder.setView(binding.root)

        // Switch payment icon
        switchGatewayIcon()

        getCurrentDate()


        binding.downloadBtn.setOnClickListener {
            checkPermissions()
        }
        alertDialog = builder.create()
        return alertDialog
    }

    private fun getCurrentDate() {
        val sdf = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.dateText.text = String.format(Locale.getDefault(),"Date: %s", currentDate)
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

//        val file = File(Environment.getExternalStorageState(), "/report.pdf")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            yourpath= "${requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/report.pdf";
        }
//        else
//        {
//
//            Yourpath=Environment.getExternalStorageDirectory()
//                .toString() + "/" + nameoffile + ".jpeg";
//        }

        try {
            document.writeTo(FileOutputStream(yourpath))
            Toast.makeText(context, "PDf saved!", Toast.LENGTH_SHORT).show()
        }catch (e: IOException){
            e.printStackTrace()
            Log.d(TAG, "${e.message}")
        }

        document.close()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    generatePDF()
                } else {
                    Toast.makeText(
                        context,
                        "Write permission is required to save PDF!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkPermissions() {
        if (isPermissionApproved()) {
            generatePDF()
        } else {
            requestWriteStoragePermissions()
        }
    }

    private fun isPermissionApproved(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteStoragePermissions() {
        if (isPermissionApproved())
            return
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}