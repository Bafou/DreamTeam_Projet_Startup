package services;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class help write and read access android private file
 * @author Lucas Delvallet https://github.com/LucasDelvallet
 */
public class File_IO {

    public static final String USER_LOCATION_FILE = "userlocation";
    public static final String CAR_LOCATION_FILE = "carlocation";
    public static final String PARKING_END_TIME = "parkingendtime";

    /**
     * Any data can be save into a private file with this method. Warning, it replaces the old data if they already exists.
     * @param context
     * @param file_name
     * @param data
     */
    public static void save_to_file(Context context, String file_name, String data){
        try {
            FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * You can load data from private file. Return an empty chain if there is no file.
     * @param context
     * @param file_name
     * @return
     */
    public static String load_from_file(Context context, String file_name){
        if(does_file_exist(context, file_name)){
            try {
                FileInputStream fis = context.openFileInput(file_name);
                StringBuilder builder = new StringBuilder();
                int ch;
                while((ch = fis.read()) != -1){
                    builder.append((char)ch);
                }
                fis.close();
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    /**
     * Check if the specified file exist
     * @param context
     * @param file_name
     * @return
     */
    public static boolean does_file_exist(Context context, String file_name){
        File file = context.getFileStreamPath(file_name);
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public static void delete_all_files(Context context){
        context.deleteFile(USER_LOCATION_FILE);
        context.deleteFile(CAR_LOCATION_FILE);
        context.deleteFile(PARKING_END_TIME);
    }
}
