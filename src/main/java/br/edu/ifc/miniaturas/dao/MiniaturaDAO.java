package br.edu.ifc.miniaturas.dao;

import br.edu.ifc.miniaturas.model.Categoria;
import br.edu.ifc.miniaturas.model.Imagem;
import br.edu.ifc.miniaturas.model.Miniatura;
import br.edu.ifc.miniaturas.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Todo o SQL do CRUD fica aqui. Só PreparedStatement (evita SQL injection). */
public class MiniaturaDAO {

    private static final String COLUNAS_LISTA =
        "id, categoria, quantidade, marca, modelo, cor, ano, (imagem IS NOT NULL) AS tem_imagem";

    // ---------- CREATE ----------
    public void inserir(Miniatura m, Imagem img) throws SQLException {
        String sql = "INSERT INTO miniatura "
                   + "(categoria, quantidade, marca, modelo, cor, ano, imagem, imagem_tipo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m.getCategoria().getId());
            ps.setInt(2, m.getQuantidade());
            ps.setString(3, m.getMarca());
            ps.setString(4, m.getModelo());
            ps.setString(5, m.getCor());
            ps.setInt(6, m.getAno());
            ps.setBytes(7, img.bytes());
            ps.setString(8, img.tipo());
            ps.executeUpdate();
        }
    }

    // ---------- READ ----------
    public List<Miniatura> listarTodas() throws SQLException {
        String sql = "SELECT " + COLUNAS_LISTA + " FROM miniatura ORDER BY id";
        List<Miniatura> lista = new ArrayList<>();
        try (Connection c = Conexao.obter();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Miniatura buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS_LISTA + " FROM miniatura WHERE id = ?";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Imagem buscarImagem(int id) throws SQLException {
        String sql = "SELECT imagem, imagem_tipo FROM miniatura WHERE id = ? AND imagem IS NOT NULL";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Imagem(rs.getBytes("imagem"), rs.getString("imagem_tipo")) : null;
            }
        }
    }

    // ---------- UPDATE ----------
    /**
     * Atualiza os dados. Imagem: se "nova" != null troca; senão, se "remover" apaga;
     * senão mantém a que já existe.
     */
    public void atualizar(Miniatura m, Imagem nova, boolean remover) throws SQLException {
        String sql = "UPDATE miniatura SET categoria = ?, quantidade = ?, marca = ?, modelo = ?, cor = ?, ano = ?"
                   + (nova != null ? ", imagem = ?, imagem_tipo = ?"
                      : remover ? ", imagem = NULL, imagem_tipo = NULL" : "")
                   + " WHERE id = ?";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setInt(i++, m.getCategoria().getId());
            ps.setInt(i++, m.getQuantidade());
            ps.setString(i++, m.getMarca());
            ps.setString(i++, m.getModelo());
            ps.setString(i++, m.getCor());
            ps.setInt(i++, m.getAno());
            if (nova != null) {
                ps.setBytes(i++, nova.bytes());
                ps.setString(i++, nova.tipo());
            }
            ps.setInt(i, m.getId());
            ps.executeUpdate();
        }
    }

    // ---------- DELETE ----------
    public void excluir(int id) throws SQLException {
        try (Connection c = Conexao.obter();
             PreparedStatement ps = c.prepareStatement("DELETE FROM miniatura WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Miniatura mapear(ResultSet rs) throws SQLException {
        Miniatura m = new Miniatura();
        m.setId(rs.getInt("id"));
        m.setCategoria(Categoria.deId(rs.getInt("categoria")));
        m.setQuantidade(rs.getInt("quantidade"));
        m.setMarca(rs.getString("marca"));
        m.setModelo(rs.getString("modelo"));
        m.setCor(rs.getString("cor"));
        m.setAno(rs.getInt("ano"));
        m.setTemImagem(rs.getBoolean("tem_imagem"));
        return m;
    }
}
