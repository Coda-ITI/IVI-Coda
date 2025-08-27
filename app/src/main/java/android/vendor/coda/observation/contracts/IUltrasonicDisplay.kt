package android.vendor.coda.observation.contracts

interface IUltrasonicDisplay {
    fun displayUltrasonicRC(reading : Float)
    fun displayUltrasonicRL(reading : Float)
    fun displayUltrasonicRR(reading : Float)
}