package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository
{
    override fun getShareMessage(): String = context.getString(R.string.linkforad)
    override fun getSupportEmail(): String = context.getString(R.string.email)
    override fun getSupportThemeMessage(): String = context.getString(R.string.messagefoad)
    override fun getSupportMessage(): String = context.getString(R.string.thanksforad)
    override fun getUserAgreement(): String = context.getString(R.string.linkfoua)
    override fun getShareError(): String = context.getString(R.string.toast_share)
    override fun getUserAgreementError(): String = context.getString(R.string.toast_ua)
    override fun getSupportError(): String = context.getString(R.string.toast_mestosupport)

}