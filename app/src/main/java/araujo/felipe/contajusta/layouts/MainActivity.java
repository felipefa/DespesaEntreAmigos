package araujo.felipe.contajusta.layouts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.github.clans.fab.FloatingActionButton;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import araujo.felipe.contajusta.R;
import araujo.felipe.contajusta.adapters.DespesaAdapter;
import araujo.felipe.contajusta.models.Despesa;
import araujo.felipe.contajusta.utils.DatabaseHelper;
import araujo.felipe.contajusta.utils.DialogSobre;
import araujo.felipe.contajusta.utils.RecyclerTouchListener;

/**
 * Classe responsável pela tela inicial do app, onde são exibidas as despesas por padrão.
 * A partir dela é possível excluir uma despesa, ir para novas activities para adicionar um
 * novo amigo (ou apenas vê-los) ou adicionar/editar uma despesa.
 */
public class MainActivity extends AppCompatActivity {
    private ArrayList<Despesa> listaDespesas;
    private DatabaseHelper db;
    private FloatingActionButton fab;
    private FloatingActionButton fabAdicionarAmigo;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cria a toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        // Inicializa as variáveis da classe
        db = new DatabaseHelper(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabAdicionarAmigo = (FloatingActionButton) findViewById(R.id.fab_adicionar_amigo);
        linearLayout = (LinearLayout) findViewById(R.id.layout_sem_despesas);
        listaDespesas = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.rv_despesas);

        // Verifica se algum dado foi enviado através de uma intent antes de iniciar a MainActivity
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getBoolean("despesaSalva")) {
                String descricao = db.getDespesa(b.getLong("idDespesa")).getDescricao();
                String despesaSalva = String.format(getResources().getString(R.string.sucesso_nova_despesa), descricao);

                Snackbar.make(findViewById(R.id.activity_main),
                        despesaSalva,
                        Snackbar.LENGTH_LONG).show();
            }
        }

        atualizarLista();
        fabAdicionarAmigo();
        fabNovaDespesa();

        // Adiciona o listener para reconhecer o toque do usuário em alguma posição da lista de despesas
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Editar despesa
                Despesa despesa = listaDespesas.get(position);

                Intent intent = new Intent(MainActivity.this, ResumoDespesa.class);

                intent.putExtra("idDespesa", despesa.getId());
                intent.putExtra("editar", true);

                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                // Excluir despesa
                excluirDespesa(listaDespesas.get(position));
            }
        }));
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
            new DialogSobre(MainActivity.this);

            return true;
        } else if (id == R.id.ver_amigos) {
            Intent intent = new Intent(MainActivity.this, ListaAmigos.class);

            intent.putExtra("verAmigos", true);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Método responsável por popular o RecyclerView da MainActivity com as
     * despesas adicionadas pelo usuário.
     */
    private void atualizarLista() {
        listaDespesas = db.listarDespesas();

        if (listaDespesas.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);

            DespesaAdapter despesaAdapter = new DespesaAdapter(listaDespesas);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(despesaAdapter);
        }
    }

    /**
     * Método responsável pela exclusão de uma despesa. Para isso cria uma dialog,
     * que caso seja confirmada, exclui a despesa selecionada.
     *
     * @param despesa um objeto do tipo Despesa que será excluído.
     */
    private void excluirDespesa(final Despesa despesa) {
        String titulo = String.format(getResources().getString(R.string.excluir_algo), despesa.getDescricao());
        String textoDialog = String.format(getResources().getString(R.string.confirmar_exclusao), despesa.getDescricao());
        final String sucessoExcluir = String.format(getResources().getString(R.string.sucesso_excluir), despesa.getDescricao());

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(titulo)
                .setMessage(textoDialog)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Exclui todos os amigos da despesa
                        db.removerAmigosDespesa(despesa.getId());
                        // Exclui todos os itens da despesa (pela passagem do parâmetro -1 no lugar do id do item)
                        db.removerItemDespesa(despesa.getId(), -1);
                        // Exclui a despesa do BD
                        db.excluirDespesa(despesa.getId());

                        atualizarLista();

                        Snackbar.make(findViewById(R.id.activity_main),
                                sucessoExcluir,
                                Snackbar.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    /**
     * Define a ação ao tocar no botão para adicionar amigo.
     */
    private void fabAdicionarAmigo() {
        fabAdicionarAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(MainActivity.this, ListaAmigos.class);

                intent.putExtra("verAmigos", true);
                intent.putExtra("adicionarAmigo", true);

                startActivity(intent);
            }
        });
    }

    /**
     * Define a ação ao tocar no botão para adicionar uma despesa.
     */
    private void fabNovaDespesa() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(MainActivity.this, NovaDespesa.class);
                startActivity(intent);
            }
        });
    }

}