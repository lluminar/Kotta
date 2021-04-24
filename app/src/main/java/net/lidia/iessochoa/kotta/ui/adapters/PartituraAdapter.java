package net.lidia.iessochoa.kotta.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.Partitura;

import java.util.ArrayList;

public class PartituraAdapter extends FirestoreRecyclerAdapter<Partitura, PartituraAdapter.PartituraViewHolder> {
    private OnItemClickElementoListener listener;

    public PartituraAdapter(@NonNull FirestoreRecyclerOptions options) { super(options); }

    @NonNull
    @Override
    public PartituraAdapter.PartituraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partitura,
                parent, false);
        return new PartituraViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull PartituraViewHolder holder, int position, @NonNull Partitura model) {
        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        holder.bind(model);
        //We assign the listener
        if (listener!=null)
            holder.itemView.setOnClickListener(v -> listener.onItemClickElemento(model));

    }

    /**
     * Listener al darle a una partitura
     */
    public void setOnCLickElementoListener(OnItemClickElementoListener listener) {
        this.listener = listener;
    }

    public class PartituraViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategory, ivDownload;
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
            itemPartitura = itemView.findViewById(R.id.cvItem);

            /*itemPartitura.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClickElemento(partituraList.get(PartituraViewHolder.this.getAdapterPosition()));
            });*/
        }
        public Partitura getPartitura() { return partitura; }

        public void bind(Partitura partitura) {
//          Glide.with(itemView.getContext()).load(partitura.getPdf()).into(image);
            tvNameCv.setText(partitura.getName());
            tvInstrument.setText(partitura.getInstrument());
            tvAuthor.setText(partitura.getAuthor());
        }
    }

    public interface OnItemClickElementoListener {
        void onItemClickElemento(Partitura partitura);
    }
}
