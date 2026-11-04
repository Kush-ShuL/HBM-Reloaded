package com.hbm.client.qmaw;

import com.hbm.client.gui.LegacyScreen;
import com.hbm.network.rtty.autocal.script.Calculator;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

/** X5863 calculator/manual search, rendered with the active native Font and EditBox. */
public final class CalculatorScreen extends LegacyScreen {
    private static final int X_SIZE = 220, Y_SIZE = 50, BORDER_WIDTH = 2, MAX_HISTORY = 6, QMAW_SPACING = 7;
    private static final int BORDER_COLOR = 0xFF2d2d2d, FILL_COLOR = 0xFF3d3d3d, SELECT_COLOR = 0xFF111111;
    private record Calculation(String expression, double result) { }
    private static final List<Calculation> calcHistory = new ArrayList<>(MAX_HISTORY);
    private static final List<String> qmawHistory = new ArrayList<>(MAX_HISTORY);
    private static boolean qmawMode;

    private final List<String> qmawResults = new ArrayList<>();
    private EditBox inputField;
    private int selectedHist = -1;
    private Component latestResult = Component.literal("?");
    private String lastInput;

    public CalculatorScreen() { super(Component.translatable("hbm.key.calculator")); }

    @Override
    protected void init() {
        String previousInput = inputField == null ? "" : inputField.getValue();
        inputField = new EditBox(font, startX() + 5, startY() + 14, 210, 13, Component.empty());
        inputField.setTextColor(-1);
        inputField.setCanLoseFocus(false);
        inputField.setMaxLength(1000);
        inputField.setResponder(this::inputChanged);
        addRenderableWidget(inputField);
        setInitialFocus(inputField);
        qmawHistory.removeIf(key -> !QMAWLoader.qmaw.containsKey(key));
        lastInput = null;
        inputField.setValue(previousInput);
        inputChanged(previousInput);
    }

    private int startX() { return (width - X_SIZE) / 2; }
    private int startY() { return (height - (Y_SIZE + (font.lineHeight + 4) * MAX_HISTORY)) / 2; }
    private boolean isInputEmpty() { return inputField.getValue().isEmpty(); }
    private String sanitizedInput() { return inputField.getValue().replaceAll("[^\\d+\\-*/%^!.()\\sA-Za-z]+", ""); }

    private static double evaluate(String expression) {
        double result = Calculator.evaluateExpression(expression);
        if (!Double.isFinite(result)) throw new ArithmeticException();
        return result;
    }

    private void inputChanged(String input) {
        selectedHist = -1;
        if (qmawMode) {
            if (!input.equalsIgnoreCase(lastInput)) {
                qmawResults.clear();
                lastInput = input;
                String query = input.toLowerCase(Locale.ROOT);
                var language = minecraft.getLanguageManager();
                for (var page : QMAWLoader.sortedPages) {
                    if (page.getTitle(language).toLowerCase(Locale.ROOT).contains(query)) qmawResults.add(page.name);
                }
                qmawResults.sort((first, second) -> {
                    String a = QMAWLoader.qmaw.get(first).getTitle(language), b = QMAWLoader.qmaw.get(second).getTitle(language);
                    boolean prefixA = a.toLowerCase(Locale.ROOT).startsWith(query), prefixB = b.toLowerCase(Locale.ROOT).startsWith(query);
                    return prefixA == prefixB ? a.compareTo(b) : prefixA ? -1 : 1;
                });
            }
            return;
        }
        String expression = sanitizedInput();
        if (expression.isEmpty()) {
            latestResult = Component.literal("?");
            return;
        }
        try { latestResult = Component.literal(Double.toString(evaluate(expression))); }
        catch (ArithmeticException undefined) { latestResult = Component.translatable("gui.calc.undefinedResult"); }
        catch (RuntimeException invalid) { latestResult = Component.translatable("gui.calc.invalidExpression"); }
    }

    private List<String> manualEntries() { return isInputEmpty() ? qmawHistory : qmawResults; }
    private int entryCount() { return qmawMode ? manualEntries().size() : calcHistory.size(); }
    private void selectedHistSanityCheck() { selectedHist = Mth.clamp(selectedHist, -1, entryCount() - 1); }

    private void select(boolean up) {
        int count = Math.min(entryCount(), MAX_HISTORY);
        if (up) selectedHist = selectedHist < 0 ? count - 1 : Math.max(selectedHist - 1, -1);
        else selectedHist = selectedHist >= count - 1 ? -1 : selectedHist + 1;
    }

    private void calculatorEnter() {
        selectedHistSanityCheck();
        if (selectedHist >= 0) {
            String expression = calcHistory.get(selectedHist).expression();
            inputField.setValue(expression);
            selectedHist = -1;
            return;
        }
        String expression = sanitizedInput();
        try {
            double result = evaluate(expression);
            String plain = new BigDecimal(result, MathContext.DECIMAL64).toPlainString();
            calcHistory.addFirst(new Calculation(expression, result));
            if (calcHistory.size() > MAX_HISTORY) calcHistory.removeLast();
            minecraft.keyboardHandler.setClipboard(plain);
            inputField.setValue(plain);
            inputField.moveCursorToEnd(false);
            inputField.setHighlightPos(0);
        } catch (Exception ignored) { }
    }

    private void executeQMAW() {
        selectedHistSanityCheck();
        if (selectedHist < 0) return;
        String key = manualEntries().get(selectedHist);
        var page = QMAWLoader.qmaw.get(key);
        qmawHistory.remove(key);
        qmawHistory.addFirst(key);
        if (qmawHistory.size() > MAX_HISTORY) qmawHistory.removeLast();
        inputField.setValue(page.getTitle(minecraft.getLanguageManager()));
        selectedHist = -1;
        GuiQMAW.clickSound();
        minecraft.setScreen(new GuiQMAW(page));
    }

