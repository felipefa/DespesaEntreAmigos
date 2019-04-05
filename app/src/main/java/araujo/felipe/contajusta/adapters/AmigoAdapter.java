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
import araujo.felipe.contajusta.models.Amigo;
import araujo.felipe.contajusta.utils.DatabaseHelper;

/**
 * Adapta uma lista de objetos do tipo Amigo para exibi-la numa RecyclerView.
 */
public class AmigoAdapter extends RecyclerView.Adapter<AmigoAdapter.MyViewHolder> {

    private ArrayList<Amigo> listaAmigos;
    private DatabaseHelper db;
    private long idDespesa;
    private String activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nome, valorPagoAmigo;

        MyViewHolder(View view) {
            super(view);
            nome = (TextView) view.findViewById(R.id.tv_nome);
            valorPagoAmigo = (TextView) view.findViewById(R.id.tv_gasto);
        }
    }

    /**
     * Construtor da classe AmigoAdapter.
     *
     * @param amigos    lista de objetos do tipo Amigo
     * @param db        DatabaseHelper usado na classe que instancia o AmigoAdapter
     * @param idDespesa id da despesa onde o adapter será usado
     * @param activity  activity onde o AmigoAdapter foi instanciado
     */
    public AmigoAdapter(ArrayList<Amigo> amigos, DatabaseHelper db, long idDespesa, String activity) {
        this.listaAmigos = amigos;
        this.db = db;
        this.idDespesa = idDespesa;
        this.activity = activity;
    }

    @Override
    public AmigoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.amigo_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AmigoAdapter.MyViewHolder holder, int position) {
        Amigo amigo = listaAmigos.get(position);

        // Atribui o nome do amigo ao TextView dentro da linha da RecyclerView
        holder.nome.setText(amigo.getNome());

        // Se estiver no ResumoDespesa atribui o valor a ser pago pelo amigo na despesa ao outro TextView
        if (activity.equals("ResumoDespesa")) {
            double valor = db.calcularValorPagoAmigoEmDespesa(amigo.getId(), idDespesa);

            // Cria um locale para exibir a moeda com base no idioma e país do usuário
            Locale locale = new Locale("pt", "BR");
            //Locale locale = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
            String valorPagoAmigoEmDespesa = NumberFormat.getCurrencyInstance(locale).format(valor);

            holder.valorPagoAmigo.setText(valorPagoAmigoEmDespesa);
        }
    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }
}
