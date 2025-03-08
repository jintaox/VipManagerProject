package com.jintao.vipmanager.myview;

import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jintao.vipmanager.R;

import java.util.List;

/**
 * Author: zhanghui
 * CreateDate: 2024/5/29 18:00
 * Description:
 */
public class MySpinnerAdapter extends RecyclerView.Adapter<MySpinnerAdapter.MyViewHolder> {

    private List<String> items;
    private int selectedIndex = -1;
    private int lastIndex = -1;
    private OnSpinnerClickListener listener;
    private boolean isShowSelectImage = true;
    private int mSelectColor,mNormalColor;
    private float mTextSize;
    private int paddingLeft,paddingTop,paddingRight,paddingBottom;

    public MySpinnerAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String content = items.get(position);
        holder.llLayout.setPadding(paddingLeft,paddingTop,paddingRight,paddingBottom);
        holder.tvContent.setText(content);
        holder.tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,mTextSize);
        if (position == selectedIndex) {
            holder.tvContent.setTextColor(mSelectColor);
            if (isShowSelectImage) {
                holder.ivStatus.setColorFilter(mSelectColor, PorterDuff.Mode.SRC_IN);
                holder.ivStatus.setVisibility(View.VISIBLE);
            }else {
                holder.ivStatus.setVisibility(View.INVISIBLE);
            }
        }else {
            holder.tvContent.setTextColor(mNormalColor);
            holder.ivStatus.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(v-> {
            listener.OnItemClick(position);
        });
    }

    public List<String> getItems() {
        return items;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
    public int getLastIndex() {
        return lastIndex;
    }

    public void setSelectedIndex(int index) {
        this.lastIndex = selectedIndex;
        this.selectedIndex = index;
        notifyDataSetChanged();
    }

    public MySpinnerAdapter setShowSelectImage(boolean isShowSelectImage) {
        this.isShowSelectImage = isShowSelectImage;
        return this;
    }

    public void setOnSpinnerClickListener(OnSpinnerClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        if (items!=null && items.size()!=0) {
            return items.size();
        }else {
            return 0;
        }
    }

    public MySpinnerAdapter setTextColor(int itemSelectColor, int itemNormalColor) {
        this.mSelectColor = itemSelectColor;
        this.mNormalColor = itemNormalColor;
        return this;
    }

    public MySpinnerAdapter setTextSize(float itemTextSize) {
        this.mTextSize = itemTextSize;
        return this;
    }

    public MySpinnerAdapter setPadding(int itemPaddingLeft, int itemPaddingTop, int itemPaddingRight, int itemPaddingBottom) {
        this.paddingLeft = itemPaddingLeft;
        this.paddingTop = itemPaddingTop;
        this.paddingRight = itemPaddingRight;
        this.paddingBottom = itemPaddingBottom;
        return this;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llLayout;
        TextView tvContent;
        ImageView ivStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            llLayout = itemView.findViewById(R.id.spinner_ll_layout);
            tvContent = itemView.findViewById(R.id.spinner_tv_content);
            ivStatus = itemView.findViewById(R.id.spinner_iv_status);
        }
    }

    public interface OnSpinnerClickListener {
        void OnItemClick(int position);
    }
}
