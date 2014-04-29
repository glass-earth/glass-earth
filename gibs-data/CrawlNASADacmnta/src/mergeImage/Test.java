package mergeImage;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tuanchauict
 */
public class Test {
    public static void main(String[]args){
        System.out.println(Arrays.toString(args));
        String file = "./";
        File f = new File(file);
        System.out.println(f.getAbsoluteFile());
        try {
            System.out.println(f.getCanonicalPath());
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static int getAlpha(int rgba){
        return rgba & 0x00ff;
    }
}
