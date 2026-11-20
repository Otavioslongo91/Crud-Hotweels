-- Rode no phpMyAdmin (aba SQL) ou no cliente mysql do XAMPP
CREATE DATABASE IF NOT EXISTS crud_miniaturas
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE crud_miniaturas;

-- Uma única tabela no lugar dos 4 "Carrinhos" do JavaScript:
-- a coluna "categoria" diz em qual lista a miniatura aparece.
CREATE TABLE IF NOT EXISTS miniatura (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  categoria   TINYINT      NOT NULL,   -- 1 Mainline, 2 Premium, 3 Matchbox, 4 Outras
  quantidade  INT          NOT NULL,
  marca       VARCHAR(60)  NOT NULL,
  modelo      VARCHAR(100) NOT NULL,
  cor         VARCHAR(40)  NOT NULL,
  ano         SMALLINT     NOT NULL,
  imagem      MEDIUMBLOB   NULL,
  imagem_tipo VARCHAR(50)  NULL,
  CONSTRAINT ck_categoria  CHECK (categoria BETWEEN 1 AND 4),
  CONSTRAINT ck_quantidade CHECK (quantidade > 0)
);
