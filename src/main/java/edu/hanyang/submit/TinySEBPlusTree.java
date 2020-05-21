package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;
import java.util.LinkedList;

public class TinySEBPlusTree implements BPlusTree{

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(int key, int val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void open(String metafile, String filepath, int blocksize, int nblocks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int search(int key) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean isLeafNode(Node node) {
		return true;
	}
	public boolean isRootNode(Node node) {
		return true;
	}
	public void splitLeafNode(Node node) {
		
	}
	public void splitNonLeafNode(Node node) {
		
	}
	

}
class Set<E, T> {
	E key;
	T val;
	
	public Set<E, T>(E key, T val) {
		this.key = key;
		this.val = val;
	}
}

class Node {
//	LinkedList<> node = new LinkedList<>()
	boolean isFull = false;
	boolean isLeaf;
	boolean isRoot;
	int fanout;
	
	public boolean isHalf() {
		return true;
	}
	
	
}
