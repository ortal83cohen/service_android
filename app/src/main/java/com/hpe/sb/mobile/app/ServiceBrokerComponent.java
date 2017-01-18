package com.hpe.sb.mobile.app;

import com.hpe.sb.mobile.app.common.dataClients.article.ArticleDataModule;
import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogDataModule;
import com.hpe.sb.mobile.app.common.dataClients.comments.CommentsDataModule;
import com.hpe.sb.mobile.app.common.dataClients.displaylabels.DisplayLabelDataModule;
import com.hpe.sb.mobile.app.common.dataClients.entityDisplay.EntityDisplayDataModule;
import com.hpe.sb.mobile.app.common.dataClients.initialData.InitialDataModule;
import com.hpe.sb.mobile.app.common.dataClients.search.SearchDataModule;
import com.hpe.sb.mobile.app.common.dataClients.tenantSettings.TenantSettingsDataModule;
import com.hpe.sb.mobile.app.common.dataClients.themeSettings.ThemeSettingsDataModule;
import com.hpe.sb.mobile.app.common.dataClients.user.UserDataModule;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ConnectionContextModule;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextDataModule;
import com.hpe.sb.mobile.app.common.services.assetReader.AssetReaderModule;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeModule;
import com.hpe.sb.mobile.app.common.services.version.VersionModule;
import com.hpe.sb.mobile.app.common.uiComponents.categories.CategoryPageRecyclerViewHelper;
import com.hpe.sb.mobile.app.common.uiComponents.categories.adapter.CategoriesViewAdapter;
import com.hpe.sb.mobile.app.common.uiComponents.commonLayout.EntityDescriptionView;
import com.hpe.sb.mobile.app.common.uiComponents.discussion.DiscussionView;
import com.hpe.sb.mobile.app.common.uiComponents.entityIcon.EntityIconImageView;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitiesAdapter;
import com.hpe.sb.mobile.app.common.uiComponents.smartFeed.SmartFeedView;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.TotoItemsModule;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.adapter.TodoCardsCircularArrayAdapter;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandler;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders.ActiveRequestCardViewHolder;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders.ApprovalCardViewHolder;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders.FeedbackCardViewHolder;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders.RequestResolvedCardViewHolder;
import com.hpe.sb.mobile.app.features.login.activities.ChangingDestinationActivity;
import com.hpe.sb.mobile.app.features.login.services.LogoutServiceImpl;
import com.hpe.sb.mobile.app.features.article.ArticleActivity;
import com.hpe.sb.mobile.app.features.catalog.CategoryPageActivity;
import com.hpe.sb.mobile.app.features.detailsActivity.DetailsActivity;
import com.hpe.sb.mobile.app.features.googlepushnotification.GcmRegistrationJobService;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationActionsService;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationModule;
import com.hpe.sb.mobile.app.features.googlepushnotification.gcm.GcmListenerServiceImpl;
import com.hpe.sb.mobile.app.features.googlepushnotification.gcm.GcmRegistrationIntentService;
import com.hpe.sb.mobile.app.features.googlepushnotification.gcm.InstanceIDListenerServiceImpl;
import com.hpe.sb.mobile.app.features.home.MainActivity;
import com.hpe.sb.mobile.app.features.login.activities.PreLoginActivity;
import com.hpe.sb.mobile.app.features.login.activities.ProgrammaticLoginActivity;
import com.hpe.sb.mobile.app.features.login.activities.WebViewLoginActivity;
import com.hpe.sb.mobile.app.features.login.services.ActivationUrlService;
import com.hpe.sb.mobile.app.features.request.NewRequestActivity;
import com.hpe.sb.mobile.app.features.request.RequestModule;
import com.hpe.sb.mobile.app.features.request.recycleview.viewholder.CategoriesViewHolder;
import com.hpe.sb.mobile.app.features.request.recycleview.viewholder.ChooseRelatedEntityInCategoryViewHolder;
import com.hpe.sb.mobile.app.features.request.recycleview.viewholder.ReviewChosenRelatedEntityViewHolder;
import com.hpe.sb.mobile.app.features.router.RouterActivity;
import com.hpe.sb.mobile.app.features.router.RouterModule;
import com.hpe.sb.mobile.app.features.search.SearchResultsActivity;
import com.hpe.sb.mobile.app.infra.InfraModule;
import com.hpe.sb.mobile.app.infra.baseActivities.AbstractUnsupportedActivity;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseHttpActivity;
import com.hpe.sb.mobile.app.infra.db.ExamplesUse;
import com.hpe.sb.mobile.app.infra.db.ProviderModule;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionModule;
import com.hpe.sb.mobile.app.infra.image.ImageService;
import com.hpe.sb.mobile.app.infra.image.ImageServiceUtil;
import com.hpe.sb.mobile.app.infra.restclient.RestClientModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 * Created by malikdav on 20/03/2016.
 *
 */
