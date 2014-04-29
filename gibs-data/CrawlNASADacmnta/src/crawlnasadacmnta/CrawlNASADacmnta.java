/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlnasadacmnta;


import com.squareup.okhttp.OkHttpClient;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author tuanchauict
 */
public class CrawlNASADacmnta {

    private static final String baseDir = "./";
    
    private static final OkHttpClient client = new OkHttpClient();

    private static final Date date = new Date();
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final long timeOfDay = 1000 * 60 * 60 * 24;
    private static final Date date20120508 = new Date(1336441659380L);
    private static final Date dateNow = new Date(date.getTime() - timeOfDay);

    private static final int level = 4;
    private static final int maxRow = 9;
    private static final int maxCol = 19;

//    private static final String[] dataTypes = {"snow_covers", "dust_score", "ozon", "diep_luc_bien", "sea_temp", "land_temp", "water_vapor", "so2", "rain"};
    
//    private static final String[] dataUrls = {
//        "https://map1a.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MODIS_Terra_Snow_Cover&STYLE=&TILEMATRIXSET=EPSG4326_500m&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//snow
//        "https://map1b.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=AIRS_Dust_Score&STYLE=&TILEMATRIXSET=EPSG4326_2km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//dust
//        "https://map1c.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MLS_O3_46hPa_Night&STYLE=&TILEMATRIXSET=EPSG4326_2km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//ozon
//        "https://map1a.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MODIS_Aqua_Chlorophyll_A&STYLE=&TILEMATRIXSET=EPSG4326_1km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//diep luc bien
//        "https://map1b.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MODIS_Terra_Land_Surface_Temp_Day&STYLE=&TILEMATRIXSET=EPSG4326_1km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//land temp
//        "https://map1a.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MODIS_Terra_Water_Vapor_5km_Day&STYLE=&TILEMATRIXSET=EPSG4326_2km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//water vapor
//        "https://map1a.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=OMI_SO2_Planetary_Boundary_Layer&STYLE=&TILEMATRIXSET=EPSG4326_2km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//so2
//        "https://map1c.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=AIRS_Precipitation_Day&STYLE=&TILEMATRIXSET=EPSG4326_2km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//rain
//        "https://map1c.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MODIS_Terra_Cloud_Top_Pressure_Day&STYLE=&TILEMATRIXSET=EPSG4326_2km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//cloud top pressure
//        "https://map1b.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MODIS_Terra_Aerosol&STYLE=&TILEMATRIXSET=EPSG4326_2km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//dust
//    };

    public static final String[] dataTypes = {"land", "snow","sea"};

