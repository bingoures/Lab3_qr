package com.lab.qr;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity {

    private Button scan_btn;
    private TextView linkTextView;
    private TextView resultTextView;
    private EditText textForQR;
    private Button generate_btn;
    private ImageView QRImage;
    private Context context;

    public Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();

        scan_btn = (Button) findViewById(R.id.scan_btn);
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integratot = new IntentIntegrator(activity);
                integratot.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integratot.setPrompt("Scan");
                integratot.setCameraId(0);
                integratot.setBeepEnabled(false);
                integratot.setBarcodeImageEnabled(false);
                integratot.initiateScan();
            }
        });

        resultTextView = (TextView) findViewById(R.id.resultTextView);

        linkTextView = (TextView) findViewById(R.id.linkTextView);
        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linkTextView.getText() != ""){
                    String link = linkTextView.getText().toString();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
            }
        });

        textForQR = (EditText) findViewById(R.id.textForQR);
        generate_btn = (Button) findViewById(R.id.generate_btn);
        QRImage = (ImageView) findViewById(R.id.QRImage);

        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";
                text = textForQR.getText().toString();
                if(text.length() > 0){
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        //this.bitmap
                        QRImage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        linkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String l = linkTextView.getText().toString();
                if(l.length() > 0){
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", l);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Скопировано в буфер обмена", Toast.LENGTH_LONG).show();
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(l));
                startActivity(i);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Вы отменили сканирование", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG ).show();
                resultTextView.setText(result.getContents());
                String str = result.getContents();
                int index = str.lastIndexOf("http://");
                if(index != -1){
                    char temp = '-';
                    String resLink = "";
                    int i = 0;
                    do {
                        if( (index + i) > (str.length() - 1)) break;
                        temp = str.charAt(index + i++);
                        resLink += temp;
                    } while (temp != ' ' || (str.length() == (index+i - 1)));
                    linkTextView.setText(resLink);
                }

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
