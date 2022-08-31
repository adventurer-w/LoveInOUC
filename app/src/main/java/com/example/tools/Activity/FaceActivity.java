package com.example.tools.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.example.tools.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FaceActivity extends AppCompatActivity {

    private ImageView back;
    private ImageView pic;
    private TextView sex;
    private TextView age;
    private TextView point;
    private Uri photoUri;   //相机拍照返回图片路径
    private File outputImage;
    public static final String STR_IMAGE = "image/*";
    private Uri cropImgUri;
    private Dialog bottomDialog;
    private static final int TAKE_PHOTO = 3;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final int GET_BACKGROUND_FROM_CAPTURE_RESOULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_face);
        back = findViewById(R.id.imageView9);
        pic = findViewById(R.id.imageView8);
        sex = findViewById(R.id.textView29);
        age = findViewById(R.id.textView28);
        point = findViewById(R.id.textView30);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog bottomDialog = new Dialog(FaceActivity.this, R.style.Theme_Design_BottomSheetDialog);
                View contentView = LayoutInflater.from(FaceActivity.this).inflate(R.layout.layout_takepic, null);
                TextView tv_camera = contentView.findViewById(R.id.tv_camera);
                TextView tv_chose = contentView.findViewById(R.id.tv_chose);
                TextView tv_cancle = contentView.findViewById(R.id.tv_cancle);
                bottomDialog.setContentView(contentView);
                ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
                layoutParams.width = getResources().getDisplayMetrics().widthPixels;
                contentView.setLayoutParams(layoutParams);
                bottomDialog.getWindow().setGravity(Gravity.BOTTOM);//弹窗位置
                bottomDialog.getWindow().setWindowAnimations(R.style.Animation_Design_BottomSheetDialog);//弹窗样式
                bottomDialog.show();
                tv_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectCamera();
                        bottomDialog.dismiss();
                    }
                });
                tv_chose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectPhoto();
                        bottomDialog.dismiss();
                    }
                });
                tv_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomDialog.dismiss();
                    }
                });
            }
        });

    }

    private void selectCamera() {
        outputImage = new File(FaceActivity.this.getExternalCacheDir(), "camera_photos.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoUri = Uri.fromFile(outputImage);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STR_IMAGE);
        startActivityForResult(intent, GET_BACKGROUND_FROM_CAPTURE_RESOULT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case GET_BACKGROUND_FROM_CAPTURE_RESOULT:   //相册返回
                photoUri = data.getData();
                cropRawPhoto(photoUri);
                break;
            case TAKE_PHOTO://   拍照返回
                cropRawPhoto(photoUri);
                break;
            case RESULT_REQUEST_CODE:   //裁剪完照片
                if (cropImgUri != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("1233p", "000123");
                                final Bitmap headImage = BitmapFactory.decodeStream(FaceActivity.this.getContentResolver().openInputStream(cropImgUri));
                                final String Photo = getRealPath(FaceActivity.this, cropImgUri);

                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("api_key","uF5IOwdlbNH9WtDyOmv-vOb3bkpmFXtN")
                                        .addFormDataPart("api_secret","Hg7pC4Qjhsg2OFsBEoONiIQx0AqY2LoH")
                                        .addFormDataPart("image_file", Photo, RequestBody.create(MediaType.parse("application/octet-stream"), new File(Photo)))
                                        .addFormDataPart("return_attributes","beauty,age,gender")
                                        .build();
                                Request request = new Request.Builder()
                                        .url("https://api-cn.faceplusplus.com/facepp/v3/detect")
                                        .method("POST", body)
                                        .addHeader("User-Agent", "apifox/1.0.0 (https://www.apifox.cn)")
                                        .build();
                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();
                                Log.d("1233face", "onResponse:123 " + responseData);

                                try {
                                    JSONObject jsonObject1 = new JSONObject(responseData);
                                    JSONArray jsonArray2 = jsonObject1.getJSONArray("faces");
                                    JSONObject jsonObject3 = jsonArray2.getJSONObject(0).getJSONObject("attributes");
                                    String gender = jsonObject3.getJSONObject("gender").getString("value");
                                    int agee = jsonObject3.getJSONObject("age").getInt("value");
                                    int beauty = (int)jsonObject3.getJSONObject("beauty").getDouble("male_score");
                                    Objects.requireNonNull(FaceActivity.this).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            Glide.with(FaceActivity.this).load(Photo)
//                                                    .into(pic);
                                            pic.setImageBitmap(headImage);
                                            Log.d("1233face",Photo);
                                            if(gender.equals("Female")) sex.setText("女");
                                            else sex.setText("男");
                                            age.setText(Integer.toString(agee));
                                            point.setText(Integer.toString(beauty)+"/100");
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                Objects.requireNonNull(FaceActivity.this).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("1233pp", "aaaa");
                                        Toast.makeText(FaceActivity.this, "图片过大,上传失败2", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(FaceActivity.this, "cropImgUri为空！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void cropRawPhoto(Uri uri) {
        File cropImage = new File(Environment.getExternalStorageDirectory(), "crop_image.jpg");
        String path = cropImage.getAbsolutePath();
        try {
            if (cropImage.exists()) {
                cropImage.delete();
            }
            cropImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cropImgUri = Uri.fromFile(cropImage);
        Log.d("1233p", cropImgUri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
//设置源地址uri
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
//设置目的地址uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgUri);
//设置图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    public static String getRealPath(Context context, Uri uri) {
        if (context == null || uri == null) {
            Log.d("1233p", "smdmy");
            return null;
        }
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Byfile(context, uri);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Api11To18(context, uri);
        }
        return getRealPathFromUri_AboveApi19(context, uri);
    }

    private static String getRealPathFromUri_Byfile(Context context, Uri uri) {
        Log.d("1233p", "old");
        String uri2Str = uri.toString();
        String filePath = uri2Str.substring(uri2Str.indexOf(":") + 3);
        return filePath;
    }

    @SuppressLint("NewApi")
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = null;
        Log.d("1233p", "nnn");
        wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Log.d("1233p", "n");
        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }
}