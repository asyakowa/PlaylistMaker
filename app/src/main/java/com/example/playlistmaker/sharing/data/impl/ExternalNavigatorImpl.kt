package com.example.playlistmaker.sharing.data.impl

import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.model.AgreementData
import com.example.playlistmaker.sharing.domain.model.ShareData
import com.example.playlistmaker.sharing.domain.model.SupportData
import com.example.playlistmaker.sharing.domain.ExternalNavigator

class ExternalNavigatorImpl(
) : ExternalNavigator {

    override fun navigateToShare(shareData: ShareData): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareData.message)
        }
    }

    override fun navigateToSupport(supportData: SupportData): Intent {
        return  Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportData.email))
            putExtra(Intent.EXTRA_SUBJECT, (supportData.theme))
            putExtra(Intent.EXTRA_TEXT, (supportData.message))
        }
    }

    override fun navigateToAgreement(agreementData: AgreementData): Intent {
        val url = Uri.parse(agreementData.url)
        return Intent(Intent.ACTION_VIEW, url)
    }
}
