package com.example.playlistmaker.sharing.domain.impl

 import android.content.Intent
 import com.example.playlistmaker.sharing.domain.ExternalNavigator
 import com.example.playlistmaker.sharing.domain.SharingRepository
 import com.example.playlistmaker.sharing.domain.api.SharingInteractor
 import com.example.playlistmaker.sharing.domain.model.AgreementData
 import com.example.playlistmaker.sharing.domain.model.ShareData
 import com.example.playlistmaker.sharing.domain.model.SupportData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val repository: SharingRepository,
) : SharingInteractor {

    override fun shareApp() : Intent {
        return externalNavigator.navigateToShare(getShareData())
    }

    override fun openUserAgreement() : Intent {
        return externalNavigator.navigateToAgreement(getAgreementData())
    }

    override fun openSupport() : Intent {
        return externalNavigator.navigateToSupport(getSupportData())
    }

    override fun getShareError(): String {
        return repository.getShareError()
    }

    override fun getUserAgreementError(): String {
        return repository.getUserAgreementError()
    }

    override fun getSupportError(): String {
        return repository.getSupportError()
    }

    private fun getShareData(): ShareData {
        return ShareData(repository.getShareMessage())
    }

    private fun getSupportData(): SupportData {
        return SupportData(
            repository.getSupportEmail(),
            repository.getSupportThemeMessage(),
            repository.getSupportMessage()
        )
    }

    private fun getAgreementData(): AgreementData {
        return AgreementData(repository.getUserAgreement())
    }
}