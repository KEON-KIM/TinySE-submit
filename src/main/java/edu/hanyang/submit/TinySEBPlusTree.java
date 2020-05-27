package edu.hanyang.submit;

import edu.hanyang.indexer.BPlusTree;
import java.util.LinkedList;
import java.util.ArrayList;
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
		
		int[] integers;
		
		integers = readFromFile(filePath, 0, 2048);
		int k = 0;
		for(int i : integers) {
			System.out.println(i);
			k++;
		}
		System.out.println(k);
		
		
		
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
	public void UpdateNode() throws IOException{
		

	}
//	
//	//메타파일 읽고 루트의 offset(node번호) 찾기
//	public int read_root_node() throws IOException{
//		boolean EOF = false;
//		DataInputStream is = new DataInputStream(
//				new BufferedInputStream(
//						new FileInputStream(path+File.separator+"node.meta"), blocksize));
//		while(!EOF) {
//			try {
//				int root = is.readInt();
//				int root_offset = is.readInt();
//				if (root == 1)return root_offset;
//			}catch(Exception e) {
//				EOF = true;
//			}
//		}
//		return -1; //-1 몾찾음
//	}

	@Override
	public void open(String metafile, String filepath, int blocksize, int nblocks) throws IOException {	
	
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

class Node {
	int offset;
	
	/* status 
	 * 0 : root
	 * 1 : non leaf
	 * 2 : leaf
	 */
	int status; 
	
	List<Integer> keys;
	List<Integer> vals;
	
	
	/*생성자는 2가지
	 * stream을 input으로 받을때 <- search 할때 필요
	 * 그냥 크기만큼 key, val을 생성 <- 새로운 노드를 만들때 필요 ex) split, write
	 */
	
	
	Node(int[] integers, int blocksize, int offset, int status) {
		blocksize -= 8; // blocksize에서 한쌍 덜 읽어오게 8을 빼
		keys = new ArrayList<>(blocksize / (Integer.BYTES * 2));
		vals = new ArrayList<>(blocksize / (Integer.BYTES * 2) + 1);
		
		this.offset = offset;
		this.status = status;
		
		for(int i = 0; i < integers.length / 2; i ++) {
			
		}
		
		
	}
	
	Node(int blocksize, int offset, int status) {
		
	}
}




class BTNode {

//	static String path = "./tmp";
	
	static int offsets; //노드 생성시 offset값을 변경해주기 위해
	int fanout; // blocksize를 의미함
	int blocksize;
	int offset;//random access시 필요, 
	
	List<Integer> keys; //blocksize(fanout) 만큼의 길이
	List<Integer> vals; // blocksize+1(fanout) 만큼의 길이
	/* 추상적인 BTNode의 모습
	 *    |k_1|k_2|k_3|k_4|k_5|...|k_n|		n개의 key linked list
	 *  |p_1|p_2|p_3|p_4|p_5|...|p_n|p_n+1|	n+1개의 BTNode 주소가 저장된 ArrayList
	 * */
	
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
		this.blocksize = fanout*4;
		if(offsets ==0) {
			this.isRoot = true;
			this.isLeaf = true;
		}
		this.keys = new ArrayList<Integer>(fanout);
		this.vals = new ArrayList<Integer>(fanout+1);
		this.offset = offsets;
		offsets++;
	}
	/*
	 * Leaf or non-Leaf 노드를 만든데 쓰는 생성자
	 */
//	
//	public BTNode(int fanout, boolean isLeaf) {
//		this.fanout = fanout;
//		this.isLeaf = isLeaf;
//		this.keys = new ArrayList<>(fanout);
//		this.vals = new ArrayList<>(fanout+1);
//	}
	
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
	//노드 상태 확인 root인지 leaf인지 non_leaf인지
	public int status() {
		if(isRoot) return 1;
		else if(isLeaf) return 3;
		else return 2;
	}
	//메타 파일 생성
		//매번 메타파일 수정해야 함.
	
//	
//	public void DataToNode(int offset) throws IOException {
//		DataInputStream is = new DataInputStream(
//				new BufferedInputStream(
//						new FileInputStream(path+File.separator+"+offset+.data"), blocksize));
//		ArrayList<Integer> Nkeys = new ArrayList<Integer>();
//		ArrayList<Integer> Nvals = new ArrayList<Integer>();
//		try {
//			Nvals.add(is.readInt());
//			Nkeys.add(is.readInt());	
//		}catch(Exception e){
//			int i;
//			//노드 파일 읽어 들여오기.
//			for(i =0 ; i<keys.size(); i++) {
//				this.keys.set(i, Nkeys.get(i));
//				this.vals.set(i, Nvals.get(i));
//			}
//			this.vals.set(i+1, Nvals.get(i+1));
//			this.offset = offset;
//		}
//		
//	}
//	
//	public void insert(int key, int val) throws IOException  {
//		if(isLeaf) {
//			init_insert(key, val);
//			if(isFull()) {
//				devide(key,val);
//			}
//		}
//		DataOutputStream os = open_output_stream(path,offset,blocksize);
//		write_run_file(keys,vals,os);
//		write_meta_file();
//	}
	
	/*
	 * node안에서 key값에 해당하는 point를 return
	 * 이부분은 다시 짜야함
	 */
	public void init_insert(int key, int val) {
		if (keys.size() == 0) {
			keys.add(key);
			vals.add(val);
		}
		//if (keys.size() == this.fanout) throw new Exception();
		else {
			Iterator<Integer> it = keys.iterator();
			
			while(it.hasNext()) {
				int n = it.next();
				if(n > key) {
					keys.add(keys.indexOf(n), key);
					vals.add(vals.indexOf(n), val);
					break;
				}
			}
			keys.add(key);
			vals.add(val);
		}
	}
	public static DataOutputStream open_output_stream(String path, int offset, int blocksize) throws IOException {
		return new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path+File.separator+offset+".data"), blocksize));
	}
	//node data file 생성
	public static void write_run_file(List<Integer> Keys,List<Integer> Vals, DataOutputStream os) throws IOException {
		System.out.println("Key Array : "+Keys);
		System.out.println("Val Array : "+Vals);
		for(int i = 0; i<Keys.size();i++) {
			os.writeInt(Vals.get(i));
			os.writeInt(Keys.get(i));	
		}
		os.close();
	}//node 정보 저장 meta file 저장
