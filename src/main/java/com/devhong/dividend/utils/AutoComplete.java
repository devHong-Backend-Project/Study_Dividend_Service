package com.devhong.dividend.utils;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;

public class AutoComplete {

    private Trie trie = new PatriciaTrie();

    private void add(String s) {
        this.trie.put(s, "world");
    }

    private Object get(String s) {
        return this.trie.get(s);
    }
}
