package com.chan.android_lab9.Adapter;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chan.android_lab9.model.Detail;
import com.chan.android_lab9.model.Github;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 61915 on 17/10/21.
 */

public abstract class CommonAdapter extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<Map<String, Object>> mDatas;
    protected LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener = null;

    public CommonAdapter(Context context, int layout, List<Map<String, Object>> datas){
        mContext = context;
        mLayoutId = layout;
        mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        ViewHolder viewHolder = ViewHolder.get(mContext, parent, mLayoutId);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        convert(holder, mDatas.get(position));
        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }
    @Override
    public int getItemCount(){ return mDatas.size(); }

    public abstract void convert(ViewHolder holder, Map<String, Object> s);

    public interface OnItemClickListener
    {
        void onClick(int position);
        boolean onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener=onItemClickListener;
    }

    public void addData(Github github){
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("name", github.getLogin());
        temp.put("id", "id："+github.getId());
        temp.put("blog", "blog:"+github.getBlog());
        mDatas.add(temp);
        notifyDataSetChanged();
    }
    public void addData(Detail d){
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("name", d.getName());
        temp.put("language", clipString(d.getLanguage(), 20));
        temp.put("description", clipString(d.getDescription(), 20));
        temp.put("html_url", d.getHtml_url());
        mDatas.add(temp);
        notifyDataSetChanged();
    }

    public String getData(int pos, String tag){
        return mDatas.get(pos).get(tag).toString();
    }

    public void removeData(int pos){
        mDatas.remove(pos);
        notifyDataSetChanged();
    }
    public void clearData(){
        mDatas.removeAll(mDatas);
        notifyDataSetChanged();
    }
    private String clipString(String s, int limit){
        String res = s;
        if(s == null){
            res = "";
        }else if(s.length() > limit){
            res = s.substring(0, limit) + "...";
        }
        return res;
    }
}
