package com.fakegpspro.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fakegpspro.app.MainActivity
import com.fakegpspro.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isLocationActive = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        // تعيين القيم الافتراضية
        binding.editLatitude.setText("24.7136")
        binding.editLongitude.setText("46.6753")
        binding.editAddress.setText("الرياض، المملكة العربية السعودية")
        
        updateUI()
    }

    private fun setupClickListeners() {
        binding.btnStartStop.setOnClickListener {
            if (isLocationActive) {
                stopMockLocation()
            } else {
                startMockLocation()
            }
        }

        binding.btnGetCurrentLocation.setOnClickListener {
            // محاكاة الحصول على الموقع الحالي
            binding.editLatitude.setText("24.7136")
            binding.editLongitude.setText("46.6753")
            Toast.makeText(context, "تم الحصول على الموقع الحالي", Toast.LENGTH_SHORT).show()
        }

        binding.btnRandomLocation.setOnClickListener {
            // إنشاء موقع عشوائي
            val randomLat = (Math.random() * 180 - 90).toString().substring(0, 8)
            val randomLng = (Math.random() * 360 - 180).toString().substring(0, 8)
            
            binding.editLatitude.setText(randomLat)
            binding.editLongitude.setText(randomLng)
            binding.editAddress.setText("موقع عشوائي")
        }

        binding.btnFavoriteLocations.setOnClickListener {
            showFavoriteLocations()
        }
    }

    private fun startMockLocation() {
        val latText = binding.editLatitude.text.toString()
        val lngText = binding.editLongitude.text.toString()

        if (latText.isEmpty() || lngText.isEmpty()) {
            Toast.makeText(context, "يرجى إدخال الإحداثيات", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val latitude = latText.toDouble()
            val longitude = lngText.toDouble()

            if (latitude < -90 || latitude > 90) {
                Toast.makeText(context, "خط العرض يجب أن يكون بين -90 و 90", Toast.LENGTH_SHORT).show()
                return
            }

            if (longitude < -180 || longitude > 180) {
                Toast.makeText(context, "خط الطول يجب أن يكون بين -180 و 180", Toast.LENGTH_SHORT).show()
                return
            }

            (activity as? MainActivity)?.startMockLocationService(latitude, longitude)
            isLocationActive = true
            updateUI()
            
            Toast.makeText(context, "تم بدء محاكاة الموقع", Toast.LENGTH_SHORT).show()

        } catch (e: NumberFormatException) {
            Toast.makeText(context, "يرجى إدخال أرقام صحيحة", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopMockLocation() {
        (activity as? MainActivity)?.stopMockLocationService()
        isLocationActive = false
        updateUI()
        
        Toast.makeText(context, "تم إيقاف محاكاة الموقع", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        if (isLocationActive) {
            binding.btnStartStop.text = "إيقاف المحاكاة"
            binding.btnStartStop.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark, null))
            binding.statusText.text = "الحالة: نشط"
            binding.statusText.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            binding.btnStartStop.text = "بدء المحاكاة"
            binding.btnStartStop.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark, null))
            binding.statusText.text = "الحالة: متوقف"
            binding.statusText.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }
    }

    private fun showFavoriteLocations() {
        val locations = arrayOf(
            "الرياض - 24.7136, 46.6753",
            "جدة - 21.4858, 39.1925",
            "الدمام - 26.4207, 50.0888",
            "مكة المكرمة - 21.3891, 39.8579",
            "المدينة المنورة - 24.5247, 39.5692"
        )

        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("المواقع المفضلة")
        builder.setItems(locations) { _, which ->
            when (which) {
                0 -> {
                    binding.editLatitude.setText("24.7136")
                    binding.editLongitude.setText("46.6753")
                    binding.editAddress.setText("الرياض، المملكة العربية السعودية")
                }
                1 -> {
                    binding.editLatitude.setText("21.4858")
                    binding.editLongitude.setText("39.1925")
                    binding.editAddress.setText("جدة، المملكة العربية السعودية")
                }
                2 -> {
                    binding.editLatitude.setText("26.4207")
                    binding.editLongitude.setText("50.0888")
                    binding.editAddress.setText("الدمام، المملكة العربية السعودية")
                }
                3 -> {
                    binding.editLatitude.setText("21.3891")
                    binding.editLongitude.setText("39.8579")
                    binding.editAddress.setText("مكة المكرمة، المملكة العربية السعودية")
                }
                4 -> {
                    binding.editLatitude.setText("24.5247")
                    binding.editLongitude.setText("39.5692")
                    binding.editAddress.setText("المدينة المنورة، المملكة العربية السعودية")
                }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

