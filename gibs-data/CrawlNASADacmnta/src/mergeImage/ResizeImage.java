/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mergeImage;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author tuanchauict
 */
public class ResizeImage {
    public static void main(String[]args) throws IOException{
        File dir = new File("/Users/tuanchauict/Desktop/sync/snow/cropped/");
        String outDir = dir.getCanonicalPath() + "/../resized/";
        new File(outDir).mkdirs();
        
        
        File[] files = dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        });
        
        
        
        for(File f:files){
            System.out.println(f.getName());
            BufferedImage img = MergeByDate.readImage(f);
            BufferedImage r = resize(img, 4000, 2000);
            ImageIO.write(r, "PNG", new File(outDir + f.getName()));
        }
    }
    
    
    
    static BufferedImage resize(BufferedImage img, int width, int height){
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = res.getGraphics();
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        return res;
    }
}
