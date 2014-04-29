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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author tuanchauict
 */
public class Combine5Date {

    static String path = "/Users/tuanchauict/Desktop/all/water_vapor/cropped/";
    static String repath = path;
    static int mergeLength = 7;
    static int mergeStep = 5;
    static boolean alpha = true;
    static boolean noalpha = false;
    static boolean toggle = false;
    static float minAlpha = 0.4f;
    static boolean singleColor = false;
    static float red = 0f;
    static float green = 0f;
    static float blue = 1f;
    
    public static void settingLand(String path){
        Combine5Date.path = path;
        if(!path.endsWith("/")){
            path += "/";
        }
        
        repath = path;
        alpha = true;
        noalpha = false;
        toggle = false;
        minAlpha = 0.2f;
        singleColor = false;
        red = 0;
        green = 0;
        blue = 1;
    }
    
    public static void settingSea(String path){
        Combine5Date.path = path;
        if(!path.endsWith("/")){
            path += "/";
        }
        
        repath = path;
        alpha = true;
        noalpha = false;
        toggle = false;
        minAlpha = 0.2f;
        singleColor = false;
        red = 0;
        green = 0;
        blue = 1;
    }
    
    public static void settingSnow(String path){
        Combine5Date.path = path;
        if(!path.endsWith("/")){
            path += "/";
        }
        
        repath = path;
        alpha = true;
        noalpha = false;
        toggle = false;
        minAlpha = 0.4f;
        singleColor = true;
        red = 0;
        green = 0;
        blue = 1;
    }
    
    public static void main3(int start){
        File dir = new File(path);
        File[] files = MergeByDate.loadFiles(dir);
        
        if(start > files.length){
            return;
        }
        
        File resDir = new File(path + "../combined_30");
        repath = resDir.getAbsolutePath() + "/";
        resDir.mkdirs();
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        System.out.println(files.length);
        
        List<BufferedImage> bis = new ArrayList<>();
        for (int j = start; j < start + mergeLength && j < files.length; j++) {
            bis.add(MergeByDate.readImage(files[j]));
        }
        System.out.println(bis.size());
        if (bis.size() < mergeLength) {
            for (int j = mergeLength - bis.size(); j > 0; j--) {
                bis.add(MergeByDate.readImage(files[start - j]));
            }
        }
        combine(start + "_" + files[start].getName(), bis);
    }
    
    public static void main2(){
        File dir = new File(path);
        File[] files = MergeByDate.loadFiles(dir);
        File resDir = new File(path + "../combined_5");
        repath = resDir.getAbsolutePath() + "/";
        resDir.mkdirs();
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

//        BufferedImage[] imgs = new BufferedImage[files.length];
//        for (int i = 0; i < files.length; i++) {
//            imgs[i] = MergeByDate.readImage(files[i]);
//        }

        for (int i = 0; i < files.length; i += mergeStep) {
            List<BufferedImage> bis = new ArrayList<>();
            for (int j = i; j < i + mergeLength && j < files.length; j++) {
                bis.add(MergeByDate.readImage(files[i]));
            }
            if (bis.size() < mergeLength) {
                for (int j = mergeLength - bis.size(); j > 0; j--) {
                    bis.add(MergeByDate.readImage(files[i - j]));
                }
            }
            combine(i + "_" + files[i].getName(), bis);
        }
    }
    

