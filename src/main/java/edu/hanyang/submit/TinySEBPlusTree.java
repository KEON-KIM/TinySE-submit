package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;

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
		
	}
	public boolean isRootNode(Node node) {
		
	}
	public void splitLeafNode(Node node) {
		
	}
	public void splitNonLeafNode(Node node) {
		
	}
	

}


class Node {
	
	boolean isFull = false;
	boolean isLeaf;
	boolean isRoot;
	int fanout;
	
	public boolean isHalf() {
		
	}
	
	
}
