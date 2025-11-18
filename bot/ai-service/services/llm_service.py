from langchain_openai import ChatOpenAI
from settings import OPENAI_API_KEY

class LLMService:
    def get_llm(self):
        return ChatOpenAI(
        model="gpt-4o-mini",
        api_key=OPENAI_API_KEY,
        temperature=0.6
    )
     
    def generate_reply(self, message: str) -> str:
        # Lógica para gerar uma resposta usando um modelo de linguagem grande (LLM)
        return f"Resposta para a sessão e mensagem '{message}'"