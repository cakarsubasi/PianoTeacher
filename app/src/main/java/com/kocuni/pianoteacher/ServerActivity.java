package com.kocuni.pianoteacher;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kocuni.pianoteacher.utils.MyJSON;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerActivity extends AppCompatActivity {
    String selectedImagePath;
    private static final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");
    final int SELECT_MULTIPLE_IMAGES = 1;
    ArrayList<String> selectedImagesPaths; // Paths of the image(s) selected by the user.
    boolean imagesSelected = false;
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes

                        Intent data = result.getData();

                        String currentImagePath;
                        selectedImagesPaths = new ArrayList<>();
                        TextView numSelectedImages = findViewById(R.id.numSelectedImages);
                        if (data.getData() != null) {
                            Uri uri = data.getData();
                            currentImagePath = getPath(getApplicationContext(), uri);
                            Log.d("ImageDetails", "Single Image URI : " + uri);
                            Log.d("ImageDetails", "Single Image Path : " + currentImagePath);
                            selectedImagesPaths.add(currentImagePath);
                            imagesSelected = true;
                            numSelectedImages.setText("Number of Selected Images : " + selectedImagesPaths.size());
                        } else {
                            // When multiple images are selected.
                            // Thanks tp Laith Mihyar for this Stackoverflow answer : https://stackoverflow.com/a/34047251/5426539
                            if (data.getClipData() != null) {
                                ClipData clipData = data.getClipData();
                                for (int i = 0; i < clipData.getItemCount(); i++) {

                                    ClipData.Item item = clipData.getItemAt(i);
                                    Uri uri = item.getUri();

                                    currentImagePath = getPath(getApplicationContext(), uri);
                                    selectedImagesPaths.add(currentImagePath);
                                    Log.d("ImageDetails", "Image URI " + i + " = " + uri);
                                    Log.d("ImageDetails", "Image Path " + i + " = " + currentImagePath);
                                    imagesSelected = true;
                                    numSelectedImages.setText("Number of Selected Images : " + selectedImagesPaths.size());
                                }
                            }
                        }

                    }
                    Toast.makeText(getApplicationContext(), selectedImagesPaths.size() + " Image(s) Selected.", Toast.LENGTH_LONG).show();

                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(ServerActivity.this, new String[]{Manifest.permission.INTERNET}, 2);
        ActivityCompat.requestPermissions(ServerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        setContentView(R.layout.activity_server);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(), "Access to Storage Permission Granted. Thanks.", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(getApplicationContext(), "Access to Storage Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(), "Access to Internet Permission Granted. Thanks.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Access to Internet Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

     public void connectServer(View v){
         TextView responseText = findViewById(R.id.responseText);
         if (imagesSelected == false) { // This means no image is selected and thus nothing to upload.
             responseText.setText("No Image Selected to Upload. Select Image(s) and Try Again.");
             return;
         }
         responseText.setText("Sending the Files. Please Wait ...");

         EditText ipv4AddressView = findViewById(R.id.IPAddress);
         String ipv4Address = ipv4AddressView.getText().toString();
         EditText portNumberView = findViewById(R.id.portNumber);
         String portNumber = portNumberView.getText().toString();

         Matcher matcher = IP_ADDRESS.matcher(ipv4Address);
         if (!matcher.matches()) {
             responseText.setText("Invalid IPv4 Address. Please Check Your Inputs.");
             return;
         }

         String postUrl = "http://" + ipv4Address + ":" + portNumber + "/";

         MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

         for (int i = 0; i < selectedImagesPaths.size(); i++) {
             BitmapFactory.Options options = new BitmapFactory.Options();
             options.inPreferredConfig = Bitmap.Config.RGB_565;

             ByteArrayOutputStream stream = new ByteArrayOutputStream();
             try {
                 // Read BitMap by file path.
                 Bitmap bitmap = BitmapFactory.decodeFile(selectedImagesPaths.get(i), options);
                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
             }catch(Exception e){
                 responseText.setText("Please Make Sure the Selected File is an Image.");
                 return;
             }
             byte[] byteArray = stream.toByteArray();

             multipartBodyBuilder.addFormDataPart("image" + i, "Android_Flask_" + i + ".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
         }

         RequestBody postBodyImage = multipartBodyBuilder.build();

//        RequestBody postBodyImage = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
//                .build();

         postRequest(postUrl, postBodyImage);
    }
    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(30,TimeUnit.SECONDS).build();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                Thread gfgThread = new Thread(() -> {

                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("Failed to Connect to Server. Please Try Again.");



                });
                gfgThread.start();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                Thread gfgThread = new Thread(() -> {

                    TextView responseText = findViewById(R.id.responseText);
                    try {

                        String serverResponse= response.body().string();

                        responseText.setText( "songfromserver.json created");
                        MyJSON.saveData(serverResponse);
                        System.out.println(MyJSON.getData());
                    } catch (IOException  e) {
                        e.printStackTrace();
                    }
                });
                gfgThread.start();

            }


        }
        );

    }
    public void selectImage(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        someActivityResultLauncher.launch(photoPickerIntent);
    }



    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}