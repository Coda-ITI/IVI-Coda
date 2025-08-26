package android.vendor.coda.observation;

import android.vendor.coda.observation.IRPMReadings;
import android.vendor.coda.observation.IDoorStateReadings;
import android.vendor.coda.observation.ISpeedReadings;
import android.vendor.coda.observation.IUltrasonicReadings;

interface IObservationServiceIVIContract {
    void registerSpeedReadingsCallback(ISpeedReadings cb);

    void registerRPMReadingsCallback(IRPMReadings cb);

    void registerUltrasonic0ReadingsCallback(IUltrasonicReadings cb);
    void registerUltrasonic1ReadingsCallback(IUltrasonicReadings cb);
    void registerUltrasonic2ReadingsCallback(IUltrasonicReadings cb);
    void registerUltrasonic3ReadingsCallback(IUltrasonicReadings cb);

    void registerDoorStateFLReadingsCallback(IDoorStateReadings cb);
    void registerDoorStateFRReadingsCallback(IDoorStateReadings cb);
    void registerDoorStateRLReadingsCallback(IDoorStateReadings cb);
    void registerDoorStateRRReadingsCallback(IDoorStateReadings cb);

    void changeSystemThemeToLight();
    void changeSystemThemeToDark();
}
