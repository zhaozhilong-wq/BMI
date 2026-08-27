package com.example.bmi.ui.setting

import androidx.lifecycle.ViewModel
import com.example.bmi.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewModel(
    private val repository: BmiRepository
) : ViewModel() {

    private val _isChecked = MutableStateFlow(false)
    val isChecked = _isChecked.asStateFlow()

    fun updateChecked(checked: Boolean) {
        _isChecked.value = checked
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