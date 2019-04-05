package araujo.felipe.contajusta.layouts;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import araujo.felipe.contajusta.R;
import araujo.felipe.contajusta.models.Amigo;
import araujo.felipe.contajusta.models.AmigoItem;
import araujo.felipe.contajusta.models.Item;
import araujo.felipe.contajusta.utils.DatabaseHelper;
import araujo.felipe.contajusta.utils.DialogSobre;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * Classe responsável pela criação e edição de um Item.
 */
public class AdicionarItem extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUISICAO_GALERIA = 20;
    private static final int REQUISICAO_CAMERA = 21;
    private static final String ARQUIVO_JPEG_PREFIXO = "IMG_";
    private static final String ARQUIVO_JPEG_SUFIXO = ".jpg";

    private ArrayAdapter<String> adaptador;
    private ArrayList<String> listaAmigosItem;
    private boolean novoItem;
    private Button btnSalvar;
    private DatabaseHelper db;
    private EditText etDescricaoItem;
    private EditText etPreco;
    private Gson gson = new Gson();
    private Handler progressBarbHandler = new Handler();
    private int progressBarStatus = 0;
    private ImageView ivItem;
    private Item itemAtual;
    private LinearLayout layoutBtnImagem;
    private ListView lvAmigosItem;
    private long idDespesa;
    private ProgressDialog progressBar;
    private Spinner spinnerQtd;
    private String campoQuantidade;
    private String imagem;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_item);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Inicializa as variáveis da classe, remove o foco dos campos de texto e habilita os botões
        Button btnCancelar = (Button) findViewById(R.id.btn_cancelar_novo_item);
        btnCancelar.setOnClickListener(this);
        Button btnNovaImagem = (Button) findViewById(R.id.btn_nova_imagem);
        btnNovaImagem.setOnClickListener(this);
        btnSalvar = (Button) findViewById(R.id.btn_salvar_novo_item);
        btnSalvar.setOnClickListener(this);
        btnSalvar.setClickable(true);
        db = new DatabaseHelper(this);
        etDescricaoItem = (EditText) findViewById(R.id.et_descricaoItem);
        etDescricaoItem.clearFocus();
        etPreco = (EditText) findViewById(R.id.et_preco);
        etPreco.clearFocus();
        imagem = "";
        ivItem = (ImageView) findViewById(R.id.iv_item);
        layoutBtnImagem = (LinearLayout) findViewById(R.id.layout_btn_img);
        listaAmigosItem = new ArrayList<>();
        lvAmigosItem = (ListView) findViewById(R.id.lv_amigos_novo_item);
        spinnerQtd = (Spinner) findViewById(R.id.spinner_qtd);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Solicita as permissões para usar a câmera e acessar os arquivos ao usuário
        if (ContextCompat.checkSelfPermission(AdicionarItem.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ivItem.setEnabled(false);
            ActivityCompat.requestPermissions(AdicionarItem.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            ivItem.setEnabled(true);
            ivItem.setOnClickListener(this);
        }

        // Carrega os dados passados, se houverem, na intent que inicializa esta classe
        Bundle b = getIntent().getExtras();
        if (b != null) {
            idDespesa = b.getLong("idDespesa");
            novoItem = b.getBoolean("novoItem");

            if (novoItem) {
                itemAtual = new Item();
//                Log.d("Novo Item", itemAtual.toString());
            } else {
                itemAtual = gson.fromJson(b.getString("item"), Item.class);

                // Carrega os valores do item salvos no banco para os campos da activity
                etDescricaoItem.setText(itemAtual.getDescricao());
                etPreco.setText(String.valueOf(itemAtual.getPreco()));
                spinnerQtd.setSelection(itemAtual.getQuantidade());
                imagem = itemAtual.getImagem();
                importarImagemGaleria(null);

                // Altera o texto do botão cancelar para "Excluir" caso o item esteja sendo editado
                btnCancelar.setText(R.string.excluir);

//                Log.d("Editar item", itemAtual.toString());
            }
        }

        this.criarToolbar();
        this.criarSpinner();
        this.atualizarListaAmigos();

        // Atribui o valor selecionado no spinner à variável campoQuantidade
        spinnerQtd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                campoQuantidade = String.valueOf(spinnerQtd.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 2:
                    atualizarListaAmigos();
                    break;

                case REQUISICAO_GALERIA:
                    importarImagemGaleria(data);
                    break;

                case REQUISICAO_CAMERA:
                    resultadoCapturarImagem(data);
                    break;

                default:
                    if (data.hasExtra("idDespesa")) {
                        try {
                            idDespesa = data.getExtras().getLong("idDespesa");
                        } catch (NullPointerException e) {
                            Log.d("Erro idDespesa", e.getMessage());
                        }
                    }

                    if (data.hasExtra("novoItem")) {
                        if (!data.getExtras().getBoolean("novoItem")) {
                            itemAtual = gson.fromJson(data.getExtras().getString("item"), Item.class);
                        }
                    }

                    break;
            }
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.iv_item:
                dialogImagem();
                break;

            case R.id.btn_nova_imagem:
                dialogImagem();
                break;

            case R.id.btn_cancelar_novo_item:
                if (novoItem) {
                    // Se for um novo item, apenas volta para NovaDespesa e finaliza a Activity AdicionarItem
                    onBackPressed();
                    finish();
                } else {
                    // Se o item estiver sendo editado, a opção cancelar passa a ser excluir
                    excluirItem();
                }
                break;

            case R.id.btn_salvar_novo_item:
                String descricao = etDescricaoItem.getText().toString().trim();
                String campoPreco = etPreco.getText().toString().trim();
                double preco = (campoPreco.isEmpty() ? 0.0 : parseDouble(campoPreco));
                int quantidade = (campoQuantidade.isEmpty() ? 1 : parseInt(campoQuantidade));

                // Valida os campos básicos do item
                if (validarItem(descricao, preco, quantidade)) {
                    ArrayList<Amigo> amigos = objetosAmigosSelecionados();

                    // Se a lista de amigos do item possuir pelo menos 1 amigo selecionado, então o item é válido
                    if (amigos.size() > 0) {
                        // Atribui os valores dos campos da activity ao objeto itemAtual
                        itemAtual.setDescricao(descricao);
                        itemAtual.setDespesa(idDespesa);
                        itemAtual.setPreco(preco);
                        itemAtual.setQuantidade(quantidade);

                        if (ivItem.getVisibility() == View.VISIBLE) {
                            itemAtual.setImagem(imagem);
                        } else {
                            itemAtual.setImagem(null);
                        }

                        int idItem;
                        if (novoItem) {
                            // Insere o novo item no banco
                            db.inserirDados("item", gson.toJson(itemAtual));
                            idItem = db.listarItensDespesa(idDespesa).get(db.listarItensDespesa(idDespesa).size() - 1).getId();
                        } else {
                            // Atualiza o item já existente
                            idItem = itemAtual.getId();
                            db.atualizarItem(itemAtual);
                            // Remove os amigos que estavam no item previamente para salvar a nova seleção no lugar
                            db.removerAmigosItem(idItem);
                        }

                        // Salva os amigos selecionados naquele item na tabela amigo_item
                        AmigoItem amigoItem;
                        double valorPagoAmigoEmItem;
                        for (Amigo amigo : amigos) {
                            valorPagoAmigoEmItem = ((preco * quantidade) / amigos.size());
                            amigoItem = new AmigoItem(amigo.getId(), idItem, valorPagoAmigoEmItem);
                            db.inserirDados("amigo_item", gson.toJson(amigoItem));
                        }

                        Intent intent = new Intent(AdicionarItem.this, NovaDespesa.class);

                        intent.putExtra("idDespesa", idDespesa);
                        intent.putExtra("itemAdicionado", true);
                        intent.putExtra("itemAdicionadoDescricao", itemAtual.getDescricao());

                        // Desativa o botão salvar para que o usuário não salve o item mais de uma vez ao tocar no botão várias vezes sem querer
                        btnSalvar.setClickable(false);

                        // Finaliza a activity e retorna para a NovaDespesa
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        Snackbar.make(findViewById(R.id.activity_adicionar_item),
                                R.string.selecione_amigo,
                                Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
        }
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
            new DialogSobre(AdicionarItem.this);

            return true;
        } else if (id == R.id.ver_amigos) {
            Intent intent = new Intent(AdicionarItem.this, ListaAmigos.class);

            intent.putExtra("verAmigos", true);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                ivItem.setEnabled(true);
            }
        }
    }

    /**
     * Popula um ListView de acordo com os amigos selecionados para a despesa.
     */
    public void atualizarListaAmigos() {
        listaAmigosItem = new ArrayList<>();

        for (Amigo amigo : db.listarAmigosDespesa(idDespesa)) {
            listaAmigosItem.add(amigo.getNome());
        }

        adaptador = new ArrayAdapter<>(AdicionarItem.this, android.R.layout.simple_list_item_multiple_choice, listaAmigosItem);
        lvAmigosItem.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvAmigosItem.setAdapter(adaptador);

        if (!novoItem) {
            ArrayList<Amigo> amigos = db.listarAmigosItem(itemAtual.getId());
            for (Amigo amigo : amigos) {
                for (int iterador = 0; iterador < listaAmigosItem.size(); iterador++) {
                    if (amigo.getNome().equals(listaAmigosItem.get(iterador))) {
                        lvAmigosItem.setItemChecked(iterador, true);
                    }
                }
            }
        }
    }

    /**
     * Cria a dialog para mostrar o progresso do carregamento da imagem.
     */
    public void criarDialogProgresso() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Carregando...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressBarStatus < 100) {
                    progressBarStatus += 30;

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progressBarbHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                if (progressBarStatus >= 100) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.dismiss();
                }

            }
        }).start();
    }

    /**
     * Cria o spinner que permite a seleção da quantidade de itens.
     */
    public void criarSpinner() {
        List<Integer> listaQtd = new ArrayList<>();

        // Define as opções disponíveis para o campo "quantidade" do item
        for (int i = 1; i < 100; i++) {
            listaQtd.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaQtd);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQtd.setAdapter(adapter);

        if (!novoItem) {
            // Caso o item esteja sendo editado, define o valor do spinner ao ser carregado
            spinnerQtd.setSelection(itemAtual.getQuantidade() - 1);
        }

        // Define a quantidade de opções disponíveis ao tocar no spinner
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinnerQtd);

            // Define o tamanho do popup em px
            popupWindow.setHeight(700);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            Log.d("Erro Spinner", e.getMessage());
        }


    }

    /**
     * Cria a toolbar personalizada do item.
     */
    public void criarToolbar() {
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("Erro Toolbar", e.getMessage());
        }

        if (novoItem) {
            getSupportActionBar().setTitle("Novo Item");
        } else {
            getSupportActionBar().setTitle("Editar " + itemAtual.getDescricao());
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

    /**
     * Cria a dialog com as opções de imagem.
     */
    public void dialogImagem() {
        new MaterialDialog.Builder(AdicionarItem.this)
                .title("Opções de Imagem")
                .items(R.array.adicionarImagens)
                .itemsIds(R.array.imgItemsIds)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        switch (position) {
                            case 0:
                                // Escolher imagem da galeria
                                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);//ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, REQUISICAO_GALERIA);
                                break;

                            case 1:
                                // Capturar uma nova imagem
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUISICAO_CAMERA);
                                break;

                            case 2:
                                // Remover imagem do ImageView
                                itemAtual.setImagem(null);
                                ivItem.setImageBitmap(null);
                                ivItem.setVisibility(View.GONE);
                                layoutBtnImagem.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                })
                .show();
    }

    /**
     * Cria uma dialog para confirmar a exclusão do item selecionado.
     */
    public void excluirItem() {
        String titulo = String.format(getResources().getString(R.string.excluir_algo), itemAtual.getDescricao());
        String textoDialog = String.format(getResources().getString(R.string.confirmar_exclusao_item), itemAtual.getDescricao(), db.getDespesa(idDespesa).getDescricao());

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(textoDialog)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.removerItemDespesa(idDespesa, itemAtual.getId());

                        Intent intent = new Intent(AdicionarItem.this, NovaDespesa.class);

                        intent.putExtra("idDespesa", idDespesa);
                        intent.putExtra("itemExcluidoDescricao", itemAtual.getDescricao());
                        intent.putExtra("itemExcluidoSucesso", true);

                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    /**
     * Importa uma imagem da galeria selecionada pelo usuário.
     *
     * @param imagemIntent intent que possui os dados de uma imagem
     */
    public void importarImagemGaleria(Intent imagemIntent) {
        Uri uriImagem = null;

        if (imagemIntent == null && imagem != null) {
            // Busca o endereço da imagem de acordo com seu nome no BD
            // Caso seja uma imagem selecionada da galeria, o caminho terá "content://" no início
            if (imagem.contains("content://")) {
                uriImagem = Uri.parse(imagem);
            } else {
                // Caso seja uma imagem capturada de "dentro" do app, um novo File é instanciado com seu caminho (variável imagem)
                File diretorioImagens = new File(imagem);
                uriImagem = Uri.fromFile(diretorioImagens);
            }
        } else {
            try {
                // Busca o endereço da imagem de acordo com a seleção do usuário na galeria do dispositivo
                uriImagem = imagemIntent.getData();
                // Atribui o caminho da imagem no dispositivo à variável imagem, para poder salvá-lo no banco
                imagem = uriImagem.toString();
            } catch (NullPointerException e) {
                Log.d("Erro Importar Imagem", e.getMessage());
            }
        }

        InputStream inputStream;

        if (uriImagem != null) {
            try {
                // Obtem um input stream de acordo com o URI da imagem
                inputStream = getContentResolver().openInputStream(uriImagem);
                // Obtem um bitmap a partir do stream
                Bitmap imagem = BitmapFactory.decodeStream(inputStream);
                // Mostra uma tela de progrosso enquanto carrega a imagem
                criarDialogProgresso();
                // Atribui a imagem criado ao ImageView
                ivItem.setImageBitmap(imagem);
                // Oculta o botão que exibe as opções de imagem
                layoutBtnImagem.setVisibility(View.GONE);
                // Exibe a ImageView
                ivItem.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
//                Log.d("Erro ao abrir imagem", e.getMessage());
                Snackbar.make(findViewById(R.id.activity_adicionar_item),
                        "Erro ao importar imagem!",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Verifica o que foi selecionado no ListView.
     *
     * @return uma lista com os objetos do tipo Amigo selecionados no ListView
     */
    public ArrayList<Amigo> objetosAmigosSelecionados() {
        SparseBooleanArray selecionados = lvAmigosItem.getCheckedItemPositions();
        ArrayList<Amigo> amigos = new ArrayList<>();
        ArrayList<Amigo> amigosDespesa = db.listarAmigosDespesa(idDespesa);
        String nomeAmigo;

        for (int iterador = 0; iterador < selecionados.size(); iterador++) {
            int posicao = selecionados.keyAt(iterador);
            if (selecionados.valueAt(iterador)) {
                nomeAmigo = adaptador.getItem(posicao);
                for (int i = 0; i < amigosDespesa.size(); i++) {
                    if (amigosDespesa.get(i).getNome().equals(nomeAmigo)) {
                        amigos.add(amigosDespesa.get(i));
                    }
                }
            }
        }

        return amigos;
    }

    /**
     * Salva a imagem capturada pela câmera na galeria e a coloca no ImageView.
     *
     * @param imagemIntent intent que possui os dados de uma imagem
     */
    public void resultadoCapturarImagem(Intent imagemIntent) {
        // Cria um bitmap de acordo com o resultado da captura da imagem
        Bitmap thumbnail = (Bitmap) imagemIntent.getExtras().get("data");

        // Define o caminho em que a imagem será salva
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Camera");
        // Define uma etiqueta de tempo para identificação única da imagem
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("pt", "BR")).format(new Date());
        // Define o nome do arquivo de imagem
        String nomeImagem = ARQUIVO_JPEG_PREFIXO + timeStamp + ARQUIVO_JPEG_SUFIXO;
        // Atribui o diretório completo à variável imagem, para salvá-lo no BD
        imagem = path + "/" + nomeImagem;

        OutputStream out;

        if (thumbnail != null) {
            try {
                // Cria um novo arquivo de imagem no caminho definido anteriormente
                File imagemF = new File(imagem);
                out = new FileOutputStream(imagemF);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.d("Erro Capturar Imagem", e.getMessage());
            }

            // Mostra uma tela de progrosso enquanto carrega a imagem
            criarDialogProgresso();

            // Define a largura máxima da imagem
            ivItem.setMaxWidth(200);
            // Atribui a imagem criado ao ImageView
            ivItem.setImageBitmap(thumbnail);
            // Oculta o botão que exibe as opções de imagem
            layoutBtnImagem.setVisibility(View.GONE);
            // Exibe a ImageView
            ivItem.setVisibility(View.VISIBLE);
        } else {
            Snackbar.make(findViewById(R.id.activity_adicionar_item), "Erro ao salvar imagem!", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Valida (e exibe mensagem de erro caso esteja inválido) os campos do item.
     *
     * @param descricao  String com a descrição do item
     * @param preco      campo do tipo double com o preço do item
     * @param quantidade valor do tipo int com a quantidade de itens
     * @return true se os campos forem válidos, false caso contrário
     */
    public boolean validarItem(String descricao, double preco, int quantidade) {
        if (descricao.isEmpty()) {
            Snackbar.make(findViewById(R.id.activity_adicionar_item),
                    "Por favor, preencha a descrição!",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (preco <= 0.0) {
            Snackbar.make(findViewById(R.id.activity_adicionar_item),
                    "Por favor, o preço deve ser maior do que R$0,00!",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (quantidade <= 0) {
            Snackbar.make(findViewById(R.id.activity_adicionar_item),
                    "Por favor, a quantidade deve ser maior do que 0!",
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
