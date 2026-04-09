package com.personal.accountantAssistant.ui.scanner.parsers.profiles

import com.personal.accountantAssistant.ui.scanner.parsers.ScanParsingProfile
import java.util.Locale

internal object BrazilProfile {

    private val AMOUNT_KEYWORDS = setOf(
        "VALOR A PAGAR",
        "TOTAL A PAGAR",
        "VALOR DO DOCUMENTO",
        "VALOR TOTAL",
        "TOTAL DA FATURA",
        "TOTAL GERAL",
        "MONTANTE",
        "VALOR COBRADO",
        "VALOR DA FATURA",
        "VALOR LÍQUIDO",
        "VALOR LIQUIDO",
        "VL TOTAL",
        "VL. TOTAL",
        "VLR TOTAL",
        "TOTAL A RECOLHER",
        "TOTAL DA NOTA"
    )

    private val DUE_DATE_KEYWORDS = setOf(
        "VENCIMENTO",
        "VCTO",
        "VENC.",
        "VENCTO",
        "DATA DE VENCIMENTO",
        "DATA VENCIMENTO",
        "DT. VENCIMENTO",
        "DT VENC",
        "DT. VENC",
        "PAGAR ATÉ",
        "PAGAR ATE",
        "PAGUE ATÉ",
        "PAGUE ATE",
        "DATA LIMITE",
        "VÁLIDO ATÉ",
        "VALIDO ATE"
    )

    private val TOTAL_KEYWORDS = setOf(
        "TOTAL",
        "SUBTOTAL",
        "DESCONTO",
        "ECONOMIZE",
        "TAXA",
        "JUROS",
        "ACRESCIMOS",
        "ACRÉSCIMOS"
    )

    private val UNIT_KEYWORDS = setOf(
        "UN", "UND", "UNID",
        "KG", "KGF", "G", "GR", "TON",
        "ML", "L", "LT",
        "PCT", "CX", "DZ", "PC", "FD"
    )

    private val BANNED_TOKENS = setOf(
        "VALIDADE", "VENC", "VCTO",
        "LOTE", "COD", "CÓD", "CODIGO", "CÓDIGO",
        "REF", "ITEM",
        "SUBTOTAL", "DESCONTO", "ECONOMIZE",
        "CNPJ", "CPF", "CEP",
        "ENDERECO", "ENDEREÇO",
        "LOJA", "FILIAL", "CAIXA",
        "CHAVE", "PROTOCOLO",
        "SERIE", "SÉRIE",
        "NUMERO", "NÚMERO",
        "NF-E", "NF-CE",
        "AUTENTICACAO", "AUTENTICAÇÃO"
    )

    private val KNOWN_PROVIDERS = setOf(
        // Elétricas
        "ENERGISA","ENERGIA", "CEMIG", "CPFL", "COPEL", "LIGHT", "COELBA",
        "CELPA", "ENEL", "NEOENERGIA", "CELESC", "COELCE", "ELEKTRO",
        "EQUATORIAL", "EDP", "ELETROPAULO", "BANDEIRANTE", "CELTINS",
        // Telecom / Internet
        "CLARO", "VIVO", "OI", "TIM", "NET", "NEXTEL", "ALGAR",
        "SKY", "DIRECTV",
        // Saneamento
        "SABESP", "CEDAE", "SANEAGO", "EMBASA", "CAGECE", "COMPESA",
        "CORSAN", "SANEPAR", "SANASA", "CASAN", "CAERN",
        "BRK AMBIENTAL", "AEGEA",
        // Gás
        "COMGAS", "COPAGAZ",
        "ÁGUAS", "AGUAS"
    )

    private val BILL_STRONG_KEYWORDS = setOf(
        "FATURA", "CONTA", "BOLETO", "DOCUMENTO",
        "NOTA FISCAL", "CUPOM FISCAL", "COMPROVANTE", "RECIBO"
    )

    private val DATE_PATTERNS = listOf(
        Regex("""\b(\d{2}/\d{2}/\d{2,4})\b"""),
        Regex("""\b(\d{2}-\d{2}-\d{2,4})\b""")
    )

    val profile = ScanParsingProfile(
        locale = Locale("pt", "BR"),
        currencySymbols = setOf("R$", "BRL"),
        amountKeywords = AMOUNT_KEYWORDS,
        dueDateKeywords = DUE_DATE_KEYWORDS,
        totalKeywords = TOTAL_KEYWORDS,
        unitKeywords = UNIT_KEYWORDS,
        bannedTokens = BANNED_TOKENS,
        knownProviders = KNOWN_PROVIDERS,
        datePatterns = DATE_PATTERNS,
        billStrongKeywords = BILL_STRONG_KEYWORDS
    )
}