package net.lidia.iessochoa.kotta.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.Partitura;

/**
 * @author Lidia Martínez Torregrosa
 */
public class PartituraAdapter extends FirestoreRecyclerAdapter<Partitura, PartituraAdapter.PartituraViewHolder> {

    private final Context mContext;
    private OnItemClickDownloadListener listenerDownload;
    private OnItemClickElementoListener listener;
    private OnItemClickOptions listenerOptions;

    public PartituraAdapter(@NonNull FirestoreRecyclerOptions options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PartituraAdapter.PartituraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partitura,
                parent, false);
        return new PartituraViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull PartituraViewHolder holder, int position, @NonNull Partitura model) {
        holder.bind(model);
        //Only can delete the user that has uplouded the score
        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        if (model.getUser().equals(user.getEmail())) holder.ivOptions.setVisibility(View.VISIBLE);
        else holder.ivOptions.setVisibility(View.INVISIBLE);
    }

    /**
     * Listener al darle a una partitura
     */
    public void setOnCLickElementoListener(OnItemClickElementoListener listener) {
        this.listener = listener;
    }

    public void setListenerDownload(OnItemClickDownloadListener listenerDownload) {
        this.listenerDownload = listenerDownload;
    }

    public void setListenerOptions(OnItemClickOptions listenerOptions) {
        this.listenerOptions = listenerOptions;
    }

    public class PartituraViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategory, ivDownload, ivOptions;
        private TextView tvNameCv, tvInstrument, tvAuthor;
        private Partitura partitura;
        private CardView itemPartitura;

        public PartituraViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategoryCv);
            tvNameCv = itemView.findViewById(R.id.tvNameCv);
            tvInstrument = itemView.findViewById(R.id.tvInstrumentCv);
            tvAuthor = itemView.findViewById(R.id.tvAuthorCv);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            ivOptions = itemView.findViewById(R.id.ivOptions);
            itemPartitura = itemView.findViewById(R.id.cvItem);

            /**
             * Click on item of the score
             */
            itemPartitura.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION && listener != null)
                    listener.onItemClickElemento(getSnapshots().getSnapshot(position),position);
            });

            /**
             * Click on download icon
             */
            ivDownload.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listenerDownload != null) {
                    listenerDownload.onItemClickDownload(getSnapshots().getSnapshot(position),position);
                }
            });

            /**
             * Click on delete icon
             */
            ivOptions.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null)
                    listenerOptions.onItemClickOptions(getSnapshots().getSnapshot(position),position);
            });
        }
        public Partitura getPartitura() { return partitura; }

        /**
         * Bind data
         * @param partitura: The item of that score
         */
        public void bind(Partitura partitura) {
            String YourString = partitura.getName();
            if(YourString.length()>12){
                YourString = YourString.substring(0,11)+"...";
                tvNameCv.setText(YourString);
            }
            else { tvNameCv.setText(YourString); //Don't do any change
            }
            tvInstrument.setText(partitura.getInstrument());
            tvAuthor.setText(partitura.getAuthor());

            //Change icon and color depends of category
            switch (partitura.getCategory()) {
                case "Videojuegos":
                    ivCategory.setImageResource(R.drawable.ic_games);
                    itemPartitura.setCardBackgroundColor(mContext.getResources().getColor(R.color.blueItem));
                    break;
                case "Pop":
                    ivCategory.setImageResource(R.drawable.ic_pop);
                    itemPartitura.setCardBackgroundColor(mContext.getResources().getColor(R.color.orangeItem));
                    break;
                case "Clásica":
                    ivCategory.setImageResource(R.drawable.ic_clasic);
                    itemPartitura.setCardBackgroundColor(mContext.getResources().getColor(R.color.greenItem));
                    break;
                case "Películas":
                    ivCategory.setImageResource(R.drawable.ic_pelicula);
                    itemPartitura.setCardBackgroundColor(mContext.getResources().getColor(R.color.yellowItem));
                    break;
                case "Baladas":
                    ivCategory.setImageResource(R.drawable.ic_balada);
                    itemPartitura.setCardBackgroundColor(mContext.getResources().getColor(R.color.pinkItem));
                    break;
                case "Rock":
                    ivCategory.setImageResource(R.drawable.ic_rock);
                    itemPartitura.setCardBackgroundColor(mContext.getResources().getColor(R.color.greyItem));
                    break;
            }
        }
    }

    public interface OnItemClickElementoListener {
        void onItemClickElemento(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickElementoListener(AdapterView.OnItemClickListener onItemClickElementoListener) {
        this.listener = listener;
    }
    
    public interface OnItemClickDownloadListener {
        void onItemClickDownload(DocumentSnapshot snapshot, int position);
    }

    public interface  OnItemClickOptions {
        void onItemClickOptions(DocumentSnapshot snapshot, int position);
    }
}
