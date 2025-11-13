let Carrinho1 = [];
let Carrinho2 = [];
let Carrinho3 = [];
let Carrinho4 = [];

// SALVAR NO LOCALSTORAGE 
function salvarNoLocalStorage() {
    try {
        const dados = { Carrinho1, Carrinho2, Carrinho3, Carrinho4 };
        localStorage.setItem("Miniaturas", JSON.stringify(dados));
        console.log("Miniaturas salvas no navegador");
    } catch (e) {
        console.error("[DEBUG] Erro ao salvar no localStorage:", e);
        alert("⚠️ Erro: O navegador atingiu o limite de armazenamento. Tente imagens menores.");
    }
}

// --- CARREGAR DO LOCALSTORAGE ---
function carregarDoLocalStorage() {
    const dadosSalvos = localStorage.getItem("Miniaturas");
    if (dadosSalvos) {
        const dados = JSON.parse(dadosSalvos);
        Carrinho1 = dados.Carrinho1 || [];
        Carrinho2 = dados.Carrinho2 || [];
        Carrinho3 = dados.Carrinho3 || [];
        Carrinho4 = dados.Carrinho4 || [];
        listarMiniatura(1);
        listarMiniatura(2);
        listarMiniatura(3);
        listarMiniatura(4);
    }
}

// --- FUNÇÃO PARA COMPRIMIR IMAGEM ---
function comprimirImagem(file, maxWidth = 800, maxHeight = 800, qualidade = 0.7, callback) {
    const reader = new FileReader();
    reader.onload = function (e) {
        const img = new Image();
        img.onload = function () {
            const canvas = document.createElement("canvas");
            let width = img.width;
            let height = img.height;

            // Redimensiona proporcionalmente
            if (width > height && width > maxWidth) {
                height = height * (maxWidth / width);
                width = maxWidth;
            } else if (height > maxHeight) {
                width = width * (maxHeight / height);
                height = maxHeight;
            }

            canvas.width = width;
            canvas.height = height;

            const ctx = canvas.getContext("2d");
            ctx.drawImage(img, 0, 0, width, height);

            // Gera imagem comprimida
            const dataUrl = canvas.toDataURL("image/jpeg", qualidade);
            callback(dataUrl);
        };
        img.src = e.target.result;
    };
    reader.readAsDataURL(file);
}

// --- CREATE ---
function adicionarMiniatura() {
    const Quantidade = document.getElementById("Quantidade").value.trim();
    const Marca = document.getElementById("Marca").value.trim();
    const Modelo = document.getElementById("Modelo").value.trim();
    const Cor = document.getElementById("Cor").value.trim();
    const Ano = document.getElementById("Ano").value.trim();
    const imagemInput = document.getElementById("imagem");
    const imagem = imagemInput.files[0];
    const tabelaEscolhida = document.getElementById("TabelaDestino").value;

    // --- Verificações ---
    if (Quantidade === "" || Marca === "" || Modelo === "" || Cor === "" || Ano === "" || !imagem) {
        alert("Insira o que você ainda não inseriu, seu cabaço!");
        return;
    }

    if (isNaN(Quantidade) || Number(Quantidade) <= 0) {
        alert("Quantidade deve ser um número positivo, seu cabaço!");
        return;
    }

    const anoRegex = /^\d{4}$/;
    const anoAtual = new Date().getFullYear();
    if (!anoRegex.test(Ano) || Number(Ano) <= 0 || Number(Ano) > anoAtual) {
        alert("Ano inválido! Deve ter 4 dígitos e não pode ser futuro, seu cabaço!");
        return;
    }

    const apenasLetras = /^[A-Za-zÀ-ÿ\s]+$/;
    if (!apenasLetras.test(Cor)) {
        alert("Cor só tem letra, sem número, artista confuso!");
        return;
    }

    if (!apenasLetras.test(Marca)) {
        alert("Marca é só letras, não outros tipos de caractere (corrija, seu cabaço)!");
        return;
    }

    if (!imagem.type.startsWith("image/")) {
        alert("Isso não é uma imagem, seu cabaço (corrija)!");
        return;
    }

    // --- Compressão e salvamento ---
    comprimirImagem(imagem, 800, 800, 0.7, (imagemBase64) => {
        const item = { Quantidade, Marca, Modelo, Cor, Ano, imagemURL: imagemBase64 };

        if (tabelaEscolhida === "1") {
            Carrinho1.push(item);
            listarMiniatura(1);
        } else if (tabelaEscolhida === "2") {
            Carrinho2.push(item);
            listarMiniatura(2);
        } else if (tabelaEscolhida === "3") {
            Carrinho3.push(item);
            listarMiniatura(3);
        } else if (tabelaEscolhida === "4") {
            Carrinho4.push(item);
            listarMiniatura(4);
        } else {
            alert("Selecione uma tabela válida.");
            return;
        }

        document.getElementById("Quantidade").value = "";
        document.getElementById("Marca").value = "";
        document.getElementById("Modelo").value = "";
        document.getElementById("Cor").value = "";
        document.getElementById("Ano").value = "";
        imagemInput.value = "";

        salvarNoLocalStorage();
    });
}

