package com.mahendracandi.chatbotgeneratereportapp.model;

public class Knowledge {
    private String title;
    private boolean answer;

    private String[] content;
    private String[] content_str;
    private String[] spell;
    private String[] primary_terms;
    private String[] secondary_terms;
    private String context;

//	private List<String> content;
//	private List<String> content_str;
//	private List<String> spell;
//	private List<String> primary_terms;
//	private List<String> secondary_terms;
//	private List<String> context;

    private String created_date;
    private String knowledge_id;
    private String module;
    private String source;
    private String language;
    private String trainer;
    private String knowledge_owner;
    private String id;
    private String modified_date;
    private String created_by;
    private String modified_by;
    private String version;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }

    public String[] getContent_str() {
        return content_str;
    }

    public void setContent_str(String[] content_str) {
        this.content_str = content_str;
    }

    public String[] getSpell() {
        return spell;
    }

    public void setSpell(String[] spell) {
        this.spell = spell;
    }

    public String[] getPrimary_terms() {
        return primary_terms;
    }

    public void setPrimary_terms(String[] primary_terms) {
        this.primary_terms = primary_terms;
    }

    public String[] getSecondary_terms() {
        return secondary_terms;
    }

    public void setSecondary_terms(String[] secondary_terms) {
        this.secondary_terms = secondary_terms;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getKnowledge_id() {
        return knowledge_id;
    }

    public void setKnowledge_id(String knowledge_id) {
        this.knowledge_id = knowledge_id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getKnowledge_owner() {
        return knowledge_owner;
    }

    public void setKnowledge_owner(String knowledge_owner) {
        this.knowledge_owner = knowledge_owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
