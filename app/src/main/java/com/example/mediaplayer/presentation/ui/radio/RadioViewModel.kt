package com.example.mediaplayer.presentation.ui.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.data.model.RadioStation
import com.example.mediaplayer.data.model.RadioStationEntity
import com.example.mediaplayer.domain.RadioStationInteractorRoom
import com.example.mediaplayer.domain.repository.RadioStationDataI
import kotlinx.coroutines.launch

class RadioViewModel(
    private val radioStationData: RadioStationDataI,
    private val radioStationInteractorRoom: RadioStationInteractorRoom,
) : ViewModel() {


    private val _radio = MutableLiveData<List<RadioStation>>()
    val radio: LiveData<List<RadioStation>> get() = _radio

    private var _positionOld = MutableLiveData<Int>()
    private val positionOld: LiveData<Int> get() = _positionOld

    private var _number = MutableLiveData<Int>()
    private val number: LiveData<Int> get() = _number

    private val _saveRadio = MutableLiveData<List<RadioStationEntity>>()
    val saveRadio: LiveData<List<RadioStationEntity>> get() = _saveRadio

    init {
        getRadioStation()
        _positionOld.value = 0
        _number.value = 0
        getRadioNumberOpening()
    }

    fun insertRadioNumberOpening(radio: RadioStationEntity) {
        viewModelScope.launch {
            radioStationInteractorRoom.insertRadio(radio)
        }
    }

    fun getRadioNumberOpening() {
        viewModelScope.launch {
            _saveRadio.value = radioStationInteractorRoom.getRadio()
        }
    }

    fun iconClickListenerViewModel(position: Int) {
        val list = _radio.value?.toMutableList() ?: return
        val itemOld = positionOld.value?.let { _radio.value?.get(it) } ?: return
        val item = _radio.value?.get(position) ?: return



        list[positionOld.value!!] = itemOld.copy(isImage = false)
        list[position] = item.copy(isImage = true)

        _radio.value = list
        _positionOld.value = position
    }


    fun getRadioStation() {
        viewModelScope.launch {
            _radio.value = radioStationData.dataListRadioStation()
        }
    }


    fun editTextSearch(text: String) {
        val list = _radio.value?.toMutableList() ?: return
        list.retainAll { it.nameRadio.contains(text) }
        _radio.value = list
    }
}