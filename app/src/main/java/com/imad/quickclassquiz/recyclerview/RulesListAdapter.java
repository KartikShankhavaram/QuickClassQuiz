package com.imad.quickclassquiz.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imad.quickclassquiz.R;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RulesListAdapter extends RecyclerView.Adapter<RulesListAdapter.RulesListViewholder> {

    private LayoutInflater inflater;
    private String[] rules;

    public RulesListAdapter(Context context, String[] rules) {
        inflater = LayoutInflater.from(context);
        this.rules = rules;
    }

    @NonNull
    @Override
    public RulesListViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.rule_card_layout, parent, false);
        return new RulesListViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RulesListViewholder holder, int position) {
        TextView ruleTextView = holder.getRuleTextView();
        TextView ruleIndexTextView = holder.getRuleIndexTextView();

        ruleIndexTextView.setText(String.format(Locale.ENGLISH, "%d", position + 1));
        ruleTextView.setText(rules[position]);
    }

    @Override
    public int getItemCount() {
        return rules.length;
    }

    class RulesListViewholder extends RecyclerView.ViewHolder {

        TextView ruleTextView;
        TextView ruleIndexTextView;

        RulesListViewholder(@NonNull View itemView) {
            super(itemView);
            ruleIndexTextView = itemView.findViewById(R.id.ruleIndexView);
            ruleTextView = itemView.findViewById(R.id.ruleView);
        }

        TextView getRuleTextView() {
            return ruleTextView;
        }

        TextView getRuleIndexTextView() {
            return ruleIndexTextView;
        }
    }
}
