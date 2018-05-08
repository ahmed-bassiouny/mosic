package ntamtech.mosic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView imageBackground, imageForeground;
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
    private final int defaultAlpha = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageBackground = findViewById(R.id.image_background);
        imageForeground = findViewById(R.id.image_foreground);
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
                        checkAvailableImage();
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
                newBitmap = getController().addWaterMark(((BitmapDrawable) imageBackground.getDrawable()).getBitmap(),((BitmapDrawable)imageForeground.getDrawable()).getBitmap(),imageForeground.getImageAlpha());
                getController().saveNewImage(newBitmap, currentFile);
                getController().deleteImage(currentFile);
                filesName.remove(currentIndex);
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                newImage.performClick();
                //getController().doPhotoPrint(newBitmap);
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
                imageForeground.setImageAlpha((seekBar.getProgress() * 255) / 100);
            }
        });
        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newImage.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);
                seekBar.setVisibility(View.INVISIBLE);
                tvPercentage.setVisibility(View.INVISIBLE);
                imageForeground.setImageBitmap(null);
                imageBackground.setImageBitmap(null);
                checkAvailableImage();
            }
        });
    }

    private void checkAvailableImage(){
        if(filesName.size() == 0){
            select.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No Image Found", Toast.LENGTH_SHORT).show();
        }else {
            select.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        if (bitmap != null) {
            this.selectedBitmap = bitmap;
            currentIndex = getController().randomElement(filesName);
            currentFile = filesName.get(currentIndex);
            randomBitmap = getController().getBitmapFromStringPath(currentFile);
            imageBackground.setImageBitmap(selectedBitmap);
            imageForeground.setImageBitmap(randomBitmap);
            imageForeground.setImageAlpha(defaultAlpha);
            seekBar.setVisibility(View.VISIBLE);
            tvPercentage.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            newImage.setVisibility(View.VISIBLE);
            tvPercentage.setVisibility(View.VISIBLE);
        }
    }

    private HomeController getController() {
        if (controller == null)
            controller = new HomeController(this);
        return controller;
    }

}
