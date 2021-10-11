package com.example.ciblandroidtest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ciblandroidtest.databinding.FragmentPaymentBinding


private const val ARG_PAYMENT_TYPE = "payment_type"

class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding

    private var paymentType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paymentType = it.getString(ARG_PAYMENT_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(layoutInflater)

        // Switch payment icon
        switchGatewayIcon()

        binding.submitButton.setOnClickListener {
            val number = binding.numberEt.text.toString()
            val name = binding.nameEt.text.toString()
            val amount = binding.amountEt.text.toString()
            val narration = binding.narrationEt.text.toString()
            if (number.isNotEmpty() || name.isNotEmpty()||amount.isNotEmpty() || narration.isNotEmpty() || paymentType.isNullOrEmpty()){
                val payment = PaymentModel(
                    number, name, amount.toDouble(), narration, paymentType!!
                )
                val dialog = ConfirmationDialog(payment)
                dialog.show(parentFragmentManager, "confirmation_dialog")
            }else{
                Toast.makeText(context, "Please fill the form", Toast.LENGTH_SHORT).show()
            }

        }
        return binding.root
    }

    private fun switchGatewayIcon() {
        if (paymentType != null) {
            if (paymentType == "Bkash") {
                binding.gatewayIcon.setImageResource(R.drawable.ic_bkash)
            } else {
                binding.gatewayIcon.setImageResource(R.drawable.ic_nagad)
            }
        }
    }

}