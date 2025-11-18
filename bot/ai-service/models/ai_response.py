from pydantic import BaseModel

class AIResponse(BaseModel):
    reply: str
