package araujo.felipe.contajusta.layouts;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

import araujo.felipe.contajusta.models.Amigo;
import araujo.felipe.contajusta.R;
import araujo.felipe.contajusta.models.AmigoDespesa;
import araujo.felipe.contajusta.utils.DatabaseHelper;
import araujo.felipe.contajusta.utils.DialogSobre;

/**
 * Classe responsável pela activity que lista os amigos do app.
 * É através dela que amigos são adicionados ou excluídos.
 */
public class ListaAmigos extends AppCompatActivity {
    private ArrayAdapter<String> adaptador;
    private ArrayList<String> listaAmigosDespesa;
    private boolean adicionarAmigo;
    private boolean verAmigos;
    private DatabaseHelper db;
    private FloatingActionButton fabAddAmigo;
    private FloatingActionButton fabConfirmarAmigos;
    private Gson gson = new Gson();
    private InputMethodManager imm;
    private LinearLayout layoutSemAmigos;
    private ListView lvListaAmigos;
    private long idDespesa;
    private String tabelaAmigoDespesa = "amigo_despesa";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigos);

        // Inicializa as variáveis da classe
        db = new DatabaseHelper(this);
        fabAddAmigo = (FloatingActionButton) findViewById(R.id.fab_lista_amigos);
        fabConfirmarAmigos = (FloatingActionButton) findViewById(R.id.fab_confirma_amigos);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        layoutSemAmigos = (LinearLayout) findViewById(R.id.layout_sem_amigos);
        listaAmigosDespesa = new ArrayList<>();
        lvListaAmigos = (ListView) findViewById(R.id.lv_amigos_lista_amigos);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Verifica se algum dado foi enviado através de uma intent antes de iniciar a ListaAmigos
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getLong("idDespesa") != 0) {
                idDespesa = b.getLong("idDespesa");
            }

            if (b.getStringArrayList("amigosDespesa") != null) {
                listaAmigosDespesa = b.getStringArrayList("amigosDespesa");
            }

            if (b.getBoolean("verAmigos")) {
                verAmigos = true;
            }

            if (b.getBoolean("adicionarAmigo")) {
                adicionarAmigo = true;
            }
        }

        criarToolbar();
        fabNovoAmigo();
        fabConfirmarAmigos();
        atualizarLista();

        if (adicionarAmigo) {
            fabAddAmigo.callOnClick();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_amigos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sobre) {
            new DialogSobre(ListaAmigos.this);

            return true;
        } else if (id == R.id.excluir_tudo) {
            dialogExcluirTodosAmigos();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Popula uma ListView de acordo com todos os amigos salvos no BD.
     */
    public void atualizarLista() {
        final ArrayList<String> listaAmigos = this.getListaAmigosPorNome();

        if (listaAmigos.isEmpty()) {
            lvListaAmigos.setVisibility(View.GONE);
            layoutSemAmigos.setVisibility(View.VISIBLE);
        } else {
            layoutSemAmigos.setVisibility(View.GONE);
            lvListaAmigos.setVisibility(View.VISIBLE);

            if (verAmigos) {
                // Caso seja carregada apenas para visualizar, adicionar e excluir amigos, uma lista simples é utilizada
                adaptador = new ArrayAdapter<>(ListaAmigos.this, android.R.layout.simple_list_item_1, listaAmigos);
            } else {
                // Quando a classe é carregada a partir da NovaDespesa, é possível selecionar os amigos
                adaptador = new ArrayAdapter<>(ListaAmigos.this, android.R.layout.simple_list_item_multiple_choice, listaAmigos);

                lvListaAmigos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }

            lvListaAmigos.setAdapter(adaptador);

            // Marca os amigos que estavam selecionados anteriormente ao editar os amigos da despesa
            if (getIntent().hasExtra("amigosDespesa")) {
                listaAmigosDespesa = getIntent().getExtras().getStringArrayList("amigosDespesa");
                for (int i = 0; i < listaAmigosDespesa.size(); i++) {
//                    Log.d("listaAmigosDespesa"+i, listaAmigosDespesa.get(i));
//                    Log.d("lvListaAmigos"+i, lvListaAmigos.getItemAtPosition(i).toString());
                    String nome = listaAmigosDespesa.get(i);
                    for (int j = 0; j < listaAmigos.size(); j++) {
                        if (nome.equals(listaAmigos.get(j))) {
                            lvListaAmigos.setItemChecked(j, true);
                        }
                    }
                }
            }

            // Possibilita a exclusão do amigo ao tocar e segurar em uma posição da lista
            lvListaAmigos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int posicao, long l) {
                    excluirAmigo(posicao);

                    return true;
                }
            });

            // Exibe ou oculta os botões de adicionar amigo e confirmar seleção de amigos,
            // exibindo um ou outro, conforme a quantidade de amigos selecionados
            lvListaAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int posicao, long l) {
                    fabAddAmigo.setVisibility(View.VISIBLE);
                    fabConfirmarAmigos.setVisibility(View.GONE);

                    for (int i = 0; i < listaAmigos.size(); i++) {
                        if (lvListaAmigos.isItemChecked(i)) {
                            fabAddAmigo.setVisibility(View.GONE);
                            fabConfirmarAmigos.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }

    }

    /**
     * Cria a toolbar personalizada.
     */
    public void criarToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.todos_amigos);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * Cria a dialog de confirmação para excluir todos os amigos.
     */
    public void dialogExcluirTodosAmigos() {
        new AlertDialog.Builder(ListaAmigos.this)
                .setTitle(R.string.excluir_tudo)
                .setMessage(R.string.texto_excluir_todos_amigos)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.excluirTodosAmigos();
                        atualizarLista();
                        Snackbar.make(findViewById(R.id.activity_lista_amigos),
                                R.string.sucesso_excluir_amigos,
                                Snackbar.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    /**
     * Exclui um amigo do app.
     *
     * @param id id do amigo que deve ser excluído
     */
    public void excluirAmigo(int id) {
        final Amigo amigo = db.getAmigo(id);
        String titulo = String.format(getResources().getString(R.string.excluir_algo), amigo.getNome());
        String textoDialog = String.format(getResources().getString(R.string.confirmar_exclusao), amigo.getNome());
        final String sucessoExcluir = String.format(getResources().getString(R.string.sucesso_excluir), amigo.getNome());

        new AlertDialog.Builder(ListaAmigos.this)
                .setTitle(titulo)
                .setMessage(textoDialog)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Validar exclusão em relação a despesas e itens em que o amigo faz parte
                        db.excluirAmigo(amigo);

                        atualizarLista();

                        Snackbar.make(findViewById(R.id.activity_lista_amigos),
                                sucessoExcluir,
                                Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    /**
     * Define a ação ao tocar no botão para adicionar um novo amigo.
     */
    public void fabNovoAmigo() {
        fabAddAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cria a dialog para adicionar um novo amigo
                final View mView = getLayoutInflater().inflate(R.layout.activity_adicionar_amigo, null);
                final EditText etNomeAmigo = (EditText) mView.findViewById(R.id.et_nomeAmigo);

                new AlertDialog.Builder(ListaAmigos.this)
                        .setTitle(R.string.novo_amigo)
                        .setView(mView)
                        .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String nomeAmigo = (etNomeAmigo.getText().toString().trim()).toUpperCase();

                                // Insere o amigo no BD com o nome informado pelo usuário em letras maiúsculas
                                inserirAmigo(nomeAmigo);

                                // Esconde o teclado ao adicionar o amigo
                                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Esconde o teclado ao cancelar a adição do amigo
                                imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                                dialogInterface.cancel();
                            }
                        })
                        .show();

                // Força o foco da tela no EditText da dialog
                etNomeAmigo.requestFocus();
                // Força a exibição do teclado
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    /**
     * Define a ação ao tocar no botão para confirmar os amigos selecionados.
     */
    public void fabConfirmarAmigos() {
        fabConfirmarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Cria uma dialog para confirmar os amigos selecionados
                new AlertDialog.Builder(ListaAmigos.this)
                        .setTitle(R.string.confirmar_selecao)
                        .setMessage(R.string.texto_confirmar_amigos)
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Confirma os amigos selecionados para a despesa e os salva na tabela amigo_despesa
                                ArrayList<String> amigosDespesa = getAmigosSelecionados();
                                ArrayList<Amigo> amigos = objetosAmigosSelecionados();

                                if (amigosDespesa.size() > 0) {
                                    db.removerAmigosDespesa(idDespesa);
                                    AmigoDespesa amigoDespesa;

                                    for (Amigo amigo : amigos) {
                                        amigoDespesa = new AmigoDespesa(amigo.getId(), idDespesa);
//                                amigoDespesa.setIdAmigo(amigo.getId());
//                                amigoDespesa.setIdDespesa(idDespesa);
//                                        Log.d("amigoDespesa", amigoDespesa.toString());
                                        db.inserirDados(tabelaAmigoDespesa, gson.toJson(amigoDespesa));
                                    }

                                    Intent intent = new Intent(ListaAmigos.this, NovaDespesa.class);

                                    intent.putExtra("idDespesa", idDespesa);
                                    intent.putExtra("amigosDespesa", amigosDespesa);
                                    intent.putExtra("getAmigosSelecionados", true);

                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                } else {
                                    fabAddAmigo.setVisibility(View.VISIBLE);
                                    fabConfirmarAmigos.setVisibility(View.GONE);
                                    dialogInterface.cancel();

                                    Snackbar.make(findViewById(R.id.activity_lista_amigos),
                                            R.string.selecione_amigo,
                                            Snackbar.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fabAddAmigo.setVisibility(View.VISIBLE);
                                fabConfirmarAmigos.setVisibility(View.GONE);
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * Verifica quais posições foram selecionadas no ListView e adiciona o nome de
     * cada amigo correspondete a ela em uma lista do tipo String.
     *
     * @return uma lista de String
     */
    public ArrayList<String> getAmigosSelecionados() {
        SparseBooleanArray selecionados = lvListaAmigos.getCheckedItemPositions();
        ArrayList<String> amigos = new ArrayList<>();
        ArrayList<Amigo> todosAmigos = db.listarAmigosApp();
        String nomeAmigo;

        for (int iterador = 0; iterador < selecionados.size(); iterador++) {
            int posicao = selecionados.keyAt(iterador);
            if (selecionados.valueAt(iterador)) {
                nomeAmigo = adaptador.getItem(posicao);
                for (int i = 0; i < todosAmigos.size(); i++) {
                    if (nomeAmigo.equals(todosAmigos.get(i).getNome())) {
                        amigos.add(nomeAmigo);
                    }
                }
            }
        }

//        Log.d("Amigos selecionados: ", amigos.toString());

        return amigos;
    }

    /**
     * Obtém todos os amigos do app salvos no banco e adiciona o nome de
     * cada um deles a uma lista do tipo String.
     *
     * @return uma lista do tipo String
     */
    public ArrayList<String> getListaAmigosPorNome() {
        ArrayList<String> listaAmigos = new ArrayList<>();

        for (Amigo amigo : db.listarAmigosApp()) {
            listaAmigos.add(amigo.getNome());
        }

        return listaAmigos;
    }

    /**
     * Valida e insere o amigo no BD.
     *
     * @param nomeAmigo o nome amigo
     */
    public void inserirAmigo(String nomeAmigo) {
        final ArrayList<String> listaAmigos = this.getListaAmigosPorNome();
        boolean nomeIgual = false;

        // Verifica se o amigo já foi adicionado anteriormente
        for (String nome : listaAmigos) {
            if (nome.equals(nomeAmigo)) {
                nomeIgual = true;
            }
        }

        if (!nomeAmigo.isEmpty() && !nomeIgual) {
            // Insere o novo amigo informado pelo usuário
            Amigo amigo = new Amigo(nomeAmigo);
            db.inserirDados("amigo", gson.toJson(amigo));

            Snackbar.make(findViewById(R.id.activity_lista_amigos),
                    R.string.sucesso_add_amigo,
                    Snackbar.LENGTH_LONG).show();
        } else if (nomeIgual) {
            // Caso o nome do amigo seja igual a outro da lista
            Snackbar.make(findViewById(R.id.activity_lista_amigos),
                    R.string.erro_nome_amigo,
                    Snackbar.LENGTH_LONG).show();
        } else {
            // Caso o campo do nome do amigo esteja vazio
            Snackbar.make(findViewById(R.id.activity_lista_amigos),
                    R.string.erro_campos_vazios,
                    Snackbar.LENGTH_LONG).show();
        }

        this.atualizarLista();
    }

    /**
     * Verifica as posições selecionadas no ListView e cria uma lista de objetos do tipo Amigo
     *
     * @return uma lista do tipo Amigo
     */
    public ArrayList<Amigo> objetosAmigosSelecionados() {
        SparseBooleanArray selecionados = lvListaAmigos.getCheckedItemPositions();
        ArrayList<Amigo> amigos = new ArrayList<>();
        ArrayList<Amigo> todosAmigos = db.listarAmigosApp();
        String nomeAmigo;

        for (int iterador = 0; iterador < selecionados.size(); iterador++) {
            int posicao = selecionados.keyAt(iterador);
            if (selecionados.valueAt(iterador)) {
                nomeAmigo = adaptador.getItem(posicao);
                for (int i = 0; i < todosAmigos.size(); i++) {
                    if (todosAmigos.get(i).getNome().equals(nomeAmigo)) {
                        amigos.add(todosAmigos.get(i));
                    }
                }
            }
        }

//        Log.d("Amigos selecionados: ", amigos.toString());

        return amigos;
    }
}