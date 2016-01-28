package test.info.yangguo.qinqin.cache;

import java.util.concurrent.TimeUnit;

/**
 * Created by yangguo on 14-1-28.
 *
 * 缓存的生命周期
 */
public enum Lifecycle {
    ONEMINUTE(1, TimeUnit.MINUTES), ONEHOUR(1, TimeUnit.HOURS), THREEHOURS(3, TimeUnit.HOURS), ONEDAY(24, TimeUnit.HOURS), ONEWEEK(7, TimeUnit.DAYS);
    private int amount;
    private TimeUnit timeUnit;

    Lifecycle(int amount, TimeUnit timeUnit) {
        this.amount = amount;
        this.timeUnit = timeUnit;
    }

    public int getAmount() {
        return amount;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
  }
