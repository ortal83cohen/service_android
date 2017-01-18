package com.hpe.sb.mobile.app.common.uiComponents.discussion;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.infra.image.ImageServiceUtil;
import com.hpe.sb.mobile.app.serverModel.comment.Comment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;


public class DiscussionView extends LinearLayout {

    List<Comment> dataSet;

    @Inject
    public ImageServiceUtil imageServiceUtil;

    @Inject
    UserContextService userContext;

    public DiscussionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        ((ServiceBrokerApplication) context.getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
    }

    public void setItems(List<Comment> comments) {
        dataSet = comments;
        refresh();
    }

    private void refresh() {
        removeAllViewsInLayout();
        int position = 0;
        for (Comment comment : dataSet) {
            View view;
            if (userContext.getUserModel() != null && (comment.getAuthorId() == null || comment.getAuthorId().equals(userContext.getUserModel().getId()))) {
                //user
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.user_chat_item, this, false);
                fillCommentText(position, comment, view);
            } else {
                //agent
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.agent_chat_item, this, false);
                ImageView commentImage = (ImageView) view.findViewById(R.id.commentImage);
                TextView name = (TextView) view.findViewById(R.id.name);
                fillCommentText(position, comment, view);
                name.setText(comment.getAuthorName());
                String authorAvatarImageId = comment.getAuthorAvatarImageId();
                if (authorAvatarImageId != null && !authorAvatarImageId.isEmpty()) {
                    imageServiceUtil
                            .loadImageWithCircleMask(authorAvatarImageId, commentImage, null, null);
                }
            }

            addView(view);
            position++;
        }
    }

    private void fillCommentText(int position, Comment comment, View view) {
        TextView text = (TextView) view.findViewById(R.id.comment);
        view.setContentDescription("comment-" + position);
        text.setText(comment.getContent());
    }

    public int getItemsCount() {
        return getChildCount();
    }


    public void addItem(Comment comment) {
        dataSet.add(comment);
        refresh();
    }

    public void deleteItem() {
        dataSet.remove(dataSet.size()-1);
        refresh();
    }
}
