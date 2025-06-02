# SuperID - Gerenciador de Autenticações

**Projeto Integrador – Versão 1.0**

## Descrição
O SuperID é um aplicativo Android desenvolvido em Kotlin, que permite a criação de contas, gerenciamento seguro de senhas e login sem senha via QR Code. Integrado com Firebase (Authentication, Firestore e Functions), o sistema possibilita a utilização do SuperID como método de autenticação por sites parceiros.

## Funcionalidades
- **Cadastro de Conta:** Criação e validação de conta com email.
- **Gerenciamento de Senhas:** Cadastro, edição e exclusão de senhas organizadas por categorias.
- **Login via QR Code:** Autenticação sem senha integrada com sites parceiros.
- **Recuperação de Senha Mestre:** Redefinição da senha via email.

## Tecnologias Utilizadas
- **Linguagem:** Kotlin
- **IDE:** Android Studio
- **Backend:** Firebase Authentication, Firestore e Functions
- **Versionamento:** Git com GitHub

## Como Executar
1. Clone o repositório.
2. Execute o aplicativo no Android Studio.
3. Entre nos diretórios corretos
 ```bash
cd .\web\frontend\
```
4. Instale as dependências
```bash 
npm install
```
5. Ainda dentro do diretório frontend inicie o código com:
```bash
npm start
```

##  Como funciona
No app caso seja a primeira vez do usuário será mostrada a página de termos de uso para o usuário, em seguida deve criar a conta, após a criação o app só permitirá que o usuário efetue o login caso verifique o email de cadastro.Em seguida o usuário poderá criar uma categoria ou adicionar uma conta.É possível também scanear um QR Code de um site parceiro para efetuação de login, basta o usuário selecionar o scanner e apontar a câmera do celular para o QR Code no site parceiro.
