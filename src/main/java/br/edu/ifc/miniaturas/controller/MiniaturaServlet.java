package br.edu.ifc.miniaturas.controller;

import br.edu.ifc.miniaturas.dao.MiniaturaDAO;
import br.edu.ifc.miniaturas.model.Categoria;
import br.edu.ifc.miniaturas.model.Imagem;
import br.edu.ifc.miniaturas.model.Miniatura;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Controller do CRUD.
 *   GET  /miniaturas                  -> READ   (lista as 4 tabelas)
 *   GET  /miniaturas?acao=editar&id=  -> abre o formulário de edição
 *   POST acao=criar                   -> CREATE
 *   POST acao=atualizar               -> UPDATE (dados e/ou imagem)
 *   POST acao=excluir                 -> DELETE
 */
@WebServlet("/miniaturas")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)
public class MiniaturaServlet extends HttpServlet {

    private static final String VIEW_LISTA  = "/WEB-INF/views/listar.jsp";
    private static final String VIEW_EDICAO = "/WEB-INF/views/editar.jsp";
    private static final String[] CAMPOS = {"categoria", "quantidade", "marca", "modelo", "cor", "ano"};

    private static final Set<String> TIPOS_IMAGEM =
        Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final Pattern APENAS_LETRAS = Pattern.compile("^[\\p{L}\\s]+$");
    private static final Pattern QUANTIDADE    = Pattern.compile("^[1-9]\\d{0,8}$");
    private static final Pattern ANO           = Pattern.compile("^\\d{4}$");

    private final MiniaturaDAO dao = new MiniaturaDAO();

