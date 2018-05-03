package ntamtech.mosic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mvc.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView image1, image2;
    private TextView tvPercentage;
    SeekBar seekBar;
    private Bitmap selectedBitmap, randomBitmap;
    private HomeController controller;
    private ArrayList<String> filesName;
    private Button select, save, newImage;
    private Bitmap newBitmap;
    private ProgressBar progress;
    private String currentFile;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image1 = findViewById(R.id.image_1);
        image2 = findViewById(R.id.image_2);
        seekBar = findViewById(R.id.seek_bar);
        select = findViewById(R.id.select);
        save = findViewById(R.id.save);
        progress = findViewById(R.id.progress);
        newImage = findViewById(R.id.new_image);
        tvPercentage = findViewById(R.id.tv_percentage);
        onClick();
        ImagePicker.setMinQuality(600, 600);
        new Thread(new Runnable() {
            @Override
            public void run() {
                filesName = getController().fetchFileNames();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        if (filesName.size() == 0) {
                            Toast.makeText(MainActivity.this, "no image found", Toast.LENGTH_SHORT).show();
                        } else {
                            select.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    private void onClick() {
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.pickImage(MainActivity.this, "Select your image:");
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBitmap = getController().addWaterMark(((BitmapDrawable)image1.getDrawable()).getBitmap(),((BitmapDrawable)image2.getDrawable()).getBitmap(),image2.getImageAlpha());
                getController().saveNewImage(newBitmap, currentFile);
                getController().deleteImage(currentFile);
                filesName.remove(currentIndex);
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                getController().doPhotoPrint(newBitmap);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tvPercentage.setText(seekBar.getProgress() + "%");
                //image2.setImageAlpha();
                image2.setImageAlpha((seekBar.getProgress() * 255) / 100);
               /* newBitmap = getController().addWaterMark(selectedBitmap, getController().getBitmapFromStringPath(currentFile), seekBar.getProgress());
                myImage.setImageBitmap(newBitmap);*/
            }
        });
        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*newImage.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);
                myImage.setImageBitmap(null);*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        if (bitmap != null) {
            this.selectedBitmap = bitmap;
            currentIndex = getController().randomElement(filesName);
            currentFile = filesName.get(currentIndex);
            randomBitmap = getController().getBitmapFromStringPath(currentFile);
            //newBitmap = getController().addWaterMark(selectedBitmap, getController().getBitmapFromStringPath(currentFile), seekBar.getProgress());
            //myImage.setImageBitmap(newBitmap);
            image1.setImageBitmap(selectedBitmap);
            image2.setImageBitmap(randomBitmap);
            image2.setImageAlpha(102);
            seekBar.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            newImage.setVisibility(View.VISIBLE);
            tvPercentage.setText("40%");
        }

    }

    private HomeController getController() {
        if (controller == null)
            controller = new HomeController(this);
        return controller;
    }

}
