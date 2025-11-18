from fastapi import FastAPI
from models.ai_request import AIRequest
from models.ai_response import AIResponse
from services.llm_service import LLMService
import logging

app = FastAPI()
llm_service = LLMService()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("archon-fastapi")

@app.post("/ai", response_model=AIResponse)
async def process_ai(request: AIRequest):
    logger.info(f"Session ID: {request.sessionId}")
    logger.info(f"Message: {request.message}")
    logger.info(f"System Prompt (tamanho={len(request.systemPrompt)}):")
    logger.info(request.systemPrompt)
    logger.info("Histórico recebido:")
    history = request.history.copy()
    history.append({"role": "user", "content": request.message})
    if not history or history[0].get("role") != "system":
        history.insert(0, {"role": "system", "content": request.systemPrompt})
    
    llm = llm_service.get_llm()
    reply = llm.invoke(history).content
    return AIResponse(reply=reply)

@app.get("/health")
def health_check():
    return {"status": "AI service is running"}