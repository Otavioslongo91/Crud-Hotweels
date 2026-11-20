package br.edu.ifc.miniaturas.controller;

import br.edu.ifc.miniaturas.dao.MiniaturaDAO;
import br.edu.ifc.miniaturas.model.Imagem;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** Entrega a imagem de uma miniatura: <img src="/miniaturas/imagem?id=3">. */
@WebServlet("/imagem")
public class ImagemServlet extends HttpServlet {

    private final MiniaturaDAO dao = new MiniaturaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Imagem img = dao.buscarImagem(id);
            if (img == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setContentType(img.tipo());
            resp.setContentLength(img.bytes().length);
            resp.setHeader("X-Content-Type-Options", "nosniff");
            resp.setHeader("Cache-Control", "no-cache"); // a imagem pode ser trocada na edição
            resp.getOutputStream().write(img.bytes());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            throw new ServletException("Erro ao ler a imagem", e);
        }
    }
}
