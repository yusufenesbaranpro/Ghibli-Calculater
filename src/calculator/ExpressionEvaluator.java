package calculator;

import java.util.Stack;

/**
 * Kullanıcıdan String olarak alınan matematiksel ifadeleri çözen sınıf.
 * 
 * Shunting-yard algoritmasından ilham alan bir yaklaşım kullanır:
 * 
 * ADIM 1: İfadeyi token'lara ayır (sayılar, operatörler, parantezler)
 * ADIM 2: İki yığın (stack) kullan:
 * - Sayılar yığını → operandları tutar
 * - Operatör yığını → operatörleri ve parantezleri tutar
 * ADIM 3: Her token için:
 * - Sayı ise → sayı yığınına ekle
 * - '(' ise → operatör yığınına ekle
 * - ')' ise → '(' görünceye kadar operatörleri uygula
 * - Operatör ise → öncelik kuralına göre bekleyenleri uygula, sonra ekle
 * ADIM 4: Kalan operatörleri uygula
 * 
 * İşlem önceliği:
 * Seviye 3: ^ (üs alma — sağdan sola)
 * Seviye 2: *, /, % (çarpma, bölme, mod)
 * Seviye 1: +, - (toplama, çıkarma)
 * 
 * Örnek: "(5 + 3) * 2 / 4" → 4.0
 * 
 * @author Yusuf
 */
public class ExpressionEvaluator {

    /** Hesaplamalar için kullanılan Calculator nesnesi */
    private final Calculator calculator;

    public ExpressionEvaluator() {
        this.calculator = new Calculator();
    }

    /**
     * Verilen matematiksel ifadeyi değerlendirir ve sonucu döndürür.
     *
     * @param expression Matematiksel ifade (örn: "(5 + 3) * 2 / 4")
     * @return Hesaplama sonucu
     * @throws CalculatorException Hatalı ifade durumunda
     */
    public double evaluate(String expression) throws CalculatorException {

        // ── Boşluk kontrolü ──────────────────────────────────
        if (expression == null || expression.trim().isEmpty()) {
            throw new CalculatorException(
                    CalculatorException.ErrorType.EMPTY_EXPRESSION,
                    "Hesaplanacak bir ifade girilmedi.");
        }

        // ── Boşlukları temizle ───────────────────────────────
        String expr = expression.replaceAll("\\s+", "");

        // ── Parantez dengesi kontrolü ────────────────────────
        validateParentheses(expr);

        // ── İki yığın (stack) oluştur ────────────────────────
        // Sayılar yığını: operandları tutar
        Stack<Double> numbers = new Stack<>();
        // Operatör yığını: operatörleri ve parantezleri tutar
        Stack<Character> operators = new Stack<>();

        // ── İfadeyi karakter karakter tara ───────────────────
        int i = 0;
        while (i < expr.length()) {
            char ch = expr.charAt(i);

            // ── DURUM 0: Fonksiyon çağrısı (sin, cos, sqrt, ln, log, abs) ──
            if (Character.isLetter(ch)) {
                StringBuilder funcName = new StringBuilder();
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                    funcName.append(expr.charAt(i));
                    i++;
                }
                String func = funcName.toString().toLowerCase();
                
                // π (pi) ve e sabitleri
                if (func.equals("pi")) {
                    numbers.push(Math.PI);
                    continue;
                } else if (func.equals("e")) {
                    numbers.push(Math.E);
                    continue;
                }
                
                // Fonksiyon parantezi bekle
                if (i >= expr.length() || expr.charAt(i) != '(') {
                    throw new CalculatorException(
                            CalculatorException.ErrorType.INVALID_EXPRESSION,
                            String.format("Fonksiyon '%s' sonrasında '(' bekleniyordu.", func));
                }
                
                // İç ifadeyi bul
                i++; // '(' karakterini atla
                int depth = 1;
                int start = i;
                while (i < expr.length() && depth > 0) {
                    if (expr.charAt(i) == '(') depth++;
                    else if (expr.charAt(i) == ')') depth--;
                    i++;
                }
                
                String innerExpr = expr.substring(start, i - 1);
                double arg = evaluate(innerExpr); // Recursive çağrı
                
                // Fonksiyonu uygula
                double result;
                switch (func) {
                    case "sin":
                        result = calculator.sin(arg);
                        break;
                    case "cos":
                        result = calculator.cos(arg);
                        break;
                    case "tan":
                        result = calculator.tan(arg);
                        break;
                    case "sqrt":
                        result = calculator.sqrt(arg);
                        break;
                    case "ln":
                        result = calculator.ln(arg);
                        break;
                    case "log":
                        result = calculator.log(arg);
                        break;
                    case "abs":
                        result = calculator.abs(arg);
                        break;
                    default:
                        throw new CalculatorException(
                                CalculatorException.ErrorType.INVALID_EXPRESSION,
                                String.format("Bilinmeyen fonksiyon: '%s'", func));
                }
                numbers.push(result);
                continue;
            }

            // ── DURUM 1: Karakter bir rakam veya ondalık noktası ──
            if (Character.isDigit(ch) || ch == '.') {
                // Sayının tamamını oku (birden fazla basamak olabilir)
                StringBuilder number = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    number.append(expr.charAt(i));
                    i++;
                }
                try {
                    numbers.push(Double.parseDouble(number.toString()));
                } catch (NumberFormatException e) {
                    throw new CalculatorException(
                            CalculatorException.ErrorType.INVALID_EXPRESSION,
                            String.format("'%s' geçerli bir sayı değil.", number.toString()));
                }
                // Faktöriyel kontrolü ('5!' gibi)
                if (i < expr.length() && expr.charAt(i) == '!') {
                    double num = numbers.pop();
                    numbers.push(calculator.factorial(num));
                    i++;
                }
                continue; // i zaten sayı sonunda, while'a geri dön
            }

