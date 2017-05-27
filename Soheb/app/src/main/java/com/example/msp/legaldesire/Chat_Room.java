package com.example.msp.legaldesire;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat_Room extends Fragment {
    public static final String TAG = "chatroom123";
    private int PICK_IMAGE_REQUEST = 1, PICK_DOCUMENT_REQUEST = 2;
    private Button btn_send_msg;
    Button btn_select_media;
    private EditText input_msg;
    private TextView chat_conversation;
    private ListView list_message;
    DatabaseReference root;
    private String getLawyerEmail;
    private String user_email, user_name, room_name;
    private String temp_key;
    private String chat_msg, chat_user_name, chat_date, chat_uri, chat_type;
    private String key;
    ArrayList<String> msg = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();
    ArrayList<String> msgTime = new ArrayList<>();
    ArrayList<String> msgType = new ArrayList<>();
    ArrayList<Long> fileSize = new ArrayList<>();
    ArrayList<String> fileType = new ArrayList<>();
    ArrayList<String> filepath = new ArrayList<>();
    ArrayList<String> fileName = new ArrayList<>();
    ArrayList<Uri> fileUri = new ArrayList<>();
    Chat_Room_Adapter chat_room_adapter;
    ArrayAdapter<String> arrayAdapter;
    Uri filePath, documentPath;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public Chat_Room() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getContext().getSharedPreferences("store_name_and_email", Context.MODE_PRIVATE);
        user_name = preferences.getString("person_name", null);
        user_email = preferences.getString("person_email", null);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            key = bundle.getString("key");
            Log.d(TAG, "KEY:" + key);
        }
        root = FirebaseDatabase.getInstance().getReference().child("Chat");
        root.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getLawyerEmail = dataSnapshot.child("Lawyer Email").getValue(String.class);
                Log.d("soheb123", "sdasda  " + getLawyerEmail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat__room, container, false);
        btn_send_msg = (Button) view.findViewById(R.id.btn_add_room2);
        btn_select_media = (Button) view.findViewById(R.id.btn_select_media);
        input_msg = (EditText) view.findViewById(R.id.edit_addroom2);
        //chat_conversation = (TextView) view.findViewById(R.id.textView5);
        list_message = (ListView) view.findViewById(R.id.message_list);
        chat_room_adapter = new Chat_Room_Adapter(getContext(), msg, email, msgTime, fileUri, msgType, fileSize, fileType, filepath, fileName);
        // list_message.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list_message.setAdapter(chat_room_adapter);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = input_msg.getText().toString().trim();
                if (str.equals("")) {
                    hideKeyboard();
                } else {
                    hideKeyboard();
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.child("Message").updateChildren(map);
                    DatabaseReference message_root = root.child(key).child("Message").child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("user", user_email);
                    if (user_email.equals(getLawyerEmail)) {
                        root.child(key).child("User Seen").setValue(false);
                    } else {
                        root.child(key).child("Lawyer Seen").setValue(false);
                    }
                    root.child(key).child("Last Active").setValue(DateFormat.getDateTimeInstance().format(new Date()));
                    map2.put("msg", input_msg.getText().toString());
                    map2.put("msg_time", java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                    map2.put("uri", " ");
                    map2.put("file name", "--");
                    map2.put("filetype", "text");
                    map2.put("file size", input_msg.getText().length());
                    map2.put("path", "-");
                    map2.put("type", "text");
                    input_msg.setText(" ");
                    message_root.updateChildren(map2);
                    list_message.setSelection(chat_room_adapter.getCount() - 1);
                }
            }
        });

        btn_select_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Inide btn_media click");
                PopupMenu popup = new PopupMenu(getContext(), btn_select_media);
                popup.getMenuInflater()
                        .inflate(R.menu.popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String str = (String) menuItem.getTitle();
                        if (str.equals("Image")) {
                            Toast.makeText(getContext(), "Image clicked", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            // Show only images, no videos or anything else
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            // Always show the chooser (if there are multiple options available)
                            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

                        } else if (str.equals("Document")) {
                            Toast.makeText(getContext(), "Doc clicked", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            // Show only images, no videos or anything else
                            intent.setType("application/pdf|application/msword | application/vnd.ms-powerpoint | application/vnd.ms-excel | text/*");
                         //   intent.setType("application/pdf|application/msword|application/vnd.ms-powerpoint");
                            String[] mimetypes = {"application/pdf|application/msword|application/vnd.ms-powerpoint| application/vnd.ms-excel|text/*"};
                        //    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            // Always show the chooser (if there are multiple options available)
                            startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_DOCUMENT_REQUEST);

                        }
                        return true;
                    }
                });
                popup.show();
             /*   filePath = null;
                Log.d(TAG, "Media select button clicked");
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
            }
        });

        root.child(key).child("Message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    private void append_chat_conversation(DataSnapshot datasnapshot) {
        Iterator i = datasnapshot.getChildren().iterator();
        while (i.hasNext()) {
            String file_name = (String) ((DataSnapshot) i.next()).getValue();
            long size = (long) ((DataSnapshot) i.next()).getValue();
            String type = (String) ((DataSnapshot) i.next()).getValue();
            chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            chat_date = (String) ((DataSnapshot) i.next()).getValue();
            String path = (String) ((DataSnapshot) i.next()).getValue();
            chat_type = (String) ((DataSnapshot) i.next()).getValue();
            chat_uri = (String) ((DataSnapshot) i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot) i.next()).getValue();

            Log.d(TAG, "filename:" + file_name + "user name:" + chat_user_name + "," + "Message:" + chat_msg + "Date:" + chat_date + "uri:" + chat_uri + " chat type:" + chat_type + "size:" + size + "type:" + type + " path:" + path);

            String str = chat_msg;
            String[] splited = str.split(" ");
            String str2 = "";
            int charsInLine = 20;
            int space = charsInLine;

            // Log.d(TAG, str);

            for (int x = 0; x < splited.length; x++) {
                if (splited[x].length() < space) {
                    str2 = str2 + " " + splited[x];
                    space = space - splited[x].length();
                } else {
                    str2 = str2 + "\n" + splited[x];
                    space = charsInLine;
                }

            }
            fileName.add(file_name);
            msg.add(str2);
            email.add(chat_user_name);
            msgTime.add(chat_date);
            fileUri.add(Uri.parse(chat_uri));
            chat_room_adapter.notifyDataSetChanged();
            msgType.add(chat_type);
            fileSize.add(size);
            fileType.add(type);
            filepath.add(path);
            list_message.setSelection(chat_room_adapter.getCount() - 1);
            //   chat_conversation.append(chat_user_name + ":" + chat_msg + "\n");

        }
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void uploadImage() {
        if (filePath != null) {
            final String fileName = getFileName(filePath);
            Log.d(TAG, "Inside uploadFile");
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference imageRef = storage.getReference();
            final String path = "image/" + UUID.randomUUID().toString() + "/" + filePath.getLastPathSegment();
            StorageReference img = imageRef.child(path);
            img.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            StorageMetadata metadata = taskSnapshot.getMetadata();
                            String name = metadata.getName();
                            long size = metadata.getSizeBytes();

                            String type = metadata.getContentType();
                            Log.d(TAG, "name:" + name + " size:" + size + " type:" + type);
                            Log.d(TAG, "download URL=" + downloadUrl);
                            temp_key = root.push().getKey();
                            Log.d(TAG, "key:" + temp_key);
                            DatabaseReference message_root = root.child(key).child("Message").child(temp_key);
                            Map<String, Object> map2 = new HashMap<String, Object>();
                            map2.put("msg", " ");
                            map2.put("path", path);
                            map2.put("user", user_email);
                            map2.put("uri", downloadUrl.toString());
                            map2.put("type", "image");
                            map2.put("filetype", type);
                            map2.put("file size", size);
                            map2.put("file name", fileName);
                            map2.put("msg_time", java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                            message_root.updateChildren(map2);
                            Log.d(TAG, "This isnt executed");
                            Toast.makeText(getContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(getContext(), "File Not Found", Toast.LENGTH_SHORT).show();
        }

    }


    public void uploadDocument() {
        if (documentPath != null) {
            File f = new File("" + documentPath);
            final String fileName = f.getName();
            Log.d(TAG, "FILE NAME:" + fileName);
            Log.d(TAG, "Inside uploadDOcument");
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference documentRef = storage.getReference();
            final String path = "document/" + UUID.randomUUID().toString() + "/" + documentPath.getLastPathSegment();
            StorageReference document = documentRef.child(path);
            document.putFile(documentPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            StorageMetadata metadata = taskSnapshot.getMetadata();
                            String name = metadata.getName();
                            long size = metadata.getSizeBytes();

                            String type = metadata.getContentType();
                            Log.d(TAG, "name:" + name + " size:" + size + " type:" + type);
                            Log.d(TAG, "download URL=" + downloadUrl);
                            temp_key = root.push().getKey();
                            Log.d(TAG, "key:" + temp_key);
                            DatabaseReference message_root = root.child(key).child("Message").child(temp_key);
                            Map<String, Object> map2 = new HashMap<String, Object>();
                            map2.put("path", path);
                            map2.put("msg", " ");
                            map2.put("user", user_email);
                            map2.put("uri", downloadUrl.toString());
                            map2.put("type", "document");
                            map2.put("filetype", type);
                            map2.put("file size", size);
                            map2.put("file name", fileName);
                            map2.put("msg_time", java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                            message_root.updateChildren(map2);
                            Log.d(TAG, "This isnt executed");
                            Toast.makeText(getContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(getContext(), "File Not Found", Toast.LENGTH_SHORT).show();
        }
    }


 /*   public void uploadDocument() {
        if (documentPath != null) {
            Log.d(TAG, "Inside uploadDOcument");
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference documentRef = storage.getReference();
            final String path = "document/" + UUID.randomUUID().toString() + "/" + documentPath.getLastPathSegment();
            StorageReference document = documentRef.child(path);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = document.putBytes(data);
            uploadTask.addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    StorageMetadata metadata = taskSnapshot.getMetadata();
                    String name = metadata.getName();
                    long size = metadata.getSizeBytes();

                    String type = metadata.getContentType();
                    Log.d(TAG, "name:" + name + " size:" + size + " type:" + type);
                    Log.d(TAG, "download URL=" + downloadUrl);
                    temp_key = root.push().getKey();
                    Log.d(TAG, "key:" + temp_key);
                    DatabaseReference message_root = root.child(key).child("Message").child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("path", path);
                    map2.put("msg", " ");
                    map2.put("user", user_email);
                    map2.put("uri", downloadUrl.toString());
                    map2.put("type", "document");
                    map2.put("filetype", type);
                    map2.put("file size", size);
                    map2.put("msg_time", java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                    message_root.updateChildren(map2);
                    Log.d(TAG, "This isnt executed");
                    Toast.makeText(getContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            Log.d(TAG, "URI IMAGE:" + filePath);
            uploadImage();

           /* try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        } else if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            documentPath = data.getData();
            Log.d(TAG, "URI DOCUMENT:" + documentPath);
            uploadDocument();

        }

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
