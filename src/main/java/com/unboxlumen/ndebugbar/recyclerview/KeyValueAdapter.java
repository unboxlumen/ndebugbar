package com.unboxlumen.ndebugbar.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;

import com.unboxlumen.ndebugbar.model.KeyValueSummary;
import com.unboxlumen.ndebugbar.R.id;
import com.unboxlumen.ndebugbar.R.layout;

public class KeyValueAdapter extends BaseMultiItemQuickAdapter<KeyValueSummary, BaseViewHolder> {
    private OnItemClickListener listener;

    public KeyValueAdapter() {
        this.addItemType(0, layout.pd_item_title);
        this.addItemType(1, layout.pd_item_key_value);
        this.addItemType(2, layout.pd_item_exception);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    protected void convert(@NonNull final BaseViewHolder holder, final KeyValueSummary netSummary) {
        switch (holder.getItemViewType()) {
            case 0:
                holder.setText(id.item_title_id, netSummary.value);
                break;
            case 1:
                holder.setText(id.item_key, netSummary.key).setBackgroundColor(id.item_value, -1).setText(id.item_value, netSummary.value);
                holder.setVisible(id.item_value, true);
                holder.setGone(id.item_edit, true);
                holder.setVisible(id.item_arrow, netSummary.clickable);
                if (netSummary.clickable) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (KeyValueAdapter.this.listener != null) {
                                KeyValueAdapter.this.listener.onItemClick(holder.getAdapterPosition(), netSummary);
                            }

                        }
                    });
                }
                break;
            case 2:
                holder.setText(id.text, netSummary.value);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int var1, KeyValueSummary var2);
    }
}


