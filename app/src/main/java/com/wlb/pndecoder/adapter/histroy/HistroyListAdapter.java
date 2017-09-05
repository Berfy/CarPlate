package com.wlb.pndecoder.adapter.histroy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wlb.pndecoder.R;
import com.wlb.pndecoder.model.histroy.HistroyNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Berfy on 2017/6/14.
 * 历史车辆
 */

public class HistroyListAdapter extends BaseAdapter {

    private Context mContext;
    private List<HistroyNumber> mData = new ArrayList<>();

    public HistroyListAdapter(Context context) {
        mContext = context;
    }

    public List<HistroyNumber> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (null == convertView) {
            convertView = View.inflate(mContext, R.layout.adapter_setting_histroy, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        HistroyNumber histroyNumber = mData.get(position);
        holder.tv_title.setText(histroyNumber.getNumber());
        holder.tv_status.setText(histroyNumber.getStatus() == 0 ? "未出单" : "已出单");
        return convertView;
    }

    class Holder {
        TextView tv_title, tv_status;

        Holder(View view) {
            tv_title = (TextView) view.findViewById(R.id.tv_number);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
        }
    }
}
