import dotenv from 'dotenv';
dotenv.config();

export function encodeEnv(obj) {
    return Buffer.from(JSON.stringify(obj), "utf-8").toString("base64");
}
export function decodeEnv(key) {
    const value = process.env[key];
    if (!value) {
        throw `Missing .env parameter ${key}`
    }
    return JSON.parse(Buffer.from(process.env[key], "base64").toString("utf-8"));
}
