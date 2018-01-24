package cj.com.fileexplorer.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cj.com.fileexplorer.R;
import cj.com.filemanager.models.FileModel;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {
    private final OnItemClickListener mOnItemClickListener;
    private ArrayList<FileModel> mFiles;

    private static final int LIST_VIEW_HOLDER_TYPE = 0;
    private static final int CARD_VIEW_HOLDER_TYPE = 1;

    private boolean mShowGrid;

    public DirectoryAdapter(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        mFiles = new ArrayList<>();
    }

    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case CARD_VIEW_HOLDER_TYPE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_grid_layout, parent,
                        false);
                return new CardViewHolder(v);
            case LIST_VIEW_HOLDER_TYPE:
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.directory_item_layout, parent,
                        false);
                return new DirectoryViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(DirectoryViewHolder holder, final int position) {
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

        holder.setData(mFiles.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowGrid) {
            return CARD_VIEW_HOLDER_TYPE;
        } else {
            return LIST_VIEW_HOLDER_TYPE;
        }
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

    public void setShowGrid(boolean showGrid) {
        mShowGrid = showGrid;
    }

    public class DirectoryViewHolder extends RecyclerView.ViewHolder {
        public TextView mFileNameTextView;
        public TextView mFileSizeTextView;
        public ImageView mDirectoryItemThumbnailImageView;

        public DirectoryViewHolder(View itemView) {
            super(itemView);
            mFileNameTextView = itemView.findViewById(R.id.directoryItemTextView);
            mFileSizeTextView = itemView.findViewById(R.id.directoryItemSizeTextView);
            mDirectoryItemThumbnailImageView = itemView.findViewById(R.id
                    .directoryItemThumbnailImageView);
        }

        public void setData(FileModel fileModel) {
            mFileNameTextView.setText(fileModel.getFile().getName());



            if (fileModel.getFile().isDirectory()) {
                mDirectoryItemThumbnailImageView.setImageDrawable
                        (mDirectoryItemThumbnailImageView.getContext().getDrawable(R.drawable
                                .ic_folder_white_24dp));

                mFileSizeTextView.setVisibility(View.GONE);
            } else {
                mDirectoryItemThumbnailImageView.setImageDrawable
                        (mDirectoryItemThumbnailImageView.getContext().getDrawable(fileModel
                                .getFileIcon().getDrawableId()));

                String fileSize = Formatter.formatFileSize(mFileSizeTextView.getContext(),
                        fileModel.getFile().length());

                mFileSizeTextView.setText(fileSize);
                mFileSizeTextView.setVisibility(View.VISIBLE);
            }
        }

    }

    public class CardViewHolder extends DirectoryViewHolder {
        public ImageView mFileImageView;

        public CardViewHolder(View itemView) {
            super(itemView);
            mFileImageView = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void setData(FileModel fileModel) {
            mFileNameTextView.setText(fileModel.getFile().getName());

            if (fileModel.getFile().isDirectory()) {
                mDirectoryItemThumbnailImageView.setImageDrawable
                        (mDirectoryItemThumbnailImageView.getContext().getDrawable(R.drawable
                                .ic_folder_white_24dp));
                mFileImageView.setVisibility(View.GONE);
                mFileSizeTextView.setVisibility(View.GONE);
            } else {
                mDirectoryItemThumbnailImageView.setImageDrawable
                        (mDirectoryItemThumbnailImageView.getContext().getDrawable(fileModel
                                .getFileIcon().getDrawableId()));

                mFileImageView.setVisibility(View.VISIBLE);
                Uri uri = Uri.fromFile(fileModel.getFile());
                Picasso.with(mFileImageView.getContext())
                        .load(uri)
                        .placeholder(R.drawable.ic_insert_drive_file_black_24dp)
                        .fit()
                        .into(mFileImageView);

                String fileSize = Formatter.formatFileSize(mFileSizeTextView.getContext(),
                        fileModel.getFile().length());

                mFileSizeTextView.setText(fileSize);
                mFileSizeTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface OnItemClickListener {
        void onShortPress(FileModel fileModel);
        void onLongPress(FileModel fileModel);
    }
}
