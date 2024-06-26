package com.hari.solutionhub.Model;

public class Post {
    private String askedBy,date,postId,publisher,question,topic,questionImage;

    public Post() {
    }

    public Post(String askedBy, String date, String postId, String publisher, String question, String topic, String questionImage) {
        this.askedBy = askedBy;
        this.date = date;
        this.postId = postId;
        this.publisher = publisher;
        this.question = question;
        this.topic = topic;
        this.questionImage = questionImage;
    }

    public String getAskedBy() {
        return askedBy;
    }

    public void setAskedBy(String askedBy) {
        this.askedBy = askedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }
}
