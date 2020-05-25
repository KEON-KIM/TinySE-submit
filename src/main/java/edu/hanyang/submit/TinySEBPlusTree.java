package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class TinySEBPlusTree implements BPlusTree{
	private BTNode init_node;
	
	public static void main(String[] args) throws IOException {
		String filepath = "./src/test/resources/stage3-15000000.data";
		
		DataInputStream is =  new DataInputStream(
									new BufferedInputStream(
											new FileInputStream(filepath), 1024)
													);
		
		
		int offset = 0;
		int num_pairs = is.available() / 8 ; // 총 바이트 / (4*2)
		int blocksize = 1024;
		//init node 생성
		BTNode init_node = new BTNode(blocksize);
		init_node.offset = offset;
		
		for(int i = 0; i < blocksize / 8; i++) {
			int key = is.readInt();
			int val = is.readInt();
			init_node.insert(key, val);
			
		}
		
		for(int i : init_node.keys) {
			System.out.println(i);
		}
	}
	private static byte[] readFromFile(String filePath, int position, int size)
		throws IOException {
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		file.seek(position);
		byte[] bytes = new byte[size];
		file.read(bytes);
		file.close();
		return bytes;
	}
	
	private static void writeToFile(String filePath, byte[] data, int position)
		throws IOException{
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		file.seek(position);
		file.write(data);
		file.close();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(int key, int val, int nblocks) {
		BTNode p = this.init_node;
		BTNode newNode = new BTNode(nblocks);
		
		
		
	}

	@Override
	public void open(String metafile, String filepath, int blocksize, int nblocks) throws IOException {
		init_node = new BTNode(blocksize);
		
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(filepath), blocksize)
							);
		
		while(is.available() != 0) {
			
		}
		
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
	ArrayList<Integer> vals; // blocksize+1(fanout) 만큼의 길이
	/* 추상적인 BTNode의 모습
	 *    |k_1|k_2|k_3|k_4|k_5|...|k_n|		n개의 key linked list
	 *  |p_1|p_2|p_3|p_4|p_5|...|p_n|p_n+1|	n+1개의 BTNode 주소가 저장된 ArrayList
	 * */
	
	
	int fanout; // blocksize를 의미함
	int offset; //random access시 필요
	
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
		this.vals = new ArrayList<>(fanout+1);
	}
	/*
	 * Leaf or non-Leaf 노드를 만든데 쓰는 생성자
	 */
	
	public BTNode(int fanout, boolean isLeaf) {
		this.fanout = fanout;
		this.isLeaf = isLeaf;
		this.keys = new ArrayList<>(fanout);
		this.vals = new ArrayList<>(fanout+1);
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
	public void insert(int key, int val)  {
		if (keys.size() == 0) {
			this.keys.add(key);
			this.vals.add(val);
		}
		//if (keys.size() == this.fanout) throw new Exception();
		
		
		Iterator<Integer> it = keys.iterator();
		
		
		
		while(it.hasNext()) {
			int n = it.next();
			if(n > key) {
				keys.add(keys.indexOf(n), key);
				vals.add(keys.indexOf(n), val);
				return ;
			}
		}
		keys.add(key);
		vals.add(val);
		
	}
	
	/*
	 * node안에서 key값에 해당하는 point를 return
	 * 이부분은 다시 짜야함
	 */
	public Integer getkey(int key){ 
		
		if(key >= keys.get(this.fanout - 1)) return vals.get(this.fanout);
		
		Iterator<Integer> it = keys.iterator();
		int i = 0;
		
		/*
		 * 이부분이문제
		 * it.next가 key보다 큰게 나왔을때 i++을 할까 안할까에따라 다름
		 */
		while((it.next() <= key) && it.hasNext() ){
            i++;
        }
		return vals.get(i);
    }


}

