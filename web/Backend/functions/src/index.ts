import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import * as QRCode from "qrcode";
import cors from "cors";
import { v4 as uuidv4 } from "uuid";

admin.initializeApp();
const db = admin.firestore();
const { Timestamp } = admin.firestore;
const corsMiddleware = cors({ origin: true });

// 🔐 Gerar QR Code de login
export const performAuth = functions.https.onRequest((req, res) => {
    corsMiddleware(req, res, async () => {
        try {
            const { apiKey, siteUrl } = req.body;

            if (!apiKey || !siteUrl) {
                return res.status(400).json({ error: "Dados incompletos." });
            }

            const loginToken = uuidv4();
            const createdAt = Timestamp.now();

            await db.collection("login").doc(loginToken).set({
                apiKey,
                siteUrl,
                createdAt,
                status: "pending"
            });

            const qrImage = await QRCode.toDataURL(loginToken);
            return res.status(200).json({ qrCode: qrImage, loginToken });
        } catch (err) {
            console.error("Erro ao gerar QR Code:", err);
            return res.status(500).json({ error: "Erro interno" });
        }
    });
});

// 📲 Confirmar login via app mobile
export const confirmAuth = functions.https.onRequest((req, res) => {
    corsMiddleware(req, res, async () => {
        try {
            const { loginToken, userUID } = req.body;

            const ref = db.collection("login").doc(loginToken);
            const doc = await ref.get();

            if (!doc.exists) {
                return res.status(404).json({ error: "Token inválido." });
            }

            await ref.update({
                status: "authenticated",
                user: userUID,
                authenticatedAt: Timestamp.now()
            });

            return res.status(200).json({ success: true });
        } catch (err) {
            console.error("Erro ao confirmar login:", err);
            return res.status(500).json({ error: "Erro interno ao confirmar login" });
        }
    });
});

// 🔁 Verificar status do login
export const getLoginStatus = functions.https.onRequest((req, res) => {
    corsMiddleware(req, res, async () => {
        try {
            const { loginToken } = req.query;

            const doc = await db.collection("login").doc(String(loginToken)).get();

            if (!doc.exists) {
                return res.status(404).json({ error: "Token inválido." });
            }

            const data = doc.data();

            return res.status(200).json({
                status: data?.status,
                user: data?.user || null
            });
        } catch (err) {
            console.error("Erro ao verificar status:", err);
            return res.status(500).json({ error: "Erro interno ao verificar status" });
        }
    });
});