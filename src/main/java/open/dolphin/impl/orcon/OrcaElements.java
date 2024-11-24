package open.dolphin.impl.orcon;

/**
 * ORCA の画面 id を対応させる enum.
 * @author pns
 */
public enum OrcaElements {
    マスターメニューキー("M00"),
    マスターメニュー選択番号("M00.fixed1.SELNUM"),

    業務メニューキー("M01"),
    業務メニュー選択番号("M01.fixed1.SELNUM"),

    診療行為キー("K02"),
    診療行為患者番号("K02.fixed2.PTNUM"),
    中途表示ボタン("K02.fixed2.B12CS"),
    病名登録ボタン("K02.fixed2.B07S"),
    中途終了選択番号("K10.fixed1.SELNUM"),

    診療行為請求確認キー("K03"),
    領収書("K03.fixed3.HAKFLGCOMBO.HAKFLG"),
    明細書("K03.fixed3.MEIPRTFLG_COMB.MEIPRTFLG"),
    処方箋("K03.fixed3.SYOHOPRTFLGCOMBO.SYOHOPRTFLG"),

    病名登録キー("C02"),
    病名登録患者番号("C02.fixed6.PTNUM"),

    日次統計キー("L01"),
    日計表("L01.fixed.CHK01"),

    月次統計キー("G01"),
    患者数一覧表("G01.fixed.CHK02"),

    確認画面キーGID2("GID2"),
    プレビューGID2("GID2.fixed1.B10"),

    確認画面キーLID2("LID2"),
    プレビューLID2("LID2.fixed1.B10"),

    処理結果キーG99("G99"),
    プレビューG99("G99.fixed6.B12"),

    処理結果キーL99("L99"),
    プレビューL99("L99.fixed6.B12"),


    エラー閉じるボタン("KERR.fixed1.B01")
    ;

    String id;
    OrcaElements(String s) {
        id = s;
    }
}
