export function getOrCreateUserId(): string {
    if(typeof window ===  'undefined'){
        return '';
    }

    let userId = localStorage.getItem("archon_user_id");

    if(!userId){
        userId = crypto.randomUUID();
        localStorage.setItem('archon_user_id', userId);
    }

    return userId;
}