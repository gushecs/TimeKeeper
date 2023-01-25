package chronos;

import java.io.Serializable;

public enum LogType implements Serializable {
    CREATION("✯"),
    RESET("⟲"),
    TIMEADD("⏱+"),
    TIMESUBTRACT("⏱-"),
    SETTIME("⏲"),
    START("▶"),
    PAUSE("⏸"),
    LOG("✍"),
    TIMEREXPIRED("⏰"),
    CHRONOMETERLOAD("⏱"),
    TIMERLOAD("⏲");

    private final String value;

    LogType(String value) {this.value = value;}

    @Override
    public String toString() {return value;}
}
