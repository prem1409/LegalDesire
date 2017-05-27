package com.example.msp.legaldesire;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.msp.legaldesire.Chat_Room_Adapter;
import com.example.msp.legaldesire.R;

import java.util.ArrayList;

/**
 * Created by MSP on 5/17/2017.
 */

public class Chat_List_Adapter extends ArrayAdapter<String> {
    public static String TAG = "chatlistadapter";
    Context context;
    LayoutInflater inflater;

    ArrayList<String> chat_list;
    ArrayList<Boolean> newMsg;

    public Chat_List_Adapter(Context context, ArrayList<String> chat_list, ArrayList<Boolean> newMsg) {
        super(context, R.layout.chat_list_adapter, chat_list);
        this.context = context;
        this.chat_list = chat_list;
        this.newMsg = newMsg;

    }

    public class ViewHolder {
        TextView mChat;
        TextView mNewMsg;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_list_adapter, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.mChat = (TextView) convertView.findViewById(R.id.chat_name);
        holder.mNewMsg = (TextView) convertView.findViewById(R.id.new_msg);
        holder.mChat.setText(chat_list.get(position));
        if (newMsg.get(position)) {
            Log.d(TAG,"view is gone");
            holder.mNewMsg.setVisibility(View.GONE);
        }
        else{
            Log.d(TAG,"view is not gone");
        }

        return convertView;
    }
}