    private static final String[] dataUrls = {
        "https://map1b.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MODIS_Terra_Land_Surface_Temp_Day&STYLE=&TILEMATRIXSET=EPSG4326_1km&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//land temp
        "https://map1a.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=MODIS_Aqua_Snow_Cover&STYLE=&TILEMATRIXSET=EPSG4326_500m&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//snow cover aqua
        "https://map1a.vis.earthdata.nasa.gov/wmts-geo/wmts.cgi?TIME={date}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=Sea_Surface_Temp_Infrared&STYLE=&TILEMATRIXSET=EPSG4326_500m&TILEMATRIX={level}&TILEROW={row}&TILECOL={col}&FORMAT=image%2Fpng",//sea temp
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        registerSSL();

        try{
            Thread thrs[] = new FetchDataThread[300];
            for(int i = 0; i < thrs.length; i++){
                thrs[i] = new FetchDataThread(i % 3, i / 3);
                thrs[i].start();
            }

            for(Thread t:thrs){
                t.join();
            }

        }
        catch (Exception e){

        }
        try {
            Thread []ths = new Thread[dataTypes.length];
            for(int i = 0; i < ths.length; i++){
                FetchRunner run = new FetchRunner(i);
                ths[i] = new Thread(run);
                ths[i].start();
            }
            for (Thread th : ths) {
                th.join();
            }
//            createUrlTable();
        } catch (Exception ex) {
            Logger.getLogger(CrawlNASADacmnta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void registerSSL() {
        System.setProperty("jsse.enableSNIExtension", "false");
        // Create a new trust manager that trust all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

// Activate the new trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    private static String buildURL(int dataType, Date date, int level, int row, int col) {
        String result = dataUrls[dataType];
        result = result.replace("{date}", format.format(date)).replace("{level}", "" + level).replace("{row}", row + "").replace("{col}", "" + col);

        return result;
    }

    private static String buildURL(int dataType, String date, int level, int row, int col) {
        String result = dataUrls[dataType];
        result = result.replace("{date}", date).replace("{level}", "" + level).replace("{row}", row + "").replace("{col}", "" + col);

        return result;
    }

    private static String buildTableRow(int id, Date date, int level, int row, int col) {
        return String.format("\"%s/%s\" \"%s\" \"%s\"", dataTypes[id], format.format(date), level + "_" + row + "_" + col + ".png", buildURL(id, date, level, row, col));
    }

    private static void createUrlTable() throws Exception {
        PrintWriter fos = new PrintWriter(baseDir + "url_table_0.txt");
        for (int i = 0; i < dataTypes.length; i++) {

            Date date = date20120508;
            while (!date.after(dateNow)) {
                for (int k = 0; k <= maxRow; k++) {
                    for (int j = 0; j <= maxCol; ++j) {
                        String row = buildTableRow(i, date, level, k, j) + "\n";
                        fos.append(row);
//                        System.out.print(row);
                    }
                }

                date = dateAfter(date);
            }
        }
        fos.close();

    }

    private static byte[] fetchData(String url) {
        try {
            URL u = new URL(url);
//            URLConnection con = u.openConnection();
            HttpsURLConnection con = (HttpsURLConnection) client.open(u);

            con.setDoInput(true);
//            System.out.println("opened");
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116");
            con.connect();
//            System.out.println("con: " + con.);
//            System.out.println("connected");
            byte[] buffer = new byte[2048];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InputStream is = con.getInputStream();
            int length = is.read(buffer);
            while (length >= 0) {
//                System.out.println("length = " + length);
                bos.write(buffer, 0, length);
                length = is.read(buffer);
            }

            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String mkdir(String dirname) {
        File file = new File(baseDir + dirname);
        file.mkdirs();
        return file.getAbsolutePath() + "/";
    }

    private static boolean writeFile(String filename, byte[] data) {
        BufferedOutputStream bos = null;
        try {
            File file = new File(filename);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
            bos.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(CrawlNASADacmnta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    private static Date dateAfter(Date date) {
        return new Date(date.getTime() + timeOfDay);
    }
    private static Date dateAfter(Date date, int numdates){
        return new Date(date.getTime() + timeOfDay * numdates);
    }

    static class FetchRunner extends Thread {

        int id;
        String strDir;
        String baseUrl;

        public FetchRunner(int id) {
            this.id = id;
            strDir = dataTypes[id];
            baseUrl = dataUrls[id];
        }

        @Override
        public void run() {
            Date date = date20120508;
            System.out.println(strDir);
            while (!date.after(dateNow)) {
                System.out.println(format.format(date));
                String dir = mkdir(strDir + "/" + format.format(date));
                for (int i = 0; i <= maxRow; ++i) {
                    for (int j = 0; j <= maxCol; ++j) {
                        System.out.println("loading : " + strDir + " > " + i + " - " + j);
                        String url = buildURL(id, date, level, i, j);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }).run();

//                        System.out.println("url = " + url);
                        byte[] data = fetchData(url);

                        if (data != null) {
//                            System.out.println("data length = " + data.length);
                            writeFile(dir + level + "_" + i + "_" + j + ".png", data);
//                            System.out.println("complete");
                        }
                    }
                }

                date = dateAfter(date);
            }
        }

    }

    static class FetchDataThread extends  Thread{
        int id;
        int offset;
        String strDir;
        String baseUrl;

        FetchDataThread(int id, int offset){
            this.id = id;
            strDir = dataTypes[id];
            baseUrl = dataUrls[id];
            this.offset = offset;
        }

        @Override
        public void run(){
            Date date = dateAfter(date20120508, offset);
            System.out.println(strDir);
            while (!date.after(dateNow)) {
                System.out.println(format.format(date));
                String dir = mkdir(strDir + "/" + format.format(date));
                for (int i = 0; i <= maxRow; ++i) {
                    for (int j = 0; j <= maxCol; ++j) {
                        String filename = dir + level + "_" + i + "_" + j + ".png";
                        File file = new File(filename);
                        if(file.exists()){
                            System.out.println("skip: " + filename);
                            continue;
                            
                        }
                        System.out.println("loading : " + strDir + " > " + i + " - " + j);
                        String url = buildURL(id, date, level, i, j);

//                        System.out.println("url = " + url);
                        byte[] data = fetchData(url);

                        if (data != null) {
//                            System.out.println("data length = " + data.length);
//                            writeFile(dir + level + "_" + i + "_" + j + ".png", data);
                            
                            BufferedOutputStream bos = null;
                            try {
                                bos = new BufferedOutputStream(new FileOutputStream(file));
                                bos.write(data);
                                bos.close();
                             
                            } catch (IOException ex) {
                                Logger.getLogger(CrawlNASADacmnta.class.getName()).log(Level.SEVERE, null, ex);
                            }
//                            System.out.println("complete");
                        }
                    }
                }

                date = dateAfter(date, offset);
            }
        }


    }
}
