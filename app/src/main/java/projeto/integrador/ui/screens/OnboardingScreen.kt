package projeto.integrador.ui.screens

import android.R.attr.contentDescription
import android.R.attr.enabled
import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import projeto.integrador.R

@Composable
fun OnboardingScreen(navController: NavHostController, sharedPreferences: SharedPreferences) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fundo da tela
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // Respeita as barras do sistema
                .background(MaterialTheme.colorScheme.background)
        ) {
            val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 }) // Estado da página

            Column(modifier = Modifier.fillMaxSize()) {
                // Pager com as telas de boas-vindas
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    when (page) {
                        0 -> OnboardingPage(
                            title = "SuperID: Um novo jeito de fazer login",
                            description = "Evite memorizar dezenas de senhas...",
                            highlight = "Conecte-se com praticidade e segurança.",
                            imageResId = R.drawable.manwithphone
                        )
                        1 -> OnboardingPage(
                            title = "Login com QR Code, sem digitar senha",
                            description = "Acesse sites parceiros apenas escaneando...",
                            highlight = "A tecnologia do SuperID faz o login por você.",
                            imageResId = R.drawable.phonewithqrcode
                        )
                        2 -> OnboardingPage(
                            title = "Gerencie suas senhas com segurança total",
                            description = "Armazene senhas de sites, apps e até teclados...",
                            highlight = "Suas credenciais organizadas e protegidas.",
                            imageResId = R.drawable.vault
                        )
                        3 -> TermsOfUsePage( // Última página: termos de uso
                            navController = navController,
                            sharedPreferences = sharedPreferences
                        )
                    }
                }

                // Indicadores de página (bolinhas)
                PagerIndicator(
                    pageCount = 4,
                    currentPageIndex = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnboardingPage(title: String, description: String, highlight: String, @DrawableRes imageResId: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineLargeEmphasized, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(36.dp))
        Text(text = description, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = highlight, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Ilustração da página
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )
    }
}

@Composable
fun PagerIndicator(pageCount: Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    // Linha com os indicadores de página
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            val color = if (index == currentPageIndex) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun TermsOfUsePage(navController: NavHostController, sharedPreferences: SharedPreferences) {
    var accepted by remember { mutableStateOf(false) } // Estado do checkbox

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Título
        Text(
            text = "Termos de Uso",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Caixa de texto com os termos
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(8.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "$termosDeUso\n\n$politicaPrivacidade",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Checkbox de aceitação dos termos
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = accepted, onCheckedChange = { accepted = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Li e estou de acordo com os Termos de Uso e Política de Privacidade")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de continuar (só habilitado se os termos forem aceitos)
        Button(
            onClick = {
                sharedPreferences.edit(commit = true) {
                    putBoolean("isFirstTime", false) // Marca que o onboarding já foi visto
                }
                navController.navigate("signUp") // Vai para tela de cadastro
            },
            shape = RoundedCornerShape(12.dp),
            enabled = accepted,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Continuar", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

val termosDeUso = """
        TERMO DE USO DO APLICATIVO SUPERID

        Este Termo de Uso apresenta as “Condições Gerais” aplicáveis ao uso do aplicativo SuperID, desenvolvido com fins educacionais pelos alunos do curso de Engenharia de Software da Pontifícia Universidade Católica de Campinas.

        1. ACEITAÇÃO DOS TERMOS
        Ao criar uma conta no SuperID, o usuário declara ter lido, compreendido e aceito, sem reservas, todas as cláusulas deste Termo.

        2. DO OBJETO
        O SuperID tem como finalidade permitir que usuários armazenem senhas de forma criptografada e realizem login sem o uso de senha em sites parceiros. O app é destinado exclusivamente ao uso acadêmico.

        3. CADASTRO DO USUÁRIO
        Para utilizar o aplicativo, o usuário deve fornecer nome, e-mail válido e senha mestre. O e-mail será validado por meio do Firebase Authentication.

        4. DAS RESPONSABILIDADES DO USUÁRIO
        O usuário se compromete a:
        - Fornecer informações verídicas;
        - Manter sua senha mestre em sigilo;
        - Utilizar o aplicativo de forma ética e legal.

        5. LIMITAÇÃO DE RESPONSABILIDADE
        O SuperID é um aplicativo acadêmico, sem garantias de segurança em nível comercial. O uso do app é de responsabilidade exclusiva do usuário.

        6. MODIFICAÇÕES DOS TERMOS
        Os termos poderão ser modificados a qualquer momento pelos desenvolvedores, sendo recomendada a verificação periódica.

        7. CONTATO
        Dúvidas poderão ser encaminhadas aos professores orientadores do Projeto Integrador III: Prof. Mateus Dias, Profa. Renata Arantes e Prof. Luã Marcelo.
    """.trimIndent()

val politicaPrivacidade = """
        POLÍTICA DE PRIVACIDADE DO APLICATIVO SUPERID

        Esta Política de Privacidade descreve como o aplicativo SuperID coleta, armazena e utiliza os dados dos usuários.

        1. INFORMAÇÕES COLETADAS
        - Nome, e-mail e senha mestre (armazenada de forma segura);
        - UID e IMEI do dispositivo;
        - Senhas e categorias cadastradas pelo usuário;
        - Tokens de autenticação.

        2. FINALIDADE DOS DADOS
        Os dados coletados são utilizados para:
        - Gerenciar a conta do usuário;
        - Armazenar senhas de forma segura;
        - Permitir login sem senha em sites parceiros.

        3. COMPARTILHAMENTO DE DADOS
        Os dados não são compartilhados com terceiros, exceto quando necessário para o funcionamento do login com QR Code em sites parceiros.

        4. ARMAZENAMENTO DOS DADOS
        Os dados são armazenados no Firebase Firestore com criptografia, utilizando autenticação via Firebase.

        5. DIREITOS DO USUÁRIO
        O usuário pode:
        - Solicitar a exclusão de sua conta;
        - Redefinir sua senha mestre;
        - Revogar permissões do app.

        6. SEGURANÇA
        O aplicativo utiliza criptografia e tokens para garantir a segurança das informações armazenadas.

        7. ALTERAÇÕES NA POLÍTICA
        Esta política poderá ser alterada a qualquer momento. Recomendamos que o usuário a revise periodicamente.

        8. CONTATO
        Para dúvidas, entre em contato com a equipe do Projeto Integrador III da PUC-Campinas.
    """.trimIndent()