    public static void main(String[] args) {
        File dir = new File(path);
        File[] files = MergeByDate.loadFiles(dir);
        File resDir = new File(path + "../combined");
        repath = resDir.getAbsolutePath() + "/";
        resDir.mkdirs();
        Arrays.sort(files, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        BufferedImage[] imgs = new BufferedImage[files.length];
        for (int i = 0; i < files.length; i++) {
            imgs[i] = MergeByDate.readImage(files[i]);
        }

        for (int i = 0; i < files.length; i += mergeStep) {
            List<BufferedImage> bis = new ArrayList<>();
            for (int j = i; j < i + mergeLength && j < files.length; j++) {
                bis.add(imgs[j]);
            }
            if (bis.size() < mergeLength) {
                for (int j = mergeLength - bis.size(); j > 0; j--) {
                    bis.add(imgs[i - j]);
                }
            }
            combine(i + "_" + files[i].getName(), bis);
        }
    }

    static void combine(String firstname, List<BufferedImage> list) {
        int w = list.get(0).getWidth();
        int h = list.get(0).getHeight();

        int[] rgbs = initZemorInts(w * h);

        float[] hs = initZemorFloats(rgbs.length);
        float[] as = initZemorFloats(rgbs.length);
        float[] ss = initZemorFloats(rgbs.length);
        float[] bs = initZemorFloats(rgbs.length);
        int[] count = initZemorInts(hs.length);

        for (BufferedImage img : list) {
            rgbs = img2ints(img);
            float[][] has = rgbs2hs(rgbs);
            float[] hss = has[0];
            float[] ass = has[1];
            float[] sss = has[2];
            float[] bss = has[3];
            
            for (int i = 0; i < hss.length; i++) {
                if (hss[i] >= 0) {
                    hs[i] += hss[i];
                    ss[i] += sss[i];
                    bs[i] += bss[i];
                    count[i]++;
                }

                as[i] += ass[i];

                
            }
            
//            img.flush();
            img = null;
        }
        
        list.clear();
        
        for (int i = 0; i < hs.length; i++) {
            if (count[i] > 0) {
                hs[i] /= count[i];
                ss[i] /= count[i];
                bs[i] /= count[i];
            } else {
                hs[i] = -1;
            }
            as[i] = as[i] / mergeLength + minAlpha;
            if (as[i] > 1) {
                as[i] = 1;
            }
        }

        rgbs = hs2rgbs(hs, as, ss, bs);

        BufferedImage combine = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
//        Graphics g = combine.getGraphics();
        combine.setRGB(0, 0, w, h, rgbs, 0, w);

        File file = new File(repath + "c_" + (alpha ? "a_" : "n_") + firstname);
        try {
            ImageIO.write(combine, "PNG", file);
            combine.flush();
            combine = null;
            System.out.println("done: " + firstname);
        } catch (IOException ex) {
            Logger.getLogger(Combine5Date.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static int[] img2ints(BufferedImage img) {
        return img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
    }

    static int[] initZemorInts(int length) {
        int[] arr = new int[length];
        for (int i = 0; i < length; i++) {
            arr[i] = 0;
        }

        return arr;
    }

    static float[] initZemorFloats(int length) {
        float[] arr = new float[length];
        for (int i = 0; i < length; i++) {
            arr[i] = 0;
        }

        return arr;
    }

    static float[] rgb2h(int val) {
        Color color = new Color(val, true);
        int alpha = color.getAlpha();
        if (alpha == 0) {

            return new float[]{-1, 0, 0, 0};

        }

        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        return new float[]{hsb[0], 1, hsb[1], hsb[2]};
    }

    static float[][] rgbs2hs(int[] vals) {
        float[] arr = new float[vals.length];
        float[] aarr = new float[vals.length];
        float[] sarr = new float[vals.length];
        float[] barr = new float[vals.length];
        for (int i = 0; i < vals.length; i++) {
            float[] vs = rgb2h(vals[i]);
            arr[i] = vs[0];
            aarr[i] = vs[1];
            sarr[i] = vs[2];
            barr[i] = vs[3];
        }

        return new float[][]{arr, aarr, sarr, barr};
    }

    static int[] sumArr(int[] arr1, int[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            arr2[i] += arr1[i];
        }
        return arr2;
    }

    static float[] sumArr(float[] arr1, float[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            arr2[i] += arr1[i];
        }
        return arr2;
    }

    static int[] devices(int[] arr, int number) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] /= number;
        }
        return arr;
    }

    static float[] devices(float[] arr, int number) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] /= number;
        }
        return arr;
    }

    static int h2rgb(float h, float s, float b) {
        if (h >= 0) {
            return Color.HSBtoRGB(toggle ? 1 - h : h, s, b);
        }

        return Color.TRANSLUCENT;
    }

    static int ha2rgb(float h, float a, float s, float b) {
        if(noalpha && a > 0){
            a = 1;
        }
        
        if (h >= 0) {
            int c;
            if (singleColor) {
                h = toggle?toggle(h):h;
                Color cc = new Color(red * h, green * h, 1f, a);
                c = cc.getRGB();
                return c;
            } else {
                c = Color.HSBtoRGB(scaleH(toggle ? toggle(h) : h), s, b);
                Color co = new Color(c);
                Color ca = new Color(co.getRed(), co.getGreen(), co.getBlue(), (int) (a * 255));

            return ca.getRGB();
            }
            
        } else {
            return Color.TRANSLUCENT;
        }
    }

    static float scaleH(float h) {
//        return h > 0.72f?0.72f:h;
        return h;
    }
    
    static float toggle(float h){
//        h = 0.72f - h;
//        return h < 0?0:h;
        return h;
    }

    static int[] hs2rgbs(float[] hs, float[] as, float[] ss, float[] bs) {
        int[] rgbs = new int[hs.length];
        if (!alpha) {
            for (int i = 0; i < hs.length; i++) {
                rgbs[i] = h2rgb(hs[i], ss[i], bs[i]);
            }
        } else {
            for (int i = 0; i < hs.length; i++) {
                rgbs[i] = ha2rgb(hs[i], as[i], ss[i], bs[i]);
            }
        }

        return rgbs;
    }

}
