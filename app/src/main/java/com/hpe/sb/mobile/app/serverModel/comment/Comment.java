package com.hpe.sb.mobile.app.serverModel.comment;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {

    private String authorId;
    private String authorName;
    private String authorAvatarImageId;
    private String content;
    private CommentVisibility commentVisibility;
    private CommentFunctionalType commentFunctionalType;
    private long creationTime;

    public Comment() {
    }

    public Comment(CommentFunctionalType commentFunctionalType, CommentVisibility commentVisibility, String content,
                   long creationTime) {
        this(null, null, null, commentFunctionalType, commentVisibility, content, creationTime);
    }

    public Comment(String authorId, String authorName, String authorAvatarImageId, CommentFunctionalType commentFunctionalType,
                   CommentVisibility commentVisibility, String content, long creationTime) {
        this.authorAvatarImageId = authorAvatarImageId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.commentFunctionalType = commentFunctionalType;
        this.commentVisibility = commentVisibility;
        this.content = content;
        this.creationTime = creationTime;
    }

    protected Comment(Parcel in) {
        authorId = in.readString();
        authorName = in.readString();
        authorAvatarImageId = in.readString();
        content = in.readString();
        creationTime = in.readLong();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getAuthorAvatarImageId() {
        return authorAvatarImageId;
    }

    public void setAuthorAvatarImageId(String authorAvatarImageId) {
        this.authorAvatarImageId = authorAvatarImageId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public CommentFunctionalType getCommentFunctionalType() {
        return commentFunctionalType;
    }

    public void setCommentFunctionalType(CommentFunctionalType commentFunctionalType) {
        this.commentFunctionalType = commentFunctionalType;
    }

    public CommentVisibility getCommentVisibility() {
        return commentVisibility;
    }

    public void setCommentVisibility(CommentVisibility commentVisibility) {
        this.commentVisibility = commentVisibility;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (creationTime != comment.creationTime) return false;
        if (authorId != null ? !authorId.equals(comment.authorId) : comment.authorId != null) return false;
        if (authorName != null ? !authorName.equals(comment.authorName) : comment.authorName != null) return false;
        if (authorAvatarImageId != null ? !authorAvatarImageId.equals(comment.authorAvatarImageId) : comment.authorAvatarImageId != null)
            return false;
        if (content != null ? !content.equals(comment.content) : comment.content != null) return false;
        if (commentVisibility != comment.commentVisibility) return false;
        return commentFunctionalType == comment.commentFunctionalType;

    }

    @Override
    public int hashCode() {
        int result = authorId != null ? authorId.hashCode() : 0;
        result = 31 * result + (authorName != null ? authorName.hashCode() : 0);
        result = 31 * result + (authorAvatarImageId != null ? authorAvatarImageId.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (commentVisibility != null ? commentVisibility.hashCode() : 0);
        result = 31 * result + (commentFunctionalType != null ? commentFunctionalType.hashCode() : 0);
        result = 31 * result + (int) (creationTime ^ (creationTime >>> 32));
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authorId);
        dest.writeString(authorName);
        dest.writeString(authorAvatarImageId);
        dest.writeString(content);
        dest.writeLong(creationTime);
    }
}
