import axios from "axios";
import { useState, useEffect, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";

// 🔗 Endereço local do emulador Firebase Functions
const baseURL = "https://us-central1-projeto-integrador-8e633.cloudfunctions.net";


export default function LoginQR() {
    const [qrCode, setQRCode] = useState<string | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [erro, setErro] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const [polling, setPolling] = useState<number | null>(null);
    const [timeLeft, setTimeLeft] = useState<number | null>(null);
    const [userData, setUserData] = useState<any>(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isScanning, setIsScanning] = useState(false);
    const [scanStatus, setScanStatus] = useState<'idle' | 'scanning' | 'success' | 'error'>('idle');
    const timerRef = useRef<NodeJS.Timeout | null>(null);
    const contentRef = useRef<HTMLDivElement>(null);

    // Limpa os intervalos quando o componente é desmontado
    useEffect(() => {
        return () => {
            if (polling) {
                clearInterval(polling);
            }
            if (timerRef.current) {
                clearInterval(timerRef.current);
            }
        };
    }, [polling]);

    // Efeito para controlar o contador regressivo
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
            if (timerRef.current) {
                clearTimeout(timerRef.current);
            }
        };
    }, [timeLeft, polling]);

    const formatTime = (seconds: number): string => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
    };

    const verificarStatusLogin = async (token: string) => {
    try {
        const res = await axios.get(`${baseURL}/getLoginStatus?loginToken=${token}`);
        console.log("verificando login")
            
    if (res.data.status === 'authenticated') {
        if (polling) {
            clearInterval(polling);
            setPolling(null);
    }

    setScanStatus('success');

    // Espera 1 segundo para mostrar o feedback de sucesso
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
}
 else if (res.data.status === 'pending') {
                setScanStatus('scanning');
            }
        } catch (err) {
            console.error("Erro ao verificar status do login", err);
            setScanStatus('error');
        }
    };
const getUserCredentials = async (token: string) => {
    try {
        const res = await axios.post(`${baseURL}/confirmAuth`, {
            loginToken: token
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (res.data.success) {
            return {
                name: res.data.nome,
                email: res.data.email
            };
        } else {
            return null;
        }
    } catch (error) {
        console.error("Erro ao confirmar login:", error);
        return null;
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
                siteUrl: window.location.hostname || "www.localhost-dev.com"
            }, {
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            console.log("Resposta recebida:", res.data);
            
            if (res.data.qrCode) {
                setQRCode(res.data.qrCode);
                setToken(res.data.loginToken);
                setTimeLeft(90); // 1 minuto e 30 segundos
                console.log("gerado qr code");
                
                // Iniciar verificação periódica do status de autenticação
                if (res.data.loginToken) {
                    const intervalId = window.setInterval(() => {
                        verificarStatusLogin(res.data.loginToken);
                        console.log("verificando status...")
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
                {qrCode && !isAuthenticated ? (
                    <div className="flex flex-col items-center">
                        <div className={`relative transition-all duration-500 ${scanStatus === 'success' ? 'scale-110' : ''}`}>
                            <img
                                src={qrCode}
                                alt="QR Code para autenticação"
                                className={`w-64 h-64 border-4 ${scanStatus === 'scanning' ? 'border-yellow-500 animate-pulse' : scanStatus === 'success' ? 'border-green-500' : 'border-white'} rounded-xl mb-4 transition-all duration-300`}
                            />
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
                        <p className={`text-center mb-4 transition-all duration-300 ${scanStatus === 'scanning' ? 'text-yellow-400' : 'text-zinc-300'}`}>
                            {scanStatus === 'scanning' 
                                ? 'QR Code escaneado! Aguardando confirmação...'
                                : scanStatus === 'success'
                                ? 'Autenticação confirmada!'
                                : 'Escaneie este QR Code com o aplicativo móvel para fazer login'}
                        </p>
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

                {isAuthenticated && userData && (
                    <div className="animate-fade-in">
                        <div className="text-center mb-6">
                            <div className="w-20 h-20 bg-green-500 rounded-full flex items-center justify-center mx-auto mb-4">
                                <svg className="h-12 w-12 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                </svg>
                            </div>
                            <h2 className="text-2xl font-bold text-green-400 mb-2">Login realizado com sucesso!</h2>
                            <p className="text-zinc-400">Bem-vindo(a) de volta</p>
                        </div>
                        <div className="space-y-3">
                            <p className="text-green-300">
                                <span className="font-medium">Nome:</span> {userData.name || 'Não informado'}
                            </p>
                            <p className="text-green-300">
                                <span className="font-medium">Email:</span> {userData.email || 'Não informado'}
                            </p>
                            {userData.photoURL && (
                                <div className="mt-4 flex justify-center">
                                    <img 
                                        src={userData.photoURL} 
                                        alt="Foto do usuário"
                                        className="w-20 h-20 rounded-full border-2 border-green-500"
                                    />
                                </div>
                            )}
                        </div>
                        <div className="mt-6 space-y-4">
                            <button
                                onClick={() => {
                                    setUserData(null);
                                    setIsAuthenticated(false);
                                    setToken(null);
                                    setQRCode(null);
                                    setScanStatus('idle');
                                }}
                                className="w-full px-4 py-3 bg-blue-600 hover:bg-blue-700 rounded-lg font-medium transition-colors flex items-center justify-center space-x-2"
                            >
                                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                                </svg>
                                <span>Fazer logout</span>
                            </button>
                            <button
                                onClick={() => {
                                    // Navegar para o dashboard ou página inicial
                                    alert('Redirecionando para o dashboard...');
                                }}
                                className="w-full px-4 py-3 bg-green-600 hover:bg-green-700 rounded-lg font-medium transition-colors"
                            >
                                Ir para o Dashboard
                            </button>
                        </div>
                    </div>
                )}

                {token && !isAuthenticated && (
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
                    </div>
                )}
            </div>
        </div>
    );
}