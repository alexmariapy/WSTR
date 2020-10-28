package com.writingstar.autotypingandtextexpansion.RecyclerClass;

import android.content.Context;
import android.graphics.Color;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.writingstar.autotypingandtextexpansion.ClassActView.PhraseListActivity;
import com.writingstar.autotypingandtextexpansion.Model.TxpGetSet;
import com.writingstar.autotypingandtextexpansion.R;

import java.util.Collections;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private List<TxpGetSet> mPhraseList;
    OnItemClickListener mItemClickListener;
    private static final int TYPE_ITEM = 0;
    private final LayoutInflater mInflater;
    private final OnStartDragListener mDragStartListener;
    private Context mContext;

    public ItemAdapter(Context context, List<TxpGetSet> list, OnStartDragListener dragListner) {
        this.mPhraseList = list;
        this.mInflater = LayoutInflater.from(context);
        mDragStartListener = dragListner;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mInflater.inflate(R.layout.phrase_list_item, viewGroup, false);
        return new VHItem(v);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {

        if (viewHolder instanceof VHItem) {
            final VHItem holder = (VHItem) viewHolder;
            ((VHItem) viewHolder).title.setText(mPhraseList.get(i).getPhrase_detail());

            ((VHItem) viewHolder).image_menu.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });

            ((VHItem) viewHolder).remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhraseList.remove(i);
                    notifyDataSetChanged();
                    if (mPhraseList.size() == 0) {
                        ((PhraseListActivity) mContext).hideRecycler();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPhraseList.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
        public EditText title;
        private ImageView image_menu, remove;

        public VHItem(View itemView) {
            super(itemView);
            title = (EditText) itemView.findViewById(R.id.txt_phrase);
            image_menu = (ImageView) itemView.findViewById(R.id.image_menu);
            remove = (ImageView) itemView.findViewById(R.id.remove);
            itemView.setOnClickListener(this);

            title.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TxpGetSet obj = mPhraseList.get(getAdapterPosition());
                    obj.setPhrase_detail(s.toString());
                    mPhraseList.set(getAdapterPosition(), obj);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }

            });

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        mPhraseList.remove(position);
        notifyItemRemoved(position);
    }

    public List<TxpGetSet> getPhraseList() {
        return mPhraseList;
    }

    public void refreshAdapter(List<TxpGetSet> lst) {
        mPhraseList = lst;
        notifyDataSetChanged();
        if (mPhraseList.size() > 0) {
            ((PhraseListActivity) mContext).showRecycler();
        }

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.v("", "Log position" + fromPosition + " " + toPosition);
        if (fromPosition < mPhraseList.size() && toPosition < mPhraseList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mPhraseList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mPhraseList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }

    public void updateList(List<TxpGetSet> list) {
        mPhraseList = list;
        notifyDataSetChanged();
    }
}