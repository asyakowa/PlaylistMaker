package com.example.playlistmaker.sharing.domain

interface SharingRepository {
    fun getShareMessage(): String
    fun getSupportEmail(): String
    fun getSupportThemeMessage(): String
    fun getSupportMessage(): String
    fun getUserAgreement(): String
    fun getShareError(): String
    fun getUserAgreementError(): String
    fun getSupportError(): String
}