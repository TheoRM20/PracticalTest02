package ro.pub.cs.systems.eim.practicaltest02.model;

public class TimeInformation {

    private String hours;
    private String minutes;
    private String seconds;

    public TimeInformation(String hours, String minutes, String seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public TimeInformation() {
    }

    @Override
    public String toString() {
        return "TimeInformation{" +
                "hours='" + hours + '\'' +
                ", minutes='" + minutes + '\'' +
                ", seconds='" + seconds + '\'' +
                '}';
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }
}
