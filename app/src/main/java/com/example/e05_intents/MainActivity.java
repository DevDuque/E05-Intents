package com.example.e05_intents;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int BARCODE_REQUEST_CODE = 200;
    private ImageView profileImageView;
    private TextView profilePhone, profileEmail, barcodeText;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileImageView = findViewById(R.id.profile_image);
        profilePhone = findViewById(R.id.profile_phone);
        profileEmail = findViewById(R.id.profile_email);
        barcodeText = findViewById(R.id.barcode_text);
        scanButton = findViewById(R.id.scan_button);

        // Abrir a câmera para alterar a foto de perfil
        profileImageView.setOnClickListener(v -> openCamera());

        // Abrir discador ao clicar no número de telefone
        profilePhone.setOnClickListener(v -> {
            String phoneNumber = profilePhone.getText().toString();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });

        // Abrir aplicativo de e-mail ao clicar no e-mail
        profileEmail.setOnClickListener(v -> {
            String email = profileEmail.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + email));
            startActivity(intent);
        });

        // Botão para escanear código de barras
        scanButton.setOnClickListener(v -> {
            scanBarcode();
        });
    }

    // Método para abrir a câmera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    // Método para iniciar o escaneamento do código de barras
    private void scanBarcode() {
        try {
            // Intent para abrir o ZXing Barcode Scanner
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE"); // Modo para escanear código de barras de produto
            startActivityForResult(intent, BARCODE_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // Caso o ZXing não esteja instalado, redirecionar o usuário para a Play Store
            Uri uri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(playStoreIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Captura a imagem da câmera
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profileImageView.setImageBitmap(photo);
        }

        if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Captura o resultado do escaneamento
            String barcode = data.getStringExtra("SCAN_RESULT");
            barcodeText.setText("Código de Barras: " + barcode);
        }
    }
}
