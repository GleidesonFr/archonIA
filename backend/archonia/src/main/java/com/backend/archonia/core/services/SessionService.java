package com.backend.archonia.core.services;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.backend.archonia.models.UserSession;

@Service
public class SessionService {
    
    private final ConcurrentMap<String, UserSession> sessions = new ConcurrentHashMap<>();

    private static final String SESSION_PREFIX = "session:";
    private final RedisTemplate<String, Object> redisTemplate;
    
    public SessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public UserSession createSession(){
        return sessions.computeIfAbsent(java.util.UUID.randomUUID().toString(), id -> new UserSession(id, new ArrayList<>()));
    }

    public UserSession getSession(String sessionId){
        String key = SESSION_PREFIX + sessionId;
        UserSession session = (UserSession) redisTemplate.opsForValue().get(key);
        if (session == null) {
            session = new UserSession(sessionId, new ArrayList<>());
            saveSession(session);
        }
        return session;
    }

    public void saveSession(UserSession session){
        String key = SESSION_PREFIX + session.getSessionId();
        redisTemplate.opsForValue().set(key, session);
    }

    public void clearSession(String sessionId){
        redisTemplate.delete(SESSION_PREFIX + sessionId);
    }

    public boolean sessionExists(String sessionId){
        return redisTemplate.hasKey(SESSION_PREFIX + sessionId);
    }

    public void updateSession(UserSession session){
        saveSession(session);
    }

    //Para propósitos de monitoramento

    public int getActiveSessionCount(){
        return (int) redisTemplate.keys(SESSION_PREFIX + "*").size();
    }
}
