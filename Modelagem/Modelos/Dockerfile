# Usamos Python 3.11 Slim para garantir compatibilidade com scikit-learn 1.6+ e numpy 2.0+
# (Imagens alpine podem ter problemas de compilação com numpy/pandas novos)
FROM python:3.11-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# 1. Copia apenas o requirements primeiro para aproveitar o cache do Docker
COPY requirements.txt .

# 2. Instala as dependências
RUN pip install --no-cache-dir -r requirements.txt

# 3. Copia o script de download e app.py
COPY download-model-if-missing.sh app.py ./
RUN chmod +x download-model-if-missing.sh

# 4. Copia o modelo se existir localmente, senão baixa da release
COPY modelo_atraso_voos_rf_res.pkl* ./ 2>/dev/null || true
RUN if [ ! -f modelo_atraso_voos_rf_res.pkl ]; then \
      echo "📥 Baixando modelo da release..."; \
      apt-get update && apt-get install -y curl && \
      curl -L -o modelo_atraso_voos_rf_res.pkl \
        https://github.com/Mateus-Redivo/FlightOnTime/releases/download/v1.0.0/modelo_atraso_voos_rf_res.pkl || \
        (echo "❌ Falha ao baixar modelo" && exit 1); \
    fi

# Expõe a porta 5000 (padrão definido no seu app.py)
EXPOSE 5000

# Comando para iniciar a aplicação
# Nota: Para produção real, recomenda-se usar gunicorn, mas python app.py funciona para este setup
CMD ["python", "app.py"]
