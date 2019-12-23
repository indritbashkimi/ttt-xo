package com.ibashkimi.tris.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ibashkimi.tris.BuildConfig
import com.ibashkimi.tris.R
import com.ibashkimi.tris.databinding.FragmentAboutBinding

class AboutFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAboutBinding.inflate(inflater, container, false).run {
            toolbar.apply {
                setTitle(R.string.about)
                setNavigationIcon(R.drawable.ic_back_nav)
                setNavigationOnClickListener { findNavController().navigateUp() }
            }
            sourceCode.setOnClickListener {
                CustomTabsIntent.Builder().build().launchUrl(
                    requireContext(),
                    Uri.parse(requireContext().getString(R.string.source_code_url))
                )
            }
            feedback.setOnClickListener(this@AboutFragment)
            version.text = getString(R.string.version, BuildConfig.VERSION_NAME)
            androidJetpack.setOnClickListener(this@AboutFragment)
            kotlin.setOnClickListener(this@AboutFragment)
            kotlinCoroutines.setOnClickListener(this@AboutFragment)
            material.setOnClickListener(this@AboutFragment)
            root
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.feedback -> sendFeedback()
            R.id.android_jetpack -> openUrl(R.string.android_jetpack_website)
            R.id.kotlin -> openUrl(R.string.kotlin_website)
            R.id.kotlinCoroutines -> openUrl(R.string.kotlinx_coroutines_website)
            R.id.material -> openUrl(R.string.material_components_website)
        }
    }

    private fun sendFeedback() {
        val address = getString(R.string.author_email)
        val subject = getString(R.string.feedback_subject)

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$address"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)

        val chooserTitle = getString(R.string.feedback_chooser_title)
        startActivity(Intent.createChooser(emailIntent, chooserTitle))
    }

    private fun openUrl(url: Int) = openUrl(getString(url))

    private fun openUrl(url: String) {
        CustomTabsIntent.Builder().build().launchUrl(requireContext(), Uri.parse(url))
    }
}