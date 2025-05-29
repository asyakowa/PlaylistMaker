package com.example.playlistmaker.sharing.domain.api

import android.content.Intent

interface SharingInteractor {
    fun shareApp() : Intent
    fun openUserAgreement() : Intent
    fun openSupport() : Intent

    fun getShareError(): String
    fun getUserAgreementError(): String
    fun getSupportError(): String
}
