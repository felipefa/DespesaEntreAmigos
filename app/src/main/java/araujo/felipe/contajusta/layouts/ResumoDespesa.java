package araujo.felipe.contajusta.layouts;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import araujo.felipe.contajusta.R;
import araujo.felipe.contajusta.adapters.AmigoAdapter;
import araujo.felipe.contajusta.adapters.ItemAdapter;
import araujo.felipe.contajusta.models.Amigo;
import araujo.felipe.contajusta.models.Item;
import araujo.felipe.contajusta.utils.DatabaseHelper;
import araujo.felipe.contajusta.utils.DialogSobre;

/**
 * Classe responsável pela activity que exibe o resumo da despesa.
 */
public class ResumoDespesa extends AppCompatActivity {
    private boolean editarDespesa;
    private Button btnEditar;
    private Button btnFinalizar;
    private DatabaseHelper db;
    private long idDespesa;
    private RecyclerView rvAmigos;
    private RecyclerView rvItens;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_despesa);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getLong("idDespesa") != 0) {
                idDespesa = b.getLong("idDespesa");
            }
            if (b.getBoolean("editar")) {
                editarDespesa = true;
            }
        }

        btnEditar = (Button) findViewById(R.id.btn_editar_resumo);
        btnFinalizar = (Button) findViewById(R.id.btn_finalizar_resumo);
        db = new DatabaseHelper(this);
        rvAmigos = (RecyclerView) findViewById(R.id.rv_amigos_resumo);
        rvItens = (RecyclerView) findViewById(R.id.rv_itens_resumo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView tvValorTotal = (TextView) findViewById(R.id.tv_valor_total_resumo);

        double valorTotal = db.calcularValorTotalDespesa(idDespesa);
        Locale locale = new Locale("pt", "BR");
//        Locale locale = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        String valor = NumberFormat.getCurrencyInstance(locale).format(valorTotal);
        tvValorTotal.setText(valor);

        this.criarToolbar();
        this.criarBotoes();
        this.atualizarListaAmigos();
        this.atualizarListaItens();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sobre) {
            new DialogSobre(ResumoDespesa.this);

            return true;
        } else if (id == R.id.ver_amigos) {
            Intent intent = new Intent(ResumoDespesa.this, ListaAmigos.class);

            intent.putExtra("verAmigos", true);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Popula a RecyclerView de amigos com o nome e o valor que
     * cada amigo deve pagar através do AmigoAdapter.
     */
    private void atualizarListaAmigos() {
        ArrayList<Amigo> listaAmigos = db.listarAmigosDespesa(idDespesa);

        AmigoAdapter amigoAdapter = new AmigoAdapter(listaAmigos, db, idDespesa, "ResumoDespesa");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        rvAmigos.setLayoutManager(layoutManager);
        rvAmigos.setItemAnimator(new DefaultItemAnimator());
        rvAmigos.setAdapter(amigoAdapter);
    }

    /**
     * Popula a RecyclerView de itens com a descrição, o preço unitário e a
     * quantidade de cada item da despesa definidos através do ItemAdapter.
     */
    private void atualizarListaItens() {
        ArrayList<Item> listaItens = db.listarItensDespesa(idDespesa);

        ItemAdapter itemAdapter = new ItemAdapter(listaItens);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        rvItens.setLayoutManager(layoutManager);
        rvItens.setItemAnimator(new DefaultItemAnimator());
        rvItens.setAdapter(itemAdapter);
    }

    /**
     * Define as ações de cada botão da activity.
     */
    private void criarBotoes() {
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editarDespesa) {
                    Intent intent = new Intent(ResumoDespesa.this, NovaDespesa.class);

//                    Log.d("ResumoDespesa", String.valueOf(idDespesa));
                    intent.putExtra("idDespesa", idDespesa);
                    intent.putExtra("editar", true);

                    startActivity(intent);
                } else {
                    onBackPressed();
                }
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizarDespesa();
            }
        });
    }

    /**
     * Cria a toolbar personalizada.
     */
    private void criarToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String titulo = String.format(getResources().getString(R.string.resumo_despesa), db.getDespesa(idDespesa).getDescricao());
        getSupportActionBar().setTitle(titulo);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * Cria uma dialog para confirmar a finalização da despesa e ir para a MainActivity.
     */
    private void finalizarDespesa() {
        new AlertDialog.Builder(ResumoDespesa.this)
                .setTitle(R.string.atencao)
                .setMessage(R.string.finalizar_despesa)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ResumoDespesa.this, MainActivity.class);
                        intent.putExtra("despesaSalva", true);
                        intent.putExtra("idDespesa", idDespesa);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}
