"use client";

import { useState } from "react";
import { sendMessage } from "@/services/services";

export default function Home() {
   const [message, setMessage] = useState("");
   const [chat, setChat] = useState<string[]>([]);
   const [loading, setLoading] = useState(false);
   const [sessionId, setSessionId] = useState<string | null>(null);
   

   async function handleSend(){
    if(!message.trim()){
      return;
    }

    setLoading(true);
    setChat((prev) => [...prev, `Você: ${message}`]);

    try{
      const data = await sendMessage(sessionId, message);

      if(!sessionId){
        setSessionId(data.sessionId);
      }
      setChat((prev) => [...prev, `Archon: ${data.response}`]);
    }catch(err){
      setChat((prev) => [...prev, "Erro ao falar com o Archon"]);
    }finally{
      setLoading(false);
      setMessage("");
    }
   }

    return (
      <main style={{ padding: 20 }}>
        <h1>ArchonIA</h1>

        <div style={{ margin: "20px 0" }}>
          {chat.map((msg, index) => (
            <p key={index}>{msg}</p>
          ))}
        </div>

        <input
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder="Digite sua mensagem..."
        />

        <button onClick={handleSend} disabled={loading}>
          {loading ? "Enviando..." : "Enviar"}
        </button>
      </main>
    );
}
