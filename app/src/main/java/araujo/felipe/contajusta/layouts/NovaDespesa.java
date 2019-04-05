package araujo.felipe.contajusta.layouts;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import araujo.felipe.contajusta.adapters.AmigoAdapter;
import araujo.felipe.contajusta.adapters.ItemAdapter;
import araujo.felipe.contajusta.models.Amigo;
import araujo.felipe.contajusta.models.Despesa;
import araujo.felipe.contajusta.models.Item;
import araujo.felipe.contajusta.utils.DatabaseHelper;
import araujo.felipe.contajusta.R;
import araujo.felipe.contajusta.utils.RecyclerTouchListener;

import static java.lang.Long.parseLong;

/**
 * Classe responsável pela criação e edição de uma despesa.
 */
public class NovaDespesa extends AppCompatActivity {
    private ArrayList<Item> itens;
    private ArrayList<String> listaAmigosDespesa;
    private boolean editar;
    private Button btnAddAmigo;
    private Button btnAddAmigoGrande;
    private Button btnAddItem;
    private Button btnAddItemGrande;
    private Button btnCancelar;
    private Button btnResumo;
    private DatabaseHelper db;
    private EditText etDescricaoDespesa;
    private Gson gson = new Gson();
    private TextInputLayout tilDescricaoDespesa;
    private Locale locale;
    private long idDespesa = 0;
    private RecyclerView lvListaAmigos;
    private RecyclerView lvListaItens;
    private String descricaoDespesa;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_despesa);

        btnAddAmigo = (Button) findViewById(R.id.btn_add_amigo);
        btnAddItem = (Button) findViewById(R.id.btn_add_item);
        btnAddAmigoGrande = (Button) findViewById(R.id.btn_add_amigo_grande);
        btnAddItemGrande = (Button) findViewById(R.id.btn_add_item_grande);
        btnCancelar = (Button) findViewById(R.id.btn_cancelar_nova_despesa);
        btnResumo = (Button) findViewById(R.id.btn_resumo_nova_despesa);
        db = new DatabaseHelper(this);
        descricaoDespesa = getResources().getString(R.string.despesa);
        editar = false;
        etDescricaoDespesa = (EditText) findViewById(R.id.et_descricaoDespesa);
        etDescricaoDespesa.clearFocus();
        itens = new ArrayList<>();
        listaAmigosDespesa = new ArrayList<>();
        lvListaAmigos = (RecyclerView) findViewById(R.id.lv_amigos_nova_despesa);
        lvListaItens = (RecyclerView) findViewById(R.id.lv_itens_nova_despesa);
        tilDescricaoDespesa = (TextInputLayout) findViewById(R.id.til_descricao_despesa);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Despesa despesa = new Despesa();
        locale = new Locale("pt", "BR");
//        locale = new Locale(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
        DateFormat formatoIdDespesa = new SimpleDateFormat("yyMMddHHmmss", locale);
        DateFormat formatoDiaDespesa = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", locale);
        Date diaHoraAparelho = new Date();
        idDespesa = parseLong(formatoIdDespesa.format(diaHoraAparelho));

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.getBoolean("getAmigosSelecionados")) {
                Snackbar.make(findViewById(R.id.activity_nova_despesa),
                        R.string.sucesso_amigos_despesa,
                        Snackbar.LENGTH_LONG).show();
            }

            if (b.getBoolean("editar") && b.getLong("idDespesa") != 0) {
                editar = b.getBoolean("editar");
//                Log.d("editar despesa", String.valueOf(editar));
                idDespesa = b.getLong("idDespesa");
                descricaoDespesa = db.getDespesa(idDespesa).getDescricao();
                etDescricaoDespesa.setText(descricaoDespesa);
            }
        } else {
            despesa.setId(idDespesa);
            despesa.setDescricao(getResources().getString(R.string.despesa_nao_finalizada));
            despesa.setDia(formatoDiaDespesa.format(diaHoraAparelho));
            db.inserirDados("despesa", gson.toJson(despesa));
        }

        etDescricaoDespesa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                descricaoDespesa = etDescricaoDespesa.getText().toString().trim();

                if (idDespesa != 0 && !descricaoDespesa.equals("")) {
                    getSupportActionBar().setTitle(descricaoDespesa);
                    tilDescricaoDespesa.setErrorEnabled(false);
                    tilDescricaoDespesa.setError(null);
                } else if (descricaoDespesa.equals("")) {
                    getSupportActionBar().setTitle(R.string.despesa);
                    tilDescricaoDespesa.setErrorEnabled(true);
                    tilDescricaoDespesa.setError("* Campo obrigatório");
                }

            }
        });
