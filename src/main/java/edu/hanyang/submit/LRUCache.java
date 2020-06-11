package edu.hanyang.submit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LRUCache {
	private Map<Integer, Data> DataMap;
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
		this.DataMap = new HashMap<>();
		this.capacity = capacity;
		head = new Data(0, null);
		tail = new Data(0, null);
		head.next = tail;
		tail.prev = head;

	}

	//시간 복잡도 O(1)
	private void remove(Data Data) {
		if(Data.prev != null){
			Data.prev.next = Data.next;
        }
        else{
            head = Data.next;
        }
        if(Data.next != null){
        	Data.next.prev = Data.prev;
        }
        else{
            tail = Data.prev;
        }
	}

	//시간 복잡도 O(1)
	private void insertToHead(Data Data) {
		this.head.next.prev = Data;
		Data.next = this.head.next;
		Data.prev = this.head;
		this.head.next = Data;

		DataMap.put(Data.key, Data);
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
		if (DataMap.containsKey(key)) {
			Data getNode = DataMap.get(key);
			remove(getNode);
			insertToHead(getNode);
			return getNode.node;
		}
		//cache에 key가 없다멵 기존에있던 file에서 읽어들이기
		Node node = TinySEBPlusTree.readFile(key);
		Data newData = new Data(node.offset, node);
		insertToHead(newData);
		return node;
	}

	//시간 복잡도 O(1)
	public void put(int key, Node node) throws IOException {
		Data newData = new Data(key, node);
		if (DataMap.containsKey(key)) {
			Data oldData = DataMap.get(key);
			remove(oldData);
		} else {
			if (DataMap.size() >= this.capacity) {
				Data delData = tail.prev;
				remove(delData);
			}
		}
		insertToHead(newData);
		TinySEBPlusTree.writeFile(newData.node);
	}
}