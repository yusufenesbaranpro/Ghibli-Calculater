package calculator;

/**
 * Hesap makinesi işlemleri sırasında oluşabilecek hataları yönetmek için
 * tasarlanmış özel exception sınıfı.
 * 
 * Desteklenen hata türleri:
 *  - Sıfıra bölme hatası
 *  - Geçersiz karakter girişi
 *  - Eksik veya fazla parantez
 *  - Geçersiz matematiksel ifade
 * 
 * @author Yusuf
 */
public class CalculatorException extends Exception {

    /** Hata türlerini tanımlayan enum */
    public enum ErrorType {
        DIVISION_BY_ZERO("Sıfıra bölme hatası"),
        INVALID_CHARACTER("Geçersiz karakter"),
        MISMATCHED_PARENTHESES("Eşleşmeyen parantez"),
        INVALID_EXPRESSION("Geçersiz matematiksel ifade"),
        EMPTY_EXPRESSION("Boş ifade");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final ErrorType errorType;

    /**
     * Belirli bir hata türü ve mesaj ile CalculatorException oluşturur.
     *
     * @param errorType Hata türü
     * @param message   Detaylı hata mesajı
     */
    public CalculatorException(ErrorType errorType, String message) {
        super(String.format("[%s] %s: %s", errorType.name(), errorType.getDescription(), message));
        this.errorType = errorType;
    }

    /**
     * Belirli bir hata türü, mesaj ve sebep ile CalculatorException oluşturur.
     *
     * @param errorType Hata türü  
     * @param message   Detaylı hata mesajı
     * @param cause     Bu hataya sebep olan orijinal exception
     */
    public CalculatorException(ErrorType errorType, String message, Throwable cause) {
        super(String.format("[%s] %s: %s", errorType.name(), errorType.getDescription(), message), cause);
        this.errorType = errorType;
    }

    /**
     * Hata türünü döndürür.
     *
     * @return ErrorType enum değeri
     */
    public ErrorType getErrorType() {
        return errorType;
    }
}
