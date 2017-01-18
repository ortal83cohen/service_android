package com.hpe.sb.mobile.app.features.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;
import com.hpe.sb.mobile.app.common.dataClients.search.SearchClient;
import com.hpe.sb.mobile.app.common.utils.keyboard.KeyboardUtils;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;


/**
 * the activity has 2 states:
 * 1. if the query received is null or empty then we assume no search was made and we show the user the no search icon
 * 2. if the query is not empty we perform a search and display the results
 * **/
public class SearchResultsActivity extends BaseActivity {

    private final Set<EntityType> relatedEntitiesTypes =  new HashSet<EntityType>() {{
        add(EntityType.ARTICLE);
        add(EntityType.SUPPORT_OFFERING);
        add(EntityType.SERVICE_OFFERING);
        add(EntityType.HUMAN_RESOURCE_OFFERING);
    }};

    private static final String QUERY = "query";

    private String originQuery = "";

    @Inject
    public SearchClient searchClient;

    private Button searchButton;

    private EditText searchBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ServiceBrokerApplication)getApplicationContext()).getServiceBrokerComponent().inject(this);
        Intent intent = getIntent();
        setContentView(R.layout.activity_search_results);
        searchButton = (Button) findViewById(R.id.search_btn);
        searchBox = (EditText) findViewById(R.id.search);
        String query = intent.getExtras().getString(QUERY);
        originQuery = query != null ? query : "";
        searchBox.setText(originQuery);
        handleBackButton();
        setButtonHandler();
        if (query != null && !query.isEmpty()) {
            SearchResultsActivityUtil.initLoadingGif(this);
            SearchResultsActivityUtil.sendSearchQuery(SearchResultsActivity.this, searchClient, query, relatedEntitiesTypes);
        } else {
            SearchResultsActivityUtil.showNoSearchIcon(this, searchBox);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchBox = (EditText) findViewById(R.id.search);
        if (searchBox != null) {
            searchBox.setText(originQuery);
        }
        if (originQuery == null || originQuery.isEmpty()) {
            KeyboardUtils.showKeyboardWhenActivityCreated(this, searchBox, false);
        }
    }

    private void handleBackButton() {
        ImageButton searchBack = (ImageButton) findViewById(R.id.search_results_back);
        if (searchBack != null) {
            searchBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    public static Intent createIntent(Context context, String oldQuery) {
        Intent intent = new Intent(context, SearchResultsActivity.class);
        intent.putExtra(QUERY, oldQuery);
        return intent;
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

    private void setButtonHandler() {
        searchBox.setOnEditorActionListener(new SearchBoxListener(searchBox));
        searchButton.setOnClickListener(new SearchBoxListener(searchBox));
    }

    public class SearchBoxListener implements TextView.OnEditorActionListener, View.OnClickListener {

        private final EditText searchBox;


        public SearchBoxListener(EditText searchBox) {
            this.searchBox = searchBox;
        }

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if(i== EditorInfo.IME_ACTION_DONE){
                makeSearchIfNeeded();
            }
            return true;
        }

        @Override
        public void onClick(View view) {
            makeSearchIfNeeded();
        }

        private void makeSearchIfNeeded() {
            String query = searchBox.getText() != null ? searchBox.getText().toString() : "" ;
            if (!query.isEmpty() && !isEqual(query, originQuery)) {
                //if no search string or old search string == new search string, no need to execute search
                if (isNullOrEmpty(originQuery)) {
                    //about to execute the first search in succession
                    originQuery = query;
                    KeyboardUtils.hideKeyboard(SearchResultsActivity.this);
                    SearchResultsActivityUtil.initLoadingGif(SearchResultsActivity.this);
                    SearchResultsActivityUtil.sendSearchQuery(SearchResultsActivity.this, searchClient, query, relatedEntitiesTypes);
                }
                else {
                    //not the first search in succession ->
                    // start a new search activity to maintain history
                    Intent intent = createIntent(getApplicationContext(), query);
                    startActivity(intent);
                }
            }
        }

        private boolean isEqual(String a, String b) {
            return (originQuery == null) ? (b == null) : a.equals(b);
        }
        private boolean isNullOrEmpty(String a) {
            return a == null || a.isEmpty();
        }

    }
}
