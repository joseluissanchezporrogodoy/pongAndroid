package com.apparte.pongfinal;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;


/**
 * Created by jlsanchez on 18/1/16.
 */
public class DialogImage {
    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_FILE=1;
    ///

    //
    public static void selectImage(Activity activity,final Context mContext) {
        final int REQUEST_CAMERA = 0, SELECT_FILE = 1;
        final Dialog dialog = new Dialog(mContext);
        //
        final String dir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/Folder/";
        File newdir = new File(dir);
        newdir.mkdirs();
        //
        dialog.setContentView(R.layout.custom_image_picker_dialog);
        dialog.setTitle("Image Picker");
        dialog.setCancelable(true);
        LinearLayout camara = (LinearLayout)dialog.findViewById(R.id.camara);
        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ((Activity) mContext).startActivityForResult(intent, REQUEST_CAMERA);
                dialog.dismiss();


            }
        });
        LinearLayout galeria = (LinearLayout)dialog.findViewById(R.id.galeria);
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    ( (Activity)mContext ).startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                dialog.dismiss();
            }
        });
        LinearLayout cancelar = (LinearLayout)dialog.findViewById(R.id.cancelarcamera);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



}
