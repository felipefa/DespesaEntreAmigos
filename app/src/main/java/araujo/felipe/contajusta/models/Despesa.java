package araujo.felipe.contajusta.models;

/**
 * Classe que define um objeto Despesa para salvá-lo na tabela despesa do BD.
 */
public class Despesa {
    private Long id;
    private String descricao;
    private String dia;
    private String valorTotal;

    /**
     * Instancia uma nova Despesa.
     */
    public Despesa() {
    }

    /**
     * Instancia uma nova Despesa já definindo seu id.
     *
     * @param id o id da Despesa
     */
    public Despesa(long id) {
        this.id = id;
    }

    /**
     * Obtém o id da Despesa.
     *
     * @return o id da Despesa
     */
    public long getId() {
        return this.id;
    }

    /**
     * Define o id da Despesa.
     *
     * @param id id da Despesa do tipo long
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Obtém a descrição salva.
     *
     * @return a descricao da despesa
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Atribui a descrição da despesa ao objeto.
     *
     * @param descricao uma string com a descrição
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Obtém a data salva ao instanciar o objeto.
     *
     * @return uma String com a data salva
     */
    public String getDia() {
        return dia;
    }

    /**
     * Atribui a data em que objeto foi criado.
     *
     * @param dia uma String com a data no formato dd/MM/yyyy - HH:mm:ss
     */
    public void setDia(String dia) {
        this.dia = dia;
    }

    /**
     * Obtém o valor total da despesa.
     *
     * @return String com o valor total da despesa
     */
    public String getValorTotal() {
        return this.valorTotal;
    }

    /**
     * Atribui o valor total da despesa ao objeto.
     *
     * @param valorTotal o valor total da despesa
     */
    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public String toString() {
        return "Despesa [id=" + this.id + ", descricao=" + this.descricao + ", dia=" +
                this.dia + ", valor total=" + this.valorTotal + "]";
    }

}