// --- READ ---
function listarMiniatura(numTabela) {
    const tabela = document.getElementById(`table${numTabela}`);
    tabela.innerHTML = "";

    let dados = [];
    if (numTabela === 1) dados = Carrinho1;
    else if (numTabela === 2) dados = Carrinho2;
    else if (numTabela === 3) dados = Carrinho3;
    else dados = Carrinho4;

    dados.forEach((item, index) => {
        tabela.innerHTML += `
            <tr>
                <td>${item.Quantidade}</td>
                <td>${item.Marca}</td>
                <td>${item.Modelo}</td>
                <td>${item.Cor}</td>
                <td>${item.Ano}</td>
                <td>${item.imagemURL ? `<img src="${item.imagemURL}" alt="imagem da miniatura" width="80"/>` : "sem imagem"}</td>
                <td>
                    <button onclick="editarCarrinho(${index}, ${numTabela})">Editar</button>
                    <button onclick="excluirCarrinho(${index}, ${numTabela})">Excluir</button>
                    <input type="file" onchange="editarFoto(${index}, ${numTabela}, this)" accept="image/*">
                    <button onclick="excluirFoto(${index}, ${numTabela})">Excluir imagem</button>
                </td>
            </tr>
        `;
    });
}

// --- UPDATE ---
function editarCarrinho(index, numTabela) {
    let lista;
    if (numTabela === 1) lista = Carrinho1;
    else if (numTabela === 2) lista = Carrinho2;
    else if (numTabela === 3) lista = Carrinho3;
    else lista = Carrinho4;

    const novaQuantidade = prompt("Nova Quantidade:", lista[index].Quantidade);
    const novaMarca = prompt("Nova Marca:", lista[index].Marca);
    const novoModelo = prompt("Novo Modelo:", lista[index].Modelo);
    const novaCor = prompt("Nova Cor:", lista[index].Cor);
    const novoAno = prompt("Novo Ano:", lista[index].Ano);

    if (!novaQuantidade || !novaMarca || !novoModelo || !novaCor || !novoAno) {
        alert("Preencha todos os campos, seu cabaço!");
        return;
    }

    if (isNaN(novaQuantidade) || Number(novaQuantidade) <= 0) {
        alert("Quantidade deve ser número positivo!");
        return;
    }

    const anoRegex = /^\d{4}$/;
    const anoAtual = new Date().getFullYear();
    if (!anoRegex.test(novoAno) || Number(novoAno) <= 0 || Number(novoAno) > anoAtual) {
        alert("Ano inválido (4 dígitos e não futuro)!");
        return;
    }

    const apenasLetras = /^[A-Za-zÀ-ÿ\s]+$/;
    if (!apenasLetras.test(novaMarca) || !apenasLetras.test(novaCor)) {
        alert("Marca e Cor devem conter apenas letras!");
        return;
    }

    lista[index] = {
        Quantidade: novaQuantidade.trim(),
        Marca: novaMarca.trim(),
        Modelo: novoModelo.trim(),
        Cor: novaCor.trim(),
        Ano: novoAno.trim(),
        imagemURL: lista[index].imagemURL
    };

    listarMiniatura(numTabela);
    salvarNoLocalStorage();
}

// --- EDITAR FOTO ---
function editarFoto(index, numTabela, input) {
    let lista;
    if (numTabela === 1) lista = Carrinho1;
    else if (numTabela === 2) lista = Carrinho2;
    else if (numTabela === 3) lista = Carrinho3;
    else lista = Carrinho4;

    const novaImagem = input.files[0];
    if (novaImagem) {
        if (!novaImagem.type.startsWith("image/")) {
            alert("Arquivo não é imagem!");
            return;
        }

        // 🔧 Comprimir nova imagem antes de salvar
        comprimirImagem(novaImagem, 800, 800, 0.7, (novaImagemURL) => {
            lista[index].imagemURL = novaImagemURL;
            listarMiniatura(numTabela);
            salvarNoLocalStorage();
        });
    }
}

// --- DELETE ---
function excluirCarrinho(index, numTabela) {
    let lista;
    if (numTabela === 1) lista = Carrinho1;
    else if (numTabela === 2) lista = Carrinho2;
    else if (numTabela === 3) lista = Carrinho3;
    else lista = Carrinho4;

    if (confirm("Tem certeza que vai excluir, seu panaca?")) {
        lista.splice(index, 1);
        listarMiniatura(numTabela);
        salvarNoLocalStorage();
    }
}

function excluirFoto(index, numTabela) {
    let lista;
    if (numTabela === 1) lista = Carrinho1;
    else if (numTabela === 2) lista = Carrinho2;
    else if (numTabela === 3) lista = Carrinho3;
    else lista = Carrinho4;

    if (confirm("Tem certeza que quer excluir a imagem, seu boboca?")) {
        lista[index].imagemURL = "";
        listarMiniatura(numTabela);
        salvarNoLocalStorage();
    }
}

window.addEventListener("load", carregarDoLocalStorage);