package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.*;

public class TinySEBPlusTree implements BPlusTree{
	private BTNode root;

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
		root = new BTNode(blocksize);
		
	}

	@Override
	public int search(int key) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean isLeafNode(BTNode node) {
		return true;
	}
	public boolean isRootNode(BTNode node) {
		return true;
	}
	public void splitLeafNode(BTNode node) {
		
	}
	public void splitNonLeafNode(BTNode node) {
		
	}
	

}

class BTNode {
	LinkedList<Integer> keys = new LinkedList<>(); //blocksize(fanout) 만큼의 길이
	ArrayList<BTNode> points; // blocksize+1(fanout) 만큼의 길이
	
	int fanout;
	
	boolean isRoot = false;
	boolean isFull = false;
	boolean isLeaf;
	
	/*
	 * 제일 첫 노드 생성시 쓰는 생성자
	 * root = true
	 * leaf = true
	 * full = false
	 */
	public BTNode(int fanout) {
		this.fanout = fanout;
		this.isRoot = true;
		this.isLeaf = true;
		this.points = new ArrayList<>(fanout+1);
	}
	/*
	 * Leaf or non-Leaf 노드를 만든데 쓰는 생성자
	 */
	
	public BTNode(int fanout, boolean isLeaf) {
		this.fanout = fanout;
		this.isLeaf = isLeaf;
		this.points = new ArrayList<>(fanout+1);
	}
	
	public boolean isFull() {
		if(keys.size() == fanout) this.isFull = true;
		return isFull;
	}
	/*
	 * halfrule을 만족하면 true
	 * 아니라면 false
	 */
	public boolean isHalfRule() { 
		if(keys.size() > fanout/2) return true;
		return false;
	}
	
	/*
	 * node안에서 key값에 해당하는 point를 return
	 * 이부분은 다시 짜야함
	 */
	public BTNode getkey(int key){ 
		
		//node안에 key가 있으면 해당 point+1 return
		if (keys.contains(key)) {
			return points.get(keys.indexOf(key) + 1);
		}
		//node안에 key가 없으면 keys의 범위를 검색해 points return
		Iterator<Integer> it = keys.iterator();
		while(it.hasNext()){
            int n = it.next();
            if(key < n) {
            	return points.get(keys.indexOf(n));
            }
        }
		
		return points.get(points.size()-1);	
    }


}



//	
//	/*
//	 * Leaf or non-Leaf 노드를 만든데 쓰는 생성자
//	 */
//	public Node(int fanout, boolean isLeaf, int label, Set point) {
//		this.fanout = fanout;
//		this.isLeaf = isLeaf;
//		node.add(new Set(label, point));
//	}
//	
//	
//	public boolean isHalf() {
//		if (this.node.size() > fanout/2) {
//			return true;
//		}
//		return false;
//	}
//	
//	
//}
