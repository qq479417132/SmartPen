package com.cleverm.smartpen.util.device;

/**
 * Created by xiong,An android project Engineer,on 24/8/2016.
 * Data:24/8/2016  上午 09:48
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class EasyDeviceInfo {

    private static class EasyDeviceInfoHolder {
        private static final EasyDeviceInfo INSTANCE = new EasyDeviceInfo();
    }

    private EasyDeviceInfo() {
    }

    public static final EasyDeviceInfo getInstance() {
        return EasyDeviceInfoHolder.INSTANCE;
    }


    public com.cleverm.smartpen.util.device.Application APPLICATION() {
        return com.cleverm.smartpen.util.device.Application.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.Permission PERMISSION() {
        return com.cleverm.smartpen.util.device.Permission.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.IP IP() {
        return com.cleverm.smartpen.util.device.IP.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.CheckDeviceInfo CHECK() {
        return com.cleverm.smartpen.util.device.CheckDeviceInfo.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.Phone PHONE() {
        return com.cleverm.smartpen.util.device.Phone.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.APP APP() {
        return com.cleverm.smartpen.util.device.APP.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.RAM RAM() {
        return com.cleverm.smartpen.util.device.RAM.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.ROM ROM() {
        return com.cleverm.smartpen.util.device.ROM.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.FileSteam FILE() {
        return com.cleverm.smartpen.util.device.FileSteam.INSTANCE;
    }

    public com.cleverm.smartpen.util.device.Time TIME() {
        return com.cleverm.smartpen.util.device.Time.INSTANCE;
    }
}
