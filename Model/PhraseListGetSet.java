package com.writingstar.autotypingandtextexpansion.Model;

public class PhraseListGetSet {

	public int phrase_id;
    public String phrase_title;
    public String phrase_detail;

 /*   public PhraseListGetSet(int phrase_id, String phrase_title, String phrase_detail) {
        this.phrase_id = phrase_id;
        this.phrase_title = phrase_title;
        this.phrase_detail = phrase_detail;
    }
*/
    public int getPhrase_id() {
        return phrase_id;
    }

    public void setPhrase_id(int phrase_id) {
        this.phrase_id = phrase_id;
    }

    public String getPhrase_title() {
        return phrase_title;
    }

    public void setPhrase_title(String phrase_title) {
        this.phrase_title = phrase_title;
    }

    public String getPhrase_detail() {
        return phrase_detail;
    }

    public void setPhrase_detail(String phrase_detail) {
        this.phrase_detail = phrase_detail;
    }

}
