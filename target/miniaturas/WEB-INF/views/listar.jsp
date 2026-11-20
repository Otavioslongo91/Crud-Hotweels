<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crud HotWheels</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>

<header>
    <h1>Crud de Miniaturas</h1>
</header>

<c:if test="${not empty sucesso}"><p class="msg sucesso"><c:out value="${sucesso}" /></p></c:if>
<c:if test="${not empty erro}"><p class="msg erro"><c:out value="${erro}" /></p></c:if>
<c:if test="${not empty erros}">
    <ul class="msg erro">
        <c:forEach items="${erros}" var="e"><li><c:out value="${e}" /></li></c:forEach>
    </ul>
</c:if>

<!-- CREATE -->
<form class="formulario" action="${ctx}/miniaturas" method="post" enctype="multipart/form-data">
    <input type="hidden" name="acao" value="criar">

    <input type="text" name="quantidade" placeholder="Quantidade" value="<c:out value='${form.quantidade}' />">
    <input type="text" name="marca" placeholder="Marca da miniatura" maxlength="60" value="<c:out value='${form.marca}' />">
    <input type="text" name="modelo" placeholder="Modelo da miniatura" maxlength="100" value="<c:out value='${form.modelo}' />">
    <input type="text" name="cor" placeholder="Cor da miniatura" maxlength="40" value="<c:out value='${form.cor}' />">
    <input type="text" name="ano" placeholder="Ano da miniatura" maxlength="4" value="<c:out value='${form.ano}' />">
    <input type="file" name="imagem" accept="image/*">

    <select name="categoria">
        <c:forEach items="${categorias}" var="cat">
            <option value="${cat.id}" ${form.categoria == cat.id ? 'selected' : ''}><c:out value="${cat.nome}" /></option>
        </c:forEach>
    </select>

    <button type="submit">Adicionar</button>
</form>

<!-- READ: uma tabela por categoria -->
<c:forEach items="${grupos}" var="g">
    <h2>Lista de Miniatura <c:out value="${g.key.nome}" /></h2>
    <table>
        <thead>
            <tr>
                <th>Quantidade</th><th>Marca</th><th>Modelo</th><th>Cor</th>
                <th>Ano</th><th>Imagem</th><th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty g.value}">
                <tr><td colspan="7">Nenhuma miniatura cadastrada.</td></tr>
            </c:if>
            <c:forEach items="${g.value}" var="m">
                <tr>
                    <td><c:out value="${m.quantidade}" /></td>
                    <td><c:out value="${m.marca}" /></td>
                    <td><c:out value="${m.modelo}" /></td>
                    <td><c:out value="${m.cor}" /></td>
                    <td><c:out value="${m.ano}" /></td>
                    <td>
                        <c:choose>
                            <c:when test="${m.temImagem}">
                                <img src="${ctx}/imagem?id=${m.id}" alt="Imagem de <c:out value='${m.modelo}' />">
                            </c:when>
                            <c:otherwise>sem imagem</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a class="btn" href="${ctx}/miniaturas?acao=editar&id=${m.id}">Editar</a>
                        <form action="${ctx}/miniaturas" method="post"
                              onsubmit="return confirm('Tem certeza que vai excluir?');">
                            <input type="hidden" name="acao" value="excluir">
                            <input type="hidden" name="id" value="${m.id}">
                            <button type="submit" class="btn perigo">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:forEach>

</body>
</html>
