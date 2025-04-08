package com.bindiya.strinx.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bindiya.strinx.data.model.RandomStringData
import com.bindiya.strinx.domain.usecase.DeleteAllEntryUseCase
import com.bindiya.strinx.domain.usecase.DeleteEntryUseCase
import com.bindiya.strinx.domain.usecase.GenerateRandomStringUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bindiya.strinx.data.datasource.Result
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val generateRandomStringUseCase: GenerateRandomStringUseCase,
    private val deleteRandomStringUseCase: DeleteEntryUseCase,
    private val deleteAllStringsUseCase: DeleteAllEntryUseCase,
) : ViewModel() {
    private val _randomStrings = MutableLiveData<List<RandomStringData>>(emptyList())
    val randomStrings: LiveData<List<RandomStringData>> = _randomStrings
    private val _loadState = MutableLiveData<Boolean>(false)
    val loadState: LiveData<Boolean> = _loadState

    fun generateRandomString(length: Int) {
        if (length > 1000) {
            _errorData.postValue("Length should be less than 1000")
            return
        }
        viewModelScope.launch {
            _loadState.value = true
            generateRandomStringUseCase(length).run {
                handleResult(
                    onSuccess = {
                        _randomStrings.value = _randomStrings.value?.plus(data!!)
                    })
            }
        }
    }

    fun deleteRandomString(randomStringData: RandomStringData) {
        viewModelScope.launch {
            _loadState.value = true
            deleteRandomStringUseCase(randomStringData.value).run {
                handleResult(
                    onSuccess = {
                        _randomStrings.value =
                            _randomStrings.value?.filterNot { it == randomStringData }
                    })
            }
        }
    }

    fun deleteAllStrings() {
        viewModelScope.launch {
            _loadState.value = true
            deleteAllStringsUseCase().run {
                handleResult(
                    onSuccess = {
                        _randomStrings.value = emptyList()
                    })
            }
        }
    }

    private val _errorData = MutableLiveData<String>("")
    val errorData: LiveData<String> = _errorData

    private fun <T> Result<T>.handleResult(
        onSuccess: () -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        when (this) {
            is Result.Error -> {
                onError?.invoke(errorMessage)
                _errorData.postValue(errorMessage)
                _loadState.value = false
            }

            is Result.Success -> {
                _loadState.value = false
                onSuccess.invoke()
            }
        }
    }
}