//	
//	public void write_meta_file(BTNode Node) throws IOException{
//		DataOutputStream os = new DataOutputStream(
//				new BufferedOutputStream(
//						new FileOutputStream(path+File.separator+"node.meta",true), blocksize)); //true 주게되면 이어쓰기
//		int status=Node.status();
//		System.out.println("Status : "+status);
//		System.out.println("Node offSet : "+Node.offset);
//		os.writeInt(Node.offset);
//		os.writeInt(status);
//		os.close();
//	}
//	public void write_meta_file() throws IOException{
//		DataOutputStream os = new DataOutputStream(
//				new BufferedOutputStream(
//						new FileOutputStream(path+File.separator+"node.meta",true), blocksize)); //true 주게되면 이어쓰기
//		int status=status();
//		System.out.println("Status : "+status);
//		System.out.println("Node offSet : "+offset);
//		os.writeInt(offset);
//		os.writeInt(status);
//		os.close();
//	}
//	//Root가 Full일 때 나누는 경우
//	public void devide(int key, int val) throws IOException{
//		System.out.println("offset : "+offset);
//		BTNode root = new BTNode(fanout); //root노드 생성 
//		DataOutputStream os1 = open_output_stream(path,root.offset,blocksize);
//		
//		System.out.println("offset : "+root.offset);
//		BTNode newest = new BTNode(fanout); //new 노드 생성
//		DataOutputStream os2 = open_output_stream(path,newest.offset,blocksize);
//		System.out.println("offset : "+newest.offset);
//		//중간값 미리 저장
//		int tmp=keys.get(fanout/2);
//		
//		//분할
//		for(int i =fanout/2; i <= keys.size() ;i++) {
////			System.out.println("Key Value : "+keys.get(fanout/2));
//			newest.keys.add(keys.get(fanout/2));
//			newest.vals.add(vals.get(fanout/2));
//			keys.remove(fanout/2);
//			vals.remove(fanout/2);
//		}
//		//중간 값 root 노드에 저장
//		root.keys.add(tmp);
//		root.vals.add(offset);
//		root.vals.add(newest.offset);
//		root.isRoot = true;
//	
//		//origin 노드 root 효력 잃음
//		this.isRoot = false;
//		//각 노드data 생성
//		write_run_file(root.keys,root.vals,os1);
//		write_meta_file(root);
//		write_run_file(newest.keys,newest.vals,os2);
//		write_meta_file(newest);
//		
//	}
//	public Integer getkey(int key){ 
//		
//		if(key >= keys.get(this.fanout - 1)) return vals.get(this.fanout);
//		
//		Iterator<Integer> it = keys.iterator();
//		int i = 0;
//		
//		/*
//		 * 이부분이문제
//		 * it.next가 key보다 큰게 나왔을때 i++을 할까 안할까에따라 다름
//		 */
//		while((it.next() <= key) && it.hasNext() ){
//            i++;
//        }
//		return vals.get(i);
//    }


}




