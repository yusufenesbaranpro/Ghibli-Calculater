package calculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Yapılan işlemlerin geçmişini yöneten sınıf.
 * 
 * Özellikler:
 * - Son N işlemi (varsayılan 5) bir ArrayList'te saklar
 * - Kapasite dolduğunda en eski kaydı otomatik siler (FIFO)
 * - İşlem geçmişini listeleme imkânı sunar
 * - Geçmişi temizleme özelliği vardır
 * 
 * @author Yusuf
 */
public class HistoryManager {

    /** İşlem geçmişini tutan liste */
    private final List<String> history;

    /** Tutulacak maksimum kayıt sayısı */
    private final int maxSize;

    /** Varsayılan geçmiş boyutu */
    private static final int DEFAULT_MAX_SIZE = 5;

    /**
     * Varsayılan kapasiteyle (5 kayıt) HistoryManager oluşturur.
     */
    public HistoryManager() {
        this(DEFAULT_MAX_SIZE);
    }

    /**
     * Belirtilen kapasiteyle HistoryManager oluşturur.
     *
     * @param maxSize Tutulacak maksimum işlem sayısı
     */
    public HistoryManager(int maxSize) {
        this.maxSize = Math.max(1, maxSize); // En az 1 kayıt tutulmalı
        this.history = new ArrayList<>();
    }

    /**
     * Yeni bir işlem kaydını geçmişe ekler.
     * Kapasite doluysa en eski kayıt silinir.
     *
     * @param expression Yapılan işlem ifadesi
     * @param result     İşlemin sonucu
     */
    public void addEntry(String expression, double result) {
        // Sonucu düzgün formatlayalım (gereksiz ondalık basamakları kaldır)
        String formattedResult = formatResult(result);
        String entry = String.format("%s = %s", expression, formattedResult);

        // Kapasite doluysa en eski kaydı sil (FIFO — First In First Out)
        if (history.size() >= maxSize) {
            history.remove(0);
        }

        history.add(entry);
    }

    /**
     * Hatalı işlemi de geçmişe kaydeder.
     *
     * @param expression   Yapılan işlem ifadesi
     * @param errorMessage Hata mesajı
     */
    public void addErrorEntry(String expression, String errorMessage) {
        String entry = String.format("%s → HATA: %s", expression, errorMessage);

        if (history.size() >= maxSize) {
            history.remove(0);
        }

        history.add(entry);
    }

    /**
     * Tüm geçmiş kayıtlarını döndürür.
     *
     * @return Geçmiş kayıtlarının kopyası
     */
    public List<String> getHistory() {
        return new ArrayList<>(history); // Savunmacı kopya döndür
    }

    /**
     * Geçmişteki kayıt sayısını döndürür.
     *
     * @return Kayıt sayısı
     */
    public int size() {
        return history.size();
    }

    /**
     * Geçmişin boş olup olmadığını kontrol eder.
     *
     * @return Geçmiş boşsa true
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }

    /**
     * Tüm geçmişi temizler.
     */
    public void clear() {
        history.clear();
    }

    /**
     * Geçmişi güzel formatlı bir String olarak döndürür.
     * Konsola yazdırmak için kullanılır.
     *
     * @return Formatlanmış geçmiş listesi
     */
    public String getFormattedHistory() {
        if (history.isEmpty()) {
            return "  📭  Geçmişte kayıtlı işlem yok.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  📋  Son %d İşlem Geçmişi:\n", history.size()));
        sb.append("  ─────────────────────────────────────\n");

        for (int i = 0; i < history.size(); i++) {
            sb.append(String.format("   %d. %s\n", i + 1, history.get(i)));
        }

        sb.append("  ─────────────────────────────────────");
        return sb.toString();
    }

    // ══════════════════════════════════════════════════════════
    // YARDIMCI METOTLAR (private)
    // ══════════════════════════════════════════════════════════

    /**
     * Sonucu düzgün formatlar.
     * Tam sayı sonuçlarda ondalık gösterimi kaldırır.
     * Örn: 4.0 → "4", 3.14 → "3.14"
     */
    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        }
        return String.valueOf(result);
    }
}
