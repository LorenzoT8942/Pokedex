package it.lorenzotanzi.pokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>{

    private Context mContext;
    private ArrayList<Item> mGalleryList;
    private OnItemClickListener mListener;
    // interfaccia on click listener implementata sotto dall'adapter stesso
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    // il metodo onItemClick verrà sovrascritto nella GalleryActivity
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
        // setta testo dell'elemento della gallery
        holder.mTextViewSprite.setText(sprite);
        // per disporre l'immagine viene usata la libreria Picasso da url a bitmap
        Picasso.get().load(url).fit().centerInside().into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mGalleryList.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewSprite;

        public GalleryViewHolder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_view);
            mTextViewSprite = itemView.findViewById(R.id.text_view_sprite);
            // cliccando su un elemento della galleria, parte il metodo onItemClicked
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
