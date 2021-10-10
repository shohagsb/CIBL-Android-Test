package com.example.ciblandroidtest

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.util.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo







class ConfirmationDialog : DialogFragment() {
    private lateinit var alertDialog: AlertDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_confirmation, null)
        builder.setView(view)
        val downloadButton: Button = view.findViewById(R.id.downloadBtn)
//        val callBt: TextView = view.findViewById(R.id.btn_call)
//
//        errorTv.text = String.format("Error: $errorMsg")

//        callBt.setOnClickListener {
//            alertDialog.dismiss()
//        }
        downloadButton.setOnClickListener {
            generatePDF()
        }
        alertDialog = builder.create()
        return alertDialog
    }

    private fun generatePDF() {
        val document = PdfDocument()
        val paint = Paint()
        val pageInfo = PageInfo.Builder(100, 100, 1).create()
        // start a page
        val page: PdfDocument.Page = document.startPage(pageInfo)

        val canvas = page.canvas
        canvas.drawText("Payment Invoice",40F,50F, paint)

        document.finishPage(page)

//        document.writeTo()

        document.close()
    }
}