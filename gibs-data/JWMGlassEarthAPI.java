/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.glassearth.webmap.api;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author tuanchauict
 */
public class JWMGlassEarthAPI {

    public static enum GEType {
        LandSurfaceTemperature("land-temp"),
        SeaSurfaceTemperature("sea-temp"),
        AerosolOpticalDepth("aerosol"),
        ChlorophyllA("chlorophyllA"),
        CloudTopTemperature("cloud-top-temp"),
        DustScore("dust-score"),
        Ozone("ozone"),
        SnowCover("snow-cover"),
        SulfurDioxide("so2"),
        WaterVapor("water-vapor"),
        Precipitation("precipitation");

        GEType(String value) {
            this.value = value;
        }
        String value;

        @Override
        public String toString() {
            return value;
        }
    }
    private static final long timeOfDay = 1000 * 60 * 60 * 24;
    private static final Date date20120508 = new Date(1336441659380L);
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 
     * @param type
     * @param date
     * @param level
     * @return the information of an 
     */
    public static GEInfo getInfo(GEType type, Date date, int level) {
        if(type == null){
            throw new IllegalArgumentException("Invalid GEType");
        }
        
        if(date.before(date20120508) || date.after(new Date())){
            throw new IllegalArgumentException("Invalid date.");
        }
        
        GEInfo result = new GEInfo();
        result.level = level;
        long d = (date.getTime() - date20120508.getTime()) / timeOfDay;
        if (GEType.DustScore == type) {
            result.group = 1;
            result.step = 1;
            result.date = date;
        } else {
            result.group = 2;
            result.step = 5;

            d = d / 5 * 5;
            Date newDate = dateAfter(date20120508, (int) d);

            result.date = newDate;

        }
        StringBuilder builder = new StringBuilder("http://wmapi.glassearth.net/");
        builder.append(result.version)
                .append("/")
                .append(type.toString())
                .append("?day=").append(format.format(result.date))
                .append("&level=").append(result.level);
        result.url = builder.toString();

        return result;
    }

    private static Date dateAfter(Date date, int numdates) {
        return new Date(date.getTime() + timeOfDay * numdates);
    }

    public static class GEInfo {

        private final String version = "v1";
        private String url;
        private Date date;
        private int level;
        private int group;
        private int step;

        public String getVersion() {
            return version;
        }

        public String getUrl() {
            return url;
        }

        public Date getDate() {
            return date;
        }

        public int getLevel() {
            return level;
        }

        public int getGroup() {
            return group;
        }

        @Override
        public String toString(){
            StringBuilder buider = new StringBuilder();
            buider.append("version: ").append(version).append("\n")
                    .append("date: ").append(format.format(date)).append("\n")
                    .append("level: ").append(level).append("\n")
                    .append("group: ").append(group).append("\n")
                    .append("step: ").append(step).append("\n")
                    .append("url: ").append(url);
            
            
            
            return buider.toString();
        }
    }

    public static void main(String args[]) {
        GEInfo r = getInfo(GEType.AerosolOpticalDepth, dateAfter(date20120508, 1), 1);
        System.out.println(r);
    }
}
