package araujo.felipe.contajusta.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import araujo.felipe.contajusta.R;
import araujo.felipe.contajusta.models.Item;

/**
 * Adapta uma lista de objetos do tipo Item para exibi-la numa RecyclerView.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private ArrayList<Item> listaItens;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView descricao, preco, quantidade;

        MyViewHolder(View view) {
            super(view);
            descricao = (TextView) view.findViewById(R.id.tv_descricao);
            preco = (TextView) view.findViewById(R.id.tv_preco);
            quantidade = (TextView) view.findViewById(R.id.tv_quantidade);
        }
    }

    /**
     * Construtor da classe ItemAdapter.
     *
     * @param itens lista de objetos do tipo Item
     */
    public ItemAdapter(ArrayList<Item> itens) {
        this.listaItens = itens;
    }

    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.MyViewHolder holder, int position) {
        Item item = listaItens.get(position);

        // Cria um locale para exibir a moeda com base no idioma e país do usuário
        Locale locale = new Locale("pt","BR");
//        Locale locale = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        String preco = NumberFormat.getCurrencyInstance(locale).format(item.getPreco());
        String quantidade = "Quantidade: "+String.valueOf(item.getQuantidade());

        holder.descricao.setText(item.getDescricao());
        holder.preco.setText(preco);
        holder.quantidade.setText(quantidade);
    }

    @Override
    public int getItemCount() {
        return listaItens.size();
    }
}
