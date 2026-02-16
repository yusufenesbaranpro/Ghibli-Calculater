package calculator;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * Hesap makinesi FXML Controller sinifi.
 *
 * Kullanici etkilesimlerini yonetir:
 * - Buton tiklamalari -> ifade olusturma
 * - Esittir (=) -> ifadeyi degerlendirme
 * - C / BS -> temizleme / silme
 * - Gecmis paneli -> toggle/temizle
 * - Klavye destegi
 */
public class CalculatorController {

    @FXML
    private Label expressionLabel;
    @FXML
    private Label resultLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private VBox historyPanel;
    @FXML
    private ListView<String> historyList;
    @FXML
    private VBox scientificPanel;
    @FXML
    private Button modBtn;
    @FXML
    private Button backspaceBtn;
    @FXML
    private Label memoryIndicator;
    @FXML
    private Button themeToggle;

    private final StringBuilder currentExpression = new StringBuilder();
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();
    private final HistoryManager historyManager = new HistoryManager(5);
    private boolean lastResultShown = false;
    private double memory = 0.0;
    private boolean isDarkMode = false;

    @FXML
    public void initialize() {
        // Mod butonuna % text'i programatik olarak ayarla (FXML'de % sorun cikariyor)
        if (modBtn != null) {
            modBtn.setText("%");
        }

        // Backspace butonuna unicode karakter ata
        if (backspaceBtn != null) {
            backspaceBtn.setText("\u232B");
        }

        // Klavye destegini sahne hazir olunca ekle
        resultLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress);
            }
        });
    }

    // ====================================================
    // BUTON OLAY ISLEYICILERI
    // ====================================================

    @FXML
    public void handleInput(ActionEvent event) {
        String value = ((Button) event.getSource()).getText();

        // Goruntu karakterlerini gercek operatorlere donustur
        value = mapDisplayToInternal(value);

        // Sonuc gosterildikten sonra sayi yazilirsa sifirdan basla
        if (lastResultShown && isDigitOrDot(value)) {
            currentExpression.setLength(0);
        }
        lastResultShown = false;

        // Hata mesajını gizle
        hideError();

        currentExpression.append(value);
        updateDisplay();
    }

    @FXML
    public void handleEquals(ActionEvent event) {
        evaluateExpression();
    }

    @FXML
    public void handleSpecial(ActionEvent event) {
        String value = ((Button) event.getSource()).getText();

        if ("C".equals(value)) {
            currentExpression.setLength(0);
            expressionLabel.setText("");
            resultLabel.setText("0");
            lastResultShown = false;
        } else {
            // Backspace (BS veya unicode)
            if (currentExpression.length() > 0) {
                currentExpression.deleteCharAt(currentExpression.length() - 1);
            }
            if (currentExpression.length() == 0) {
                resultLabel.setText("0");
                expressionLabel.setText("");
            } else {
                updateDisplay();
            }
            lastResultShown = false;
        }
    }

    @FXML
    public void handleHistory(ActionEvent event) {
        // Geçmiş panelini aç/kapat (toggle)
        boolean show = !historyPanel.isVisible();
        historyPanel.setVisible(show);
        historyPanel.setManaged(show);
        
        // Geçmiş gösteriliyorsa listeyi güncelle
        if (show && historyList != null) {
            historyList.setItems(FXCollections.observableArrayList(historyManager.getHistory()));
        }
        
        // Pencere boyutunu ayarla
        if (resultLabel.getScene() != null && resultLabel.getScene().getWindow() != null) {
            javafx.stage.Stage stage = (javafx.stage.Stage) resultLabel.getScene().getWindow();
            if (show) {
                // Geçmiş açıldığında pencereyi genişlet
                stage.setWidth(620);
            } else {
                // Geçmiş kapandığında standart boyuta dön
                stage.setWidth(380);
            }
        }
    }

    @FXML
    public void handleClearHistory(ActionEvent event) {
        historyManager.clear();
        historyList.setItems(FXCollections.observableArrayList());
    }

    @FXML
    public void handleScientific(ActionEvent event) {
        boolean show = !scientificPanel.isVisible();
        scientificPanel.setVisible(show);
        scientificPanel.setManaged(show);
    }

    @FXML
    public void handleMemory(ActionEvent event) {
        String cmd = ((Button) event.getSource()).getText();
        
        try {
            switch (cmd) {
                case "MC": // Memory Clear
                    memory = 0.0;
                    updateMemoryIndicator();
                    break;
                    
                case "MR": // Memory Recall
                    if (memory != 0.0) {
                        currentExpression.setLength(0);
                        currentExpression.append(formatResult(memory));
                        updateDisplay();
                        lastResultShown = true;
                    }
                    break;
                    
                case "M+": // Memory Add
                    if (resultLabel.getText() != null && !resultLabel.getText().equals("0") 
                        && !resultLabel.getText().equals("Hata")) {
                        try {
                            double value = Double.parseDouble(resultLabel.getText().replace(",", ""));
                            memory += value;
                            updateMemoryIndicator();
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                    break;
                    
                case "M-": // Memory Subtract
                    if (resultLabel.getText() != null && !resultLabel.getText().equals("0") 
                        && !resultLabel.getText().equals("Hata")) {
                        try {
                            double value = Double.parseDouble(resultLabel.getText().replace(",", ""));
                            memory -= value;
                            updateMemoryIndicator();
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            showError("Bellek işlemi başarısız: " + e.getMessage());
        }
    }

    private void updateMemoryIndicator() {
        if (memoryIndicator != null) {
            if (memory != 0.0) {
                memoryIndicator.setText("M");
            } else {
                memoryIndicator.setText("");
            }
        }
    }

    @FXML
    public void handleFunction(ActionEvent event) {
        String func = ((Button) event.getSource()).getText();
        
        // Sonuc gosterildikten sonra fonksiyon yazilirsa sifirdan basla
        if (lastResultShown) {
            currentExpression.setLength(0);
        }
        lastResultShown = false;

        // Fonksiyon adını ekle ve parantez aç
        switch (func) {
            case "√":
                currentExpression.append("sqrt(");
                break;
            default:
                currentExpression.append(func).append("(");
                break;
        }
        updateDisplay();
    }

    @FXML
    public void handleCopy(ActionEvent event) {
        String result = resultLabel.getText();
        if (result != null && !result.equals("0") && !result.equals("Hata")) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(result);
            clipboard.setContent(content);
            
            // Kısa bir geri bildirim (opsiyonel)
            String original = result;
            resultLabel.setText("Kopyalandı!");
            new Thread(() -> {
                try {
                    Thread.sleep(800);
                    javafx.application.Platform.runLater(() -> resultLabel.setText(original));
                } catch (InterruptedException e) {
                    // Ignore
                }
            }).start();
        }
    }

    @FXML
    public void handleThemeToggle(ActionEvent event) {
        isDarkMode = !isDarkMode;
        
        // CSS dosyasını değiştir
        String cssFile = isDarkMode ? "styles-dark.css" : "styles.css";
        String cssPath = getClass().getResource(cssFile).toExternalForm();
        
        // Mevcut CSS'i temizle ve yenisini ekle
        resultLabel.getScene().getStylesheets().clear();
        resultLabel.getScene().getStylesheets().add(cssPath);
        
        // Buton metnini güncelle
        if (themeToggle != null) {
            themeToggle.setText(isDarkMode ? "☀️" : "🌙");
        }
    }

    // ====================================================
    // KLAVYE DESTEGI
    // ====================================================

    private void handleKeyPress(KeyEvent event) {
        // Ctrl+C: Sonucu kopyala
        if (event.isControlDown() && event.getCode().toString().equals("C")) {
            handleCopy(null);
            event.consume();
            return;
        }
        
        switch (event.getCode()) {
            case DIGIT0:
            case NUMPAD0:
                appendChar("0");
                break;
            case DIGIT1:
            case NUMPAD1:
                appendChar("1");
                break;
            case DIGIT2:
            case NUMPAD2:
                appendChar("2");
                break;
            case DIGIT3:
            case NUMPAD3:
                appendChar("3");
                break;
            case DIGIT4:
            case NUMPAD4:
                appendChar("4");
                break;
            case DIGIT5:
            case NUMPAD5:
                appendChar("5");
                break;
            case DIGIT6:
            case NUMPAD6:
                appendChar("6");
                break;
            case DIGIT7:
            case NUMPAD7:
                appendChar("7");
                break;
            case DIGIT8:
            case NUMPAD8:
                appendChar("8");
                break;
            case DIGIT9:
            case NUMPAD9:
                appendChar("9");
                break;
            case ADD:
                appendChar("+");
                break;
            case SUBTRACT:
                appendChar("-");
                break;
            case MULTIPLY:
                appendChar("*");
                break;
            case DIVIDE:
                appendChar("/");
                break;
            case PERIOD:
            case DECIMAL:
                appendChar(".");
                break;
            case ENTER:
            case EQUALS:
                evaluateExpression();
                break;
            case BACK_SPACE:
                if (currentExpression.length() > 0) {
                    currentExpression.deleteCharAt(currentExpression.length() - 1);
                    if (currentExpression.length() == 0) {
                        resultLabel.setText("0");
                        expressionLabel.setText("");
                    } else {
                        updateDisplay();
                    }
                }
                break;
            case ESCAPE:
            case DELETE:
                currentExpression.setLength(0);
                expressionLabel.setText("");
                resultLabel.setText("0");
                lastResultShown = false;
                break;
            default:
                String ch = event.getText();
                if (ch != null && ch.length() == 1 && "+-*/().%^".indexOf(ch.charAt(0)) >= 0) {
                    appendChar(ch);
                }
                break;
        }
        event.consume();
    }

    // ====================================================
    // YARDIMCI METOTLAR
    // ====================================================

    private void evaluateExpression() {
        if (currentExpression.length() == 0)
            return;

        // Önceki hata mesajını temizle
        hideError();

        String expr = currentExpression.toString();
        try {
            double result = evaluator.evaluate(expr);
            String formatted = formatResult(result);

            expressionLabel.setText(mapInternalToDisplay(expr) + " =");
            resultLabel.setText(formatted);
            historyManager.addEntry(mapInternalToDisplay(expr), result);
            
            // Geçmişi otomatik güncelle
            updateHistory();

            currentExpression.setLength(0);
            currentExpression.append(formatted);
            lastResultShown = true;

        } catch (CalculatorException e) {
            expressionLabel.setText(mapInternalToDisplay(expr));
            resultLabel.setText("Hata");
            
            // Hata mesajını göster
            showError(e.getMessage());
            
            historyManager.addErrorEntry(
                    mapInternalToDisplay(expr),
                    e.getErrorType().getDescription());
                    
            // Geçmişi otomatik güncelle
            updateHistory();
        }
    }
    
    private void updateHistory() {
        if (historyList != null) {
            historyList.setItems(FXCollections.observableArrayList(historyManager.getHistory()));
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            
            // 5 saniye sonra otomatik olarak gizle
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    javafx.application.Platform.runLater(this::hideError);
                } catch (InterruptedException e) {
                    // Ignore
                }
            }).start();
        }
    }

    private void hideError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    private void appendChar(String ch) {
        if (lastResultShown && isDigitOrDot(ch)) {
            currentExpression.setLength(0);
        }
        lastResultShown = false;
        hideError();
        currentExpression.append(ch);
        updateDisplay();
    }

    private void updateDisplay() {
        String display = mapInternalToDisplay(currentExpression.toString());
        resultLabel.setText(display.isEmpty() ? "0" : display);
    }

    private String mapDisplayToInternal(String value) {
        switch (value) {
            case "x":
                return "*";
            default:
                return value;
        }
    }

    private String mapInternalToDisplay(String expr) {
        return expr.replace("*", "x").replace("/", "/");
    }

    private boolean isDigitOrDot(String s) {
        return s.length() == 1 && (Character.isDigit(s.charAt(0)) || s.charAt(0) == '.');
    }

    private String formatResult(double result) {
        if (result == (long) result && !Double.isInfinite(result)) {
            return String.valueOf((long) result);
        }
        return String.format("%.8g", result);
    }
}
