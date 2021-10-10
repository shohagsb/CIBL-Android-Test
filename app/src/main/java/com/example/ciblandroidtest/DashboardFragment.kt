package com.example.ciblandroidtest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.ciblandroidtest.databinding.ActivityMainBinding
import com.example.ciblandroidtest.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        binding.bkashGroup.setOnClickListener{
            navigateToPayment("Bkash")
        }
        binding.nagadGroup.setOnClickListener {
            navigateToPayment("Nagad")
        }
        return binding.root
    }

    private fun navigateToPayment(type: String) {
        val bundle = bundleOf("payment_type" to type)
        this.findNavController().navigate(R.id.action_dashboardFragment_to_paymentFragment, bundle)
    }
}