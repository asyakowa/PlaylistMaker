package com.example.playlistmaker.sharing.domain

import android.content.Intent


interface SharingInteractor {
    fun shareApp() : Intent
    fun openUserAgreement() : Intent
    fun openSupport() : Intent

    fun getShareError(): String
    fun getUserAgreementError(): String
    fun getSupportError(): String
}