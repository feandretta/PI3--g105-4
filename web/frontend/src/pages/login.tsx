async function gerarQRCode() {
    console.log("QR Code gerado!");
    // Aqui você pode colocar a lógica real futuramente, como fazer uma requisição à Firebase Function
}

function Login() {
    return (
        <div className="min-h-screen bg-zinc-900 flex items-center justify-center p-4">
            <div className="bg-zinc-800 text-white rounded-2xl shadow-lg p-8 w-full max-w-md">
                <h2 className="text-3xl font-bold mb-6 text-center">Entrar no SuperID</h2>

                <form className="space-y-4">
                    <div>
                        <label htmlFor="email" className="block mb-1 text-sm font-medium">
                            E-mail
                        </label>
                        <input
                            type="email"
                            id="email"
                            className="w-full px-4 py-2 rounded-lg bg-zinc-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="seu@email.com"
                        />
                    </div>

                    <div>
                        <label htmlFor="senha" className="block mb-1 text-sm font-medium">
                            Senha
                        </label>
                        <input
                            type="password"
                            id="senha"
                            className="w-full px-4 py-2 rounded-lg bg-zinc-700 text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="••••••••"
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-blue-600 hover:bg-blue-700 transition-colors px-4 py-2 rounded-lg text-white font-semibold"
                    >
                        Entrar
                    </button>

                    <button
                        type="button"
                        onClick={gerarQRCode}
                        className="w-full mt-2 bg-green-600 hover:bg-green-700 transition-colors px-4 py-2 rounded-lg text-white font-semibold"
                    >
                        Login via QR Code
                    </button>
                </form>

               <div className="flex justify-between items-center">
                   <p className="text-sm text-zinc-400 mt-4 text-center">
                       Esqueceu a senha?{" "}
                       <a href="#" className="text-blue-400 hover:underline">
                           Recuperar
                       </a>
                   </p>
                   <a href="register" className="text-sm text-blue-400 hover:underline trans mt-4 text-end">Registrar-se</a>
               </div>
            </div>
        </div>
    );
}

export default Login;
