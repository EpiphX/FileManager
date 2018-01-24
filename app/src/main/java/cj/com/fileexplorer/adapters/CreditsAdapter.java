package cj.com.fileexplorer.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cj.com.fileexplorer.R;
import cj.com.fileexplorer.models.CreditModel;

/**
 * To show license and credits for third party libraries and resources.
 */
public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {

    private ArrayList<CreditModel> mCreditModels;

    public CreditsAdapter() {
        mCreditModels = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flat_icon_credit_cell, parent, false);
        return new CreditsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(holder.mTextView.getContext()
                .getDrawable(mCreditModels.get(position).getDrawableResourceId()), null, null, null);
        holder.mTextView.setCompoundDrawablePadding(holder.mTextView.getResources()
                .getDimensionPixelSize(R.dimen.activity_horizontal_margin));
    }

    @Override
    public int getItemCount() {
        return mCreditModels.size();
    }

    public void showIcons(ArrayList<CreditModel> iconModels) {
        mCreditModels = iconModels;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.creditTextView);
        }
    }
}
