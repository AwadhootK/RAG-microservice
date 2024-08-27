from langchain_community.embeddings import SentenceTransformerEmbeddings


class EmbeddingAdapter(SentenceTransformerEmbeddings):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    def _embed_documents(self, texts):
        return super().embed_documents(texts)

    def __call__(self, input):
        return self._embed_documents(input)
