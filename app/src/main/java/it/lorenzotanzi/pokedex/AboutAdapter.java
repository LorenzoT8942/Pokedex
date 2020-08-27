package it.lorenzotanzi.pokedex;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.Holder>{
    private List<String> mGenerals;
    private List<String> mEmails;

    AboutAdapter(List<String> Generals, List<String> Emails){
        mGenerals = Generals;
        mEmails = Emails;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardView cl = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_about, parent, false);

        return new Holder(cl);
    }


    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.tv_developer_general.setText(mGenerals.get(position));
        holder.tv_developer_mail.setText(mEmails.get(position));

        if(position == 0){
            holder.iv_image.setImageResource(R.drawable.ic_claudio);
            //holder.cl_item_about.setBackgroundColor(Color.LTGRAY);

        }
        if(position == 1){
            holder.iv_image.setImageResource(R.drawable.ic_ana);
            //holder.cl_item_about.setBackgroundColor(Color.GRAY);
        }
        if(position == 2){
            holder.iv_image.setImageResource(R.drawable.ic_dominique);
            //holder.cl_item_about.setBackgroundColor(Color.LTGRAY);
        }
        if(position == 3){
            holder.iv_image.setImageResource(R.drawable.ic_alison);
            //holder.cl_item_about.setBackgroundColor(Color.GRAY);
        }
            /*if(position == 4){
                holder.iv_image.setImageResource(R.drawable.ic_lorenzo);
                holder.cl_item_about.setBackgroundColor(Color.LTGRAY);
            }*/
    }

    @Override
    public int getItemCount() {
        return mGenerals.size();
    }

    static class Holder extends RecyclerView.ViewHolder{

        ImageView iv_image;
        TextView tv_developer_general;
        TextView tv_developer_mail;
        ConstraintLayout cl_item_about;

        Holder(CardView cl) {
            super(cl);
            iv_image = cl.findViewById(R.id.iv_image);
            tv_developer_general = cl.findViewById(R.id.tv_developer_general);
            tv_developer_mail = cl.findViewById(R.id.tv_developer_mail);
            cl_item_about = cl.findViewById(R.id.cl_item_about);
        }
    }

}
