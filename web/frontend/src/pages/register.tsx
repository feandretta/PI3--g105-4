import { Link } from "react-router-dom";

function Register() {
    return (
        <div className="min-h-screen bg-zinc-900 flex items-center justify-center p-4">
            <div className="bg-zinc-800 text-white rounded-2xl shadow-lg p-8 w-full max-w-md">
                <h2 className="text-3xl font-bold mb-6 text-center">Criar Conta no SuperID</h2>

                <form className="space-y-4">
                    <input type="text" placeholder="Nome" className="w-full px-4 py-2 rounded-lg bg-zinc-700 text-white" />
                    <input type="email" placeholder="Email" className="w-full px-4 py-2 rounded-lg bg-zinc-700 text-white" />
                    <input type="password" placeholder="Senha" className="w-full px-4 py-2 rounded-lg bg-zinc-700 text-white" />

                    <button className="w-full bg-green-600 hover:bg-green-700 px-4 py-2 rounded-lg text-white font-semibold">
                        Criar Conta
                    </button>
                </form>

                <p className="text-sm text-zinc-400 mt-4 text-center">
                    Já tem conta?
                    <Link to="/login" className="text-blue-400 hover:underline">
                        Fazer login
                    </Link>
                </p>
            </div>
        </div>
    );
}

export default Register;
