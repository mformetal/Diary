
package miles.diary.data.model.weather;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import miles.diary.util.Logg;

public class Main {

    final String DEGREE  = "\u00b0";

    @SerializedName("temp")
    @Expose
    private Double temp;
    @SerializedName("pressure")
    @Expose
    private Double pressure;
    @SerializedName("humidity")
    @Expose
    private Integer humidity;
    @SerializedName("temp_min")
    @Expose
    private Double tempMin;
    @SerializedName("temp_max")
    @Expose
    private Double tempMax;
    @SerializedName("sea_level")
    @Expose
    private Double seaLevel;
    @SerializedName("grnd_level")
    @Expose
    private Double grndLevel;

    /**
     * 
     * @return
     *     The temp
     */
    public Double getTemp() {
        return temp;
    }

    public String formatTemperature() {
        Locale current = Locale.getDefault();
        if (current.equals(Locale.US)) {
            return String.format("%2.0f", getTemp() * 9/5 - 459.67) + DEGREE + " F";
        } else {
            return String.format("%2.0f", getTemp() - 273.15) + DEGREE + " C";
        }
    }

    /**
     * 
     * @param temp
     *     The temp
     */
    public void setTemp(Double temp) {
        this.temp = temp;
    }

    /**
     * 
     * @return
     *     The pressure
     */
    public Double getPressure() {
        return pressure;
    }

    /**
     * 
     * @param pressure
     *     The pressure
     */
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    /**
     * 
     * @return
     *     The humidity
     */
    public Integer getHumidity() {
        return humidity;
    }

    /**
     * 
     * @param humidity
     *     The humidity
     */
    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    /**
     * 
     * @return
     *     The tempMin
     */
    public Double getTempMin() {
        return tempMin;
    }

    /**
     * 
     * @param tempMin
     *     The temp_min
     */
    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    /**
     * 
     * @return
     *     The tempMax
     */
    public Double getTempMax() {
        return tempMax;
    }

    /**
     * 
     * @param tempMax
     *     The temp_max
     */
    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

    /**
     * 
     * @return
     *     The seaLevel
     */
    public Double getSeaLevel() {
        return seaLevel;
    }

    /**
     * 
     * @param seaLevel
     *     The sea_level
     */
    public void setSeaLevel(Double seaLevel) {
        this.seaLevel = seaLevel;
    }

    /**
     * 
     * @return
     *     The grndLevel
     */
    public Double getGrndLevel() {
        return grndLevel;
    }

    /**
     * 
     * @param grndLevel
     *     The grnd_level
     */
    public void setGrndLevel(Double grndLevel) {
        this.grndLevel = grndLevel;
    }

}
