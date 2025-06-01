// 📦 Importação de módulos necessários
import * as functions from "firebase-functions"; // Funções do Firebase para Cloud Functions
import * as admin from "firebase-admin"; // Admin SDK para acesso ao Firestore
import * as QRCode from "qrcode"; // (Importado mas não utilizado) Geração de QR Codes
import cors from "cors"; // Middleware para permitir requisições externas (ex: web)
import { v4 as uuidv4 } from "uuid"; // Geração de UUIDs únicos (tokens)

// Inicializa o app admin e serviços
admin.initializeApp();
const db = admin.firestore(); // Referência ao banco Firestore
const { Timestamp } = admin.firestore;
const corsMiddleware = cors({ origin: true }); // Middleware CORS para liberar todas as origens

// 🔐 Função que gera um loginToken para login via QR Code
export const performAuth = functions.https.onRequest(async (req, res) => {
    corsMiddleware(req, res, async () => {
        try {
            const { apiKey, siteUrl } = req.body;

            // Verifica se os dados obrigatórios foram enviados
            if (!apiKey || !siteUrl) {
                return res.status(400).json({ error: "Dados incompletos." });
            }

            // Gera token de login único
            const loginToken = uuidv4();
            const createdAt = Timestamp.now();

            // Salva o token e site no Firestore na coleção "login"
            await db.collection("login").doc(loginToken).set({
                apiKey,
                siteUrl,
                createdAt,
                status: "pending" // Status inicial aguardando confirmação do app
            });

            // Retorna o loginToken para que o front-end possa gerar o QR Code
            return res.status(200).json({ loginToken: loginToken });
        } catch (err) {
            console.error("Erro ao retornar token", err);
            return res.status(500).json({ error: "Erro interno" });
        }
    });
});

// 📲 Função chamada pelo app mobile para confirmar um login via QR Code
export const confirmAuth = functions.https.onRequest((req, res) => {
    corsMiddleware(req, res, async () => {
        try {
            const { loginToken } = req.body;

            // Referência ao documento correspondente ao token
            const ref = db.collection("login").doc(loginToken);
            const docTokenSnap = await ref.get();

            // Verifica se o token existe
            if (!docTokenSnap.exists) {
                return res.status(404).json({ error: "Token inválido." });
            }

            const docToken = docTokenSnap.data();

            // Verifica se os dados mínimos estão presentes
            if (!docToken?.user || !docToken?.siteUrl) {
                return res.status(400).json({ error: "Dados de login incompletos." });
            }

            // Acessa os acessos do usuário baseado no domínio do site
            const acessosRef = db
                .collection("usuarios")
                .doc(docToken.user)
                .collection("acessos");

            const acessosSnapshot = await acessosRef
                .where("dominio", "==", docToken.siteUrl)
                .limit(1)
                .get();

            // Verifica se existe acesso vinculado a esse domínio
            if (acessosSnapshot.empty) {
                return res.status(404).json({ error: "Acesso não encontrado para este domínio." });
            }

            const acesso = acessosSnapshot.docs[0].data();

            // Carrega os dados do usuário
            const userRef = db.collection("usuarios").doc(docToken.user);
            const userSnap = await userRef.get();

            if (!userSnap.exists) {
                return res.status(404).json({ error: "Usuário não existente" });
            }

            const user = userSnap.data();

            // Verifica se os dados mínimos do usuário estão presentes
            if (!user?.nome || !user?.email) {
                return res.status(400).json({ error: "Dados de usuário incompletos." });
            }

            // Retorna nome e email do usuário para o front-end
            return res.status(200).json({
                success: true,
                nome: user.nome,
                email: acesso.email
            });
        } catch (err) {
            console.error("Erro ao confirmar login:", err);
            return res.status(500).json({ error: "Erro interno ao confirmar login" });
        }
    });
});

// 🔁 Função que permite ao site verificar o status de um login já iniciado
export const getLoginStatus = functions.https.onRequest((req, res) => {
    corsMiddleware(req, res, async () => {
        try {
            const { loginToken } = req.query;

            // Busca o documento com o token informado
            const doc = await db.collection("login").doc(String(loginToken)).get();

            if (!doc.exists) {
                return res.status(404).json({ error: "Token inválido." });
            }

            const data = doc.data();

            // ⚠️ ERRO: 'ref' e 'acesso' não estão definidos aqui!
            // O trecho abaixo parece incompleto ou copiado do `confirmAuth`
            // Ele tenta atualizar o status e dados do usuário, mas `ref` e `acesso` não existem neste escopo
            await ref.update({
                status: "authenticated",
                user: {
                    email: acesso.email,
                    name: acesso.nome || acesso.email.split('@')[0]
                },
                updatedAt: admin.firestore.FieldValue.serverTimestamp()
            });

            // Retorna status atual e dados do usuário (se disponíveis)
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
