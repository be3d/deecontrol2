package com.ysoft.dctrl.ui.notification;

/**
 * Created by pilar on 26.7.2017.
 */
public class SpinnerNotification extends IconNotification {
    public SpinnerNotification() {
        super();
        super.setTimeout(0);
    }
    @Override
    protected IconType getIconType() {
        return IconType.SPINNER;
    }


    @Override
    public void setTimeout(int timeout) {
        throw new UnsupportedOperationException("Timeout cannot be set on SpinnerNotification");
    }
}
