package cj.com.fileexplorer.adapters;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import cj.com.fileexplorer.R;
import cj.com.filemanager.models.FileModel;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {
    private final OnItemClickListener mOnItemClickListener;
    private ArrayList<FileModel> mFiles;

    public DirectoryAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mFiles = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_grid_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onShortPress(mFiles.get(position));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onLongPress(mFiles.get(position));
                return true;
            }
        });

        holder.mFileNameTextView.setText(mFiles.get(position).getFile().getName());

        if (mFiles.get(position).getFile().isDirectory()) {
            holder.mFileNameTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable
                    .ic_folder_black_24dp, 0,0,0);

            holder.mFileImageView.setImageDrawable(ContextCompat.getDrawable(holder
                    .mFileImageView.getContext(),
                    R.drawable.ic_insert_drive_file_black_24dp));
        } else {
            holder.mFileNameTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(mFiles.get
                            (position).getFileIcon().getDrawableId(),
                    0,0,
                    0);

            Uri uri = Uri.fromFile(mFiles.get(position).getFile());
            Picasso.with(holder.mFileImageView.getContext()).load(uri)
                    .placeholder(R.drawable.ic_insert_drive_file_black_24dp)
                    .fit()
                    .into(holder.mFileImageView);


        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public void setFiles(ArrayList<FileModel> files) {
        if (files != null) {
            mFiles.clear();
            mFiles.addAll(files);
        } else {
            mFiles = files;
        }
        notifyDataSetChanged();
    }

    public void clearFiles() {
        mFiles.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mFileNameTextView;

        // TODO: Grid like views will need to be moved into their own view holder or adapter.
        private ImageView mFileImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mFileImageView = itemView.findViewById(R.id.imageView);
            mFileNameTextView = itemView.findViewById(R.id.directoryItemTextView);
        }
    }

    public interface OnItemClickListener {
        void onShortPress(FileModel fileModel);
        void onLongPress(FileModel fileModel);
    }
}
