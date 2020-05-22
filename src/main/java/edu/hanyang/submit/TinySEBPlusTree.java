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
	ArrayList<Integer> keys; //blocksize(fanout) 만큼의 길이
	ArrayList<BTNode> points; // blocksize+1(fanout) 만큼의 길이
	/* 추상적인 BTNode의 모습
	 *    |k_1|k_2|k_3|k_4|k_5|...|k_n|		n개의 key linked list
	 *  |p_1|p_2|p_3|p_4|p_5|...|p_n|p_n+1|	n+1개의 BTNode 주소가 저장된 ArrayList
	 * */
	
	
	int fanout; // blocksize를 의미함
	
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
		this.keys = new ArrayList<>(fanout);
		this.points = new ArrayList<>(fanout+1);
	}
	/*
	 * Leaf or non-Leaf 노드를 만든데 쓰는 생성자
	 */
	
	public BTNode(int fanout, boolean isLeaf) {
		this.fanout = fanout;
		this.isLeaf = isLeaf;
		this.keys = new ArrayList<>(fanout);
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
		
		if(key >= keys.get(this.fanout - 1)) return points.get(this.fanout);
		
		Iterator<Integer> it = keys.iterator();
		int i = 0;
		
		/*
		 * 이부분이문제
		 * it.next가 key보다 큰게 나왔을때 i++을 할까 안할까에따라 다름
		 */
		while((it.next() <= key) && it.hasNext() ){
            i++;
        }
		return points.get(i);
    }
	public static void main(String[] args) {
		ArrayList<Integer> a = new ArrayList<>(3);
		a.add(1);
		a.add(2);
		a.add(3);
		
		System.out.println(a.get(-1));
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
