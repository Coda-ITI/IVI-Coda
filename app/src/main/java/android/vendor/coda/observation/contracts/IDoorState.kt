package android.vendor.coda.observation.contracts

interface IDoorState {
    fun setFLDoorState(isOpen : Boolean)
    fun setFRDoorState(isOpen : Boolean)
    fun setRLDoorState(isOpen : Boolean)
    fun setRRDoorState(isOpen : Boolean)
}