package br.edu.ifc.miniaturas.model;

public class Miniatura {
    private int id;
    private Categoria categoria;
    private int quantidade;
    private String marca;
    private String modelo;
    private String cor;
    private int ano;
    private boolean temImagem; // a imagem em si é servida por /imagem?id=...

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public boolean isTemImagem() { return temImagem; }
    public void setTemImagem(boolean temImagem) { this.temImagem = temImagem; }
}
