/**
 * Lógica Principal do Formulário de Predição
 */
const predictForm = document.getElementById('predict-form');

// Verificamos se o formulário existe na página atual antes de adicionar o listener
if (predictForm) {
    predictForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const btn = e.target.querySelector('button');
        const resultCard = document.getElementById('result-card');
        
        // Elementos do Gauge (Velocímetro)
        const gaugeFill = document.getElementById('gauge-fill');
        const gaugeText = document.getElementById('gauge-text');
        const statusTitle = document.getElementById('result-status');
        const statusDesc = document.getElementById('result-description');

        // 1. Estado de Carregamento
        btn.innerText = "Calculando Probabilidade...";
        btn.disabled = true;

        // Simulação de processamento do modelo
        setTimeout(() => {
            // Restaurar botão
            btn.innerText = "Analisar Probabilidade";
            btn.disabled = false;
            
            // Gerar probabilidade aleatória (Simulando o Modelo)
            const probabilidade = Math.floor(Math.random() * 100);
            
            // 2. Exibir o Card de Resultado
            resultCard.style.display = "block";
            
            // Pequeno delay para garantir que o display:block foi processado antes do scroll
            setTimeout(() => {
                resultCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }, 100);

            // 3. Animar o Gauge (0.5 turn equivale a 100% no CSS semi-circular)
            const turnValue = (probabilidade / 100) * 0.5;
            gaugeFill.style.transform = `rotate(${turnValue}turn)`;
            gaugeText.innerText = `${probabilidade}%`;

            // 4. Lógica de Cores e Feedback Visual
            if (probabilidade <= 30) {
                gaugeFill.style.background = "#4caf50"; // Verde
                statusTitle.innerText = "Voo Pontual";
                statusTitle.className = "status-low";
                statusDesc.innerText = "As condições históricas sugerem que este voo tem altíssima chance de decolar no horário previsto.";
            } else if (probabilidade <= 60) {
                gaugeFill.style.background = "#ff9800"; // Laranja
                statusTitle.innerText = "Atenção Necessária";
                statusTitle.className = "status-medium";
                statusDesc.innerText = "Existe uma probabilidade moderada de pequenos atrasos. Recomendamos acompanhar o painel do aeroporto.";
            } else {
                gaugeFill.style.background = "#f44336"; // Vermelho
                statusTitle.innerText = "Risco de Atraso";
                statusTitle.className = "status-high";
                statusDesc.innerText = "Probabilidade elevada de atraso detectada. Fatores como congestionamento aéreo ou histórico da rota influenciam este resultado.";
            }

        }, 1500); // Tempo de simulação de 1.5 segundos
    });
}

/**
 * Lógica de Animação de Entrada (Reveal)
 * Ativa as classes .reveal presentes no HTML ao carregar a página
 */
window.addEventListener('load', () => {
    const scrollElements = document.querySelectorAll('.reveal');
    
    // Adiciona a classe active com um pequeno delay para garantir fluidez
    setTimeout(() => {
        scrollElements.forEach((el) => {
            el.classList.add('active');
        });
    }, 100);
});