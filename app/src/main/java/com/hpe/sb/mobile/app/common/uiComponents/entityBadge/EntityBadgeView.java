package com.hpe.sb.mobile.app.common.uiComponents.entityBadge;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.dataClients.displaylabels.BadgeProperties;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;

/**
 * Created by malikdav on 25/04/2016.
 */
public class EntityBadgeView extends MetricFontCustomTextView{

    public EntityBadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(isInEditMode()) {
            // For preview debugging, will be override upon setImage
            setExampleBadge();
        }
    }

    public void setBadge(BadgeProperties badgeProperties) {
        this.setBackground(badgeProperties.getDrawable());
        this.setTextColor(badgeProperties.getBackgroundColor());
        this.setText(badgeProperties.getText());
    }

    private void setExampleBadge() {
        this.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.service_badge));
        this.setTextColor(ContextCompat.getColor(getContext(), R.color.offering_service_type_color));
        this.setText("EXAMPLE");
    }
}
