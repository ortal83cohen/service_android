package com.hpe.sb.mobile.app.features.catalog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogClient;
import com.hpe.sb.mobile.app.common.dataClients.catalog.restClient.CatalogRestClient;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitiesRecyclerViewHelper;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitySelectionHandler;
import com.hpe.sb.mobile.app.common.utils.ModelsConverter;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CategoryPageActivity extends BaseActivity {
    private static final String TAG = CategoryPageActivity.class.getSimpleName();
    private static final String OFFERINGS_BY_CATEGORY_RESULT = "offeringsByCategoryResult";
    private static final String CATEGORY = "category";
    private List<Offering> offerings;
    private CatalogGroup category;

    @Inject
    public CatalogRestClient catalogRestClient;

    @Inject
    public CatalogClient catalogClient;

    public CategoryPageActivity() {
        offerings = new ArrayList<>();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ServiceBrokerApplication)getApplicationContext()).getServiceBrokerComponent().inject(this);
        Intent intent = getIntent();
        this.category = intent.getExtras().getParcelable(CATEGORY);
        setContentView(R.layout.activity_category_page);
        initHeader();
        handleBackButton();


    }

    @Override
    protected void onResume() {
        super.onResume();

        getOfferingsByCatalogGroup();
    }

    private void getOfferingsByCatalogGroup() {
        final View loadingGif = findViewById(R.id.loading_gif);
        loadingGif.setVisibility(View.VISIBLE);
        catalogClient.getOfferingsByCatalogGroup(getApplicationContext(), false, 0, CatalogClient.MAX_NUMBER_OF_OFFERINGS,
                category.getId(), CatalogClient.CATALOG_GROUP_CATEGORY_LEVEL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<OfferingsByCatalogGroupResult>(CategoryPageActivity.this, sbExceptionsHandler) {
                    @Override
                    public void onCompleted() {
                        stopLoadingGif(loadingGif);
                        Log.e(getClass().getName(), "getOfferingsByCatalogGroup done for category id: " + category.getId());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        stopLoadingGif(loadingGif);
                        String msg = "Failed to get get Offerings By Category, error: " + throwable;
                        Log.e(getClass().getName(), msg);
                        stopLoadingGif(loadingGif);
                        //Toast.makeText(CategoryPageActivity.this, "Error loading results", Toast.LENGTH_LONG).show();
                        super.onError(throwable);
                    }

                    @Override
                    public void onNext(OfferingsByCatalogGroupResult offeringsByCategoryResult) {
                        if (offeringsByCategoryResult != null &&
                                offeringsByCategoryResult.getOfferings() != null &&
                                !offeringsByCategoryResult.getOfferings().isEmpty()) {
                            stopLoadingGif(loadingGif);
                            initOfferingList(offeringsByCategoryResult);
                        }
                    }
                });
    }

    @Override
    public void showNoConnectionSnackbar() {
        snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("1", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOfferingsByCatalogGroup();
            }
        });
        // Changing message text color
        snackbar.setActionTextColor(getResources().getColor(R.color.offering_service_type_color));

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        textView.setText("");
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.refresh, 0);
        snackbar.show();
    }

    private void stopLoadingGif(View loadingGif) {
        if (loadingGif != null) {
            loadingGif.setVisibility(View.GONE);
        }
    }

    public static Intent createIntent(Context context, CatalogGroup catalogGroup) {
        Intent intent = new Intent(context, CategoryPageActivity.class);
        intent.putExtra(CATEGORY, catalogGroup);
        return intent;
    }

    private void initHeader() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.category_page_header);
        ImageButton back = (ImageButton) findViewById(R.id.category_page_header_back);
        if (header != null && back != null) {
            int color = Color.BLACK;
            final String backgroundColor = category.getBackgroundColor();
            if (backgroundColor != null) {
                try {
                    color = Color.parseColor(backgroundColor);
                }catch(Exception e){
                    Log.e(TAG, "Failed to parse color " + backgroundColor + "; set black");
                }
            }
            header.setBackgroundColor(color);
            back.setBackgroundColor(color);
        }
        MetricFontCustomTextView title = (MetricFontCustomTextView) findViewById(R.id.category_page_header_title);
        if (title != null) {
            title.setText(category.getTitle());
        }
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    private void initOfferingList(OfferingsByCatalogGroupResult offeringsByCategoryResult) {
        offerings = offeringsByCategoryResult.getOfferings();
        RecyclerView offeringListView = (RecyclerView) findViewById(R.id.category_page_related_entities_list);
        List<EntityItem> entityItems = new ModelsConverter().convertOfferingsToEntityItems(offerings);
        final RelatedEntitiesRecyclerViewHelper helper = new RelatedEntitiesRecyclerViewHelper(getApplicationContext());
        helper.initRecyclerView(offeringListView, false, null, new RelatedEntitySelectionHandler(CategoryPageActivity.this));
        helper.updateItems(offeringListView, entityItems);
    }

    private void handleBackButton() {
        ImageButton backButton = (ImageButton) findViewById(R.id.category_page_header_back);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rest_excample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
