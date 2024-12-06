package open.dolphin.impl.orcon;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * マクロ.
 * @author pns
 */
public class OrconMacro {
    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait wait60sec;
    private final OrcaController context;
    private final OrconPanel panel;
    private final OrconProperties props;
    private final Logger logger;

    public OrconMacro(OrcaController context) {
        this.context = context;
        panel = context.getOrconPanel();
        props = context.getOrconProps();
        logger = LoggerFactory.getLogger(OrconMacro.class);
    }

    /**
     * ログイン処理. driver 作成して chrome を起動する.
     */
    public void login() {
        logger.info("login");
        Rectangle bounds = props.loadBounds();
        panel.setLoginState(true);

        try {
            ChromeOptions option = new ChromeOptions();
            List<String> args = new ArrayList<>();
            args.add(String.format("--window-position=%d,%d", bounds.x, bounds.y));
            args.add(String.format("--window-size=%d,%d", bounds.width, bounds.height));
            option.addArguments(args);

            // load chrome extensions
            try {
                Path path = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                String userDir = path.getFileName().toString().contains(".jar")
                    ? path.getParent().toString()
                    : System.getProperty("user.dir");

                File extensionsDir = new File(userDir + "/chrome/extensions/");
                File[] extensions = extensionsDir.listFiles();
                if (extensions != null) {
                    Stream.of(extensions).forEach(option::addExtensions);
                }

            } catch (URISyntaxException ex) {
                ex.printStackTrace(System.err);
            }

            driver = new ChromeDriver(option);
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(300));
            wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            wait60sec = new WebDriverWait(driver, Duration.ofSeconds(60));

            driver.get(panel.getAddressField().getText());
            WebElement user = driver.findElement(By.id("user"));
            WebElement pass = driver.findElement(By.id("pass"));
            WebElement login = driver.findElement(By.className("gtk-button"));
            user.sendKeys(panel.getUserField().getText());
            pass.sendKeys(new String(panel.getPasswordField().getPassword()));
            login.click();

            // (M01)業務メニューまで進む
            loginMoveToGyomu();
            // (K02)診療行為入力 まで進む
            //m01ToShinryoKoi();

        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            quit();
        }
    }

    /**
     * from (M00)マスターメニュー to (M01)業務メニュー.
     */
    public void loginMoveToGyomu() {
        logger.info("業務メニューまで進む");
        WebElement m00selnum = driver.findElement(By.xpath("//*[@id=\"M00.fixed1.SELNUM\"]"));
        m00selnum.sendKeys("01", Keys.ENTER);
    }

    /**
     * from ANYWHERE to (M01)業務メニュー.
     */
    public void backToGyomu() {
        logger.info("業務メニューに戻る");
        WebElement back = findButtonElement("戻る");
        while (!back.getDomAttribute("id").contains("M01")) {
            back.click();
            back = findButtonElement("戻る");
        }
    }

    /**
     * (G01)月次統計 患者数一覧表プレビューを開く.
     */
    public void m01ToKanjasu() {
        logger.info("患者数一覧表");
        backToGyomu();
        // 業務メニュー選択番号
        WebElement m01selnum = driver.findElement(By.xpath("//*[@id=\"M01.fixed1.SELNUM\"]"));
        m01selnum.sendKeys("52", Keys.ENTER);

        WebElement chohyoNum = driver.findElement(By.xpath("//*[@id=\"G01.fixed.PARA0201\"]"));
        chohyoNum.sendKeys(KeyUtils.selectAll(), Keys.BACK_SPACE);

        WebElement g01 = driver.findElement(By.xpath("//*[@id=\"G01.fixed\"]/label[7]"));
        g01.click(); // 患者数一覧チェックボックスクリック
        sendThrough(Keys.F12); // 処理開始

        WebElement preview = driver.findElement(By.xpath("//*[@id=\"GID2.fixed1.B10\"]"));
        wait.until(ExpectedConditions.elementToBeClickable(preview));
        preview.click();

        preview = driver.findElement(By.xpath("//*[@id=\"G99.fixed6.B12\"]"));
        wait60sec.until(ExpectedConditions.elementToBeClickable(preview));
        preview.click();

        // 行選択番号
        WebElement selnum = driver.findElement(By.xpath("//*[@id=\"XC01.fixed32.SELNUM\"]"));
        selnum.sendKeys(KeyUtils.selectAll(), Keys.BACK_SPACE, "1", Keys.ENTER);

        // needs extension PDF Viewer by pdfjs.robwu.nl
        setPdfViewerScale("1.25");
    }

    /**
     * (L01)日次統計 日計表を開く.
     */
    public void m01ToNikkei() {
        logger.info("日計表");
        backToGyomu();
        // 業務メニュー選択番号
        WebElement m01selnum = driver.findElement(By.xpath("//*[@id=\"M01.fixed1.SELNUM\"]"));
        m01selnum.sendKeys("51", Keys.ENTER);

        WebElement l01 = driver.findElement(By.xpath("//*[@id=\"L01.fixed\"]/label[1]"));
        l01.click(); // 日計表チェックボックスクリック
        sendThrough(Keys.F12);

        WebElement preview = driver.findElement(By.xpath("//*[@id=\"LID2.fixed1.B10\"]"));
        wait.until(ExpectedConditions.elementToBeClickable(preview));
        preview.click();

        preview = driver.findElement(By.xpath("//*[@id=\"L99.fixed6.B12\"]"));
        wait60sec.until(ExpectedConditions.elementToBeClickable(preview));
        preview.click();

        // 最終行を選択
        try { Thread.sleep(300); } catch (Exception e) {} // 全行読むのに時間かかる
        WebElement clist = driver.findElement(By.xpath("//*[@id=\"XC01.fixed32.scrolledwindow26.CLIST\"]/tbody"));
        int size = clist.findElements(By.tagName("tr")).size();
        WebElement selnum = driver.findElement(By.xpath("//*[@id=\"XC01.fixed32.SELNUM\"]"));
        selnum.sendKeys(KeyUtils.selectAll(), Keys.BACK_SPACE, String.valueOf(size), Keys.ENTER);

        // needs extension PDF Viewer by pdfjs.robwu.nl
        setPdfViewerScale("1.25");
    }

    /**
     * from (M01)業務メニュー to (K02)診療行為入力.
     */
    public void m01ToShinryoKoi() {
        logger.info("診療行為入力まで進む");
        backToGyomu();
        // 業務メニュー選択番号
        WebElement m01selnum = driver.findElement(By.xpath("//*[@id=\"M01.fixed1.SELNUM\"]"));
        m01selnum.sendKeys("21", Keys.ENTER);
    }

    /**
     * from (M01)業務メニュー to (D01)レセプトチェック指示.
     */
    public void m01ToReceiptCheck() {
        logger.info("レセプトチェック");
        backToGyomu();
        // 業務メニュー選択番号
        WebElement m01selnum = driver.findElement(By.xpath("//*[@id=\"M01.fixed1.SELNUM\"]"));
        m01selnum.sendKeys("41", Keys.ENTER);

        LocalDate today = LocalDate.now().minusMonths(1);
        String yearMonth = String.format("%d.%d", today.getYear(), today.getMonthValue());
        WebElement d01sryym = driver.findElement(By.xpath("//*[@id=\"D01.fixed6.SRYYM\"]"));
        d01sryym.sendKeys(KeyUtils.selectAll(), Keys.BACK_SPACE, yearMonth);
        WebElement chkkbn = driver.findElement(By.xpath("//*[@id=\"D01.fixed6.CMB_USRCHKKBN.USRCHKKBN\"]"));
        chkkbn.sendKeys("01", Keys.ENTER);
        sendThrough(Keys.F12);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"DID2.fixed1.B10\"]")));
        sendThrough(Keys.F10);
        WebElement done = driver.findElement(By.xpath("//*[@id=\"D99.fixed6.scrolledwindow28.JOBKANRILST\"]/tbody/tr[2]/td[2]"));
        wait60sec.until(ExpectedConditions.textToBe(
            By.xpath("//*[@id=\"D99.fixed6.scrolledwindow28.JOBKANRILST\"]/tbody/tr[2]/td[2]"), "帳票印刷処理"));
        sendThrough(Keys.F12);
    }

    /**
     * from (K02)診療行為入力 to (C02)病名登録.
     */
    public void k02ToByomeiToroku() {
        logger.info("病名登録へ移動");
        StringBuilder sb = new StringBuilder();
        sb.append(Keys.SHIFT);
        sb.append(Keys.F7);
        sendThrough(sb);
    }

    /**
     * at (K02)診療行為入力 do 中途終了展開.
     */
    public void k20ChutoTenkai() {
        logger.info("中途終了展開");
        try {
            WebElement chutoButton = driver.findElement(By.xpath("//*[@id=\"K02.fixed2.B12CS\"]"));
            chutoButton.click();
            By chutoField = By.xpath("//*[@id=\"K10.fixed1.SELNUM\"]");
            wait.until(ExpectedConditions.presenceOfElementLocated(chutoField));

            // 選択番号1番入力, ENTER ２回で展開
            WebElement k10selnum = driver.findElement(chutoField);
            k10selnum.sendKeys("1", Keys.ENTER, Keys.ENTER);

        } catch (RuntimeException ex) {
            // 中途終了がない場合
            logger.error(ex.getMessage());
        }
    }

    /**
     * at (K02)診療行為入力 do 外来管理加算削除.
     */
    public void k02GairaiKanriDelete() {
        logger.info("外来管理加算削除");
        // list of "入力コード" column
        List<WebElement> elements = driver.findElements(By.xpath("/html/body/div[2]/div/div/div[2]/div[6]/div/div[19]/table/tbody/tr/td[2]/input"));
        WebElement target = null;
        for (int r=0; r<10; r++) {
            // 112011010 外来管理加算コード検索
            if ("112011010".equals(elements.get(r).getAttribute("value"))) {
                target = elements.get(r);
                break;
            }
        }
        // 見つかったら コマンド-A で全選択, 削除
        if (target!= null) {
            target.sendKeys(KeyUtils.selectAll(), Keys.BACK_SPACE);
        }
    }

    /**
     * at (K03)診療行為入力 do 請求確認でのコンボボックス. 0 発行なし, 1 発行あり
     * @param n 0:全て印刷しない, 1: 領収書のみ印刷, 2: 処方箋のみ印刷, 3:両方印刷
     */
    public void k03SelectPrintForms(int n) {
        logger.info("印刷帳票選択");
        WebElement ryosyusyoElement = driver.findElement(By.xpath("//*[@id=\"K03.fixed3.HAKFLGCOMBO.HAKFLG\"]"));
        WebElement meisaisyoElement = driver.findElement(By.xpath("//*[@id=\"K03.fixed3.MEIPRTFLG_COMB.MEIPRTFLG\"]"));
        WebElement syohoElement = driver.findElement(By.xpath("//*[@id=\"K03.fixed3.SYOHOPRTFLGCOMBO.SYOHOPRTFLG\"]"));

        String ryosyusyo, meisaisyo, syoho;
        switch (n) {
            case 1 -> { ryosyusyo = "1"; meisaisyo = "0"; syoho = "0"; }
            case 2 -> { ryosyusyo = "0"; meisaisyo = "0"; syoho = "1"; }
            case 3 -> { ryosyusyo = "1"; meisaisyo = "1"; syoho = "1"; }
            default -> { ryosyusyo = "0"; meisaisyo = "0"; syoho = "0"; }
        }
        ryosyusyoElement.click();
        ryosyusyoElement.sendKeys(ryosyusyo);
        meisaisyoElement.click();
        meisaisyoElement.sendKeys(meisaisyo);
        syohoElement.click();
        syohoElement.sendKeys(syoho);
    }

    /**
     * activeElement にキーを流す.
     * @param chord charsequence
     */
    public void sendThrough(CharSequence chord) {
        try {
            WebElement activeElement = driver.switchTo().activeElement();
            activeElement.sendKeys(Keys.chord(chord));
        } catch (RuntimeException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * PDF の表示倍率をセットする. PDF Viewer (pdfjs.robwu.nl) が必要.
     * @param value selection value
     */
    private void setPdfViewerScale(String value) {
        try {
            // StaleElementReferenceException 対策 - iframe 切り替えに時間がかかる？
            Thread.sleep(300);

            WebElement frame = driver.findElement(By.xpath("//*[@id=\"XC01.fixed32.PSAREA\"]/iframe"));
            driver.switchTo().frame(frame);
            WebElement selectElement = driver.findElement(By.xpath("//*[@id=\"scaleSelect\"]"));
            Select select = new Select(selectElement);
            select.selectByValue(value);
            driver.switchTo().defaultContent();

        } catch (RuntimeException | InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * 終了処理.
     */
    public void close() {
        logger.info("close");
        org.openqa.selenium.Point loc = driver.manage().window().getPosition();
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();
        Rectangle bounds = new Rectangle(loc.x, loc.y, size.width, size.height);

        props.saveBounds(bounds);
        props.viewToModel();
        panel.setLoginState(false);

        quit();
    }

    /**
     * ドライバ終了で chrome が閉じる.
     */
    public void quit() {
        driver.quit();
        panel.getLoginButton().setEnabled(true);
    }

    /**
     * 指定した文字のボタンを探す.
     * @param text text to search
     * @return button element or null
     */
    private WebElement findButtonElement(String text) {
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        return buttons.stream().filter(b -> text.equals(b.getText())).findFirst().orElse(null);
    }

    /**
     * ページのキー ("M01" 等) を返す.
     * @return key of the present page
     */
    public String whereAmI() {
        return driver.getTitle().substring(1,4);
    }

    /**
     * ページ更新を待つ ExpectedCondition.
     */
    private class PageUpdated implements ExpectedCondition<Boolean> {
        private String oldPageTitle;

        public void setOldWhereAmI(String oldPageTitle) {
            this.oldPageTitle = oldPageTitle;
        }

        @Override
        public Boolean apply(WebDriver input) {
            return !input.getTitle().equals(oldPageTitle);
        }
    }
}
