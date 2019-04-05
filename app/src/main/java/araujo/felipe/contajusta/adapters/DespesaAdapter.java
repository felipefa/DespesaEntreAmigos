package araujo.felipe.contajusta.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import araujo.felipe.contajusta.R;
import araujo.felipe.contajusta.models.Despesa;

/**
 * Adapta uma lista de objetos do tipo Despesa para exibi-la numa RecyclerView.
 */
public class DespesaAdapter extends RecyclerView.Adapter<DespesaAdapter.MyViewHolder> {

    private ArrayList<Despesa> listaDespesas;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView descricao, dia;

        MyViewHolder(View view) {
            super(view);
            descricao = (TextView) view.findViewById(R.id.tv_descricao);
            dia = (TextView) view.findViewById(R.id.tv_dia);
        }
    }

    /**
     * Construtor da classe DespesaAdapter.
     *
     * @param despesas lista de objetos do tipo Despesa
     */
    public DespesaAdapter(ArrayList<Despesa> despesas) {
        this.listaDespesas = despesas;
    }

    @Override
    public DespesaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.despesa_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DespesaAdapter.MyViewHolder holder, int position) {
        Despesa despesa = listaDespesas.get(position);
        // Atribui os campos descrição e dia ao RecyclerView
        holder.descricao.setText(despesa.getDescricao());
        holder.dia.setText(despesa.getDia());
    }

    @Override
    public int getItemCount() {
        return listaDespesas.size();
    }
}
