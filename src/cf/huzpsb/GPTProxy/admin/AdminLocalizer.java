package cf.huzpsb.GPTProxy.admin;

import nano.http.d2.database.csv.Localizer;

public class AdminLocalizer {
    public static final Localizer l;

    static {
        l = new Localizer();
        l.add("tokens", "余额");
        l.add("lastUse", "最后使用时间");
    }
}
