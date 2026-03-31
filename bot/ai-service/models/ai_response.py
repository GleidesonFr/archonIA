from pydantic import BaseModel

class AIResponse(BaseModel):
    sessionId: str
    reply: str
