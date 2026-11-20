package br.edu.ifc.miniaturas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Conexão JDBC com o MySQL do XAMPP (padrão: usuário root, sem senha). */
public final class Conexao {

    private static final String URL =
        "jdbc:mysql://localhost:3306/crud_miniaturas"
        + "?useSSL=false&allowPublicKeyRetrieval=true"
        + "&characterEncoding=UTF-8&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Conexao() { }

    public static Connection obter() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
