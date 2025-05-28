import axios from "axios";
import { useState, useEffect } from "react";

// 🔗 Endereço local do emulador Firebase Functions
const baseURL = "https://us-central1-projeto-integrador-8e633.cloudfunctions.net";


export default function LoginQR() {
    const [qrCode, setQRCode] = useState<string | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [erro, setErro] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const [polling, setPolling] = useState<number | null>(null);

    // Limpa o intervalo quando o componente é desmontado
    useEffect(() => {
        return () => {
            if (polling) {
                clearInterval(polling);
            }
        };
    }, [polling]);

    const verificarStatusLogin = async (token: string) => {
        try {
            const res = await axios.get(`${baseURL}/checkAuthStatus?token=${token}`);
            
            if (res.data.status === 'authenticated') {
                // Login bem-sucedido
                if (polling) {
                    clearInterval(polling);
                    setPolling(null);
                }
                alert('Login realizado com sucesso!');
                // Redirecionar ou atualizar o estado do usuário aqui
            }
        } catch (err) {
            console.error("Erro ao verificar status do login", err);
        }
    };

    const gerarQRCode = async () => {
        setLoading(true);
        setErro(null);
        console.log("Iniciando geracao de QR Code...")
        try {
            console.log("Tentando se conectar a:", `${baseURL}/performAuth`);
            
            const res = await axios.post(`${baseURL}/performAuth`, {
                apiKey: "fe8fde5d-90a5-4707-8ecb-6b93301cd1c4",
                siteUrl: window.location.hostname || "localhost"
            }, {
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            console.log("Resposta recebida:", res.data);
            
            if (res.data.qrCode) {
                setQRCode(res.data.qrCode);
                setToken(res.data.loginToken);
                
                // Iniciar verificação periódica do status de autenticação
                if (res.data.loginToken) {
                    const intervalId = window.setInterval(() => {
                        verificarStatusLogin(res.data.loginToken);
                    }, 3000) as unknown as number;
                    setPolling(intervalId);
                }
            } else {
                throw new Error("QR Code não retornado na resposta");
            }
        } catch (err: any) {
            console.error("Erro ao gerar QR Code:", err);
            const errorMessage = err.response?.data?.message || err.message || "Erro ao gerar QR Code";
            setErro(`Erro: ${errorMessage}`);
            setQRCode(null);
            setToken(null);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-zinc-900 text-white flex flex-col items-center justify-center p-4">
            <h1 className="text-2xl font-bold mb-6">Login com QR Code</h1>
            
            <div className="bg-zinc-800 p-8 rounded-xl shadow-lg max-w-md w-full">
                {qrCode ? (
                    <div className="flex flex-col items-center">
                        <img
                            src={qrCode}
                            alt="QR Code para autenticação"
                            className="w-64 h-64 border-4 border-white rounded-xl mb-4"
                        />
                        <p className="text-center text-zinc-300 mb-4">
                            Escaneie este QR Code com o aplicativo móvel para fazer login
                        </p>
                        <button
                            onClick={() => {
                                setQRCode(null);
                                setToken(null);
                            }}
                            className="text-blue-400 hover:text-blue-300 text-sm"
                        >
                            Gerar novo QR Code
                        </button>
                    </div>
                ) : (
                    <div className="flex flex-col items-center">
                        <button
                            onClick={gerarQRCode}
                            disabled={loading}
                            className={`px-6 py-3 rounded-lg font-semibold transition-colors w-full max-w-xs ${
                                loading
                                    ? "bg-blue-400 cursor-not-allowed"
                                    : "bg-blue-600 hover:bg-blue-700"
                            }`}
                        >
                            {loading ? "Gerando QR Code..." : "Gerar QR Code de Login"}
                        </button>
                        
                        <p className="text-center text-zinc-400 mt-4 text-sm">
                            Você precisará do aplicativo móvel para escanear o QR Code
                        </p>
                    </div>
                )}

                {token && (
                    <div className="mt-6 p-4 bg-zinc-700 rounded-lg">
                        <p className="text-sm text-zinc-300 mb-2">Token de autenticação:</p>
                        <code className="text-green-400 break-words text-sm bg-black/30 p-2 rounded block overflow-x-auto">
                            {token}
                        </code>
                    </div>
                )}

                {erro && (
                    <div className="mt-4 p-3 bg-red-900/30 border border-red-700 rounded-lg">
                        <p className="text-red-400 text-sm">{erro}</p>
                        <p className="text-red-300 text-xs mt-1">
                            Certifique-se de que o emulador do Firebase Functions está rodando.
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
}