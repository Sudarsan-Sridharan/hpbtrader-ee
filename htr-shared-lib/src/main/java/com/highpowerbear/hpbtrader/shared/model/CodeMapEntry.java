package com.highpowerbear.hpbtrader.shared.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by robertk on 6.4.2016.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CodeMapEntry<K, V> {
    private K key;
    private V value;

    public CodeMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