//        Log.d("NovaDespesa - idDespesa", String.valueOf(idDespesa));
//        Log.d("despesa", despesa.toString());

        this.criarToolbar();
        this.atualizarListaAmigos();
        this.atualizarListaItens();
        this.criarBotoes();

        lvListaAmigos.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), lvListaAmigos, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Snackbar.make(view, "Toque e segure para remover um amigo da despesa.", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                //Excluir Amigo
            }
        }));
        lvListaItens.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), lvListaItens, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                opcoesItem(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                excluirItem(position);
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            idDespesa = data.getExtras().getLong("idDespesa");

            if (requestCode == 1) {
//                Log.d("listaAmigosDespesa: ", listaAmigosDespesa.toString());
                if (data.getExtras().getBoolean("getAmigosSelecionados")) {
                    Snackbar.make(findViewById(R.id.activity_nova_despesa),
                            R.string.sucesso_amigos_despesa,
                            Snackbar.LENGTH_LONG).show();
                }
            }

            if (requestCode == 2) {
                atualizarListaAmigos();
                atualizarListaItens();

                // Exibe mensagem de sucesso para item adicionado a despesa
                if (data.getExtras().getBoolean("itemAdicionado")) {
                    String sucessoAdicionar = String.format(
                            getResources().getString(R.string.sucesso_novo_item),
                            data.getExtras().getString("itemAdicionadoDescricao"));

                    Snackbar.make(findViewById(R.id.activity_nova_despesa),
                            sucessoAdicionar,
                            Snackbar.LENGTH_LONG).show();
                }

                // Exibe mensagem de sucesso para item excluído de dentro da activity AdicionarItem
                if (data.getExtras().getBoolean("itemExcluidoSucesso")) {
                    String sucessoExcluir = String.format(
                            getResources().getString(R.string.sucesso_excluir_item),
                            data.getExtras().getString("itemExcluidoDescricao"),
                            db.getDespesa(idDespesa).getDescricao());

                    Snackbar.make(findViewById(R.id.activity_nova_despesa),
                            sucessoExcluir,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        cancelarDespesa();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sobre) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(NovaDespesa.this);
            mBuilder.setTitle(R.string.sobre);
            mBuilder.setMessage(R.string.texto_sobre);
            mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialogAddAmigo = mBuilder.create();
            dialogAddAmigo.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.atualizarListaAmigos();
        this.atualizarListaItens();
//        Log.d("idDespesa", String.valueOf(idDespesa));
    }

    /**
     * Popula um RecyclerView com uma lista de amigos que fazem parte da despesa em questão.
     */
    public void atualizarListaAmigos() {
        listaAmigosDespesa = new ArrayList<>();

        for (Amigo amigo : db.listarAmigosDespesa(idDespesa)) {
            listaAmigosDespesa.add(amigo.getNome());
        }

        if (listaAmigosDespesa.isEmpty()) {
            lvListaAmigos.setVisibility(View.GONE);
            btnAddAmigo.setVisibility(View.GONE);
            btnAddAmigoGrande.setVisibility(View.VISIBLE);
        } else {
            btnAddAmigoGrande.setVisibility(View.GONE);
            btnAddAmigo.setVisibility(View.VISIBLE);
            lvListaAmigos.setVisibility(View.VISIBLE);

            AmigoAdapter amigoAdapter = new AmigoAdapter(db.listarAmigosDespesa(idDespesa), db, idDespesa, "NovaDespesa");
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

            lvListaAmigos.setLayoutManager(layoutManager);
            lvListaAmigos.setItemAnimator(new DefaultItemAnimator());
            lvListaAmigos.setAdapter(amigoAdapter);
        }
    }

    /**
     * Popula um RecyclerView com uma lista de itens que fazem parte da despesa em questão.
     */
    public void atualizarListaItens() {
        itens = db.listarItensDespesa(idDespesa);

        if (itens.isEmpty()) {
            lvListaItens.setVisibility(View.GONE);
            btnAddItem.setVisibility(View.GONE);
            btnAddItemGrande.setVisibility(View.VISIBLE);
        } else {
            btnAddItemGrande.setVisibility(View.GONE);
            btnAddItem.setVisibility(View.VISIBLE);
            lvListaItens.setVisibility(View.VISIBLE);

            ItemAdapter itemAdapter = new ItemAdapter(itens);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

            lvListaItens.setLayoutManager(layoutManager);
            lvListaItens.setItemAnimator(new DefaultItemAnimator());
            lvListaItens.setAdapter(itemAdapter);
        }
    }

    /**
     * Cria uma dialog para confirmar o cancelamento da despesa.
     */
    public void cancelarDespesa() {
        new AlertDialog.Builder(NovaDespesa.this)
                .setTitle(R.string.atencao)
                .setMessage(R.string.cancelar_despesa)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!editar) {
                            db.excluirDespesa(idDespesa);
                        }
                        Intent intent = new Intent(NovaDespesa.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    /**
     * Cria a toolbar personalizada.
     */
    public void criarToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(descricaoDespesa);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelarDespesa();
            }
        });
    }

    /**
     * Cria os botões da activity.
     */
    public void criarBotoes() {
        btnAddAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddAmigoGrande.setVisibility(View.GONE);
                lvListaAmigos.setVisibility(View.VISIBLE);

                Intent intent = new Intent(NovaDespesa.this, ListaAmigos.class);

                intent.putExtra("idDespesa", idDespesa);
                intent.putExtra("amigosDespesa", listaAmigosDespesa);

                startActivityForResult(intent, 1);
            }
        });

        btnAddAmigoGrande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddAmigoGrande.setVisibility(View.GONE);
                lvListaAmigos.setVisibility(View.VISIBLE);

                Intent intent = new Intent(NovaDespesa.this, ListaAmigos.class);

                intent.putExtra("idDespesa", idDespesa);

                startActivityForResult(intent, 1);
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!db.listarAmigosDespesa(idDespesa).isEmpty()) {
                    btnAddItemGrande.setVisibility(View.GONE);
                    lvListaItens.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(NovaDespesa.this, AdicionarItem.class);

                    intent.putExtra("idDespesa", idDespesa);
                    intent.putExtra("novoItem", true);

                    startActivityForResult(intent, 2);
                } else {
                    Snackbar.make(view,
                            R.string.erro_sem_amigos,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        btnAddItemGrande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!db.listarAmigosDespesa(idDespesa).isEmpty()) {
                    btnAddItemGrande.setVisibility(View.GONE);
                    lvListaItens.setVisibility(View.VISIBLE);
                    btnAddItem.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(NovaDespesa.this, AdicionarItem.class);

                    intent.putExtra("idDespesa", idDespesa);
                    intent.putExtra("novoItem", true);

                    startActivityForResult(intent, 2);
                } else {
                    Snackbar.make(view,
                            R.string.erro_sem_amigos,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelarDespesa();
            }
        });

        btnResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarDespesa()) {
                    Intent intent = new Intent(NovaDespesa.this, ResumoDespesa.class);

                    intent.putExtra("idDespesa", idDespesa);
                    intent.putExtra("descricaoDespesa", descricaoDespesa);

                    Despesa despesa = db.getDespesa(idDespesa);

                    double valorTotal = db.calcularValorTotalDespesa(idDespesa);
                    String valor = NumberFormat.getCurrencyInstance(locale).format(valorTotal);

                    despesa.setDescricao(descricaoDespesa);
                    despesa.setValorTotal(valor);

                    db.atualizarDespesa(despesa);

                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Cria uma dialog para confirmar a exclusão do item selecionado.
     *
     * @param posicao posicao da lista correspondente ao item que deve ser excluído
     */
    public void excluirItem(int posicao) {
        final Item item = itens.get(posicao);
        String titulo = String.format(getResources().getString(R.string.excluir_algo), item.getDescricao());
        String textoDialog = String.format(getResources().getString(R.string.confirmar_exclusao_item), item.getDescricao(), descricaoDespesa);
        final String sucessoExcluir = String.format(getResources().getString(R.string.sucesso_excluir_item), item.getDescricao(), descricaoDespesa);

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(textoDialog)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.removerItemDespesa(idDespesa, item.getId());

                        atualizarListaAmigos();
                        atualizarListaItens();

                        Snackbar.make(findViewById(R.id.activity_nova_despesa),
                                sucessoExcluir,
                                Snackbar.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    /**
     * Cria uma dialog para dar as opções de editar ou excluir o item selecionado.
     *
     * @param posicao a posição do item ao qual as opções pertencem
     */
    public void opcoesItem(final int posicao) {
        String titulo = String.format(getResources().getString(R.string.opcoes_item), itens.get(posicao).getDescricao());

        new MaterialDialog.Builder(NovaDespesa.this)
                .title(titulo)
                .items(R.array.menuContexto)
                .itemsIds(R.array.menuItemsIds)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int item, CharSequence text) {
                        switch (item) {
                            case 0:
                                // Editar item
                                Intent intent = new Intent(NovaDespesa.this, AdicionarItem.class);

                                intent.putExtra("idDespesa", idDespesa);
                                intent.putExtra("item", gson.toJson(itens.get(posicao)));

                                startActivityForResult(intent, 2);
                                break;

                            case 1:
                                excluirItem(posicao);
                                dialog.cancel();
                                break;

                            case 2:
                                dialog.cancel();
                                break;
                        }
                    }
                })
                .show();

    }

    /**
     * Valida a despesa caso todos os campos estejam preenchidos.
     *
     * @return true se a despesa for válida, false caso contrário.
     */
    public boolean validarDespesa() {
        if (etDescricaoDespesa.getText().toString().trim().isEmpty()) {
            tilDescricaoDespesa.setErrorEnabled(true);
            tilDescricaoDespesa.setError("* Campo obrigatório");
            Snackbar.make(findViewById(R.id.activity_nova_despesa),
                    R.string.erro_sem_descricao_despesa,
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (db.listarAmigosDespesa(idDespesa).isEmpty()) {
            Snackbar.make(findViewById(R.id.activity_nova_despesa),
                    R.string.erro_sem_amigos,
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (db.listarItensDespesa(idDespesa).isEmpty()) {
            Snackbar.make(findViewById(R.id.activity_nova_despesa),
                    R.string.erro_sem_itens,
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
