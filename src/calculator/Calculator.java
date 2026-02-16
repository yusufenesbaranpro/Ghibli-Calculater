package calculator;

/**
 * Temel matematik işlemlerini gerçekleştiren Calculator sınıfı.
 * 
 * Bu sınıf, Clean Code prensiplerine uygun olarak tasarlanmıştır:
 *  - Her metot tek bir sorumluluk taşır (Single Responsibility)
 *  - Hata durumları CalculatorException ile yönetilir
 *  - Metot isimleri açık ve anlaşılırdır
 * 
 * Desteklenen işlemler:
 *  - Toplama  (+)
 *  - Çıkarma  (-)
 *  - Çarpma   (*)
 *  - Bölme    (/)
 *  - Mod alma (%)
 *  - Üs alma  (^)
 * 
 * @author Yusuf
 */
public class Calculator {

    /**
     * İki sayıyı toplar.
     *
     * @param a İlk sayı
     * @param b İkinci sayı
     * @return Toplam sonucu
     */
    public double add(double a, double b) {
        return a + b;
    }

    /**
     * İlk sayıdan ikinci sayıyı çıkarır.
     *
     * @param a İlk sayı (eksilen)
     * @param b İkinci sayı (çıkan)
     * @return Çıkarma sonucu
     */
    public double subtract(double a, double b) {
        return a - b;
    }

    /**
     * İki sayıyı çarpar.
     *
     * @param a İlk sayı
     * @param b İkinci sayı
     * @return Çarpma sonucu
     */
    public double multiply(double a, double b) {
        return a * b;
    }

    /**
     * İlk sayıyı ikinci sayıya böler.
     * Sıfıra bölme durumunda CalculatorException fırlatır.
     *
     * @param a Bölünen
     * @param b Bölen (sıfır olamaz)
     * @return Bölme sonucu
     * @throws CalculatorException Bölen sıfır olduğunda
     */
    public double divide(double a, double b) throws CalculatorException {
        if (b == 0) {
            throw new CalculatorException(
                CalculatorException.ErrorType.DIVISION_BY_ZERO,
                String.format("%.2f / 0 işlemi tanımsızdır.", a)
            );
        }
        return a / b;
    }

    /**
     * İlk sayının ikinci sayıya bölümünden kalanı hesaplar (mod).
     * Sıfıra mod alma durumunda CalculatorException fırlatır.
     *
     * @param a Bölünen
     * @param b Bölen (sıfır olamaz)
     * @return Mod sonucu
     * @throws CalculatorException Bölen sıfır olduğunda
     */
    public double modulo(double a, double b) throws CalculatorException {
        if (b == 0) {
            throw new CalculatorException(
                CalculatorException.ErrorType.DIVISION_BY_ZERO,
                String.format("%.2f %% 0 işlemi tanımsızdır.", a)
            );
        }
        return a % b;
    }

    /**
     * Üs alma işlemi yapar.
     *
     * @param base  Taban sayı
     * @param power Üs değeri
     * @return Üs alma sonucu
     */
    public double power(double base, double power) {
        return Math.pow(base, power);
    }

    /**
     * Karekök hesaplar.
     *
     * @param a Sayı (negatif olamaz)
     * @return Karekök sonucu
     * @throws CalculatorException Negatif sayı durumunda
     */
    public double sqrt(double a) throws CalculatorException {
        if (a < 0) {
            throw new CalculatorException(
                CalculatorException.ErrorType.INVALID_EXPRESSION,
                String.format("%.2f sayısının karekökü tanımsızdır (negatif).", a)
            );
        }
        return Math.sqrt(a);
    }

    /**
     * Sinüs hesaplar (radyan cinsinden).
     *
     * @param angle Açı (radyan)
     * @return Sinüs değeri
     */
    public double sin(double angle) {
        return Math.sin(angle);
    }

    /**
     * Kosinüs hesaplar (radyan cinsinden).
     *
     * @param angle Açı (radyan)
     * @return Kosinüs değeri
     */
    public double cos(double angle) {
        return Math.cos(angle);
    }

    /**
     * Tanjant hesaplar (radyan cinsinden).
     *
     * @param angle Açı (radyan)
     * @return Tanjant değeri
     */
    public double tan(double angle) {
        return Math.tan(angle);
    }

    /**
     * Doğal logaritma (ln) hesaplar.
     *
     * @param a Sayı (pozitif olmalı)
     * @return ln(a)
     * @throws CalculatorException Negatif veya sıfır durumunda
     */
    public double ln(double a) throws CalculatorException {
        if (a <= 0) {
            throw new CalculatorException(
                CalculatorException.ErrorType.INVALID_EXPRESSION,
                String.format("ln(%.2f) tanımsızdır (pozitif olmalı).", a)
            );
        }
        return Math.log(a);
    }

    /**
     * 10 tabanlı logaritma hesaplar.
     *
     * @param a Sayı (pozitif olmalı)
     * @return log10(a)
     * @throws CalculatorException Negatif veya sıfır durumunda
     */
    public double log(double a) throws CalculatorException {
        if (a <= 0) {
            throw new CalculatorException(
                CalculatorException.ErrorType.INVALID_EXPRESSION,
                String.format("log(%.2f) tanımsızdır (pozitif olmalı).", a)
            );
        }
        return Math.log10(a);
    }

    /**
     * Mutlak değer hesaplar.
     *
     * @param a Sayı
     * @return |a|
     */
    public double abs(double a) {
        return Math.abs(a);
    }

    /**
     * Faktöriyel hesaplar (n!).
     *
     * @param n Pozitif tamsayı
     * @return n!
     * @throws CalculatorException Negatif veya ondalıklı sayı durumunda
     */
    public double factorial(double n) throws CalculatorException {
        if (n < 0) {
            throw new CalculatorException(
                CalculatorException.ErrorType.INVALID_EXPRESSION,
                "Faktöriyel negatif sayılar için tanımsızdır."
            );
        }
        if (n != Math.floor(n)) {
            throw new CalculatorException(
                CalculatorException.ErrorType.INVALID_EXPRESSION,
                "Faktöriyel sadece tam sayılar için tanımlıdır."
            );
        }
        if (n > 170) {
            throw new CalculatorException(
                CalculatorException.ErrorType.INVALID_EXPRESSION,
                "Faktöriyel değeri çok büyük (overflow)."
            );
        }
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * Operatör karakterine göre uygun işlemi gerçekleştirir.
     * ExpressionEvaluator tarafından kullanılan yardımcı metot.
     *
     * @param a        İlk operand
     * @param b        İkinci operand
     * @param operator İşlem operatörü (+, -, *, /, %, ^)
     * @return İşlem sonucu
     * @throws CalculatorException Geçersiz operatör veya sıfıra bölme durumunda
     */
    public double calculate(double a, double b, char operator) throws CalculatorException {
        switch (operator) {
            case '+': return add(a, b);
            case '-': return subtract(a, b);
            case '*': return multiply(a, b);
            case '/': return divide(a, b);
            case '%': return modulo(a, b);
            case '^': return power(a, b);
            default:
                throw new CalculatorException(
                    CalculatorException.ErrorType.INVALID_CHARACTER,
                    String.format("'%c' geçerli bir operatör değil. Kullanılabilir: +, -, *, /, %%, ^", operator)
                );
        }
    }
}
