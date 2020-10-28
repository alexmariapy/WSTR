package com.writingstar.autotypingandtextexpansion.Model;

public class TxpGetSet {

    public int phrase_id;
    public String phrase_title;
    public String phrase_detail;
    public String phrase_modified_time;
    public String phrase_note;
    public String phrase_use_time;
    public int phrase_usage_count;
    public int backspace_undo;
    public int smart_case;
    public int append_case;
    public int space_for_expansion;
    public int within_words;

    public TxpGetSet(String tit) {
        this.phrase_detail = tit;
    }

    public TxpGetSet(int phrase_id, String phrase_title, String phrase_detail, String phrase_modified_time, String phrase_note, String phrase_use_time, int phrase_usage_count, int backspace_undo, int smart_case, int append_case, int space_for_expansion, int within_words) {
        this.phrase_id = phrase_id;
        this.phrase_title = phrase_title;
        this.phrase_detail = phrase_detail;
        this.phrase_modified_time = phrase_modified_time;
        this.phrase_note = phrase_note;
        this.phrase_use_time = phrase_use_time;
        this.phrase_usage_count = phrase_usage_count;
        this.backspace_undo = backspace_undo;
        this.smart_case = smart_case;
        this.append_case = append_case;
        this.space_for_expansion = space_for_expansion;
        this.within_words = within_words;
    }

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

    public String getPhrase_modified_time() {
        return phrase_modified_time;
    }

    public void setPhrase_modified_time(String phrase_modified_time) {
        this.phrase_modified_time = phrase_modified_time;
    }

    public String getPhrase_note() {
        return phrase_note;
    }

    public void setPhrase_note(String phrase_note) {
        this.phrase_note = phrase_note;
    }

    public String getPhrase_use_time() {
        return phrase_use_time;
    }

    public void setPhrase_use_time(String phrase_use_time) {
        this.phrase_use_time = phrase_use_time;
    }

    public int getPhrase_usage_count() {
        return phrase_usage_count;
    }

    public void setPhrase_usage_count(int phrase_usage_count) {
        this.phrase_usage_count = phrase_usage_count;
    }

    public int getBackspace_undo() {
        return backspace_undo;
    }

    public void setBackspace_undo(int backspace_undo) {
        this.backspace_undo = backspace_undo;
    }

    public int getSmart_case() {
        return smart_case;
    }

    public void setSmart_case(int smart_case) {
        this.smart_case = smart_case;
    }

    public int getAppend_case() {
        return append_case;
    }

    public void setAppend_case(int append_case) {
        this.append_case = append_case;
    }

    public int getSpace_for_expansion() {
        return space_for_expansion;
    }

    public void setSpace_for_expansion(int space_for_expansion) {
        this.space_for_expansion = space_for_expansion;
    }

    public int getWithin_words() {
        return within_words;
    }

    public void setWithin_words(int within_words) {
        this.within_words = within_words;
    }
}
