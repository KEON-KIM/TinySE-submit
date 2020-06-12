package edu.hanyang.submit;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache {
	private Map<Integer, Data> dataMap;
	private int capacity;
	private Data head;
	private Data tail;

	private class Data {
		private int key;
		private Node node;
		private Data prev;
		private Data next;

		public Data(int key, Node node) {
			this.key = key;
			this.node = node;
			this.next = null;
			this.prev = null;
		}
	}

	public LRUCache(int capacity) {
		this.dataMap = new HashMap<>(capacity);
		this.capacity = capacity;
		head = new Data(0, null);
		tail = new Data(0, null);
		head.next = tail;
		tail.prev = head;

	}

	//시간 복잡도 O(1)
	private void remove(Data data) {
//		if(data.prev != null){
//			data.prev.next = data.next;
//        }
//        else{
//            head = data.next;
//        }
//        if(data.next != null){
//        	data.next.prev = data.prev;
//        }
//        else{
//            tail = data.prev;
//        }
		data.prev.next = data.next;
		data.next.prev = data.prev;

		dataMap.remove(data.key);
	}

	//시간 복잡도 O(1)
	private void insertToHead(Data data) {
		this.head.next.prev = data;
		data.next = this.head.next;
		data.prev = this.head;
		this.head.next = data;

		dataMap.put(data.key, data);
//		Data.next = head;
//		Data.prev = null;
//        if(head != null) {
//            head.prev = Data;
//        }
//        head = Data;
//        if(tail == null){
//            tail = head;
//        }
	}

	//시간 복잡도 O(1)
	public Node get(int key) throws IOException {
		if (dataMap.containsKey(key)) {
			Data getNode = dataMap.get(key);
			remove(getNode);
			insertToHead(getNode);
			return getNode.node;
		} else {
//		//cache에 key가 없다멵 기존에있던 file에서 읽어들이기
//			if(dataMap.size() >= this.capacity) {
//				Data delData = tail.prev;
//				remove(delData);
//			}
			Node node = TinySEBPlusTree.readFile(key);
			if (dataMap.size() >= this.capacity) {
				Data delData = tail.prev;
				remove(delData);
			}
			insertToHead(new Data(key, node));
			return node;
		}
	}

	//시간 복잡도 O(1)
	public void put(int key, Node node) throws IOException {
		Data newData = new Data(key, node);
		if (dataMap.containsKey(key)) {
			Data oldData = dataMap.get(key);
			remove(oldData);
		} else {
			if (dataMap.size() >= this.capacity) {
				Data delData = tail.prev;
				remove(delData);
			}
		}
		insertToHead(newData);
		TinySEBPlusTree.writeFile(newData.node);
	}
}


//public class LRUCache {
//	   class Data {
//	        int key;
//	        Node value;
//	        Data pre;
//	        Data next;
//	        public Data(int key, Node value){
//	            this.key = key;
//	            this.value = value;
//	        }
//	    }
//	    int capacity;
//	    HashMap<Integer, Data> map;
//	    Data head;
//	    Data end;
//	    public LRUCache(int capacity) {
//	        this.capacity = capacity;
//	        map = new HashMap<Integer, Data>(capacity);
//	    }
//	    public void remove(Data n){
//	        if(n.pre != null){
//	            n.pre.next = n.next;
//	        }
//	        else{
//	            head = n.next;
//	        }
//	        if(n.next != null){
//	            n.next.pre = n.pre;
//	        }
//	        else{
//	            end = n.pre;
//	        }
//	    }
//	    public void setHead(Data n){
//	        n.next = head;
//	        n.pre = null;
//	        if(head != null) {
//	            head.pre = n;
//	        }
//	        head = n;
//	        if(end == null){
//	            end = head;
//	        }
//	    }
//	    public Node get(int key) throws IOException {
//	        if(map.containsKey(key)){
//	            Data n = map.get(key);
//	            remove(n);
//	            setHead(n);
//	            return n.value;
//	        }
//	        else {
//	            Node newNode = TinySEBPlusTree.readFile(key);
//	            put(key, newNode);
//	            return newNode;
//	        }
//	    }
//	    public void put(int key, Node value) throws IOException {
//	        if(map.containsKey(key)){
//	            Data n = map.get(key);
//	            n.value = value;
//	            remove(n);
//	            setHead(n);
//	        }
//	        else{
//	            Data n = new Data(key,value);
//	            if(map.size() >= capacity){
//	            	TinySEBPlusTree.writeFile(value);
//	                map.remove(end.key);
//	                remove(end);
//	            }
//	            setHead(n);
//	            map.put(key,n);
//	        }
//	    }
//	}
