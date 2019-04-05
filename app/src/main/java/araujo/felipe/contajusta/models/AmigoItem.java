package araujo.felipe.contajusta.models;

/**
 * Define um objeto do tipo AmigoItem para salvar a ligação entre Amigo e Item no BD.
 */
public class AmigoItem {
    private double valorPagoAmigo;
    private int id;
    private int idAmigo;
    private int idItem;

    /**
     * Instancia um novo objeto AmigoItem.
     *
     * @param idAmigo        o id do amigo
     * @param idItem         o id do item
     * @param valorPagoAmigo o valor pago pelo amigo
     */
    public AmigoItem(int idAmigo, int idItem, double valorPagoAmigo) {
        this.idAmigo = idAmigo;
        this.idItem = idItem;
        this.valorPagoAmigo = valorPagoAmigo;
    }

    /**
     * Obtém o id do AmigoItem.
     *
     * @return o id obtido
     */
    public int getId() {
        return id;
    }

    /**
     * Define o id do AmigoItem.
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
     * Obtém o id do Item.
     *
     * @return o id do Item
     */
    public int getIdItem() {
        return idItem;
    }

    /**
     * Define o id do Item.
     *
     * @param idItem id do Item do tipo int
     */
    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    /**
     * Obtém o valor que deve ser pago pelo amigo.
     *
     * @return double com o valor
     */
    public double getValorPagoAmigo() {
        return valorPagoAmigo;
    }

    /**
     * Atribui o valor que deve ser pago pelo amigo ao Objeto.
     *
     * @param valorPagoAmigo o valor que deve ser pago amigo
     */
    public void setValorPagoAmigo(double valorPagoAmigo) {
        this.valorPagoAmigo = valorPagoAmigo;
    }

    @Override
    public String toString() {
        return "AmigoItem = [id=" + this.id + ", idAmigo=" + this.idAmigo + ", idItem=" + this.idItem +
                ", valorPagoAmigo=" + this.valorPagoAmigo + "]";
    }
}
