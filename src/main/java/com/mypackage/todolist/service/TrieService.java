package com.mypackage.todolist.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrieService {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEndOfWord;
    }

    private final TrieNode root = new TrieNode();

    @PostConstruct
    public void loadWords() {
        String filePath = "src/main/resources/words.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String word;
            while ((word = br.readLine()) != null) {
                insert(word.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            if (ch < 'a' || ch > 'z') continue; // Ignore non-alphabetic characters
            int index = ch - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.isEndOfWord = true;
    }

    public List<String> getSuggestions(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = root;

        for (char ch : prefix.toCharArray()) {
            int index = ch - 'a';
            if (node.children[index] == null) {
                return results;
            }
            node = node.children[index];
        }

        findWords(node, prefix, results);
        return results;
    }

    private void findWords(TrieNode node, String prefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(prefix);
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            int index = ch - 'a';
            if (node.children[index] != null) {
                findWords(node.children[index], prefix + ch, results);
            }
        }
    }
}
