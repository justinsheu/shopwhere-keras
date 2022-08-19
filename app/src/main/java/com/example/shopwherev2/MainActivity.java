package com.example.shopwherev2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

public class  MainActivity extends AppCompatActivity {

    private Button takePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePicture = (Button)findViewById(R.id.takepic);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(openCamera.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(openCamera, 101);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 101 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Intent openImageView = new Intent(MainActivity.this, OpenCamera.class);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            openImageView.putExtra("byteArray", bs.toByteArray());
            startActivity(openImageView);
        }
    }
}