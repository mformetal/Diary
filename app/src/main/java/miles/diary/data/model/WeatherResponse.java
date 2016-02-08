package miles.diary.data.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Locale;

/**
 * Created by mbpeele on 2/4/16.
 */
public class WeatherResponse {

    private double latitude;
    private double longitude;
    private String timezone;
    private int offset;
    private Flags flags;
    @SerializedName("currently") private Weather currentWeather;
    @SerializedName("hourly") private Forecast hourlyForecast;
    @SerializedName("daily") private Forecast dailyForecast;

    public Forecast getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast(Forecast dailyForecast) {
        this.dailyForecast = dailyForecast;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(Weather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public Forecast getHourly() {
        return hourlyForecast;
    }

    public void setHourly(Forecast hourly) {
        this.hourlyForecast = hourly;
    }

    public static class Weather implements Parcelable {
        final String DEGREE  = "\u00b0";

        private int time;
        private String summary;
        private String icon;
        private double precipIntensity;
        private double precipProbability;
        private float temperature;
        private float apparentTemperature;
        private float dewPoint;
        private float humidity;
        private float windSpeed;
        private int windBearing;
        private float cloudCover;
        private float pressure;
        private float ozone;

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public double getPrecipIntensity() {
            return precipIntensity;
        }

        public void setPrecipIntensity(double precipIntensity) {
            this.precipIntensity = precipIntensity;
        }

        public double getPrecipProbability() {
            return precipProbability;
        }

        public void setPrecipProbability(double precipProbability) {
            this.precipProbability = precipProbability;
        }

        public float getTemperature() {
            return temperature;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }

        public String formatTemperature() {
            return String.format("%2.0f", getApparentTemperature()) + DEGREE + " F";
        }

        public float getApparentTemperature() {
            return apparentTemperature;
        }

        public void setApparentTemperature(float apparentTemperature) {
            this.apparentTemperature = apparentTemperature;
        }

        public float getDewPoint() {
            return dewPoint;
        }

        public void setDewPoint(float dewPoint) {
            this.dewPoint = dewPoint;
        }

        public float getHumidity() {
            return humidity;
        }

        public void setHumidity(float humidity) {
            this.humidity = humidity;
        }

        public float getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(float windSpeed) {
            this.windSpeed = windSpeed;
        }

        public int getWindBearing() {
            return windBearing;
        }

        public void setWindBearing(int windBearing) {
            this.windBearing = windBearing;
        }

        public float getCloudCover() {
            return cloudCover;
        }

        public void setCloudCover(float cloudCover) {
            this.cloudCover = cloudCover;
        }

        public float getPressure() {
            return pressure;
        }

        public void setPressure(float pressure) {
            this.pressure = pressure;
        }

        public float getOzone() {
            return ozone;
        }

        public void setOzone(float ozone) {
            this.ozone = ozone;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.DEGREE);
            dest.writeInt(this.time);
            dest.writeString(this.summary);
            dest.writeString(this.icon);
            dest.writeDouble(this.precipIntensity);
            dest.writeDouble(this.precipProbability);
            dest.writeFloat(this.temperature);
            dest.writeFloat(this.apparentTemperature);
            dest.writeFloat(this.dewPoint);
            dest.writeFloat(this.humidity);
            dest.writeFloat(this.windSpeed);
            dest.writeInt(this.windBearing);
            dest.writeFloat(this.cloudCover);
            dest.writeFloat(this.pressure);
            dest.writeFloat(this.ozone);
        }

        public Weather() {
        }

        protected Weather(Parcel in) {
            this.time = in.readInt();
            this.summary = in.readString();
            this.icon = in.readString();
            this.precipIntensity = in.readDouble();
            this.precipProbability = in.readDouble();
            this.temperature = in.readFloat();
            this.apparentTemperature = in.readFloat();
            this.dewPoint = in.readFloat();
            this.humidity = in.readFloat();
            this.windSpeed = in.readFloat();
            this.windBearing = in.readInt();
            this.cloudCover = in.readFloat();
            this.pressure = in.readFloat();
            this.ozone = in.readFloat();
        }

        public static final Parcelable.Creator<Weather> CREATOR = new Parcelable.Creator<Weather>() {
            public Weather createFromParcel(Parcel source) {
                return new Weather(source);
            }

            public Weather[] newArray(int size) {
                return new Weather[size];
            }
        };
    }

    public static class Forecast {
        private String summary;
        private String icon;
        private List<Weather> data;

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public List<Weather> getData() {
            return data;
        }

        public void setData(List<Weather> data) {
            this.data = data;
        }
    }

    public static class Flags {
        private List<String> sources;
        @SerializedName("isd-stations") private List<String> stations;
        private String units;

        public List<String> getSources() {
            return sources;
        }

        public void setSources(List<String> sources) {
            this.sources = sources;
        }

        public List<String> getStations() {
            return stations;
        }

        public void setStations(List<String> stations) {
            this.stations = stations;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }
    }
}
