/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mergeImage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 *
 * @author tuanchauict
 */
public class ReadSizeImage {
    public static void main(String[]args){
        
        String []paths = {
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-10.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-11.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-12.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-13.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-14.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-15.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-16.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-17.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-18.png",
            "/Volumes/DATA/workspace/test/new_data/cloud/merged/2012-05-19.png",
            
        };
        
        
        String path = "/Users/tuanchauict/Downloads/land/combined_30";
        File dir = new File(path);
        File[]files = dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        });
        
        for(File f:files){
            showSize(f);
        }
        
    }

    private static void showSize(File file) {
        BufferedImage img = MergeByDate.readImage(file);
        int w = img.getWidth();
        int h = img.getHeight();
        int[]rgbs = Combine5Date.img2ints(img);
        int maxW = 0;
        int maxH = 0;
        boolean [][]m = new boolean[w][h];
        for(int i = 0; i < rgbs.length; i++){
            Color c = new Color(rgbs[i], true);
            m[i%w][i/w] = c.getAlpha()==0;
        }
        int []rowAs = new int[h];
        for(int i = 0; i < h; i++){
            for(int j = 1; j < w; j++){
                if(m[j][i] && !m[j-1][i]){
                    rowAs[i] = j;
                }
            }
        }
        
        int []colAs = new int[w];
        for(int i = 0; i < w; i++){
            for(int j = 1; j < h; j++){
                if(m[i][j] && !m[i][j-1]){
                    colAs[i] = j;
                }
            }
        }
        
        System.out.printf("%50s%10d%10d\n", file.getName(), max(rowAs), max(colAs));
        
        
    }
    
    static int max(int...arr){
        int max = arr[0];
        for(int i = 0; i < arr.length; i++){
            if(max < arr[i]){
                max = arr[i];
            }
        }
        
        return max;
    }
    
    
}
