package android.vendor.coda.observation;

interface IDoorStateReadings {
	void onDoorStateChanged(int position, boolean isOpen);
}
