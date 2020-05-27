package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class TinySEBPlusTree implements BPlusTree{
//	static int M=3;
//	static String path = "./tmp/";
//	static int blocksize = M*4;
//	static BTNode node = new BTNode(M); //비교 해야할 글로벌 노드 
	
	
	int blocksize;
	int nblocks;
	RandomAccess node_File;
	RandomAccess meta_File;
	
	int offset;
	int node_cur;
	
	
	public static void make_node(String filepath, int blocksize, int nblocks) {
		
	}
	
	public static void main(String[] args) throws IOException {
		String filePath = "./test2.data";
		
		String filetest = "./test.data";
		
		List<Integer> a = new ArrayList<>(10);
		
		a.add(1);
		a.add(2);
		
		
		
		
	}
	public static String make_dir(String tmdir, int step) {
		String path = tmdir+File.separator+String.valueOf(step);
		File Folder = new File(path);
		
		if (!Folder.exists()) {
			try {
				Folder.mkdir();
			} catch(Exception e) {
				e.getStackTrace();
			}
		}
		return path;
	}
	public static void make_tmp(String tmdir) {
		File Folder = new File(tmdir);

		if (!Folder.exists()) {
			try {
				Folder.mkdir();
			} catch(Exception e) {
				e.getStackTrace();
			}
		}
		
	}
	
	private static int[] readFromFile(String filePath, int position, int size)
		throws IOException {
		
		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		file.seek(position);
		
		int[] integers = new int[size/Integer.BYTES];
		for(int i = 0; i < integers.length; i++) {
			integers[i] = file.readInt();
		}
		file.close();
		return integers;
	}
	
	private static void writeToFile(String filePath, int[] integers, int position)
		throws IOException{
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		file.seek(position);
		for(int i : integers) {
			file.writeInt(i);
		}
		file.close();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insert(int key, int val) throws IOException{

	}
	@Override
	public void open(String metafile, String filepath, int blocksize, int nblocks) throws IOException {	
	
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
	public int search_leaf_node_offset(int key) {
		return 0;
	}
	

}

class Node {
	int offset;
	
	/* status 
	 * 0 : root
	 * 1 : non leaf
	 * 2 : leaf
	 */
	int status; 
	
	int max_keys;
	
	List<Integer> keys;
	List<Integer> vals;
	
	
	/*생성자는 2가지
	 * stream을 input으로 받을때 <- search 할때 필요
	 * 그냥 크기만큼 key, val을 생성 <- 새로운 노드를 만들때 필요 ex) split, write
	 */
	
	
	Node(int[] integers, int blocksize, int offset, int status) {
		int max_keys = (blocksize - 2*Integer.BYTES) / (Integer.BYTES * 2);
		int max_vals = max_keys + 1;
		
		this.max_keys = max_keys;
		keys = new ArrayList<>(max_keys);
		vals = new ArrayList<>(max_vals);
		
		this.offset = offset;
		this.status = status;
		
		/**/
		for(int i = 0; i < integers.length / 2; i ++) {
			vals.add(integers[i*2]); //0, 2, 4, 8, 16, ... 번째 숫자들어감
			keys.add(integers[i*2+1]); //1, 3, 5, 7, 9 ... 번째 숫자 들어감
		}
		vals.add(integers[integers.length-1]);
		
	}
	
	Node(int blocksize, int offset, int status) {
		int max_keys = (blocksize - 2*Integer.BYTES) / (Integer.BYTES * 2);
		int max_vals = max_keys + 1;
		
		this.max_keys = max_keys;
		keys = new ArrayList<>(max_keys);
		vals = new ArrayList<>(max_vals);
		
		this.offset = offset;
		this.status = status;
	}
	
	public boolean isFull() {
		if(keys.size() == this.max_keys) return true;
		
		return false;
	}
	
	/*
	 * insert를 하기전에 full인지 아닌지부터 check*/
	public void insert(int key, int val) {
		if (keys.size() == 0) {
			keys.add(key);
			vals.add(val);
			return ;
		}
		Iterator<Integer> it = keys.iterator();
			
		while(it.hasNext()) {
			int n = it.next();
			if(n > key) {
				keys.add(keys.indexOf(n), key);
				vals.add(vals.indexOf(n), val);
				return;
			}
		}
		keys.add(key);
		vals.add(val);
		
	}
	
	public Integer getkey(int key){ 
	
	}
	

	
}





