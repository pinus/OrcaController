package open.dolphin.impl.orcon;

/**
 * ORCA の画面 id を対応させる enum.
 * @author pns
 */
public enum OrcaElements {
    マスターメニュー("M00"),
    マスターメニュー選択番号("M00.fixed1.SELNUM"),

    業務メニュー("M01"),
    業務メニュー選択番号("M01.fixed1.SELNUM"),

    診療行為("K02"),
    中途表示ボタン("K02.fixed2.B12CS"),
    病名登録ボタン("K02.fixed2.B07S"),
    中途終了選択番号("K10.fixed1.SELNUM"),

    診療行為請求確認("K03"),
    領収書("K03.fixed3.HAKFLGCOMBO.HAKFLG"),
    明細書("K03.fixed3.MEIPRTFLG_COMB.MEIPRTFLG"),
    処方箋("K03.fixed3.SYOHOPRTFLGCOMBO.SYOHOPRTFLG"),

    病名登録タイトル("C02"),

    エラー閉じるボタン("KERR.fixed1.B01")
    ;

    String id;
    OrcaElements(String s) {
        id = s;
    }
}
