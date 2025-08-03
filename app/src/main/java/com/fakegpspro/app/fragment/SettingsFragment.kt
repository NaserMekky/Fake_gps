package com.fakegpspro.app.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fakegpspro.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // فتح إعدادات المطور
        binding.btnDeveloperOptions.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }

        // فتح إعدادات الموقع
        binding.btnLocationSettings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }

        // فتح إعدادات التطبيق
        binding.btnAppSettings.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${requireContext().packageName}")
                startActivity(intent)
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }

        // معلومات التطبيق
        binding.btnAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun showAboutDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("حول التطبيق")
        builder.setMessage("""
            GPS Pro - محاكي الموقع الاحترافي
            
            الإصدار: 1.0
            
            تطبيق متقدم لمحاكاة الموقع الجغرافي بطريقة احترافية وآمنة.
            
            الميزات:
            • محاكاة الموقع بدقة عالية
            • واجهة مستخدم سهلة الاستخدام
            • دعم المواقع المفضلة
            • إشعارات ذكية
            
            تم التطوير بواسطة: Manus AI
        """.trimIndent())
        builder.setPositiveButton("موافق", null)
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

