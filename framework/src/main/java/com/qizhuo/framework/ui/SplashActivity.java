package com.qizhuo.framework.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import com.qizhuo.framework.R;


/**
 * Created by huzongyao on 2018/6/4.
 */

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        startWithPermission();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startWithPermission() {
/**
 *Ad Units should be in the type of IronSource.Ad_Unit.AdUnitName, example
 */
        Dexter.withContext(SplashActivity.this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        try {
                            //TODO here is the rom file download code
                            String path = Environment.getExternalStorageDirectory().toString()+"/"+Environment.DIRECTORY_DOWNLOADS;
                            Log.d("Files", "Path: " + path);
                            File directory = new File(path);
                            File[] files = directory.listFiles();
                            List<String> filesFolder=new ArrayList<>();
                            if  (null!=files) {
                                for (int i = 0; i < files.length; i++) {
                                    filesFolder.add(files[i].getName());
                                }
                            }
                            //   ScheduledExecutorService scheduledExecutorService=new ScheduledExecutorService();
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.setAction(getString(R.string.action_gallery_page));
                                    startActivity(intent);
                                    finish();
                                }
                            }, 3000L);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//gs://fcemulator-5b6f4.appspot.com/
//                        StorageReference storageReference;
//                        storageReference = FirebaseStorage.getInstance().getReference();
//                        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//                            @Override
//                            public void onSuccess(ListResult listResult) {
//                                if(listResult.getItems().size()>0){
//                                    for(StorageReference storageReference:listResult.getItems()){
//                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                            @Override
//                                            public void onSuccess(Uri uri) {
//
//                                                if(!filesFolder.contains(uri.getLastPathSegment())){
//                                                    Log.d(TAG, "onSuccess: "+uri.getLastPathSegment());
//                                                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                                                    DownloadManager.Request request = new DownloadManager.Request(uri);
//                                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
//                                                    downloadManager.enqueue(request);
//                                                }
////
//                                            }
//                                        });
//                                    }
//
//                                }else {
//                                    Intent intent = new Intent();
//                                    intent.setAction(getString(R.string.action_gallery_page));
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.d(TAG, "onFailure: "+e);
//                            }
//                        });
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }
}
