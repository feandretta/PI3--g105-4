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
export const performAuth = functions.https.onRequest(async (req, res) => {
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

            return res.status(200).json({loginToken : loginToken });
        } catch (err) {
            console.error("Erro ao retornar token", err);
            return res.status(500).json({ error: "Erro interno" });
        }
    });
});

// 📲 Confirmar login via app mobile
export const confirmAuth = functions.https.onRequest((req, res) => {
    corsMiddleware(req, res, async () => {
        try {
            const { loginToken } = req.body;

            const ref = db.collection("login").doc(loginToken);
            const docTokenSnap = await ref.get();

            if (!docTokenSnap.exists) {
                return res.status(404).json({ error: "Token inválido." });
            }

            const docToken = docTokenSnap.data();

            if (!docToken?.user || !docToken?.siteUrl) {
                return res.status(400).json({ error: "Dados de login incompletos." });
            }

            const acessosRef = db
                .collection("usuarios")
                .doc(docToken.user)
                .collection("acessos");

            const acessosSnapshot = await acessosRef
                .where("dominio", "==", docToken.siteUrl)
                .limit(1)
                .get();

            if (acessosSnapshot.empty) {
                return res.status(404).json({ error: "Acesso não encontrado para este domínio." });
            }

            const acesso = acessosSnapshot.docs[0].data();

                        const userRef = db
                .collection("usuarios")
                .doc(docToken.user);

            const userSnap = await userRef.get();

            if (!userSnap.exists) {
                return res.status(404).json({ error: "Usuário não existente" });
            }

            const user = userSnap.data();

            if (!user?.nome || !user?.email) {
                return res.status(400).json({ error: "Dados de usuário incompletos." });
            }

            return res.status(200).json({
                success: true,
                nome : user.nome,
                email: acesso.email
            });
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