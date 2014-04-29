/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mergeImage;

import crawlnasadacmnta.CrawlNASADacmnta;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static mergeImage.CropImage.width;
import org.omg.CORBA.Environment;

/**
 *
 * @author tuanchauict
 */
public class MergeByDate {

    private static final String baseDir = "./";
//    private static final String[] dataTypes = {"diep_luc_bien",
//                                                "dust_score",
//                                                "land_temp",
//                                                "ozon",
//                                                "rain",
//                                                "sea_temp",
//                                                "snow_covers",
//                                                "so2",
//                                                "water_vapor"};

    
    public static Set<String> setTypes = new HashSet<>();

    static {
        setTypes.addAll(Arrays.asList(CrawlNASADacmnta.dataTypes));
    }

    public static void main(String[] args) {
        File[] dirs = listFolder(new File(baseDir), true);
        System.out.println("folder: " + dirs.length);

        Thread[] thrs = new Thread[dirs.length];
        for (int i = 0; i < thrs.length; i++) {
            thrs[i] = new MergeThread2(dirs[i]);
            thrs[i].start();
//            break;
        }

        for (Thread t : thrs) {
            try {
                t.join();
//                break;
            } catch (InterruptedException ex) {
                Logger.getLogger(MergeByDate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    static BufferedImage mergeByDate(File[] files) {
        BIData[] bis = new BIData[files.length];
        for (int i = 0; i < files.length; i++) {
            bis[i] = new BIData(files[i]);
        }

        return merge(bis);
//        File parent = files[0].getParentFile();
//        String parentname = parent.getName();
//        parent = parent.getParentFile();
//        String dir = parent.getAbsolutePath() + "/merge/";
//        parent = new File(dir);
//        parent.mkdirs();
//        System.out.println(parentname);
//        try {
//            ImageIO.write(combined, "PNG", new File(dir + parentname + ".png"));
//        } catch (IOException ex) {
//            Logger.getLogger(MergeByDate.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    static File[] loadFiles(String dirname) {
        File dir = new File(baseDir + dirname);
        return loadFiles(dir);
    }

    static File[] loadFiles(File dir) {
        return dir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        });
    }

    static File[] listFolder(File baseDir) {
        return baseDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
//                System.out.println(pathname.getName());
                return pathname.isDirectory() && !pathname.getName().contains("merged");
            }
        });
    }

    static File[] listFolder(File baseDir, boolean basedList) {
        return baseDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
//                System.out.println(pathname.getName());
                return pathname.isDirectory() && !pathname.getName().contains("merged") && setTypes.contains(pathname.getName());
            }
        });
    }

    public static BufferedImage readImage(String filename) {
        return readImage(new File(filename));
    }

    static BufferedImage readImage(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            return img;
        } catch (IOException ex) {
            Logger.getLogger(MergeByDate.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    static BufferedImage merge(BIData... img) {
        if (img.length <= 0) {
            return null;
        }

        int tw = img[0].w;
        int th = img[0].h;

        int numRows = 0;
        int numCols = 0;
        for (BIData i : img) {
            if (i.row > numRows) {
                numRows = i.row;
            }
            if (i.col > numCols) {
                numCols = i.col;
            }
        }
//        System.out.printf("%d\t%d\t%d\t%d\n", tw, th, numRows, numCols);
        numCols++;
        numRows++;

        BufferedImage combined = new BufferedImage(tw * numCols, th * numRows, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        for (BIData i : img) {
            g.drawImage(i.image, i.col * tw, i.row * th, null);
        }

        return combined;
    }

    ;
    
    public static class BIData {

        int row;
        int col;
        BufferedImage image;
        int w, h;

        public BIData(File file) {
            String name = file.getName();
            System.out.println(file.getPath());
//            System.err.println(name);
            String[] arg = name.split("_");
            row = Integer.parseInt(arg[1]);
            col = Integer.parseInt(arg[2].split("\\.")[0]);
            image = readImage(file);
            w = image.getWidth();
            h = image.getHeight();
        }
    }

    public static class MergeThread extends Thread {

        File dir;

        public MergeThread(File dir) {
            this.dir = dir;
        }

        @Override
        public void run() {
            File[] ddirs = listFolder(dir);
            String mergeFolderName = dir.getAbsolutePath() + "/" + "merged/";
            new File(mergeFolderName).mkdirs();

            for (File d : ddirs) {
                File[] files = loadFiles(d);
                BufferedImage img = mergeByDate(files);
                if (img != null) {
                    File png = new File(mergeFolderName + d.getName() + ".png");
                    try {
                        ImageIO.write(img, "PNG", png);
                    } catch (IOException ex) {
                        Logger.getLogger(MergeByDate.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    static class MergeThread2 extends Thread {

        File dir;

        public MergeThread2(File dir) {
            this.dir = dir;
        }

        @Override
        public void run() {
            File[] ddirs = listFolder(dir);
            String mergeFolderName = dir.getAbsolutePath() + "/" + "merged/";
            new File(mergeFolderName).mkdirs();

            DoMergeThread thrs[] = new DoMergeThread[5];
            for (int i = 0; i < thrs.length; i++) {
                thrs[i] = new DoMergeThread(ddirs, i, thrs.length, mergeFolderName);
                thrs[i].start();
            }

            for (Thread t : thrs) {
                try {
                    t.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MergeByDate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    static class DoMergeThread extends Thread {

        static int width = 640;
        static int height = 320;
        File[] dirs;
        int offset;
        int step;
        String targetDir;

        public DoMergeThread(File[] dirs, int offset, int step, String targetDir) {
            this.dirs = dirs;
            this.offset = offset;
            this.step = step;
            this.targetDir = targetDir;
        }

        public void run() {
            for (int i = offset; i < dirs.length; i += step) {
                File[] files = loadFiles(dirs[i]);
                if (files.length < 200) {
                    continue;
                }
                try {
                    BufferedImage img = mergeByDate(files);
                    if (img != null) {
                        File png = new File(targetDir + dirs[i].getName() + ".png");
                        try {
                            ImageIO.write(img, "PNG", png);
                            
                            
                            
//                   BufferedImage crp = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

//                   crp.getGraphics().drawImage(img, 0, 0, null);
                        } catch (IOException ex) {
                            Logger.getLogger(MergeByDate.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
