package net.lidia.iessochoa.kotta.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.lidia.iessochoa.kotta.R;
import net.lidia.iessochoa.kotta.model.Partitura;

import java.util.List;

public class PartituraAdapter extends RecyclerView.Adapter<PartituraAdapter.PartituraViewHolder> {
    private List<Partitura> partituraList;
    private OnItemClickElementoListener listener;

    @NonNull
    @Override
    public PartituraAdapter.PartituraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partitura,
                parent, false);
        return new PartituraViewHolder(itemView);
    }

    /**
     * When the adapter is going to show a new item, it calls this method and indicates the position
     * in the list of the element to show.
     * @param holder: Where we show the data
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull PartituraViewHolder holder, int position) {
        if (partituraList != null) {
            final Partitura partitura = partituraList.get(position);
            holder.tvNameCv.setText(partitura.getNombre());

            //We assign the listener
            if (listener!=null)
                holder.itemView.setOnClickListener(v -> listener.onItemClickElemento(partitura));
        }
    }

    /**
     * Count the number of music sheet
     * @return: number of partitura
     */
    @Override
    public int getItemCount() {
        if (partituraList != null)
            return partituraList.size();
        else return 0;
    }

    /**
     * When database is modify, update the recycleview
     */
    public void setListaPartituras(List<Partitura> partituras){
        partituraList = partituras;
        notifyDataSetChanged();
    }

    /**
     * Listener al darle a una partitura
     */
    public void setOnCLickElementoListener(OnItemClickElementoListener listener) {
        this.listener = listener;
    }

    public class PartituraViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategory;
        private TextView tvNameCv;
        private TextView tvInstrument;
        private ImageView ivDownload;
        private Partitura partitura;
        private CardView itemPartitura;

        public PartituraViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategoryCv);
            tvNameCv = itemView.findViewById(R.id.tvNameCv);
            tvInstrument = itemView.findViewById(R.id.tvInstrumentCv);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            itemPartitura = itemView.findViewById(R.id.cvItem);

            itemPartitura.setOnClickListener(v -> {
                if (listener != null)
                    listener.onItemClickElemento(partituraList.get(PartituraViewHolder.this.getAdapterPosition()));
            });
        }
        public Partitura getPartitura() { return partitura; }
    }

    public interface OnItemClickElementoListener {
        void onItemClickElemento(Partitura partitura);
    }
}
