export async function sendMessage(userId: string, sessionId : string | null, message : string){
    const response = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/chat/send`,
        {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ sessionId, message, userId }),
        }
    );

    if(!response.ok){
        throw new Error('Erro ao comunicar com o ArchonIA');
    }

    return response.json();
}

export async function getSessions(userId : string){
    const response = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/session?userId=${userId}`
    );

    if(!response.ok){
        throw new Error("Error ao buscar sessões");
    }

    return response.json();
}