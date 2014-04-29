/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mergeImage;

/**
 *
 * @author tuanchauict
 */
public class CombineImage {
    public static void main(String[] args){
        String[] keywords = crawlnasadacmnta.CrawlNASADacmnta.dataTypes;
        String path = "./land/merged/";
        
        if(args.length >= 2){
            int start = Integer.parseInt(args[1]);
            
            if(args[0].equals("0")||args[0].equals("land")){
                path = "./land/merged/";
//                path = "/Users/tuanchauict/Desktop/all/land_temp/cropped/";
                Combine5Date.settingLand(path);
                Combine5Date.main3(start);
            }
            else if(args[0].equals("1")||args[0].equals("sea")){
                path = "./sea/merged/";
                Combine5Date.settingSea(path);
                Combine5Date.main3(start);
            }
            else if(args[0].equals("2")||args[0].equals("snow")){
                path = "./snow/merged/";
                Combine5Date.settingSnow(path);
                Combine5Date.main3(start);
            }
            else{
                System.out.println("INVALID ARGUMENT");
            }
        }
        else{
            System.out.println("NO ARGUMENT");
        }
        
    }
}
