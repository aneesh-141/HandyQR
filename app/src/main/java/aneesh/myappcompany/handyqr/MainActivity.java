package aneesh.myappcompany.handyqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    EditText qrvalue;
    Button generateBtn, scanBtn, share, refresh;
    ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrvalue = findViewById(R.id.qrInput);
        generateBtn = findViewById(R.id.generateBtn);
        scanBtn = findViewById(R.id.scanBtn);
        qrImage = findViewById(R.id.qrPlaceHolder);
        refresh = findViewById(R.id.refresh);
        share = findViewById(R.id.share);

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = qrvalue.getText().toString();
                if(data.isEmpty()){
                    qrvalue.setError("Value Required!");
                }
                else {

                    //Encoding the QR code
                    QRGEncoder qrgEncoder = new QRGEncoder(data, null,  QRGContents.Type.TEXT, 500);

                    //Creating the bitmap
                    Bitmap bitmap = qrgEncoder.getBitmap();
                    qrImage.setImageBitmap(bitmap);

                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrvalue.setText("");
                qrvalue.setHint("Enter the link/text");
                qrImage.setImageResource(android.R.drawable.screen_background_dark_transparent);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!qrvalue.getText().toString().equals("")){

                    Drawable drawable=qrImage.getDrawable();
                    Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();

                    //Toast.makeText(MainActivity.this, bitmap.toString(), Toast.LENGTH_LONG).show();
                    Log.i( "hello", bitmap.toString());

                    try {
                        File file = new File(getApplicationContext().getExternalCacheDir(), File.separator+bitmap+".png");
                        FileOutputStream fOut = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        file.setReadable(true, false);
                        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID +".provider", file);

                        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setType("image/png");

                        startActivity(Intent.createChooser(intent, "Share image via"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Empty text field!", Toast.LENGTH_LONG).show();
                }


            }

        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Scanner.class));
            }
        });
    }
}


