/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mergeImage;

import crawlnasadacmnta.CrawlNASADacmnta;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author tuanchauict
 */
public class CropImage {
    static int width = 10240;
    static int height = 5120;
    
    private static final String baseDir = "/Users/tuanchauict/Desktop/all/";
//    private static final String[] dataTypes = {"snow_covers", "dust_score", "ozon", "diep_luc_bien", "sea_temp", "land_temp", "water_vapor", "so2", "rain"};
    private static final String[] dataTypes = CrawlNASADacmnta.dataTypes;
    
    public static void main(String[]args){
//        crop1();
        crop2("/Users/tuanchauict/Desktop/sync/snow/combined_30/");
    }

    private static void crop1() {
        for(int i = 0; i < dataTypes.length; i++){
            String dir = baseDir + dataTypes[i] + "/merged";
            String cropDir = baseDir + dataTypes[i] + "/cropped/";
            new File(cropDir).mkdirs();
            File[]files = MergeByDate.loadFiles(new File(dir));
            for(File f:files){
                BufferedImage img = MergeByDate.readImage(f);
                BufferedImage crp = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

                crp.getGraphics().drawImage(img, 0, 0, null);
                File out = new File(cropDir + f.getName());
                try {
                    ImageIO.write(crp, "PNG", out);
                } catch (IOException ex) {
                    Logger.getLogger(CropImage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private static void crop2(String path){
        File dir = new File(path);
        String cropDir = path + "../cropped/";
        new File(cropDir).mkdirs();
        File[]files = MergeByDate.loadFiles(dir);
        for(File f:files){
            System.out.println(f.getName());
            BufferedImage img = MergeByDate.readImage(f);
            BufferedImage crp = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

            crp.getGraphics().drawImage(img, 0, 0, null);
            File out = new File(cropDir + f.getName());
            try {
                ImageIO.write(crp, "PNG", out);
                crp.flush();
                img.flush();
            } catch (IOException ex) {
                Logger.getLogger(CropImage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
