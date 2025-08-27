package android.vendor.coda.observation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CarDataViewModel : ViewModel() {
    private val _rpm = MutableLiveData<Float>()
    val rpm: LiveData<Float> get() = _rpm

    private val _speed = MutableLiveData<Float>()
    val speed: LiveData<Float> get() = _speed

    private val _ultrasonicRL = MutableLiveData<Float>()
    val ultrasonicRL: LiveData<Float> get() = _ultrasonicRL

    private val _ultrasonicRC = MutableLiveData<Float>()
    val ultrasonicRC: LiveData<Float> get() = _ultrasonicRC

    private val _ultrasonicRR = MutableLiveData<Float>()
    val ultrasonicRR: LiveData<Float> get() = _ultrasonicRR

    private val _doorFL = MutableLiveData<Boolean>()
    val doorFL: LiveData<Boolean> get() = _doorFL

    private val _doorFR = MutableLiveData<Boolean>()
    val doorFR: LiveData<Boolean> get() = _doorFR

    private val _doorRL = MutableLiveData<Boolean>()
    val doorRL: LiveData<Boolean> get() = _doorRL

    private val _doorRR = MutableLiveData<Boolean>()
    val doorRR: LiveData<Boolean> get() = _doorRR

    fun updateRPM(value: Float) {
        _rpm.postValue(value)
    }

    fun updateSpeed(value: Float) {
        _speed.postValue(value)
    }

    fun updateUltrasonicRL(value: Float) {
        _ultrasonicRL.postValue(value)
    }

    fun updateUltrasonicRC(value: Float) {
        _ultrasonicRC.postValue(value)
    }

    fun updateUltrasonicRR(value: Float) {
        _ultrasonicRR.postValue(value)
    }

    fun updateDoorFL(isOpen: Boolean) {
        _doorFL.postValue(isOpen)
    }

    fun updateDoorFR(isOpen: Boolean) {
        _doorFR.postValue(isOpen)
    }

    fun updateDoorRL(isOpen: Boolean) {
        _doorRL.postValue(isOpen)
    }

    fun updateDoorRR(isOpen: Boolean) {
        _doorRR.postValue(isOpen)
    }
}