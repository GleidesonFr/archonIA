from pydantic import BaseModel

class AIRequest(BaseModel):
    sessionId: str
    message: str
    systemPrompt: str
    history: list[dict]
    