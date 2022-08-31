package com.example.tools.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.tools.tools.MyData;
import com.example.tools.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeActivity extends AppCompatActivity {
    private ImageView change_pic;
    private TextView change_name;
    private ImageView back;
    private ConstraintLayout change_password;
    private ConstraintLayout info;
    private TextView tv_sex;
    private TextView tv_info;
    private boolean sex;
    private RadioGroup rgSex;
    private boolean sex2;
    private EditText edit;
    private ConstraintLayout change_sex;
    private String my_info;
    private String my_name;
    private int my_sex;
    private String token;
    private String pic_url;
    private Bitmap bitmap;
    private static final int GET_BACKGROUND_FROM_CAPTURE_RESOULT = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final int TAKE_PHOTO = 3;
    private Uri photoUri;   //相机拍照返回图片路径
    private File outputImage;
    public static final String STR_IMAGE = "image/*";
    private Uri cropImgUri;
    private Dialog bottomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_information);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        change_sex = findViewById(R.id.ConstraintLayout3);
        info = findViewById(R.id.ConstraintLayout4);
        change_name = findViewById(R.id.textView17);
        back = findViewById(R.id.imageView4);
        change_pic = findViewById(R.id.imageView5);
        change_password = findViewById(R.id.ConstraintLayout5);
        tv_info = findViewById(R.id.textView20);
        tv_sex = findViewById(R.id.textView18);

        MyData myData = new MyData(ChangeActivity.this);
        my_info = myData.load_info();
        my_name = myData.load_name();
        pic_url = myData.load_pic_url();
        if (myData.load_sex().equals("男")) {
            my_sex = 1;
        } else {
            my_sex = 0;
        }
        token = myData.load_token();

        go_ui();
        go_pic();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog = new Dialog(ChangeActivity.this, R.style.Theme_Design_BottomSheetDialog);
                View contentView = LayoutInflater.from(ChangeActivity.this).inflate(R.layout.layout_takepic, null);
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
        change_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(ChangeActivity.this);
                final View view = factory.inflate(R.layout.layout_sex, null);
                rgSex = (RadioGroup) view.findViewById(R.id.rgSex);
                RadioButton rb_1 = (RadioButton) rgSex.findViewById(R.id.radio0);
                RadioButton rb_0 = (RadioButton) rgSex.findViewById(R.id.radio1);
                if(my_sex==1){
                    rb_1.setChecked(true);
                    rb_0.setChecked(false);
                }else{
                    rb_0.setChecked(true);
                    rb_1.setChecked(false);
                }
                new AlertDialog.Builder(ChangeActivity.this)
                        .setTitle("请选择新的性别吧~")//提示框标题
                        .setView(view)
                        .setPositiveButton("确定",//提示框的两个按钮
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (rgSex.getCheckedRadioButtonId() == R.id.radio0) {
                                            sex2 = true;
                                            my_sex = 1;
                                            change_my_info();
                                        } else if (rgSex.getCheckedRadioButtonId() == R.id.radio1) {
                                            sex2 = false;
                                            my_sex = 0;
                                            change_my_info();
                                        } else {
                                            return;
                                        }
                                    }
                                }).setNegativeButton("取消", null).create().show();
                change_my_info();
            }
        });
        change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(ChangeActivity.this);
                final View view = factory.inflate(R.layout.layout_name, null);
                edit = view.findViewById(R.id.name);
                edit.setText(my_name);
                new AlertDialog.Builder(ChangeActivity.this)
                        .setTitle("请输入新的昵称")
                        .setView(view)
                        .setPositiveButton("确定",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (!checkName(edit.getText().toString())) {
                                            Toast.makeText(ChangeActivity.this, "修改失败，昵称不合法！", Toast.LENGTH_SHORT).show();
                                        } else {
                                            my_name = edit.getText().toString();
                                            change_my_info();
                                        }
                                    }
                                }).setNegativeButton("取消", null).create().show();
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChangeActivity.this, ChangePasswordActivity.class);
                intent.putExtra("type",1);
                ChangeActivity.this.startActivity(intent);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(ChangeActivity.this);
                final View view = factory.inflate(R.layout.layout_info, null);
                edit = view.findViewById(R.id.name);
                edit.setText(my_info);
                new AlertDialog.Builder(ChangeActivity.this)
                        .setTitle("请输入新的签名")
                        .setView(view)
                        .setPositiveButton("确定",
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //事件
                                        if (!checkInfo(edit.getText().toString())) {
                                            Toast.makeText(ChangeActivity.this, "修改失败，签名不合法！", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            my_info = edit.getText().toString();
                                            change_my_info();
                                        }
                                    }
                                }).setNegativeButton("取消", null).create().show();
            }
        });
    }

    void change_my_info() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n        \"info\": \"" + my_info + "\",\n        \"nickname\": \"" + my_name + "\",\n        \"gender\": " + my_sex + "\n    }");
                Log.d("1233c h", my_sex+"");
                Request request = new Request.Builder()
                        .url("http://101.43.145.51:10002/itnews/api/self/info-refresh")
                        .method("POST", body)
                        .addHeader("Authorization", token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Log.d("1233c h", request.toString());
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("1233 hhh", response.toString());
                    ChangeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            go_ui();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void go_ui() {
        if (my_sex == 1) {
            tv_sex.setText("男");
        } else {
            tv_sex.setText("女");
        }
        change_name.setText("✎" + my_name);
        tv_info.setText(my_info);
    }

    void go_pic() {
        Glide.with(ChangeActivity.this).load(pic_url)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(change_pic);
    }

    private void selectCamera() {
        outputImage = new File(ChangeActivity.this.getExternalCacheDir(), "camera_photos.jpg");
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
                                final Bitmap headImage = BitmapFactory.decodeStream(ChangeActivity.this.getContentResolver().openInputStream(cropImgUri));

                                final String Photo = getRealPath(ChangeActivity.this, cropImgUri);
                                Log.d("1233p", "????");
                                MyData myData = new MyData(ChangeActivity.this);
                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();
                                MediaType mediaType = MediaType.parse("text/plain");
                                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("img", Photo, RequestBody.create(MediaType.parse("application/octet-stream"), new File(Photo)))
                                        .addFormDataPart("type", "1")
                                        .build();
                                Request request = new Request.Builder()
                                        .url("http://101.43.145.51:10002/itnews/api/img-upload")
                                        .method("POST", body)
                                        .addHeader("Authorization", myData.load_token())
                                        .build();
                                Response response = client.newCall(request).execute();

                                String responseData = response.body().string();
                                Log.d("1233g", "onResponse:123 " + responseData);
                                try {
                                    JSONObject jsonObject1 = new JSONObject(responseData);
                                    int code = jsonObject1.getInt("code");
                                    final String msg = jsonObject1.getString("msg");
                                    if (code != 1000) {
                                        Objects.requireNonNull(ChangeActivity.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d("1233pp", "sbsbsb");
                                                Toast.makeText(ChangeActivity.this, "图片过大,上传失败6", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                                        int img_id = jsonObject2.getInt("img_id");
                                        pic_url = jsonObject2.getString("img_url");
                                        OkHttpClient client2 = new OkHttpClient().newBuilder()
                                                .build();
                                        MediaType mediaType2 = MediaType.parse("application/json");
                                        RequestBody body2 = RequestBody.create(mediaType2, "{\n    \"img_id\": " + img_id + "\n}");
                                        Request request2 = new Request.Builder()
                                                .url("http://101.43.145.51:10002/itnews/api/self/avatar-upload")
                                                .method("POST", body2)
                                                .addHeader("Authorization", token)
                                                .addHeader("Content-Type", "application/json")
                                                .build();
                                        Response response2 = client2.newCall(request2).execute();

                                        Objects.requireNonNull(ChangeActivity.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Glide.with(ChangeActivity.this).load(pic_url)
                                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                                        .into(change_pic);
                                            }
                                        });
                                        Log.d("1233g", "onResponse:666 " + responseData);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                Objects.requireNonNull(ChangeActivity.this).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("1233pp", "aaaa");
                                        Toast.makeText(ChangeActivity.this, "图片过大,上传失败2", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Toast.makeText(ChangeActivity.this, "cropImgUri为空！", Toast.LENGTH_SHORT).show();
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

    void change_my_pic() {
        Glide.with(ChangeActivity.this).load(pic_url)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(change_pic);
    }

    public boolean checkName(String str) {
        String regexp = "^[\\u4e00-\\u9fa50-9a-zA-Z]{1,6}$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    public boolean checkInfo(String str) {
        String regexp = "^[\\u4e00-\\u9fa50-9a-zA-Z，。,.!]{1,20}$";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