            // ── DURUM 2: Negatif sayı desteği ────────────────────
            // Eğer '-' ifadenin başında veya '(' sonrasındaysa, bu bir negatif işaretidir
            if (ch == '-' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(expr.charAt(i - 1)))) {
                StringBuilder number = new StringBuilder("-");
                i++;
                if (i >= expr.length()
                        || (!Character.isDigit(expr.charAt(i)) && expr.charAt(i) != '.' && expr.charAt(i) != '(')) {
                    throw new CalculatorException(
                            CalculatorException.ErrorType.INVALID_EXPRESSION,
                            "'-' işaretinden sonra bir sayı veya ifade bekleniyordu.");
                }
                // Eğer '-(' gibi bir durum varsa, -1 * (...) olarak işle
                if (expr.charAt(i) == '(') {
                    numbers.push(-1.0);
                    operators.push('*');
                    continue;
                }
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    number.append(expr.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(number.toString()));
                continue;
            }

            // ── DURUM 3: Açılış parantezi ────────────────────────
            if (ch == '(') {
                operators.push(ch);
                i++;
                continue;
            }

            // ── DURUM 4: Kapanış parantezi ───────────────────────
            // '(' görünene kadar operatörleri uygula
            if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    applyTopOperator(numbers, operators);
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // '(' karakterini çıkar
                }
                i++;
                // Faktöriyel kontrolü (')!' gibi)
                if (i < expr.length() && expr.charAt(i) == '!') {
                    if (numbers.isEmpty()) {
                        throw new CalculatorException(
                                CalculatorException.ErrorType.INVALID_EXPRESSION,
                                "Faktöriyel için sayı bulunamadı.");
                    }
                    double num = numbers.pop();
                    numbers.push(calculator.factorial(num));
                    i++;
                }
                continue;
            }

            // ── DURUM 4.5: Faktöriyel (!) ───────────────────────
            if (ch == '!') {
                if (numbers.isEmpty()) {
                    throw new CalculatorException(
                            CalculatorException.ErrorType.INVALID_EXPRESSION,
                            "Faktöriyel için sayı bulunamadı.");
                }
                double num = numbers.pop();
                numbers.push(calculator.factorial(num));
                i++;
                continue;
            }

            // ── DURUM 5: Operatör (+, -, *, /, %, ^) ────────────
            if (isOperator(ch)) {
                // Mevcut operatörün önceliği, yığındakinden düşük veya eşitse
                // önce yığındaki operatörleri uygula
                while (!operators.isEmpty()
                        && operators.peek() != '('
                        && shouldApplyFirst(operators.peek(), ch)) {
                    applyTopOperator(numbers, operators);
                }
                operators.push(ch);
                i++;
                continue;
            }

            // ── DURUM 6: Tanınmayan karakter ────────────────────
            throw new CalculatorException(
                    CalculatorException.ErrorType.INVALID_CHARACTER,
                    String.format(
                            "'%c' karakteri tanınmıyor. Sadece sayılar ve +, -, *, /, %%, ^ operatörleri kullanılabilir.",
                            ch));
        }

        // ── Kalan tüm operatörleri uygula ───────────────────
        while (!operators.isEmpty()) {
            if (operators.peek() == '(' || operators.peek() == ')') {
                throw new CalculatorException(
                        CalculatorException.ErrorType.MISMATCHED_PARENTHESES,
                        "Parantezler düzgün eşleşmiyor.");
            }
            applyTopOperator(numbers, operators);
        }

        // ── Sonuç kontrolü ──────────────────────────────────
        if (numbers.size() != 1) {
            throw new CalculatorException(
                    CalculatorException.ErrorType.INVALID_EXPRESSION,
                    "İfade düzgün hesaplanamadı. Lütfen ifadenizi kontrol edin.");
        }

        return numbers.pop();
    }

    // ══════════════════════════════════════════════════════════
    // YARDIMCI METOTLAR (private)
    // ══════════════════════════════════════════════════════════

    /**
     * Verilen karakterin bir operatör olup olmadığını kontrol eder.
     */
    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '^';
    }

    /**
     * Bir operatörün öncelik seviyesini döndürür.
     * Yüksek değer = yüksek öncelik
     */
    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1; // En düşük öncelik
            case '*':
            case '/':
            case '%':
                return 2; // Orta öncelik
            case '^':
                return 3; // En yüksek öncelik
            default:
                return 0;
        }
    }

    /**
     * stackOp operatörünün newOp operatöründen önce uygulanıp uygulanmayacağını
     * belirler.
     * 
     * Kural:
     * - stackOp'un önceliği newOp'tan büyükse → evet
     * - Öncelikler eşitse ve operatör sol-ilişkili ise → evet
     * - '^' sağ-ilişkili olduğundan, eşit öncelikte uygulanmaz
     */
    private boolean shouldApplyFirst(char stackOp, char newOp) {
        int stackPrecedence = precedence(stackOp);
        int newPrecedence = precedence(newOp);

        if (stackPrecedence > newPrecedence) {
            return true;
        }
        // Eşit öncelikte: '^' sağdan sola ilişkili, diğerleri soldan sağa
        if (stackPrecedence == newPrecedence && newOp != '^') {
            return true;
        }
        return false;
    }

    /**
     * Operatör yığınının tepesindeki operatörü alıp,
     * sayı yığınından iki operand çekerek işlemi uygular.
     * Sonucu tekrar sayı yığınına koyar.
     */
    private void applyTopOperator(Stack<Double> numbers, Stack<Character> operators)
            throws CalculatorException {

        if (numbers.size() < 2) {
            throw new CalculatorException(
                    CalculatorException.ErrorType.INVALID_EXPRESSION,
                    "İşlem için yeterli sayı yok. İfadenizi kontrol edin.");
        }

        char operator = operators.pop();
        double b = numbers.pop(); // İkinci operand (yığından önce çıkan)
        double a = numbers.pop(); // İlk operand

        // Calculator sınıfını kullanarak işlemi gerçekleştir
        double result = calculator.calculate(a, b, operator);
        numbers.push(result);
    }

    /**
     * Parantezlerin dengeli olup olmadığını kontrol eder.
     * Her '(' için bir ')' olmalı ve ')' hiçbir zaman '(' den önce gelmemeli.
     *
     * @param expression Kontrol edilecek ifade
     * @throws CalculatorException Parantezler dengeli değilse
     */
    private void validateParentheses(String expression) throws CalculatorException {
        int depth = 0;

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
                if (depth < 0) {
                    throw new CalculatorException(
                            CalculatorException.ErrorType.MISMATCHED_PARENTHESES,
                            String.format("Pozisyon %d: Fazladan ')' karakteri bulundu.", i + 1));
                }
            }
        }

        if (depth > 0) {
            throw new CalculatorException(
                    CalculatorException.ErrorType.MISMATCHED_PARENTHESES,
                    String.format("%d adet '(' karakterinin eşi ')' bulunamadı.", depth));
        }
    }
}
