package swd.affiliatemarketing.hotpromotion.model;

import java.io.Serializable;

public class PromotionCodeTracking implements Serializable {
    public String promotionCode, timeOfUsing;

    public PromotionCodeTracking() {
    }

    public PromotionCodeTracking(String code, String time) {
        this.promotionCode = code;
        this.timeOfUsing = time;
    }
}