@Singleton

@Component(modules = {
        RestClientModule.class,
        ConnectionContextModule.class,
        UserContextDataModule.class,
        ThemeSettingsDataModule.class,
        TenantSettingsDataModule.class,
        InfraModule.class,
        ProviderModule.class,
        CatalogDataModule.class,
        CommentsDataModule.class,
        SearchDataModule.class,
        UserDataModule.class,
        RequestModule.class,
        ArticleDataModule.class,
        RouterModule.class,
        DisplayLabelDataModule.class,
        EntityDisplayDataModule.class,
        GooglePushNotificationModule.class,
        EncryptionModule.class,
        TotoItemsModule.class,
        AssetReaderModule.class,
        DateTimeModule.class,
        InitialDataModule.class,
        VersionModule.class
})
public interface ServiceBrokerComponent {

    void inject(ExamplesUse examplesUse);

    void inject(CategoryPageActivity activity);

    void inject(SmartFeedView view);

    void inject(BaseHttpActivity activity);

    void inject(PreLoginActivity activity);

    void inject(WebViewLoginActivity webViewLoginActivity);

    void inject(ProgrammaticLoginActivity programmaticLoginActivity);

    void inject(NewRequestActivity newRequestActivity);

    void inject(MainActivity activity);

    void inject(TodoCardsCircularArrayAdapter todoCardsCircularArrayAdapter);

    void inject(ImageService imageService);

    void inject(ImageServiceUtil imageServiceUtil);

    void inject(DetailsActivity detailsActivity);

    void inject(EntityIconImageView entityIconImageView);

    void inject(DiscussionView discussionView);

    void inject(RelatedEntitiesAdapter relatedEntitiesAdapter);

    void inject(EntityDescriptionView requestDescription);

    void inject(AbstractUnsupportedActivity abstractUnsupportedActivity);

    void inject(ArticleActivity articleActivity);

    void inject(CategoriesViewAdapter categoriesViewAdapter);

    void inject(CategoriesViewHolder categoriesViewHolder);

    void inject(CategoryPageRecyclerViewHelper recyclerViewHelper);

    void inject(TodoCardsViewActionsLogicHandler todoCardsViewEventsHandler);

    void inject(SearchResultsActivity searchResultsActivity);

    void inject(RouterActivity routerActivity);

    void inject(InstanceIDListenerServiceImpl instanceIDListenerService);

    void inject(GcmRegistrationIntentService gcmRegistrationIntentService);

    void inject(GcmRegistrationJobService gcmRegistrationJobService);

    void inject(GcmListenerServiceImpl gcmListenerService);

    void inject(GooglePushNotificationActionsService googlePushNotificationActionsService);

    void inject(ChooseRelatedEntityInCategoryViewHolder categoryViewHolder);

    void inject(ReviewChosenRelatedEntityViewHolder reviewChosenRelatedEntityViewHolder);

    void inject(RequestResolvedCardViewHolder requestResolvedCardViewHolder);

    void inject(FeedbackCardViewHolder feedbackCardViewHolder);

    void inject(ApprovalCardViewHolder approvalCardViewHolder);

    void inject(ActiveRequestCardViewHolder activeRequestCardViewHolder);

    void inject(ActivationUrlService activationUrlService);

    void inject(BaseActivity baseActivity);

    void inject(LogoutServiceImpl logoutService);

    void inject(ChangingDestinationActivity changingDestinationActivity);
}
