package com.rishi.frendzapp.ui.mediacall;

public interface CallVideoActionsListener {

    /**
     * On/off device camera depends on argument
     *
     * @param turnOn - if true - turn camera on or turn it off
     */
    void onCam(boolean turnOn);

    /**
     * Switching between front and back cameras
     */
    void switchCam();


}
