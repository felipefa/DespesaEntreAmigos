package araujo.felipe.contajusta.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import araujo.felipe.contajusta.models.AmigoDespesa;
import araujo.felipe.contajusta.models.AmigoItem;
import araujo.felipe.contajusta.models.Despesa;
import araujo.felipe.contajusta.models.Item;
import araujo.felipe.contajusta.models.Amigo;

/**
 * Classe responsável pelo Database helper, é nela que os objetos Amigo, Despesa, Item,
 * AmigoItem e AmigoDespesa são usados para salvar os dados do app no BD.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DespesaEntreAmigosDB.db";

    // Tabela amigo
    private static final String TABLE_AMIGO = "amigo";
    // Nome das colunas da tabela amigo
    private static final String KEY_ID_AMIGO = "id";
    private static final String KEY_NOME_AMIGO = "nome";
    private String[] camposTabelaAmigo = {KEY_ID_AMIGO, KEY_NOME_AMIGO};

    // Tabela despesa
    private static final String TABLE_DESPESA = "despesa";
    // Nome das colunas da tabela despesa
    private static final String KEY_ID_DESPESA = "id";
    private static final String KEY_DIA_DESPESA = "dia";
    private static final String KEY_DESCRICAO_DESPESA = "descricao";
    private static final String KEY_VALOR_TOTAL_DESPESA = "valor_total";
    private String[] camposTabelaDespesa = {KEY_ID_DESPESA, KEY_DIA_DESPESA, KEY_DESCRICAO_DESPESA, KEY_VALOR_TOTAL_DESPESA};

    // Tabela item
    private static final String TABLE_ITEM = "item";
    // Nome das colunas da tabela item
    private static final String KEY_ID_ITEM = "id";
    private static final String KEY_DESCRICAO_ITEM = "descricao";
    private static final String KEY_PRECO_ITEM = "preco";
    private static final String KEY_QUANTIDADE_ITEM = "quantidade";
    private static final String KEY_DESPESA_ITEM = "id_despesa";
    private static final String KEY_IMAGEM_ITEM = "imagem";
    private String[] camposTabelaItem = {KEY_ID_ITEM, KEY_DESCRICAO_ITEM, KEY_PRECO_ITEM, KEY_QUANTIDADE_ITEM, KEY_DESPESA_ITEM, KEY_IMAGEM_ITEM};

    // Tabela amigo_despesa
    private static final String TABLE_AMIGO_DESPESA = "amigo_despesa";
    // Nome das colunas da tabela amigo_despesa
    private static final String KEY_ID_AMIGO_DESPESA = "id";
    private static final String KEY_AMIGO_AMIGO_DESPESA = "id_amigo";
    private static final String KEY_DESPESA_AMIGO_DESPESA = "id_despesa";

    // Tabela amigo_item
    private static final String TABLE_AMIGO_ITEM = "amigo_item";
    // Nome das colunas da tabela amigo_item
    private static final String KEY_ID_AMIGO_ITEM = "id";
    private static final String KEY_AMIGO_AMIGO_ITEM = "id_amigo";
    private static final String KEY_ITEM_AMIGO_ITEM = "id_item";
    private static final String KEY_VALOR_PAGO_AMIGO_ITEM = "valor_pago_amigo";

    /**
     * Instancia um novo DatabaseHelper.
     *
     * @param context o contexto (activity) em que o DatabaseHelper é instanciado
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Cria tabela amigo
        String CREATE_TABLE_AMIGO = "CREATE TABLE " + TABLE_AMIGO + " ( " +
                KEY_ID_AMIGO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NOME_AMIGO + " TEXT " + " );";
        db.execSQL(CREATE_TABLE_AMIGO);

        // Cria tabela despesa
        String CREATE_TABLE_DESPESA = "CREATE TABLE " + TABLE_DESPESA + " ( " +
                KEY_ID_DESPESA + " INTEGER PRIMARY KEY, " +
                KEY_DIA_DESPESA + " TEXT, " +
                KEY_DESCRICAO_DESPESA + " TEXT, " +
                KEY_VALOR_TOTAL_DESPESA + " TEXT " + " );";
        db.execSQL(CREATE_TABLE_DESPESA);

        // Cria tabela item
        String CREATE_TABLE_ITEM = "CREATE TABLE " + TABLE_ITEM + " ( " +
                KEY_ID_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_DESCRICAO_ITEM + " TEXT, " +
                KEY_PRECO_ITEM + " REAL, " +
                KEY_QUANTIDADE_ITEM + " INTEGER, " +
                KEY_DESPESA_ITEM + " INTEGER, " +
                KEY_IMAGEM_ITEM + " TEXT " + " );";
        db.execSQL(CREATE_TABLE_ITEM);

        // Cria tabela amigo_despesa
        String CREATE_TABLE_AMIGO_DESPESA = "CREATE TABLE " + TABLE_AMIGO_DESPESA + "( " +
                KEY_ID_AMIGO_DESPESA + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_AMIGO_AMIGO_DESPESA + " INTEGER, " +
                KEY_DESPESA_AMIGO_DESPESA + " INTEGER, " +
                "FOREIGN KEY (" + KEY_AMIGO_AMIGO_DESPESA + ") REFERENCES " + TABLE_AMIGO + "(" + KEY_ID_AMIGO + "), " +
                "FOREIGN KEY (" + KEY_DESPESA_AMIGO_DESPESA + ") REFERENCES " + TABLE_DESPESA + "(" + KEY_ID_DESPESA + ")" + " );";
        db.execSQL(CREATE_TABLE_AMIGO_DESPESA);

        // Cria tabela amigo_item
        String CREATE_TABLE_AMIGO_ITEM = "CREATE TABLE " + TABLE_AMIGO_ITEM + "( " +
                KEY_ID_AMIGO_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_AMIGO_AMIGO_ITEM + " INTEGER, " +
                KEY_ITEM_AMIGO_ITEM + " INTEGER, " +
                KEY_VALOR_PAGO_AMIGO_ITEM + " REAL, " +
                "FOREIGN KEY (" + KEY_AMIGO_AMIGO_ITEM + ") REFERENCES " + TABLE_AMIGO + "(" + KEY_ID_AMIGO + "), " +
                "FOREIGN KEY (" + KEY_ITEM_AMIGO_ITEM + ") REFERENCES " + TABLE_ITEM + "(" + KEY_ID_ITEM + ")" + " );";
        db.execSQL(CREATE_TABLE_AMIGO_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS ";

        db.execSQL(DROP_TABLE + TABLE_AMIGO);
        db.execSQL(DROP_TABLE + TABLE_DESPESA);
        db.execSQL(DROP_TABLE + TABLE_ITEM);
        db.execSQL(DROP_TABLE + TABLE_AMIGO_DESPESA);
        db.execSQL(DROP_TABLE + TABLE_AMIGO_ITEM);

        this.onCreate(db);
    }

    /**
     * Atualiza a descrição e o valor total em uma entrada da tabela despesa
     * com o id correspondente.
     *
     * @param despesa a despesa que contem os dados que serão atualizados
     */
    public void atualizarDespesa(Despesa despesa) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRICAO_DESPESA, despesa.getDescricao());
        values.put(KEY_VALOR_TOTAL_DESPESA, despesa.getValorTotal());

        db.update(TABLE_DESPESA,
                values,
                KEY_ID_DESPESA + " = ?",
                new String[]{String.valueOf(despesa.getId())});

        db.close();
    }

    /**
     * Atualiza todos os campos de uma entrada da tabela item,
     * exceto o id do item e o id da despesa a qual ele faz parte.
     *
     * @param item o item que contem os dados que serão atualizados
     */
    public void atualizarItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRICAO_ITEM, item.getDescricao());
        values.put(KEY_PRECO_ITEM, item.getPreco());
        values.put(KEY_QUANTIDADE_ITEM, item.getQuantidade());
        values.put(KEY_IMAGEM_ITEM, item.getImagem());

        db.update(TABLE_ITEM,
                values,
                KEY_ID_ITEM + " = ?",
                new String[]{String.valueOf(item.getId())});

        db.close();
    }

    /**
     * Calcula o valor a ser pago pelo amigo em uma despesa de acordo com seus respectivos ids.
     *
     * Para fazer o cálculo, é feita a soma do valor a ser pago pelo amigo, determinado pelo seu id,
     * em cada item que faz parte daquela despesa, também determinada pelo seu id.
     *
     * @param idAmigo   o id do amigo que terá seu valor a ser pago calculado
     * @param idDespesa o id da despesa em que o amigo faz parte
     * @return um double com o valor a ser pago calculado
     */
    public double calcularValorPagoAmigoEmDespesa(int idAmigo, long idDespesa) {
        String query = " SELECT " + TABLE_AMIGO_ITEM + "." + KEY_VALOR_PAGO_AMIGO_ITEM +
                " FROM " + TABLE_AMIGO_ITEM +
                " INNER JOIN " + TABLE_ITEM + " ON " +
                TABLE_ITEM + "." + KEY_ID_ITEM + " = " + TABLE_AMIGO_ITEM + "." + KEY_ITEM_AMIGO_ITEM +
                " WHERE " + TABLE_AMIGO_ITEM + "." + KEY_AMIGO_AMIGO_ITEM + " = " + String.valueOf(idAmigo) +
                " AND " + TABLE_ITEM + "." + KEY_DESPESA_ITEM + " = " + String.valueOf(idDespesa);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        double valor = 0.0;
        if (cursor.moveToFirst()) {
            do {
                valor += cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return valor;
    }

    /**
     * Calcula o valor total da despesa a partir da soma do preço total de cada item,
     * obtido através do método listarPrecoItem.
     *
     * @param idDespesa o id da despesa que terá seu valor total calculado
     * @return o valor total da despesa
     */
    public double calcularValorTotalDespesa(long idDespesa) {
        Cursor cursor = listarPrecoItem(idDespesa);
        double valorTotal = 0.0;

        if (cursor.moveToFirst()) {
            do {
                valorTotal += cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return valorTotal;
    }

    /**
     * Exclui uma entrada da tabela amigo.
     *
     * @param amigo o amigo que será excluído da tabela
     */
    public void excluirAmigo(Amigo amigo) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_AMIGO,
                KEY_ID_AMIGO + " = ?",
                new String[]{String.valueOf(amigo.getId())});

        db.close();

//        Log.d("excluirAmigo", amigo.toString());
    }

    /**
     * Exclui uma entrada da tabela despesa, após remover os itens e amigos associados a ela.
     *
     * @param idDespesa o id da despesa que será excluída da tabela
     */
    public void excluirDespesa(long idDespesa) {
        removerItemDespesa(idDespesa, -1);
        removerAmigosDespesa(idDespesa);

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_DESPESA,
                KEY_ID_DESPESA + " = ?",
                new String[]{String.valueOf(idDespesa)});

        db.close();

//        Log.d("excluirDespesa", despesa.toString());
    }

    /**
     * Exclui todas as despesas do BD sem verificar dependências.
     */
    public void excluirTodasDespesas() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_DESPESA);

        db.close();
    }

    /**
     * Exclui todos os amigos do BD sem verificar dependências.
     */
    public void excluirTodosAmigos() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_AMIGO);

        db.close();
    }

    /**
     * Exclui todos os itens do BD sem verificar dependências.
     */
    public void excluirTodosItens() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_ITEM);

        db.close();
    }

    /**
     * Seleciona um amigo usando seu id através do método listarAmigosApp.
     *
     * @param id o id do amigo que será buscado
     * @return o Amigo encontrado
     */
    public Amigo getAmigo(int id) {
        return this.listarAmigosApp().get(id);
    }

    /**
     * Seleciona uma despesa usando seu id através do método listarDespesas.
     *
     * @param id o id da despesa que será buscada
     * @return a Despesa encontrada
     */
    public Despesa getDespesa(long id) {
        for (Despesa despesa:listarDespesas()) {
            if (despesa.getId() == id) {
                return despesa;
            }
        }

        return null;
    }

    /**
     * Insere dados conforme os parâmetros passados.
     *
     * @param tabela a tabela em que os dados serão inseridos
     * @param dados  o json de um objeto do tipo Amigo, Despesa, Item, AmigoDespesa ou AmigoItem
     */
    public void inserirDados(String tabela, String dados) {
        ContentValues values = new ContentValues();
        Gson gson = new Gson();
        SQLiteDatabase db = this.getWritableDatabase();

        switch (tabela) {
            case TABLE_AMIGO:
                Amigo amigo = gson.fromJson(dados, Amigo.class);

                values.put(KEY_NOME_AMIGO, amigo.getNome());

//                Log.d("BD inserir amigo", amigo.toString());
                break;

            case TABLE_DESPESA:
                Despesa despesa = gson.fromJson(dados, Despesa.class);

                values.put(KEY_ID_DESPESA, despesa.getId());
                values.put(KEY_DIA_DESPESA, despesa.getDia());
                values.put(KEY_DESCRICAO_DESPESA, despesa.getDescricao());

//                Log.d("BD inserir despesa", despesa.toString());
                break;

            case TABLE_ITEM:
                Item item = gson.fromJson(dados, Item.class);

                values.put(KEY_DESCRICAO_ITEM, item.getDescricao());
                values.put(KEY_PRECO_ITEM, item.getPreco());
                values.put(KEY_QUANTIDADE_ITEM, item.getQuantidade());
                values.put(KEY_DESPESA_ITEM, item.getDespesa());
                values.put(KEY_IMAGEM_ITEM, item.getImagem());

//                Log.d("BD inserir item", item.toString());
                break;

            case TABLE_AMIGO_DESPESA:
                AmigoDespesa amigoDespesa = gson.fromJson(dados, AmigoDespesa.class);

                values.put(KEY_AMIGO_AMIGO_DESPESA, amigoDespesa.getIdAmigo());
                values.put(KEY_DESPESA_AMIGO_DESPESA, amigoDespesa.getIdDespesa());

//                Log.d("BD inserir amigoDespesa", amigoDespesa.toString());
                break;

            case TABLE_AMIGO_ITEM:
                AmigoItem amigoItem = gson.fromJson(dados, AmigoItem.class);

                values.put(KEY_AMIGO_AMIGO_ITEM, amigoItem.getIdAmigo());
                values.put(KEY_ITEM_AMIGO_ITEM, amigoItem.getIdItem());
                values.put(KEY_VALOR_PAGO_AMIGO_ITEM, amigoItem.getValorPagoAmigo());

//                Log.d("BD inserir amigoItem", amigoItem.toString());
                break;

            default:
                Log.d("Erro ao inserir no BD", dados);
        }

        db.insert(tabela, null, values);
        db.close();
    }

    /**
     * Seleciona todas as entradas da tabela amigos, ou seja, todos os amigos do app.
     *
     * @return todos os amigos do app numa lista de objetos do tipo Amigo
     */
    public ArrayList<Amigo> listarAmigosApp() {
        return percorrerCursorAmigo(listarDados(TABLE_AMIGO, camposTabelaAmigo));
    }

    /**
     * Seleciona os amigos da despesa através de uma consulta usando as tabelas amigo e amigo_despesa.
     *
     * @param idDespesa o id da despesa em que os amigos serão listados
     * @return a lista com os amigos que fazem parte da despesa informada
     */
    public ArrayList<Amigo> listarAmigosDespesa(long idDespesa) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + TABLE_AMIGO + "." + KEY_ID_AMIGO + ", " + TABLE_AMIGO + "." + KEY_NOME_AMIGO +
                " FROM " + TABLE_AMIGO +
                " INNER JOIN " + TABLE_AMIGO_DESPESA + " ON " + TABLE_AMIGO + "." + KEY_ID_AMIGO + " = " + TABLE_AMIGO_DESPESA + "." + KEY_AMIGO_AMIGO_DESPESA +
                " WHERE " + TABLE_AMIGO_DESPESA + "." + KEY_DESPESA_AMIGO_DESPESA + " = " + String.valueOf(idDespesa);

        return percorrerCursorAmigo(db.rawQuery(query, null));
    }

    /**
     * Seleciona os amigos do item através de uma consulta usando as tabelas amigo e amigo_item.
     *
     * @param idItem o id do item em que os amigos serão listados
     * @return a lista com os amigos que fazem parte do item informado
     */
    public ArrayList<Amigo> listarAmigosItem(int idItem) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + TABLE_AMIGO + "." + KEY_ID_AMIGO + ", " + TABLE_AMIGO + "." + KEY_NOME_AMIGO +
                " FROM " + TABLE_AMIGO +
                " INNER JOIN " + TABLE_AMIGO_ITEM + " ON " + TABLE_AMIGO + "." + KEY_ID_AMIGO + " = " + TABLE_AMIGO_ITEM + "." + KEY_AMIGO_AMIGO_ITEM +
                " WHERE " + TABLE_AMIGO_ITEM + "." + KEY_ITEM_AMIGO_ITEM + " = " + String.valueOf(idItem);

        return percorrerCursorAmigo(db.rawQuery(query, null));
    }

    /**
     * Lista os dados de uma tabela, de acordo com os parâmetros passados, em um Cursor.
     *
     * @param tabela a tabela em que os dados devem ser consultados
     * @param campos array String com os campos (colunas) da tabela que será consultada
     * @return o cursor com os dados encontrados na consulta da tabela informada
     */
    private Cursor listarDados(String tabela, String[] campos) {
        String select = "";

        for (String campo : campos) {
            select = select.concat(campo + ",");
        }
        select = select.substring(0, select.length() - 1);

        String query = "SELECT " + select + " FROM " + tabela;

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(query, null);
    }

    /**
     * Lista as despesas do app através de um Cursor gerado pelo método listarDados.
     *
     * @return a lista com as despesas do app
     */
    public ArrayList<Despesa> listarDespesas() {
        ArrayList<Despesa> despesas = new ArrayList<>();

        Cursor cursor = listarDados(TABLE_DESPESA, camposTabelaDespesa);

        Despesa despesa;
        if (cursor.moveToFirst()) {
            do {
                despesa = new Despesa();
                despesa.setId(cursor.getLong(0));
                despesa.setDia(cursor.getString(1));
                despesa.setDescricao(cursor.getString(2));
                despesa.setValorTotal(cursor.getString(3));

                despesas.add(despesa);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return despesas;
    }

    /**
     * Lista os itens da despesa informada através de um Cursor gerado pelo método listarDados.
     *
     * @param idDespesa o id da despesa a qual os itens fazem parte
     * @return a lista de itens que fazem parte da despesa informada
     */
    public ArrayList<Item> listarItensDespesa(long idDespesa) {
        ArrayList<Item> itens = new ArrayList<>();

        Cursor cursor = listarDados(TABLE_ITEM, camposTabelaItem);

        Item item;
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getLong(4) == idDespesa) {
                    item = new Item();
                    item.setId(cursor.getInt(0));
                    item.setDescricao(cursor.getString(1));
                    item.setPreco(cursor.getDouble(2));
                    item.setQuantidade(cursor.getInt(3));
                    item.setDespesa(cursor.getLong(4));
                    item.setImagem(cursor.getString(5));

                    itens.add(item);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return itens;
    }

    /**
     * Lista o preço total de itens da despesa informada.
     *
     * @param idDespesa o id da despesa a qual os itens fazem parte
     * @return o Cursor com os preços dos itens encontrados na consulta
     */
    private Cursor listarPrecoItem(long idDespesa) {
        String query = "SELECT " + TABLE_ITEM + "." + KEY_PRECO_ITEM + "*" + TABLE_ITEM + "." + KEY_QUANTIDADE_ITEM +
                " FROM " + TABLE_ITEM +
                " INNER JOIN " + TABLE_DESPESA + " ON " + TABLE_ITEM + "." + KEY_DESPESA_ITEM + " = " + TABLE_DESPESA + "." + KEY_ID_DESPESA +
                " WHERE " + TABLE_DESPESA + "." + KEY_ID_DESPESA + " = " + String.valueOf(idDespesa);

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(query, null);
    }

    /**
     * Percorre o Cursor informado por parâmetro para criar uma lista de objetos do tipo Amigo.
     *
     * @param cursor o Cursor que será percorrido
     * @return a lista de amigos montada a partir dos dados do Cursor
     */
    private ArrayList<Amigo> percorrerCursorAmigo(Cursor cursor) {
        ArrayList<Amigo> amigos = new ArrayList<>();

        Amigo amigo;
        if (cursor.moveToFirst()) {
            do {
                amigo = new Amigo();
                amigo.setId(cursor.getInt(0));
                amigo.setNome(cursor.getString(1));

                amigos.add(amigo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return amigos;
    }

    /**
     * Remove os amigos da despesa especificada, removendo todas as entradas com o mesmo
     * id da despesa passado por parâmetro, da tabela amigo_despesa.
     *
     * @param idDespesa o id da despesa que terá seus amigos removidos
     */
    public void removerAmigosDespesa(long idDespesa) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_AMIGO_DESPESA,
                KEY_DESPESA_AMIGO_DESPESA + " = ?",
                new String[]{String.valueOf(idDespesa)});

        db.close();
    }

    /**
     * Remove os amigos do item especificado, removendo todas as entradas com o mesmo id
     * do item passado por parâmetro, da tabela amigo_item.
     *
     * @param idItem o id do item que terá seus amigos removidos
     */
    public void removerAmigosItem(int idItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_AMIGO_ITEM,
                KEY_ITEM_AMIGO_ITEM + " = ?",
                new String[]{String.valueOf(idItem)});

        db.close();
    }

    /**
     * Remove os amigos do item informado antes de removê-lo da despesa. Caso o id do item seja -1,
     * todos os itens da despesa informada serão removidos.
     *
     * @param idDespesa o id da despesa que terá seus itens removidos
     * @param idItem    o id do item que será removido ou -1 para remover todos os itens da despesa
     */
    public void removerItemDespesa(long idDespesa, int idItem) {
        SQLiteDatabase db;

        if (idItem == -1) {
            // Remove os amigos do item antes de remover o item da despesa
            for (Item item : listarItensDespesa(idDespesa)) {
                removerAmigosItem(item.getId());
            }

            // Remove todos os itens da despesa
            db = this.getWritableDatabase();
            db.delete(TABLE_ITEM,
                    KEY_DESPESA_ITEM + " = ?",
                    new String[]{String.valueOf(idDespesa)});
        } else {
            // Remove os amigos do item especificado antes de removê-lo da despesa
            removerAmigosItem(idItem);

            // Remove apenas o item especificado da despesa
            db = this.getWritableDatabase();
            String query = "DELETE FROM " + TABLE_ITEM +
                    " WHERE " + TABLE_ITEM + "." + KEY_ID_ITEM + " = " + String.valueOf(idItem) +
                    " AND " + TABLE_ITEM + "." + KEY_DESPESA_ITEM + " = " + String.valueOf(idDespesa);
            db.execSQL(query);
        }

        db.close();
    }

}