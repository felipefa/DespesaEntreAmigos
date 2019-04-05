package araujo.felipe.contajusta.models;

/**
 * Classe que define um objeto Amigo para salvá-lo na tabela amigo do BD.
 */
public class Amigo {
    private int id;
    private String nome;

    /**
     * Instancia um novo objeto Amigo.
     */
    public Amigo() {}

    /**
     * Instancia um novo objeto Amigo já definindo o seu nome na instanciação.
     *
     * @param nome nome do tipo String
     */
    public Amigo(String nome) {
        super();
        this.nome = nome;
    }

    /**
     * Obtém o id do Amigo.
     *
     * @return id do Amigo
     */
    public int getId() {
        return this.id;
    }

    /**
     * Define o id do Amigo.
     *
     * @param id id do tipo int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém o nome do Amigo.
     *
     * @return nome do Amigo
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Define o nome do Amigo.
     *
     * @param nome nome do tipo String
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Amigo [id=" + this.id + ", nome=" + this.nome + "]";
    }

}
