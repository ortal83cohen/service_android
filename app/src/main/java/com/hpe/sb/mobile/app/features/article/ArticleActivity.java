package com.hpe.sb.mobile.app.features.article;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.article.ArticleClient;
import com.hpe.sb.mobile.app.common.dataClients.article.ArticleDisplayData;
import com.hpe.sb.mobile.app.common.uiComponents.entityIcon.EntityIconImageView;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.common.utils.LinkResolverUtils;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.services.SessionCookieService;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtilsImpl;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.article.Article;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

import javax.inject.Inject;

public class ArticleActivity extends BaseActivity {

    private static final String TAG = ArticleActivity.class.getSimpleName();
    public static final String ARTICLE_ID = "article_data";

    private static final String SUBTYPE = "subType";

    @Inject
    ConnectionContextService connectionContextService;

    @Inject
    public ArticleClient articleClient;

    @Inject
    SessionCookieService sessionCookieService;

    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

    private WebView webView;

    private String subType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.article_web_view_layout);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);
        Intent intent = getIntent();
        String articleId = intent.getExtras().getString(ARTICLE_ID);
        subType = intent.getExtras().getString(SUBTYPE);
        initHeader();
        final View loadingGif = findViewById(R.id.loading_gif);
        initLoadingGif(loadingGif);
        initArticleContent(articleId, loadingGif);
    }

    private void initLoadingGif(View loadingGif) {
        if (loadingGif != null) {
            loadingGif.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void initArticleContent(final String articleId, final View loadingGif) {
        if (articleId == null) {
            Log.e(TAG, "Failed to Start WebView, cannot find the article content");
            finishWebView(RESULT_ERROR, null);
        }
        EntityIconImageView articleImage = (EntityIconImageView) findViewById(R.id.article_icon);
        MetricFontCustomTextView title = (MetricFontCustomTextView) findViewById(R.id.article_page_header_title);
        MetricFontCustomTextView articleType = (MetricFontCustomTextView) findViewById(R.id.article_type);
        LinearLayout articleHeader = (LinearLayout) findViewById(R.id.article_page_header);
        if (EntityType.ARTICLE.toString().toUpperCase().equals(subType.toUpperCase())) {
            articleImage.setImage(null, EntityType.ARTICLE);
            title.setText(R.string.read_article);
            articleType.setText(R.string.article_badge_text);
            articleHeader.setBackgroundColor(getResources().getColor(R.color.related_entities_article_badge_color));
        } else if (EntityType.NEWS.toString().toUpperCase().equals(subType.toUpperCase())) {
            articleImage.setImage(null, EntityType.NEWS);
            title.setText(R.string.read_news);
            articleType.setText(R.string.news_badge_text);
            articleType.setTextColor(getResources().getColor(R.color.related_entities_news_badge_color));
            articleType.setBackground(getResources().getDrawable(R.drawable.news_badge));
            articleHeader.setBackgroundColor(getResources().getColor(R.color.related_entities_news_badge_color));
        }
        Observable<ArticleDisplayData> articleObservable = articleClient.getArticleForDisplay(getApplicationContext(), articleId);
        Observable<Void> webViewSessionInit = sessionCookieService.initWebViewSession();
        Observable.zip(articleObservable, webViewSessionInit, new Func2<ArticleDisplayData, Void, ArticleDisplayData>() {
                @Override
                public ArticleDisplayData call(ArticleDisplayData articleDisplayData, Void aVoid) {
                    return articleDisplayData;
                }
            })
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<ArticleDisplayData>(this, this.sbExceptionsHandler) {
            @Override
            public void onNext(ArticleDisplayData articleDisplayData) {
                stopLoadingGif(loadingGif);
                if (articleDisplayData != null && articleDisplayData.getArticle() != null) {
                    final View title = findViewById(R.id.title);
                    if(title != null){
                        title.setVisibility(View.VISIBLE);
                    }
                    final View webContainer = findViewById(R.id.web_view_container);
                    if(webContainer != null){
                        webContainer.setVisibility(View.VISIBLE);
                    }
                    EntityIconImageView articleImage = (EntityIconImageView) findViewById(R.id.article_icon);
                    if (articleImage != null) {
                        articleImage.setImage(null, EntityType.ARTICLE);
                    }
                    setNativeContent(articleDisplayData.getArticle());
                    setWebViewWithContent(articleDisplayData);
                } else {
                    Toast.makeText(ArticleActivity.this, "Error loading article", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                stopLoadingGif(loadingGif);
                String msg = "Failed to get article with id: " + articleId + ", error: " + e.getMessage();
                Log.e(getClass().getName(), msg);
                super.onError(e);
            }
        });
    }

    private void stopLoadingGif(View loadingGif) {
        if (loadingGif != null) {
            loadingGif.setVisibility(View.GONE);
        }
    }

    private void initHeader() {
        ImageButton back = (ImageButton) findViewById(R.id.article_page_header_back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    private void setNativeContent(Article article) {
        MetricFontCustomTextView title = (MetricFontCustomTextView) findViewById(R.id.article_title);
        if (title != null) {
            title.setText(article.getTitle());
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewWithContent(ArticleDisplayData articleDisplayData) {
        webView = (WebView) findViewById(R.id.appWebView);
        if (webView == null) {
            Log.e(TAG, "Failed to Start WebView, cannot find webView by id");
            finishWebView(RESULT_ERROR, null);
        }
        final WebSettings webSettings = webView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCachePath(getCacheDir().getPath());

        HttpLookupUtilsImpl httpLookupUtilsImpl = new HttpLookupUtilsImpl(connectionContextService);
        webView.addJavascriptInterface(new ArticleContentWebConnector(articleDisplayData.getArticle().getContent()),
                "androidConnector");
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String articleId = LinkResolverUtils.getArticleIdFromUrl(url);
                if (articleId != null && !articleId.isEmpty()) {
                    Log.i(TAG, "starting activity for article id: " + articleId);
                    startActivity(createIntent(view.getContext(), articleId,EntityType.ARTICLE.toString()));
                } else {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return true;
            }
        });
        webView.loadDataWithBaseURL(httpLookupUtilsImpl.getBaseUrl(), articleDisplayData.getArticleHTML(), "text/html", "UTF-8", "");
    }

    public static Intent createIntent(Context context, String articleId,String subType) {
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra(ArticleActivity.ARTICLE_ID, articleId);
        intent.putExtra(ArticleActivity.SUBTYPE, subType);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if(webView!=null) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finishWebView(RESULT_CANCELED, null);
            }
        } else {
            super.onBackPressed();
        }
    }

    protected void finishWebView(int result, Intent intent) {
        if (intent != null) {
            setResult(result, intent);
        } else {
            setResult(result);
        }
        finish();
    }
}
