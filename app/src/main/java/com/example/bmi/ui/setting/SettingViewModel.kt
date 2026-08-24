package com.example.bmi.ui.setting

import androidx.lifecycle.ViewModel
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewModel(
    private val repository: BmiRepository
) : ViewModel() {
    var isChecked = false

    fun check() {
        isChecked = !isChecked
    }

    private val _isLogin = MutableStateFlow(false)
    val isLogin = _isLogin.asStateFlow()

    fun login() {
        // 登录成功以后
        _isLogin.value = true
    }

    fun logout() {
        _isLogin.value = false
    }

}