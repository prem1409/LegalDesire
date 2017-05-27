package com.example.msp.legaldesire;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by MSP on 5/15/2017.
 */

public class Chat_Room_Adapter extends ArrayAdapter<String> {
    public static final String TAG = "chatroomadapter123";
    ArrayList<String> msg;
    ArrayList<String> email;
    ArrayList<String> msgtime;
    ArrayList<String> msgtype;
    ArrayList<Long> filesize;
    ArrayList<String> filetype;
    ArrayList<String> filepath;
    ArrayList<String> fileName;
    ArrayList<Uri> fileUri;
    Context context;
    LayoutInflater inflater;
    int sdk = Build.VERSION.SDK_INT;

    public Chat_Room_Adapter(Context context, ArrayList<String> msg, ArrayList<String> email, ArrayList<String> msgtime, ArrayList<Uri> fileUri, ArrayList<String> msgtype, ArrayList<Long> filesize
            , ArrayList<String> filetype, ArrayList<String> filepath, ArrayList<String> fileName) {
        super(context, R.layout.chat_room_adapter, msg);
        this.context = context;
        this.msg = msg;
        this.email = email;
        this.msgtime = msgtime;
        this.fileUri = fileUri;
        this.msgtype = msgtype;
        this.filesize = filesize;
        this.filetype = filetype;
        this.filepath = filepath;
        this.fileName = fileName;
    }

