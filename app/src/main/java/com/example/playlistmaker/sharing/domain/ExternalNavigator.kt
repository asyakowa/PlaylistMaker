package com.example.playlistmaker.sharing.domain

import android.content.Intent
import com.example.playlistmaker.sharing.domain.model.AgreementData
import com.example.playlistmaker.sharing.domain.model.ShareData
import com.example.playlistmaker.sharing.domain.model.SupportData

interface ExternalNavigator {
    fun navigateToShare(shareData: ShareData): Intent
    fun navigateToSupport(supportData: SupportData): Intent
    fun navigateToAgreement(agreementData: AgreementData): Intent
}