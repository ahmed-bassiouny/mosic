package ntamtech.mosic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v4.print.PrintHelper;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by bassiouny on 30/04/18.
 */

public class HomeController {

    private String mosicPath = Environment.getExternalStorageDirectory() +"/Mosic/";
    private String originalPath = mosicPath+"Original/";
    private String editedPath = mosicPath+"Edited/";
    private Activity activity;
    private Canvas canvas;

    public HomeController(Activity activity) {
        this.activity = activity;
        File mosicFile = new File(mosicPath);
        File originalFile = new File(originalPath);
        File editedFile = new File(editedPath);

        if(!mosicFile.exists()){
            mosicFile.mkdir();
        }
        if (!originalFile.exists()) {
            originalFile.mkdir();
        }
        if (!editedFile.exists()) {
            editedFile.mkdir();
        }
    }

    public Bitmap addWaterMark(Bitmap src, Bitmap imageFromDevice,int alpha) {

        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        canvas = new Canvas(result);
        Paint p = new Paint();
        p.setAlpha(alpha);
        canvas.drawBitmap(src, 0, 0,null );
        canvas.drawBitmap(getResizedBitmap(imageFromDevice,w,h), 0, 0, p);
        return result;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public Bitmap getBitmapFromStringPath(String fileName) {

        String photoPath1 = originalPath + fileName;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap waterMark = BitmapFactory.decodeFile(photoPath1, options);
        return waterMark;
    }

    public void saveNewImage(Bitmap result, String fileName) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(editedPath + fileName);

            result.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, "Can\'t save image", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, "Can\'t save image", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> fetchFileNames() {

        ArrayList<String> filenames = new ArrayList<>();
        File directory = new File(originalPath);

        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; i++) {

            String file_name = files[i].getName();
            // you can store name to arraylist and use it later
            filenames.add(file_name);
        }
        return filenames;
    }

    public int randomElement(ArrayList<String> files) {
        Random rand = new Random();
        return rand.nextInt(files.size());
    }
    public void deleteImage(String fileName){
        File file = new File(originalPath+fileName);
        file.delete();
    }
    public void doPhotoPrint(Bitmap bitmap) {
        PrintHelper photoPrinter = new PrintHelper(activity);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }
}
