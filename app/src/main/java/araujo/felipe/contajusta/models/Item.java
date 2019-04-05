package araujo.felipe.contajusta.models;

/**
 * Classe que define um objeto Item para salvá-lo na tabela item do BD.
 */
public class Item {
    private double preco;
    private int id;
    private int quantidade;
    private long despesa;
    private String descricao;
    private String imagem;

    /**
     * Instancia um novo Item.
     */
    public Item() {}

    /**
     * Obtém o id do Item.
     *
     * @return o id do Item
     */
    public int getId() {
        return this.id;
    }

    /**
     * Define o id do Item.
     *
     * @param id id do Item do tipo int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém a descrição salva.
     *
     * @return a descricao do item
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Atribui a descrição do item ao objeto.
     *
     * @param descricao uma string com a descrição
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Obtém o id da Despesa.
     *
     * @return o id da Despesa
     */
    public long getDespesa() {
        return this.despesa;
    }

    /**
     * Define o id da Despesa.
     *
     * @param despesa id da Despesa do tipo long
     */
    public void setDespesa(long despesa) {
        this.despesa = despesa;
    }

    /**
     * Obtém o preço do item.
     *
     * @return um double com o preço do item
     */
    public double getPreco() {
        return this.preco;
    }

    /**
     * Atribui o preço do item ao objeto.
     *
     * @param preco o preço unitário do item
     */
    public void setPreco(double preco) {
        this.preco = preco;
    }

    /**
     * Obtém a quantidade de itens.
     *
     * @return a quantidade de itens
     */
    public int getQuantidade() {
        return this.quantidade;
    }

    /**
     * Atribui a quantidade de itens ao objeto.
     *
     * @param quantidade a quantidade de itens
     */
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    /**
     * Obtém o caminho completo da imagem com seu nome no aparelho.
     *
     * @return o caminho da imagem no aparelho
     */
    public String getImagem() {
        return imagem;
    }

    /**
     * Define o caminho completo da imagem com nome no aparelho.
     *
     * @param imagem o caminho da imagem com seu respectivo nome
     */
    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @Override
    public String toString() {
        return "Item [id=" + this.id + ", descricao=" + this.descricao + ", despesa=" + this.despesa +
                ", preco=" + this.preco + ", quantidade=" + this.quantidade + ", imagem=" + this.imagem + "]";
    }

}
