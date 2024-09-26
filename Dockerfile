FROM python:3.10.14-bookworm

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt --use-deprecated=legacy-resolver

COPY . .

EXPOSE 8000

CMD ["python", "main.py"]