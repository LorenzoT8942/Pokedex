package it.lorenzotanzi.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>{

    private Context mContext;
    private ArrayList<Item> mGalleryList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public GalleryAdapter(Context context, ArrayList<Item> galleryList) {
        mContext = context;
        mGalleryList = galleryList;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, parent, false);
        return new GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        Item currentItem = mGalleryList.get(position);
        String url = currentItem.getUrl();
        String sprite = currentItem.getSprite();
        int id = currentItem.getId();
        String name = currentItem.getName();
        int color = currentItem.getColor();

        holder.mTextViewId.setText(String.valueOf(id));
        holder.mTextViewName.setText(name);
        holder.mTextViewSprite.setText(sprite);
        holder.mCard.setBackgroundColor(color);
        Picasso.get().load(url).fit().centerInside().into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mGalleryList.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewId;
        public TextView mTextViewName;
        public TextView mTextViewSprite;
        public CardView mCard;

        public GalleryViewHolder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_view);
            mTextViewId = itemView.findViewById(R.id.text_view_id);
            mTextViewName = itemView.findViewById(R.id.text_view_name);
            mTextViewSprite = itemView.findViewById(R.id.text_view_sprite);
            mCard=(CardView) itemView.findViewById(R.id.itemCard);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
