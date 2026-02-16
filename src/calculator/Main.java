package calculator;

import java.util.Scanner;

/**
 * Hesap makinesi uygulamasının giriş noktası.
 * 
 * Kullanıcıya interaktif bir konsol arayüzü sunar:
 * - Matematiksel ifadeler girilebilir (örn: (5+3)*2/4)
 * - İşlem geçmişi görüntülenebilir
 * - Geçmiş temizlenebilir
 * 
 * Komutlar:
 * gecmis → Son 5 işlemi listeler
 * temizle → Geçmişi temizler
 * cikis → Programdan çıkar
 * 
 * @author Yusuf
 */
public class Main {

    // ── Sabitler ─────────────────────────────────────────────
    private static final String COMMAND_HISTORY = "gecmis";
    private static final String COMMAND_CLEAR = "temizle";
    private static final String COMMAND_EXIT = "cikis";
    private static final String COMMAND_HELP = "yardim";

    public static void main(String[] args) {

        // ── Bileşenleri oluştur ──────────────────────────────
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        HistoryManager history = new HistoryManager(5); // Son 5 işlemi sakla
        Scanner scanner = new Scanner(System.in);

        // ── Hoş geldiniz mesajı ──────────────────────────────
        printWelcome();

        // ── Önce temel işlemleri test et ─────────────────────
        runBasicTests();

        System.out.println("\n  ✏️   İfadenizi girin (çıkış için 'cikis' yazın):\n");

        // ── Ana döngü ────────────────────────────────────────
        while (true) {
            System.out.print("  ▶  ");
            String input = scanner.nextLine().trim();

            // Boş girişi atla
            if (input.isEmpty()) {
                continue;
            }

            // ── Komut kontrolü ───────────────────────────────
            String command = input.toLowerCase();

            if (command.equals(COMMAND_EXIT)) {
                System.out.println("\n  👋  Hesap makinesi kapatılıyor. Hoşça kalın!\n");
                break;
            }

            if (command.equals(COMMAND_HISTORY)) {
                System.out.println();
                System.out.println(history.getFormattedHistory());
                System.out.println();
                continue;
            }

            if (command.equals(COMMAND_CLEAR)) {
                history.clear();
                System.out.println("\n  🗑️   Geçmiş temizlendi.\n");
                continue;
            }

            if (command.equals(COMMAND_HELP)) {
                printHelp();
                continue;
            }

            // ── İfadeyi değerlendir ──────────────────────────
            try {
                double result = evaluator.evaluate(input);
                String formattedResult = formatResult(result);

                System.out.printf("  ✅  %s = %s%n%n", input, formattedResult);

                // Geçmişe ekle
                history.addEntry(input, result);

            } catch (CalculatorException e) {
                System.out.printf("  ❌  %s%n%n", e.getMessage());

                // Hatalı işlemi de geçmişe kaydet
                history.addErrorEntry(input, e.getErrorType().getDescription());
            }
        }

        scanner.close();
    }

    // ══════════════════════════════════════════════════════════
    // TEMEL İŞLEM TESTLERİ
    // ══════════════════════════════════════════════════════════

    /**
     * Calculator sınıfının temel işlemlerini test eder.
     * Programın doğru çalıştığını doğrulamak için başlangıçta çalıştırılır.
     */
    private static void runBasicTests() {
        Calculator calc = new Calculator();

        System.out.println("  ─────────────────────────────────────");
        System.out.println("  🧪  Temel İşlem Testleri:");
        System.out.println("  ─────────────────────────────────────");

        // Toplama testi
        System.out.printf("   ✓  10 + 5  = %.0f%n", calc.add(10, 5));

        // Çıkarma testi
        System.out.printf("   ✓  20 - 8  = %.0f%n", calc.subtract(20, 8));

        // Çarpma testi
        System.out.printf("   ✓  6 * 7   = %.0f%n", calc.multiply(6, 7));

        // Bölme testi
        try {
            System.out.printf("   ✓  15 / 4  = %.2f%n", calc.divide(15, 4));
        } catch (CalculatorException e) {
            System.out.printf("   ✗  15 / 4  = HATA: %s%n", e.getMessage());
        }

        // Sıfıra bölme testi (hata bekleniyor)
        try {
            calc.divide(10, 0);
            System.out.println("   ✗  10 / 0  = HATA YAKALANMADI!");
        } catch (CalculatorException e) {
            System.out.printf("   ✓  10 / 0  → Hata yakalandı: %s%n", e.getErrorType().getDescription());
        }

        // Üs alma testi
        System.out.printf("   ✓  2 ^ 10  = %.0f%n", calc.power(2, 10));

        System.out.println("  ─────────────────────────────────────");
        System.out.println("  ✅  Tüm temel testler başarılı!");
    }

    // ══════════════════════════════════════════════════════════
    // YARDIMCI METOTLAR
    // ══════════════════════════════════════════════════════════

    /**
     * Hoş geldiniz mesajını yazdırır.
     */
    private static void printWelcome() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║                                      ║");
        System.out.println("  ║    🧮  JAVA HESAP MAKİNESİ  🧮      ║");
        System.out.println("  ║        v1.0 — OOP Edition            ║");
        System.out.println("  ║                                      ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Yardım menüsünü yazdırır.
     */
    private static void printHelp() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║          📖  KULLANIM KILAVUZU       ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.println("  ║                                      ║");
        System.out.println("  ║  İfade örnekleri:                     ║");
        System.out.println("  ║    5 + 3            → 8               ║");
        System.out.println("  ║    (10 + 2) * 5     → 60              ║");
        System.out.println("  ║    2 ^ 8            → 256             ║");
        System.out.println("  ║    (5 + 3) * 2 / 4  → 4              ║");
        System.out.println("  ║    -5 + 10           → 5              ║");
        System.out.println("  ║                                      ║");
        System.out.println("  ║  Operatörler:                         ║");
        System.out.println("  ║    +  Toplama                         ║");
        System.out.println("  ║    -  Çıkarma                         ║");
        System.out.println("  ║    *  Çarpma                          ║");
        System.out.println("  ║    /  Bölme                           ║");
        System.out.println("  ║    %  Mod (kalan)                     ║");
        System.out.println("  ║    ^  Üs alma                         ║");
        System.out.println("  ║                                      ║");
        System.out.println("  ║  Komutlar:                            ║");
        System.out.println("  ║    gecmis   → İşlem geçmişini göster  ║");
        System.out.println("  ║    temizle  → Geçmişi temizle         ║");
        System.out.println("  ║    yardim   → Bu menüyü göster        ║");
        System.out.println("  ║    cikis    → Programdan çık          ║");
        System.out.println("  ║                                      ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Sonucu düzgün formatlar (tam sayı ise ondalık göstermez).
     */
    private static String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        }
        return String.valueOf(result);
    }
}
