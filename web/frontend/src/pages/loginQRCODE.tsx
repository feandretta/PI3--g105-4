import axios from "axios";
import { useState } from "react";

// 🔗 Endereço local do emulador Firebase Functions
const baseURL = "http://localhost:5002/projeto-integrador-8e633/us-central1";

export default function LoginQR() {
    const [qrCode, setQRCode] = useState<string | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [erro, setErro] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const gerarQRCode = async () => {
        setLoading(true);
        setErro(null);

        try {
            const res = await axios.post(`${baseURL}/performAuth`, {
                apiKey: "teste-local",
                siteUrl: "localhost"
            });

            setQRCode(res.data.qrCode);
            setToken(res.data.loginToken);
        } catch (err) {
            console.error("Erro ao gerar QR Code", err);
            setErro("Erro ao gerar QR Code.");
            setQRCode(null);
            setToken(null);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-zinc-900 text-white flex flex-col items-center justify-center p-4">
            <h1 className="text-2xl font-bold mb-4">Login com QR Code</h1>

            {qrCode ? (
                <img
                    src={qrCode}
                    alt="QR Code"
                    className="w-64 h-64 border-4 border-white rounded-xl"
                />
            ) : (
                <button
                    onClick={gerarQRCode}
                    disabled={loading}
                    className={`px-6 py-2 rounded-lg font-semibold transition-colors ${
                        loading
                            ? "bg-blue-400 cursor-not-allowed"
                            : "bg-blue-600 hover:bg-blue-700"
                    }`}
                >
                    {loading ? "Gerando QR Code..." : "Gerar QR Code"}
                </button>
            )}

            {token && (
                <p className="mt-4 text-sm text-zinc-400 text-center">
                    Token gerado: <br />
                    <code className="text-green-400 break-words">{token}</code>
                </p>
            )}

            {erro && <p className="text-red-400 mt-4">{erro}</p>}
        </div>
    );
}