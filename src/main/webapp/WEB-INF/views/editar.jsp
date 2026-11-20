<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar miniatura</title>
    <link rel="stylesheet" href="${ctx}/css/estilos.css">
</head>
<body>

<header>
    <h1>Editar Miniatura #${m.id}</h1>
</header>

<c:if test="${not empty erros}">
    <ul class="msg erro">
        <c:forEach items="${erros}" var="e"><li><c:out value="${e}" /></li></c:forEach>
    </ul>
</c:if>

<!-- UPDATE -->
<form class="formulario" action="${ctx}/miniaturas" method="post" enctype="multipart/form-data">
    <input type="hidden" name="acao" value="atualizar">
    <input type="hidden" name="id" value="${m.id}">

    <input type="text" name="quantidade" placeholder="Quantidade" value="<c:out value='${form.quantidade}' />">
    <input type="text" name="marca" placeholder="Marca da miniatura" maxlength="60" value="<c:out value='${form.marca}' />">
    <input type="text" name="modelo" placeholder="Modelo da miniatura" maxlength="100" value="<c:out value='${form.modelo}' />">
    <input type="text" name="cor" placeholder="Cor da miniatura" maxlength="40" value="<c:out value='${form.cor}' />">
    <input type="text" name="ano" placeholder="Ano da miniatura" maxlength="4" value="<c:out value='${form.ano}' />">

    <select name="categoria">
        <c:forEach items="${categorias}" var="cat">
            <option value="${cat.id}" ${form.categoria == cat.id ? 'selected' : ''}><c:out value="${cat.nome}" /></option>
        </c:forEach>
    </select>

    <div class="imagem-atual">
        <c:choose>
            <c:when test="${m.temImagem}">
                <p>Imagem atual:</p>
                <img src="${ctx}/imagem?id=${m.id}" alt="Imagem atual">
                <label class="check"><input type="checkbox" name="removerImagem"> Excluir imagem</label>
            </c:when>
            <c:otherwise><p>Esta miniatura está sem imagem.</p></c:otherwise>
        </c:choose>
        <p>Trocar imagem (opcional):</p>
        <input type="file" name="imagem" accept="image/*">
    </div>

    <button type="submit">Salvar alterações</button>
    <div class="acoes"><a class="btn secundario" href="${ctx}/miniaturas">Cancelar</a></div>
</form>

</body>
</html>
