package araujo.felipe.contajusta.models;

/**
 * Classe que define um objeto AmigoDespesa para salvar a ligação entre Amigo e Despesa no BD.
 */
public class AmigoDespesa {
    private int id;
    private int idAmigo;
    private long idDespesa;

    /**
     * Instancia um novo objeto AmigoDespesa com id do Amigo e id da Despesa.
     *
     * @param idAmigo   id do Amigo
     * @param idDespesa id da Despesa
     */
    public AmigoDespesa(int idAmigo, long idDespesa) {
        this.idAmigo = idAmigo;
        this.idDespesa = idDespesa;
    }

    /**
     * Obtém o id do AmigoDespesa.
     *
     * @return o id obtido
     */
    public int getId() {
        return id;
    }

    /**
     * Define o id do AmigoDespesa.
     *
     * @param id um int que será o id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém o id do Amigo.
     *
     * @return o id do Amigo
     */
    public int getIdAmigo() {
        return idAmigo;
    }

    /**
     * Define o id do Amigo.
     *
     * @param idAmigo id do Amigo do tipo int
     */
    public void setIdAmigo(int idAmigo) {
        this.idAmigo = idAmigo;
    }

    /**
     * Obtém o id da Despesa.
     *
     * @return o id da Despesa
     */
    public long getIdDespesa() {
        return idDespesa;
    }

    /**
     * Define o id da Despesa.
     *
     * @param idDespesa id da Despesa do tipo long
     */
    public void setIdDespesa(long idDespesa) {
        this.idDespesa = idDespesa;
    }

    @Override
    public String toString() {
        return "AmigoDespesa = [id=" + this.id + ", idAmigo=" + this.idAmigo +
                ", idDespesa=" + this.idDespesa + "]";
    }
}
