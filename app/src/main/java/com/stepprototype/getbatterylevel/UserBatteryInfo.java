package com.stepprototype.getbatterylevel;

public class UserBatteryInfo {
    public String deviceInfo;
    public String batteryPercentage;

    public UserBatteryInfo() {

    }

    public UserBatteryInfo(String batteryPercentage,String deviceInfo) {
        this.batteryPercentage = batteryPercentage;
        this.deviceInfo = deviceInfo;
    }
}
