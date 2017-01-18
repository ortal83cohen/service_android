package com.hpe.sb.mobile.app.features.request.helper;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.features.request.FinishWithDirtyWarning;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;

/**
 * Created by malikdav on 10/05/2016.
 */
public class NewRequestHeadersHelper {

    public static <T extends BaseActivity & FinishWithDirtyWarning> void initHeaders(final T activity) {
        hideOfferingHeader(activity);

        ImageView quitIcon = (ImageView) activity.findViewById(R.id.quit);
        quitIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finishWithDirtyWarning();
            }
        });
    }

    public static void showOfferingHeader(Activity activity, NewRequestFlowState newRequestFlowState) {
        if(newRequestFlowState.getSelectedOffering() != null) {
            showOfferingHeader(activity, newRequestFlowState.getSelectedOffering().getTitle());
        } else {
            Resources res = activity.getResources();
            showOfferingHeader(activity, res.getString(R.string.new_request_general_request_header));
        }
    }

    public static void showCategoryHeader(Activity activity, String title) {
        showOfferingHeader(activity, title);
    }

    public static void hideCategoryHeader(Activity activity) {
        hideOfferingHeader(activity);
    }

    public static void hideOfferingHeader(Activity activity) {
        View offeringHeader = activity.findViewById(R.id.offering_header);
        offeringHeader.setVisibility(View.GONE);
    }

    public static void hideHeaders(Activity activity) {
        hideOfferingHeader(activity);

        View header = activity.findViewById(R.id.header);
        header.setVisibility(View.GONE);
    }

    private static void showOfferingHeader(Activity activity, String offeringTitle) {
        View offeringHeader = activity.findViewById(R.id.offering_header);
        MetricFontCustomTextView offeringHeaderText = (MetricFontCustomTextView) activity.findViewById(R.id.offering_header_text);
        offeringHeader.setVisibility(View.VISIBLE);
        offeringHeaderText.setText(offeringTitle);
    }
}
