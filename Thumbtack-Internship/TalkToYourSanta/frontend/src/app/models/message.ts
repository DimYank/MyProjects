export interface Message {
    id?: number;
    authorId: string;
    userRole: string;
    text: string;
    hashString?: string;
    timestamp: Date;
    delivered?: boolean;
}
