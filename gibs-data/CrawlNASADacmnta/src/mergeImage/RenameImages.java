/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mergeImage;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author tuanchauict
 */
public class RenameImages {
    public static void main(String []args){
        String path = "/Users/tuanchauict/workspace/OpenSource/astronomers/EarthModelUnity/Assets/Resources/land_temp/";
        
        File file = new File(path);
        
        File[]files = file.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        });
        
        Arrays.sort(files, 0, files.length, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        
        for(int i = 0; i < files.length; i++){
            files[i].renameTo(new File(path + i + ".png"));
        }
    }
}
