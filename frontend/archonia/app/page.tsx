"use client";

import { useEffect, useRef, useState } from "react";
import { sendMessage, getSessions } from "@/services/services";
import { getOrCreateUserId } from "@/utils/user";

type ChatMessage = {
  role: "user" | "assistant";
  content: string;
};

export default function Home() {
  const [message, setMessage] = useState("");
  const [chat, setChat] = useState<ChatMessage[]>([]);
  const [loading, setLoading] = useState(false);

  const [sessionId, setSessionId] = useState<string | null>(null);
  const [userId, setUserId] = useState<string | null>(null);
  const [sessions, setSessions] = useState<string[]>([]);

  const bottomRef = useRef<HTMLDivElement | null>(null);

  // Inicializa userId (uma vez só)
  useEffect(() => {
    const id = getOrCreateUserId();
    setUserId(id);
  }, []);

  // Scroll automático
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [chat]);

  // Recupera sessão do localStorage
  useEffect(() => {
    const stored = localStorage.getItem("archonia-session");
    if (stored) {
      setSessionId(stored);
    }
  }, []);

  // Salva sessão no localStorage
  useEffect(() => {
    if (sessionId) {
      localStorage.setItem("archonia-session", sessionId);
    } else {
      localStorage.removeItem("archonia-session");
    }
  }, [sessionId]);

  // Buscar sessões do usuário
  useEffect(() => {
    if (!userId) return;

    const id = userId;

    async function loadSessions() {
      try {
        const data = await getSessions(id);
        setSessions(data);
      } catch (err) {
        console.error("Erro ao buscar sessões", err);
      }
    }

    loadSessions();
  }, [userId]);

  // Enviar mensagem
  async function handleSend() {
    if (!message.trim() || !userId) return;

    setLoading(true);

    setChat((prev) => [
      ...prev,
      { role: "user", content: message },
    ]);

    try {
      const data = await sendMessage(userId, sessionId, message);

      // se for nova sessão
      if (!sessionId) {
        setSessionId(data.sessionId);
        setSessions((prev) => [...prev, data.sessionId]);
      }

      setChat((prev) => [
        ...prev,
        { role: "assistant", content: data.reply },
      ]);
    } catch {
      setChat((prev) => [
        ...prev,
        {
          role: "assistant",
          content: "Erro ao falar com o Archon",
        },
      ]);
    } finally {
      setLoading(false);
      setMessage("");
    }
  }

  // Nova conversa
  function handleNewSession() {
    setSessionId(null);
    setChat([]);
  }

  // Trocar sessão
  function handleSelectSession(id: string) {
    setSessionId(id);
    setChat([]); // depois vamos carregar histórico aqui
  }

  return (
    <div style={{ display: "flex", height: "100vh" }}>
      
      {/* SIDEBAR */}
      <div style={{ width: 250, borderRight: "1px solid #ccc", padding: 10 }}>
        <h3>Conversas</h3>

        <button onClick={handleNewSession}>
          Nova conversa
        </button>

        <div style={{ marginTop: 10 }}>
          {sessions.map((id) => (
            <div key={id}>
              <button onClick={() => handleSelectSession(id)}>
                {id.substring(0, 8)}...
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* CHAT */}
      <div style={{ flex: 1, padding: 20 }}>
        <h1>ArchonIA</h1>

        <div
          style={{
            margin: "20px 0",
            maxHeight: "400px",
            overflowY: "auto",
          }}
        >
          {chat.map((msg, index) => (
            <p key={index}>
              <strong>
                {msg.role === "user" ? "Você" : "Archon"}:
              </strong>{" "}
              {msg.content}
            </p>
          ))}
          <div ref={bottomRef} />
        </div>

        <input
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") handleSend();
          }}
          placeholder="Digite sua mensagem..."
        />

        <button onClick={handleSend} disabled={loading}>
          {loading ? "Enviando..." : "Enviar"}
        </button>
      </div>
    </div>
  );
}