    public class ViewHolder {
        RelativeLayout relativeLayout, relativeLayout2;
        ImageView mImage;
        TextView mMsg;
        TextView mTime;
        Button mDownloadImage;
    }


    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_room_adapter, null);
        }
        SharedPreferences preferences = getContext().getSharedPreferences("store_name_and_email", Context.MODE_PRIVATE);
        final String user_email = preferences.getString("person_email", null);
        final ViewHolder holder = new ViewHolder();

        switch (msgtype.get(position)) {
            case "text": {
                convertView = inflater.inflate(R.layout.chat_room_adapter, null);
                // holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativelayout1);
                // holder.mImage = (ImageView) convertView.findViewById(R.id.img_display);
                holder.mMsg = (TextView) convertView.findViewById(R.id.message);
                holder.mTime = (TextView) convertView.findViewById(R.id.message_date);

                if (user_email.equals(email.get(position))) {
                    Log.d(TAG, "user_email:" + user_email + "  email:" + email.get(position));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mMsg.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.mTime.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                    if (sdk > Build.VERSION_CODES.KITKAT)
                        holder.mMsg.setBackground(getContext().getDrawable(R.drawable.bubble_right_green));
                    else {
                        holder.mMsg.setBackgroundResource(R.drawable.bubble_right_green);
                    }
                } else {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mMsg.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.mTime.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_START);
                    if (sdk > Build.VERSION_CODES.KITKAT)
                        holder.relativeLayout.setBackground(getContext().getDrawable(R.drawable.bubble_left_gray));
                    else
                        holder.mMsg.setBackgroundResource(R.drawable.bubble_left_gray);


                }
                Log.d(TAG, "display text executed:" + msg.get(position));
                holder.mTime.setText(msgtime.get(position));
                holder.mMsg.setText(msg.get(position));
                break;
                // holder.mImage.setVisibility(View.GONE);
                //   holder.mMsg.setVisibility(View.VISIBLE);
            }

            case "image": {
                convertView = inflater.inflate(R.layout.chat_room_adapter2, null);
                holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativelayout2);
                holder.mImage = (ImageView) convertView.findViewById(R.id.img_display);
                holder.mTime = (TextView) convertView.findViewById(R.id.message_date2);
                holder.mDownloadImage = (Button) convertView.findViewById(R.id.btn_download_image);
                holder.mImage.setVisibility(View.GONE);

                if (user_email.equals(email.get(position))) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mDownloadImage.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.mTime.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                    if (sdk > Build.VERSION_CODES.KITKAT)
                        holder.mDownloadImage.setBackground(getContext().getDrawable(R.drawable.bubble_right_green));
                    else {
                        holder.mDownloadImage.setBackgroundResource(R.drawable.bubble_right_green);
                    }
                } else {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mDownloadImage.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.mTime.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_START);
                    if (sdk > Build.VERSION_CODES.KITKAT)
                        holder.mDownloadImage.setBackground(getContext().getDrawable(R.drawable.bubble_left_gray));
                    else
                        holder.mDownloadImage.setBackgroundResource(R.drawable.bubble_left_gray);


                }
                holder.mDownloadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadImage(position, holder.mImage);
                        holder.mImage.setVisibility(View.VISIBLE);
                        holder.mDownloadImage.setVisibility(View.GONE);
                        updateUI(position, user_email, holder);
                    }
                });
                //   Picasso.with(getContext()).load(fileUri.get(position)).into(holder.mImage);
                long size = filesize.get(position);
                double size2 = size / 1024;
                if (size2 > 1000)
                    size2 = size2 / 1024;
                holder.mTime.setText(msgtime.get(position));
                break;
            }
            case "document": {
                convertView = inflater.inflate(R.layout.chat_room_adapter3, null);
                holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativelayout3);
                holder.relativeLayout2 = (RelativeLayout) convertView.findViewById(R.id.wrap_document_part);
                holder.mTime = (TextView) convertView.findViewById(R.id.message_date3);
                holder.mDownloadImage = (Button) convertView.findViewById(R.id.btn_download_document);
                holder.mMsg = (TextView) convertView.findViewById(R.id.text_display_document_metadata);


                if (user_email.equals(email.get(position))) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.relativeLayout2.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_END);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.mTime.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_END);
                    if (sdk > Build.VERSION_CODES.KITKAT)
                        holder.relativeLayout2.setBackground(getContext().getDrawable(R.drawable.bubble_right_green));
                    else {
                        holder.relativeLayout2.setBackgroundResource(R.drawable.bubble_right_green);
                    }
                } else {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.relativeLayout2.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_START);
                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.mTime.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_START);
                    if (sdk > Build.VERSION_CODES.KITKAT)
                        holder.relativeLayout2.setBackground(getContext().getDrawable(R.drawable.bubble_left_gray));
                    else
                        holder.relativeLayout2.setBackgroundResource(R.drawable.bubble_left_gray);
                }

                String str;
                if (filesize.get(position) > 1000) {
                    long sizeInKB = filesize.get(position) / 1024;
                    str = sizeInKB + " KB";
                    if (sizeInKB > 1000) {
                        long sizeinMB = sizeInKB / 1024;
                        str = sizeInKB + " MB";
                    }
                } else {
                    str = (filesize.get(position)/8) + "bytes";
                }
                holder.mMsg.setText("document size:" + str);
                holder.mTime.setText(msgtime.get(position));
                holder.mDownloadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadDocument(position);
                    }
                });
            }
        }
      /*  holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativelayout1);
        holder.mImage = (ImageView) convertView.findViewById(R.id.img_display);
        holder.mMsg = (TextView) convertView.findViewById(R.id.message);
        holder.mTime = (TextView) convertView.findViewById(R.id.message_date);


        if (user_email.equals(email.get(position))) {
            Log.d(TAG, "user_email:" + user_email + "  email:" + email.get(position));
            holder.relativeLayout.setGravity(Gravity.END);
            //  holder.mTime.setGravity(Gravity.END);
            //   holder.mMsg.setBackgroundResource(R.drawable.bubble_right_green);
            if (sdk > Build.VERSION_CODES.KITKAT)
                holder.relativeLayout.setBackground(getContext().getDrawable(R.drawable.bubble_right_green));
            else {
                holder.relativeLayout.setBackgroundResource(R.drawable.bubble_right_green);
            }
        } else {
            holder.relativeLayout.setGravity(Gravity.START);
            //  holder.mTime.setGravity(Gravity.START);
            if (sdk > Build.VERSION_CODES.KITKAT)
                holder.relativeLayout.setBackground(getContext().getDrawable(R.drawable.bubble_left_gray));
            else
                holder.relativeLayout.setBackgroundResource(R.drawable.bubble_left_gray);


        }

        if (msg.get(position).isEmpty()) {
            Log.d(TAG, "display picture executed");
            holder.mMsg.setVisibility(View.GONE);
            holder.mImage.setVisibility(View.VISIBLE);

            Picasso.with(getContext()).load(fileUri.get(position)).into(holder.mImage);
        } else {
            Log.d(TAG, "display text executed:" + msg.get(position));
            holder.mTime.setText(msgtime.get(position));
            holder.mMsg.setText(msg.get(position));
            holder.mImage.setVisibility(View.GONE);
            holder.mMsg.setVisibility(View.VISIBLE);
        }*/
        //  return super.getView(position, convertView, parent);
        return convertView;
    }

    public void setBackgroundForLayout(int position, String user_email, ViewHolder holder) {
        if (user_email.equals(email.get(position))) {
            Log.d(TAG, "user_email:" + user_email + "  email:" + email.get(position));
            holder.relativeLayout.setGravity(Gravity.START);
            if (sdk > Build.VERSION_CODES.KITKAT)
                holder.relativeLayout.setBackground(getContext().getDrawable(R.drawable.bubble_right_green));
            else {
                holder.relativeLayout.setBackgroundResource(R.drawable.bubble_right_green);
            }
        } else {
            holder.relativeLayout.setGravity(Gravity.END);
            if (sdk > Build.VERSION_CODES.KITKAT)
                holder.relativeLayout.setBackground(getContext().getDrawable(R.drawable.bubble_left_gray));
            else
                holder.relativeLayout.setBackgroundResource(R.drawable.bubble_left_gray);


        }
    }

    public void downloadImage(int position, ImageView imageView) {
        Picasso.with(getContext()).load(fileUri.get(position)).into(imageView);
    }

    public void downloadDocument(int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child(filepath.get(position));
        File rootPath = new File(Environment.getExternalStorageDirectory(), "LegalDesire");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
            
        }

        final File localFile = new File(rootPath, fileName.get(position));

        if (localFile.exists() && localFile.length() > 0) {
            Log.d(TAG, "localFile exists");
            openFile(localFile);
        } else {
            Log.d(TAG, "localFile doesn't exists");
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Downloading");
            progressDialog.show();
            pathReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            openFile(localFile);
                            Log.e("firebase ", ";local  file created  created " + localFile.toString());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                            Toast.makeText(getContext(), "Failed to Download Document" + exception.toString(), Toast.LENGTH_SHORT);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Downloading " + ((int) progress) + "%...");
                        }
                    });
        }


    }

    public void updateUI(int position, String user_email, ViewHolder holder) {
        if (user_email.equals(email.get(position))) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mImage.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.mTime.getLayoutParams();
            params2.addRule(RelativeLayout.ALIGN_PARENT_END);
            params2.addRule(RelativeLayout.BELOW, R.id.img_display);
            if (sdk > Build.VERSION_CODES.KITKAT)
                holder.mImage.setBackground(getContext().getDrawable(R.drawable.bubble_right_green));
            else {
                holder.mImage.setBackgroundResource(R.drawable.bubble_right_green);
            }
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mImage.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.mTime.getLayoutParams();
            params2.addRule(RelativeLayout.ALIGN_PARENT_START);
            params2.addRule(RelativeLayout.BELOW, R.id.img_display);
            if (sdk > Build.VERSION_CODES.KITKAT)
                holder.mImage.setBackground(getContext().getDrawable(R.drawable.bubble_left_gray));
            else
                holder.mImage.setBackgroundResource(R.drawable.bubble_left_gray);


        }
    }

    public void openFile(File f) {
        Log.d(TAG, "inside openfile");
        String fileName = f.getName();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (fileName.contains(".pdf")) {
            intent.setDataAndType(Uri.fromFile(f), "application/pdf");
        } else if (fileName.contains(".txt")) {
            intent.setDataAndType(Uri.fromFile(f), "text/plain");
        } else if (fileName.contains(".doc") || fileName.contains(".docx")) {
            intent.setDataAndType(Uri.fromFile(f), "application/msword");
        } else if (fileName.contains(".ppt") || fileName.contains(".pptx")) {
            intent.setDataAndType(Uri.fromFile(f), "application/vnd.ms-powerpoint");
        } else {
            intent.setDataAndType(Uri.fromFile(f), "*/*");
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);

    }
}
