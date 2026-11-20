package br.edu.ifc.miniaturas.model;

/** Substitui os 4 arrays (Carrinho1..4) do código JavaScript. */
public enum Categoria {
    MAINLINE(1, "HotWheels Mainline"),
    PREMIUM(2, "HotWheels Premium"),
    MATCHBOX(3, "Matchbox"),
    OUTRAS(4, "Outras Marcas");

    private final int id;
    private final String nome;

    Categoria(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    public static Categoria deId(int id) {
        for (Categoria c : values()) {
            if (c.id == id) return c;
        }
        throw new IllegalArgumentException("Categoria inválida: " + id);
    }
}
