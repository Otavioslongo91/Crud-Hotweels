# CRUD de Miniaturas — Java (Servlet + JSP + JDBC) — Tomcat 11 + MySQL (XAMPP)

## Checklist de implantação
- [ ] Java 17+ instalado (`java -version`) — o Tomcat 11 exige isso
- [ ] XAMPP: Apache **não** é necessário; inicie só o **MySQL**
- [ ] Rodar `sql/schema.sql` no phpMyAdmin (http://localhost/phpmyadmin → aba SQL)
- [ ] Conferir usuário/senha em `util/Conexao.java` (padrão XAMPP: root, sem senha)
- [ ] `mvn clean package` → gera `target/miniaturas.war`
- [ ] Copiar o `.war` para `<TOMCAT>/webapps/` e iniciar o Tomcat 11
- [ ] Abrir http://localhost:8080/miniaturas/
- [ ] Testar: adicionar, editar (dados e imagem), excluir imagem, excluir item

## Se o projeto do professor não usa Maven
Copie as pastas `src/main/java` e `src/main/webapp` para o projeto dele e coloque estes
.jar em `WEB-INF/lib`: `mysql-connector-j`, `jakarta.servlet.jsp.jstl-api` e `jakarta.servlet.jsp.jstl`.

## Problemas comuns
| Sintoma | Causa provável |
|---|---|
| `ClassNotFoundException: javax.servlet...` | import `javax` em vez de `jakarta` (Tomcat 10+) |
| Erro de taglib `jakarta.tags.core` | faltam os .jar do JSTL no WAR |
| `Communications link failure` | MySQL do XAMPP não está iniciado |
| `Packet for query is too large` | aumentar `max_allowed_packet` no `my.ini` do XAMPP (ex.: 16M) ou usar imagens menores |
| `Unknown database` | `schema.sql` não foi executado |
