package com.xiaoi.app.testchatvoice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoi.app.testchatvoice.R;
import com.xiaoi.app.testchatvoice.bean.VoiceImfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther: Crazy.Mo
 * Date: 2016/9/30
 * Time:14:23
 * Des:
 */
public class VoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<VoiceImfo> voiceList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;

    public VoiceAdapter(Context context) {
        mContext = context;
        this.inflater = LayoutInflater.from(context);
    }


    public void setList(List<VoiceImfo> list) {
        voiceList.clear();
        if (list != null) {
            voiceList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public VoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VoiceViewHolder viewHolder = null;
        if (viewType == 0) {
            viewHolder = new VoiceViewHolder(inflater.inflate(R.layout.adapter_voice_content, parent, false));
        } else if (viewType == 1) {
            viewHolder = new VoiceViewHolder(inflater.inflate(R.layout.adapter_voice_content_full, parent, false));
        }
        viewHolder.setVoiceAdapter(this);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (voiceList.get(position).getIsLast() == 0) {
            //问题
            if (isNull(voiceList.get(position).getRequest())) {
                ((VoiceViewHolder) holder).tvRequest.setVisibility(View.GONE);
            } else {
                ((VoiceViewHolder) holder).tvRequest.setVisibility(View.VISIBLE);
            }
            ((VoiceViewHolder) holder).tvRequest.setText(voiceList.get(position).getRequest());//问题
            //答案
            if (isNull(voiceList.get(position).getAnswer()) && !isNull(voiceList.get(position).getRequest())) {
                ((VoiceViewHolder) holder).tvAnswer.setVisibility(View.GONE);
            } else {
                ((VoiceViewHolder) holder).tvAnswer.setVisibility(View.VISIBLE);
            }
            ((VoiceViewHolder) holder).tvAnswer.setText(voiceList.get(position).getAnswer());//答案

            if ("您可以这样说".equals(voiceList.get(position).getAnswer())) {
                ((VoiceViewHolder) holder).tvAnswer.setTextSize(22);
            } else {
                ((VoiceViewHolder) holder).tvAnswer.setTextSize(16);
            }
        } else if (voiceList.get(position).getIsLast() == 1) {
            ((VoiceViewHolder) holder).tvRequestLast.setText(voiceList.get(position).getRequest());
            ((VoiceViewHolder) holder).tvAnswerLast.setText(voiceList.get(position).getAnswer());
            if ("您可以这样说".equals(voiceList.get(position).getAnswer())) {
                ((VoiceViewHolder) holder).tvAnswerLast.setTextSize(22);
            } else {
                ((VoiceViewHolder) holder).tvAnswerLast.setTextSize(16);
            }
        }
    }
    /**
     * 判断字符串是否为空
     *
     * @param in
     * @return
     */
    public static boolean isNull(String in) {
        return in == null || "".equals(in.trim());
    }


    @Override
    public int getItemCount() {
        return voiceList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return voiceList.get(position).getIsLast();
    }

    class VoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnswer;
        TextView tvRequest;
        private WeakReference<VoiceAdapter> ref;
        private VoiceAdapter voiceAdapter;
        TextView tvRequestLast;
        TextView tvAnswerLast;

        private void setVoiceAdapter(VoiceAdapter adapter) {
            if (adapter != null) {
                ref = new WeakReference<VoiceAdapter>(adapter);
            }
            voiceAdapter = ref.get();
        }

        VoiceViewHolder(View view) {
            super(view);
//            ButterKnife.bind(this, view);
            tvRequest = (TextView) view.findViewById(R.id.tv_request);
            tvAnswer = (TextView) view.findViewById(R.id.tv_answer);

            tvRequestLast = (TextView) view.findViewById(R.id.tv_request_last);
            tvAnswerLast = (TextView) view.findViewById(R.id.tv_answer_last);
        }
    }
}
