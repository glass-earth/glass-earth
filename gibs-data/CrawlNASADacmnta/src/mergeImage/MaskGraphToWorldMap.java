/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mergeImage;

import java.awt.Color;
import java.awt.Graphics;
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
public class MaskGraphToWorldMap {
    public static void main(String[]args) throws IOException{
        String worldPath = "/Users/tuanchauict/Desktop/Realistic Earth/maps/world_gray_green.png";
        String maskPath = "/Users/tuanchauict/Desktop/liti/data/land/";
        
        File resultDir = new File(maskPath + "../masked/");
        resultDir.mkdirs();
        String resultDirName = resultDir.getCanonicalPath() + "/";
        
        BufferedImage world = MergeByDate.readImage(new File(worldPath));
        File[] files = MergeByDate.loadFiles(new File(maskPath));
        
        for(File f:files){
            System.out.println(f.getName());
            BufferedImage bi = mask2(world, MergeByDate.readImage(f));    
            ImageIO.write(bi, "PNG", new File(resultDirName + f.getName()));
        }
        
    }
    
    static BufferedImage mask(BufferedImage worldMap, BufferedImage mask){
        BufferedImage result = new BufferedImage(worldMap.getWidth(), worldMap.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        int[]w = Combine5Date.img2ints(worldMap);
        int[]m = Combine5Date.img2ints(mask);
        int[]r = new int[w.length];
        
        float []ccw = new float[4];
        float []ccm = new float[4];
        float []ccr = new float[4];
        for(int i = 0 ; i < w.length; i++){
            Color cw = new Color(w[i]);
            Color cm = new Color(m[i]);
            
            ccw = cw.getRGBComponents(ccw);
            ccm = cm.getRGBComponents(ccm);
            
            for(int j = 0; j < 3; j++){
                ccr[j] = ccm[j] * ccm[3] + ccw[j] * ccw[3] * (1f - ccm[3]);
            }
            
            Color cr = new Color(ccr[0], ccr[1], ccr[2], 1);
            r[i] = cr.getRGB();
        }
        
        result.setRGB(0, 0, worldMap.getWidth(), worldMap.getHeight(), r, 0, worldMap.getWidth());
        
        return result;
    }
    
    static BufferedImage mask2(BufferedImage worldMap, BufferedImage mask){
        BufferedImage result = new BufferedImage(mask.getWidth(), mask.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = result.getGraphics();
        g.drawImage(worldMap, 0, 0, null);
        g.drawImage(mask, 0, 0, null);
        return result;
    }
}