    private void deleteHistoryEntry() {
        selectedHistSanityCheck();
        if (selectedHist >= 0) {
            if (qmawMode) qmawHistory.remove(selectedHist);
            else calcHistory.remove(selectedHist);
        }
    }

    @Override
    public boolean keyPressed(int key, int scan, int modifiers) {
        if (key == GLFW.GLFW_KEY_TAB) {
            qmawMode = !qmawMode;
            lastInput = null;
            inputChanged(inputField.getValue());
            return true;
        }
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            if (qmawMode) executeQMAW();
            else calculatorEnter();
            return true;
        }
        if (key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_DOWN) {
            select(key == GLFW.GLFW_KEY_UP);
            return true;
        }
        if (key == GLFW.GLFW_KEY_DELETE && (qmawMode ? isInputEmpty() : sanitizedInput().isEmpty())) {
            deleteHistoryEntry();
            selectedHist = -1;
            return true;
        }
        selectedHist = -1;
        return inputField.keyPressed(key, scan, modifiers) || super.keyPressed(key, scan, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        return inputField.charTyped(character, modifiers) || super.charTyped(character, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        int x = startX(), y = startY(), historyHeight = (font.lineHeight + 4) * MAX_HISTORY;
        graphics.fill(x, y, x + X_SIZE, y + Y_SIZE + historyHeight, BORDER_COLOR);
        graphics.fill(x + BORDER_WIDTH, y + BORDER_WIDTH, x + X_SIZE - BORDER_WIDTH,
                y + Y_SIZE - BORDER_WIDTH + historyHeight, FILL_COLOR);
        if (!qmawMode) {
            int historyStart = y + 30 + font.lineHeight + 8;
            graphics.fill(x, historyStart - 5, x + X_SIZE, historyStart - 3, BORDER_COLOR);
        }
        graphics.drawString(font, Component.translatable(qmawMode ? "gui.calc.qmawResult" : "gui.calc.calculatorResult"), x + 4, y + 4, -1, false);
        if (qmawMode) drawQMAW(graphics, mouseX, mouseY, x, y);
        else drawCalculator(graphics, x, y);
        super.render(graphics, mouseX, mouseY, partial);
    }

    private void drawCalculator(GuiGraphics graphics, int x, int y) {
        graphics.drawString(font, Component.literal("=").append(latestResult), x + 5, y + 32, -1, false);
        for (int i = 0; i < calcHistory.size(); i++) {
            int rowY = y + 50 + (font.lineHeight + 1) * i;
            if (i == selectedHist) graphics.fill(x + 4, rowY - 1, x + X_SIZE - 5, rowY + font.lineHeight, SELECT_COLOR);
            var entry = calcHistory.get(i);
            graphics.drawString(font, entry.expression() + " = " + entry.result(), x + 5, rowY, -1, false);
        }
    }

    private void drawQMAW(GuiGraphics graphics, int mouseX, int mouseY, int x, int y) {
        var entries = manualEntries();
        int hover = mouseEntrySelected(mouseX, mouseY);
        for (int i = 0; i < Math.min(entries.size(), MAX_HISTORY); i++) {
            var page = QMAWLoader.qmaw.get(entries.get(i));
            int rowY = y + 35 + (font.lineHeight + QMAW_SPACING) * i;
            if (i == selectedHist) graphics.fill(x + 4, rowY - QMAW_SPACING / 2, x + X_SIZE - 5,
                    rowY + font.lineHeight + QMAW_SPACING / 2, SELECT_COLOR);
            graphics.drawString(font, page.getTitle(minecraft.getLanguageManager()), x + 25, rowY, hover == i ? 0xffff00 : -1, false);
            if (page.icon != null && !page.icon.isEmpty()) {
                graphics.renderItem(page.icon, x + 5, rowY - QMAW_SPACING + 2);
                graphics.renderItemDecorations(font, page.icon, x + 5, rowY - QMAW_SPACING + 2);
            }
        }
    }

    private boolean isMouseInSegment(double mouseX, double mouseY) {
        int x = startX(), y = startY();
        return mouseX > x && mouseX < x + X_SIZE && mouseY > y
                && mouseY < y + Y_SIZE + (font.lineHeight + 4) * MAX_HISTORY;
    }

    private int mouseEntrySelected(double mouseX, double mouseY) {
        if (!isMouseInSegment(mouseX, mouseY)) return -1;
        int x = startX(), y = startY();
        for (int i = 0; i < Math.min(entryCount(), MAX_HISTORY); i++) {
            int rowY = qmawMode ? y + 35 + (font.lineHeight + QMAW_SPACING) * i : y + 50 + (font.lineHeight + 1) * i;
            int top = rowY - (qmawMode ? QMAW_SPACING / 2 : 1), bottom = rowY + font.lineHeight + (qmawMode ? QMAW_SPACING / 2 : 0);
            if (mouseX > x + 4 && mouseX < x + X_SIZE - 5 && mouseY > top && mouseY < bottom) return i;
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        if (!isMouseInSegment(mouseX, mouseY)) return handled;
        selectedHist = mouseEntrySelected(mouseX, mouseY);
        if (selectedHist < 0) return handled;
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (qmawMode) executeQMAW();
            else calculatorEnter();
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && (!qmawMode || isInputEmpty())) {
            deleteHistoryEntry();
            return true;
        }
        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        long window = minecraft.getWindow().getWindow();
        if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS
                && GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) != GLFW.GLFW_PRESS && vertical != 0) {
            select(vertical > 0);
        }
        return true;
    }
}
