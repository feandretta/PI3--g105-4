// Importa dependências necessárias
import axios from "axios"; // Para fazer requisições HTTP
import { useState, useEffect, useRef } from "react"; // Hooks do React
import { motion, AnimatePresence } from "framer-motion"; // Biblioteca de animações (não usada diretamente aqui)

// Define a URL base onde estão hospedadas as Cloud Functions do Firebase
const baseURL = "https://us-central1-projeto-integrador-8e633.cloudfunctions.net";

export default function LoginQR() {
    // Estados que controlam o comportamento e feedback do componente
    const [qrCode, setQRCode] = useState<string | null>(null); // Imagem do QR code
    const [token, setToken] = useState<string | null>(null); // Token de autenticação gerado pelo backend
    const [erro, setErro] = useState<string | null>(null); // Mensagem de erro (caso ocorra)
    const [loading, setLoading] = useState(false); // Indica se o sistema está gerando o QR code
    const [polling, setPolling] = useState<number | null>(null); // Intervalo de verificação periódica
    const [timeLeft, setTimeLeft] = useState<number | null>(null); // Tempo restante para escanear o QR
    const [userData, setUserData] = useState<any>(null); // Dados do usuário retornados após login
    const [isAuthenticated, setIsAuthenticated] = useState(false); // Indica se o login foi confirmado
    const [scanStatus, setScanStatus] = useState<'idle' | 'scanning' | 'success' | 'error'>('idle'); // Estado visual

    // Referências para controle de temporizadores
    const timerRef = useRef<NodeJS.Timeout | null>(null);

    // Limpa timers ao desmontar o componente
    useEffect(() => {
        return () => {
            if (polling) clearInterval(polling);
            if (timerRef.current) clearInterval(timerRef.current);
        };
    }, [polling]);

    // Controla a contagem regressiva do tempo para escanear o QR
    useEffect(() => {
        if (timeLeft === null) return;

        if (timeLeft <= 0) {
            setQRCode(null);
            setToken(null);
            setErro("Tempo esgotado! Por favor, gere um novo QR Code.");
            if (polling) {
                clearInterval(polling);
                setPolling(null);
            }
            return;
        }

        timerRef.current = setTimeout(() => {
            setTimeLeft(timeLeft - 1);
        }, 1000);

        return () => {
            if (timerRef.current) clearTimeout(timerRef.current);
        };
    }, [timeLeft, polling]);

    // Formata os segundos para string de minutos e segundos (MM:SS)
    const formatTime = (seconds: number): string => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
    };

    // Verifica se o login foi autenticado usando o token
    const verificarStatusLogin = async (token: string) => {
        try {
            const res = await axios.get(`${baseURL}/getLoginStatus?loginToken=${token}`);
            if (res.data.status === 'authenticated') {
                if (polling) {
                    clearInterval(polling);
                    setPolling(null);
                }
                setScanStatus('success');

                // Após 1 segundo, busca os dados do usuário
                setTimeout(async () => {
                    const dados = await getUserCredentials(token);
                    if (dados) {
                        setUserData(dados);
                        setIsAuthenticated(true);
                        setTimeLeft(null);
                        setScanStatus('idle');
                    } else {
                        setErro("Erro ao obter dados do usuário.");
                        setScanStatus('error');
                    }
                }, 1000);
            } else if (res.data.status === 'pending') {
                setScanStatus('scanning');
            }
        } catch (err) {
            console.error("Erro ao verificar status do login", err);
            setScanStatus('error');
        }
    };

    // Confirma o login e retorna dados do usuário
    const getUserCredentials = async (token: string) => {
        try {
            const res = await axios.post(`${baseURL}/confirmAuth`, { loginToken: token }, {
                headers: { 'Content-Type': 'application/json' }
            });

            if (res.data.success) {
                return { name: res.data.nome, email: res.data.email };
            } else return null;
        } catch (error) {
            console.error("Erro ao confirmar login:", error);
            return null;
        }
    };

    // Gera QR code e inicia o processo de autenticação
    const gerarQRCode = async () => {
        setLoading(true);
        setErro(null);
        try {
            const res = await axios.post(`${baseURL}/performAuth`, {
                apiKey: "fe8fde5d-90a5-4707-8ecb-6b93301cd1c4",
                siteUrl: window.location.hostname || "www.localhost-dev.com"
            }, {
                headers: { 'Content-Type': 'application/json' },
            });

            if (res.data.qrCode) {
                setQRCode(res.data.qrCode);
                setToken(res.data.loginToken);
                setTimeLeft(90); // Define tempo de validade do QR

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
            {/* Título principal */}
            <h1 className="text-2xl font-bold mb-6">Login com QR Code</h1>

            <div className="bg-zinc-800 p-8 rounded-xl shadow-lg max-w-md w-full">
                {/* Se o QR foi gerado e o usuário ainda não autenticou */}
                {qrCode && !isAuthenticated ? (
                    <div className="flex flex-col items-center">
                        <div className={`relative transition-all duration-500 ${scanStatus === 'success' ? 'scale-110' : ''}`}>
                            {/* Imagem do QR Code */}
                            <img
                                src={qrCode}
                                alt="QR Code para autenticação"
                                className={`w-64 h-64 border-4 ${scanStatus === 'scanning' ? 'border-yellow-500 animate-pulse' : scanStatus === 'success' ? 'border-green-500' : 'border-white'} rounded-xl mb-4 transition-all duration-300`}
                            />
                            {/* Animações por cima do QR */}
                            {scanStatus === 'scanning' && (
                                <div className="absolute inset-0 flex items-center justify-center bg-black/50 rounded-xl">
                                    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-yellow-500"></div>
                                </div>
                            )}
                            {scanStatus === 'success' && (
                                <div className="absolute inset-0 flex items-center justify-center">
                                    <div className="bg-green-500/90 rounded-full p-2 animate-ping">
                                        <svg className="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                                        </svg>
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* Mensagem abaixo do QR */}
                        <p className={`text-center mb-4 transition-all duration-300 ${scanStatus === 'scanning' ? 'text-yellow-400' : 'text-zinc-300'}`}>
                            {scanStatus === 'scanning'
                                ? 'QR Code escaneado! Aguardando confirmação...'
                                : scanStatus === 'success'
                                ? 'Autenticação confirmada!'
                                : 'Escaneie este QR Code com o aplicativo móvel para fazer login'}
                        </p>

                        {/* Barra de progresso */}
                        {timeLeft !== null && (
                            <div className="mb-4 text-center">
                                <p className="text-yellow-400 font-medium">
                                    Tempo restante: {formatTime(timeLeft)}
                                </p>
                                <div className="w-full bg-gray-700 rounded-full h-2.5 mt-2">
                                    <div 
                                        className="bg-blue-600 h-2.5 rounded-full" 
                                        style={{ width: `${(timeLeft / 90) * 100}%` }}
                                    ></div>
                                </div>
                            </div>
                        )}

                        {/* Botão de reset do QR */}
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
                    // Tela inicial: botão para gerar QR
                    <div className="flex flex-col items-center">
                        <button
                            onClick={gerarQRCode}
                            disabled={loading}
                            className={`px-6 py-3 rounded-lg font-semibold transition-colors w-full max-w-xs ${
                                loading ? "bg-blue-400 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700"
                            }`}
                        >
                            {loading ? "Gerando QR Code..." : "Gerar QR Code de Login"}
                        </button>
                        <p className="text-center text-zinc-400 mt-4 text-sm">
                            Você precisará do aplicativo móvel para escanear o QR Code
                        </p>
                    </div>
                )}

                {/* Mensagem de sucesso e dados do usuário */}
                {isAuthenticated && userData && (
                    <div className="animate-fade-in text-center mt-6">
                        <h2 className="text-2xl font-bold text-green-400 mb-2">Login realizado com sucesso!</h2>
                        <p className="text-zinc-400">Bem-vindo(a), {userData.name}</p>
                        <p className="text-green-300">Email: {userData.email}</p>
                    </div>
                )}

                {/* Exibe o token de login (debug ou confirmação) */}
                {token && !isAuthenticated && (
                    <div className="mt-6 p-4 bg-zinc-700 rounded-lg">
                        <p className="text-sm text-zinc-300 mb-2">Token de autenticação:</p>
                        <code className="text-green-400 break-words text-sm bg-black/30 p-2 rounded block overflow-x-auto">
                            {token}
                        </code>
                    </div>
                )}

                {/* Exibe mensagens de erro se houver */}
                {erro && (
                    <div className="mt-4 p-3 bg-red-900/30 border border-red-700 rounded-lg">
                        <p className="text-red-400 text-sm">{erro}</p>
                    </div>
                )}
            </div>
        </div>
    );
}
