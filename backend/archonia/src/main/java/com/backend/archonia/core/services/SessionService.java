package com.backend.archonia.core.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.backend.archonia.models.UserSession;

@Service
public class SessionService {
    
    private static final String SESSION_PREFIX = "chat:session:";
    private static final String USER_SESSIONS_PREFIX = "user:sessions:";
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${archon.session.ttl-hours:6}")
    private long ttlHours;

    public SessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public UserSession createSession(String userId){
        String sessionId = UUID.randomUUID().toString();
        UserSession session = new UserSession(sessionId, userId, new ArrayList<>());

        saveSession(session);
        linkSessionToUser(userId, sessionId);

        return session;
    }

    public UserSession getSession(String sessionId, String userId){
        String key = SESSION_PREFIX + sessionId;
        UserSession session = (UserSession) redisTemplate.opsForValue().get(key);
        if (session == null) {
            session = new UserSession(sessionId, userId, new ArrayList<>());
            saveSession(session);
            linkSessionToUser(userId, sessionId);
        }
        return session;
    }

    public void saveSession(UserSession session){
        String key = SESSION_PREFIX + session.getSessionId();
        redisTemplate.opsForValue().set(key, session, Duration.ofHours(ttlHours)); // Expira em 6 horas
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

    public void linkSessionToUser(String userId, String sessionId){
        String key = USER_SESSIONS_PREFIX + userId;
        redisTemplate.opsForSet().add(key, sessionId);
        redisTemplate.expire(key, Duration.ofDays(7));
    }

    public Set<Object> getSessionsbyUser(String userId){
        String key = USER_SESSIONS_PREFIX + userId;
        return redisTemplate.opsForSet().members(key);
    }
}
