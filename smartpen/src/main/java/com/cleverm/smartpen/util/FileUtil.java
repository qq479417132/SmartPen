package com.cleverm.smartpen.util;

import java.io.File;

/**
 * Created by xiong,An android project Engineer,on 4/5/2016.
 * Data:4/5/2016  上午 11:46
 * Base on clever-m.com(JAVA Service)
 * Describe:
 * Version:1.0
 * Open source
 */
public class FileUtil {


    /**
     * 判断文件是否存在
     * @param file
     * @return
     */
    public static boolean checkFileExist(File file){
        if(file.exists() && file.isFile()){
            return true;
        }
        return false;
    }

    /**
     * 判断文件夹是否存在
     * @param file
     * @return
     */
    public static boolean checkFolderExist(File file){
        if(file .exists()  && file .isDirectory()){
            return true;
        }
        return false;
    }
}
