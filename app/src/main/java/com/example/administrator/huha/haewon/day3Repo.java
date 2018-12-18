package com.example.administrator.huha.haewon;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class day3Repo {

    @SerializedName("weather")
    weather weather;
    @SerializedName("result")
    Result result;

    public class Result {
        @SerializedName("message") String message;
        @SerializedName("code") String code;

        public String getMessage() {return message;}
        public String getCode() {return code;}
    }
    public class weather{
        public List<forecast3days> forecast3days = new ArrayList<>();
        public List<forecast3days> getForecast() {return forecast3days;}

        public class forecast3days{
            @SerializedName("fcst3hour") fcst3hour fcst3hour;

            public class fcst3hour{
                @SerializedName("sky") Sky sky;
                @SerializedName("temperature") temperature temperature;

                public class Sky{
                    @SerializedName("code25hour") String code25hour;
                    @SerializedName("code49hour") String code49hour;

                    public String getCode25hour() {return code25hour;}
                    public String getCode49hour() {return code49hour;}
                }

                public class temperature{
                    @SerializedName("temp25hour") String temp25hour;
                    @SerializedName("temp49hour") String temp49hour;
                    @SerializedName("temp46hour") String temp46hour;

                    public String getTemp25hour() {return temp25hour;}
                    public String getTemp49hour() {return temp49hour;}
                    public String getTemp46hour() {return temp46hour;}
                }

                public Sky getSky() {return sky;}
                public temperature getTemperature() {return temperature;}
            }

            public  fcst3hour getFcst3hour() {return fcst3hour;}
        }
    }

    public Result getResult() {return result;}
    public weather getWeather() {return weather;}

    public interface days3ApiInterface {
        @Headers({"Accept: application/json", "appKey: e35f4512-2486-4f52-aec2-2bef6d636191"})
        @GET("weather/forecast/3days")
        Call<day3Repo> get_3days_retrofit(@Query("version") int version, @Query("lat") String lat, @Query("lon") String lon);
    }
}