    // ===================== GET =====================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if ("editar".equals(req.getParameter("acao"))) {
                Miniatura m = dao.buscarPorId(idDe(req));
                if (m == null) {
                    flash(req, "erro", "Miniatura não encontrada.");
                    redirecionar(req, resp);
                    return;
                }
                abrirEdicao(req, resp, m, paraForm(m));
            } else {
                listar(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id inválido");
        } catch (SQLException e) {
            throw new ServletException("Erro no banco de dados", e);
        }
    }

    // ===================== POST =====================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String acao = req.getParameter("acao");
            if (acao == null) acao = "";
            switch (acao) {
                case "criar"     -> criar(req, resp);
                case "atualizar" -> atualizar(req, resp);
                case "excluir"   -> excluir(req, resp);
                default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação desconhecida");
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id inválido");
        } catch (IllegalStateException e) { // arquivo maior que o limite do @MultipartConfig
            flash(req, "erro", "A imagem passou do limite de 2 MB.");
            redirecionar(req, resp);
        } catch (SQLException e) {
            log("Erro de banco de dados", e);
            flash(req, "erro", "Erro no banco de dados: " + e.getMessage());
            redirecionar(req, resp);
        }
    }

    // ===================== CREATE =====================
    private void criar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, SQLException {
        Map<String, String> form = lerForm(req);
        List<String> erros = validar(form);
        Imagem img = lerImagem(req, erros, true);

        if (!erros.isEmpty()) {
            req.setAttribute("erros", erros);
            req.setAttribute("form", form);   // mantém o que a pessoa digitou
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            listar(req, resp);
            return;
        }
        dao.inserir(montar(0, form), img);
        flash(req, "sucesso", "Miniatura adicionada!");
        redirecionar(req, resp);
    }

    // ===================== UPDATE =====================
    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, SQLException {
        int id = idDe(req);
        Miniatura atual = dao.buscarPorId(id);
        if (atual == null) {
            flash(req, "erro", "Miniatura não encontrada.");
            redirecionar(req, resp);
            return;
        }

        Map<String, String> form = lerForm(req);
        List<String> erros = validar(form);
        Imagem nova = lerImagem(req, erros, false);
        boolean remover = "on".equals(req.getParameter("removerImagem"));

        if (!erros.isEmpty()) {
            req.setAttribute("erros", erros);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            abrirEdicao(req, resp, atual, form);
            return;
        }
        dao.atualizar(montar(id, form), nova, remover);
        flash(req, "sucesso", "Miniatura atualizada!");
        redirecionar(req, resp);
    }

    // ===================== DELETE =====================
    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {
        dao.excluir(idDe(req));
        flash(req, "sucesso", "Miniatura excluída.");
        redirecionar(req, resp);
    }

    // ===================== READ (view) =====================
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, SQLException {
        moverFlashParaRequest(req);

        Map<Categoria, List<Miniatura>> grupos = new EnumMap<>(Categoria.class);
        for (Categoria c : Categoria.values()) grupos.put(c, new ArrayList<>());
        for (Miniatura m : dao.listarTodas()) grupos.get(m.getCategoria()).add(m);

        req.setAttribute("grupos", grupos);
        req.setAttribute("categorias", Categoria.values());
        req.getRequestDispatcher(VIEW_LISTA).forward(req, resp);
    }

    private void abrirEdicao(HttpServletRequest req, HttpServletResponse resp,
                             Miniatura m, Map<String, String> form) throws ServletException, IOException {
        req.setAttribute("m", m);
        req.setAttribute("form", form);
        req.setAttribute("categorias", Categoria.values());
        req.getRequestDispatcher(VIEW_EDICAO).forward(req, resp);
    }

    // ===================== Validação (no servidor) =====================
    private List<String> validar(Map<String, String> f) {
        List<String> erros = new ArrayList<>();

        if (!f.get("categoria").matches("^[1-4]$"))
            erros.add("Selecione uma tabela válida.");

        if (!QUANTIDADE.matcher(f.get("quantidade")).matches())
            erros.add("Quantidade deve ser um número inteiro positivo.");

        String marca = f.get("marca");
        if (marca.isEmpty() || marca.length() > 60 || !APENAS_LETRAS.matcher(marca).matches())
            erros.add("Marca deve conter apenas letras (até 60 caracteres).");

        String modelo = f.get("modelo");
        if (modelo.isEmpty() || modelo.length() > 100)
            erros.add("Modelo é obrigatório (até 100 caracteres).");

        String cor = f.get("cor");
        if (cor.isEmpty() || cor.length() > 40 || !APENAS_LETRAS.matcher(cor).matches())
            erros.add("Cor deve conter apenas letras (até 40 caracteres).");

        String ano = f.get("ano");
        if (!ANO.matcher(ano).matches()
                || Integer.parseInt(ano) <= 0
                || Integer.parseInt(ano) > Year.now().getValue())
            erros.add("Ano inválido! Deve ter 4 dígitos e não pode ser futuro.");

        return erros;
    }

    private Imagem lerImagem(HttpServletRequest req, List<String> erros, boolean obrigatoria)
            throws IOException, ServletException {
        Part parte = req.getPart("imagem");
        if (parte == null || parte.getSize() == 0) {
            if (obrigatoria) erros.add("Selecione uma imagem.");
            return null;
        }
        if (!TIPOS_IMAGEM.contains(parte.getContentType())) {
            erros.add("A imagem deve ser JPEG, PNG, GIF ou WebP.");
            return null;
        }
        return new Imagem(parte.getInputStream().readAllBytes(), parte.getContentType());
    }

    // ===================== Utilitários =====================
    private Map<String, String> lerForm(HttpServletRequest req) {
        Map<String, String> f = new LinkedHashMap<>();
        for (String campo : CAMPOS) {
            String v = req.getParameter(campo);
            f.put(campo, v == null ? "" : v.trim());
        }
        return f;
    }

    private Map<String, String> paraForm(Miniatura m) {
        Map<String, String> f = new LinkedHashMap<>();
        f.put("categoria", String.valueOf(m.getCategoria().getId()));
        f.put("quantidade", String.valueOf(m.getQuantidade()));
        f.put("marca", m.getMarca());
        f.put("modelo", m.getModelo());
        f.put("cor", m.getCor());
        f.put("ano", String.valueOf(m.getAno()));
        return f;
    }

    /** Só chamar depois de validar(): os números já foram conferidos. */
    private Miniatura montar(int id, Map<String, String> f) {
        Miniatura m = new Miniatura();
        m.setId(id);
        m.setCategoria(Categoria.deId(Integer.parseInt(f.get("categoria"))));
        m.setQuantidade(Integer.parseInt(f.get("quantidade")));
        m.setMarca(f.get("marca"));
        m.setModelo(f.get("modelo"));
        m.setCor(f.get("cor"));
        m.setAno(Integer.parseInt(f.get("ano")));
        return m;
    }

    private int idDe(HttpServletRequest req) {
        return Integer.parseInt(req.getParameter("id")); // NumberFormatException se faltar/for inválido
    }

    // Mensagem "flash": sobrevive ao redirect (padrão Post-Redirect-Get) e some depois de exibida.
    private void flash(HttpServletRequest req, String chave, String mensagem) {
        req.getSession().setAttribute(chave, mensagem);
    }

    private void moverFlashParaRequest(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return;
        for (String chave : new String[] {"sucesso", "erro"}) {
            Object v = s.getAttribute(chave);
            if (v != null) {
                req.setAttribute(chave, v);
                s.removeAttribute(chave);
            }
        }
    }

    private void redirecionar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/miniaturas");
    }
}
