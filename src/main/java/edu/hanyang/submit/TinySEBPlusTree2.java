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

public class TinySEBPlusTree2 implements BPlusTree{
	static int offset;
	static int M=3;
	static String path = "./tmp/";
	static int blocksize = M*4;
	static BTNodes node = new BTNodes(M); //비교 해야할 글로벌 노드 
	public static void main(String[] args) throws IOException {
		
		System.out.println("fanout : "+M);
		TinySEBPlusTree tree = new TinySEBPlusTree();
		tree.insert(4, 10);
		tree.insert(5, 15);
		tree.insert(6, 20);
//		node.insert(4, 10);
//		node.insert(5, 15);
//		node.insert(6, 20);
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
	public void insert(int key, int val) throws IOException{
		// TODO Auto-generated method stub
		//현재 가르키는 노드
		if(node.isRoot) {
			node.insert(key, val);
		}
		else { //root로 업데이트 
			//meta파일 열어 확인하고 node값 업데이트 하기.
			System.out.println("Complete");
			
		}
	}
	public void UpdateNode() throws IOException{
		

	}
	
	//메타파일 읽고 루트의 offset(node번호) 찾기
	public int read_root_node() throws IOException{
		boolean EOF = false;
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(path+File.separator+"node.meta"), blocksize));
		while(!EOF) {
			try {
				int root = is.readInt();
				int root_offset = is.readInt();
				if (root == 1)return root_offset;
			}catch(Exception e) {
				EOF = true;
			}
		}
		return -1; //-1 몾찾음
	}

	@Override
	public void open(String metafile, String filepath, int blocksize, int nblocks) throws IOException {	
		DataInputStream is =  new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(filepath), blocksize)
								);
		
		int offset = 0;
		int num_pairs = is.available() / 8 ; // 총 바이트 / (4*2)
		
		//init node 생성
		BTNodes init_node = new BTNodes(blocksize);
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

	@Override
	public int search(int key) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean isLeafNode(BTNodes node) {
		return true;
	}
	public boolean isRootNode(BTNodes node) {
		return true;
	}
	public void splitLeafNode(BTNodes node) {
		
	}
	public void splitNonLeafNode(BTNodes node) {
		
	}
	

}

class BTNodes {

	static String path = "./tmp";
	static int offsets; //노드 생성시 offset값을 변경해주기 위해
	int fanout; // blocksize를 의미함
	int blocksize;
	int offset;//random access시 필요, 
	
	List<Integer> keys; //blocksize(fanout) 만큼의 길이
	List<Integer> vals; // blocksize+1(fanout) 만큼의 길이
	/* 추상적인 BTNodes의 모습
	 *    |k_1|k_2|k_3|k_4|k_5|...|k_n|		n개의 key linked list
	 *  |p_1|p_2|p_3|p_4|p_5|...|p_n|p_n+1|	n+1개의 BTNodes 주소가 저장된 ArrayList
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
	public BTNodes(int fanout) {
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
//	public BTNodes(int fanout, boolean isLeaf) {
//		this.fanout = fanout;
//		this.isLeaf = isLeaf;
//		this.keys = new ArrayList<>(fanout);
//		this.vals = new ArrayList<>(fanout+1);
//	}
	
	
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
	
	
	public void DataToNode(int offset) throws IOException {
		DataInputStream is = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(path+File.separator+"+offset+.data"), blocksize));
		ArrayList<Integer> Nkeys = new ArrayList<Integer>();
		ArrayList<Integer> Nvals = new ArrayList<Integer>();
		try {
			Nvals.add(is.readInt());
			Nkeys.add(is.readInt());	
		}catch(Exception e){
			int i;
			//노드 파일 읽어 들여오기.
			for(i =0 ; i<keys.size(); i++) {
				this.keys.set(i, Nkeys.get(i));
				this.vals.set(i, Nvals.get(i));
			}
			this.vals.set(i+1, Nvals.get(i+1));
			this.offset = offset;
		}
		
	}
	
	public void insert(int key, int val) throws IOException  {
		if(isLeaf) {
			init_insert(key, val);
			if(isFull) {
				Split(key,val);
			}
		}
		DataOutputStream os = open_output_stream(path,offset,blocksize);
		write_run_file(keys,vals,os);
		write_meta_file();
	}
	
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
	public void write_meta_file(BTNodes Node) throws IOException{
		DataOutputStream os = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path+File.separator+"node.meta",true), blocksize)); //true 주게되면 이어쓰기
		int status=Node.status();
		System.out.println("Status : "+status);
		System.out.println("Node offSet : "+Node.offset);
		os.writeInt(Node.offset);
		os.writeInt(status);
		os.close();
	}
	public void write_meta_file() throws IOException{
		DataOutputStream os = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(path+File.separator+"node.meta",true), blocksize)); //true 주게되면 이어쓰기
		int status=status();
		System.out.println("Status : "+status);
		System.out.println("Node offSet : "+offset);
		os.writeInt(offset);
		os.writeInt(status);
		os.close();
	}
	//Root가 Full일 때 나누는 경우
	//Split은 leaf nonleaf만 생각.
	public void Split(int key, int val) throws IOException{
		System.out.println("offset : "+offset);
		BTNodes root = new BTNodes(fanout); //root노드 생성 
		DataOutputStream os1 = open_output_stream(path,root.offset,blocksize);
		
		System.out.println("offset : "+root.offset);
		BTNodes newest = new BTNodes(fanout); //new 노드 생성
		DataOutputStream os2 = open_output_stream(path,newest.offset,blocksize);
		System.out.println("offset : "+newest.offset);
		//중간값 미리 저장
		int tmp=keys.get(fanout/2);
		
		//분할
		for(int i =fanout/2; i <= keys.size() ;i++) {
//			System.out.println("Key Value : "+keys.get(fanout/2));
			newest.keys.add(keys.get(fanout/2));
			newest.vals.add(vals.get(fanout/2));
			keys.remove(fanout/2);
			vals.remove(fanout/2);
		}
		//중간 값 root 노드에 저장
		root.keys.add(tmp);
		root.vals.add(offset);
		root.vals.add(newest.offset);
		root.isRoot = true;
	
		//origin 노드 root 효력 잃음
		this.isRoot = false;
		//각 노드data 생성
		write_run_file(root.keys,root.vals,os1);
		write_meta_file(root);
		write_run_file(newest.keys,newest.vals,os2);
		write_meta_file(newest);
		
	}
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
//    }\
//	public static void splitLeafNode(Node node) throws IOException {
//		if(isLeafNode(node)) { 
//			System.out.println("Select #2 : init");//root이면서 leaf인경우 무조건 leaf생성
//			Node root = new Node(blocksize, ++offset,0); //root로 생성
//			Node leaf = new Node(blocksize, ++offset,2); //leaf로 생성
//			int keytmp=node.keys.get(node.keys.size()/2);
//			int valtmp=node.vals.get(node.keys.size()/2);
//			
//			//분할
//			for(int i =node.keys.size()/2; i <= node.keys.size() ;i++) {
////					System.out.println("Key Value : "+keys.get(fanout/2));
//				leaf.keys.add(node.keys.get(node.keys.size()/2));
//				leaf.vals.add(node.vals.get(node.keys.size()/2));
//				node.keys.remove(node.keys.size()/2);
//				node.vals.remove(node.vals.size()/2);
//			}
//			node.vals.add(valtmp);
//			//중간 값 root 노드에 저장
//			root.keys.add(keytmp);
//			root.vals.add(node.offset);
//			root.vals.add(leaf.offset);
//			node.status = 2; // leaf로 변경
//			PrintNodeTest(node,root,leaf);
//			//노드 파일 업데이트. UPdateNode, UpdateMeta 둘다 순서 (origin, parent, child)
//			
//			UpdateNode(node,root,leaf);
//			//메타 파일 업데이트.
//			writeToFile(filepath, node.to_meta_buffer(),node.offset*8);
//			writeToFile(filepath, root.to_meta_buffer(),0);
//			writeToFile(filepath, leaf.to_meta_buffer(),leaf.offset*8);
//		}
//		else {//root일 경우 무조건 Non-leaf생성 
//			System.out.println("Select #2 : root");
//			Node root = new Node(blocksize, ++offset,0); //root로 생성
//			Node Nonleaf = new Node(blocksize, ++offset,1); //Nonleaf로 생성
//			int keytmp=node.keys.get(node.keys.size()/2);
//			int valtmp=node.vals.get(node.keys.size()/2);
//			
//			//분할
//			for(int i =node.keys.size()/2; i < node.keys.size() ;i++) {
////					System.out.println("Key Value : "+keys.get(fanout/2));
//				Nonleaf.keys.add(node.keys.get(node.keys.size()/2+1));
//				Nonleaf.vals.add(node.vals.get(node.keys.size()/2));
//				node.keys.remove(node.keys.size()/2);
//				node.vals.remove(node.vals.size()/2);
//			}
//			node.vals.add(valtmp);
//			//중간 값 root 노드에 저장
//			root.keys.add(keytmp);
//			root.vals.add(node.offset);
//			root.vals.add(Nonleaf.offset);
//			node.status = 1; // Nonleaf로 변경
//			PrintNodeTest(node,root,Nonleaf);
//			//노드 파일 업데이트. 
//			UpdateNode(node, root, Nonleaf);
//			//루트는 항상 맨위에 8byte만큼 저장
//			writeToFile(filepath, root.to_meta_buffer(),0);
//			
//		}
//	}
//	
//	public static void splitNonLeafNode(Node node) throws IOException{
//		if(isLeafNode(node)) { //leaf일 경우, 무조건 leaf만 생성
//			System.out.println("Select #3");
//			int cur=history.size()-1;
//			int[] integers = readFromFile(Nfilepath, history.get(cur)*blocksize, blocksize);
//			int cur_status=1;
//			if (history.size() ==1) cur_status=0;
//			Node parent = new Node(integers,blocksize,history.get(cur), cur_status); //parent node생성 leaf parent = non leaf
//			Node leaf = new Node(blocksize, ++offset,2); //leaf로 생성
//			int keytmp=node.keys.get(node.keys.size()/2); // 7/8/9 -> 8가져가기
//			int valtmp=node.vals.get(node.keys.size()/2);
//			history.remove(cur); //사용 offset은 삭제시키기
//			
//			//분할
//			for(int i =node.keys.size()/2; i <= node.keys.size() ;i++) {
////				System.out.println("Key Value : "+keys.get(fanout/2));
//				leaf.keys.add(node.keys.get(node.keys.size()/2)); 
//				leaf.vals.add(node.vals.get(node.keys.size()/2)); 
//				node.keys.remove(node.keys.size()/2);
//				node.vals.remove(node.vals.size()/2);
//			}
//			
//			node.vals.add(valtmp);
//			node.status = 1;//leaf변경
//			
//			parent.keys.add(keytmp);
//			parent.vals.add(leaf.offset);
//			inserting(parent);
////			insert(parent, keytmp,leaf.offset);
//			PrintNodeTest(node,parent,leaf);
//			UpdateNode(node,parent,leaf);
//			//Update Meta
//			writeToFile(filepath, leaf.to_meta_buffer(),leaf.offset*8);
//			
//		}
//		else { //leaf도, root도 아닐 경우(Nonleaf) 무조건 Nonleaf만 생성
//			System.out.println("Select #4");
//			System.out.println("History : "+history);
//			int cur =history.size()-1;
//			int[] NodeBuffer = readFromFile(Nfilepath, history.get(cur)*blocksize, blocksize);
//			int[] MetaBuffer= readFromMeta(filepath,history.get(cur)*8,8);
//			Node parent = new Node(NodeBuffer,blocksize,history.get(cur), MetaBuffer[0]); //parent node생성 leaf parent = non leaf
//			System.out.println("Current Node Status : "+MetaBuffer[0]);
//			System.out.println("Current Node Offset : "+MetaBuffer[1]);
//			Node Nonleaf = new Node(blocksize, ++offset,1); //Nonleaf로 생성
//			int keytmp=node.keys.get(node.keys.size()/2); // 7/8/9 -> 8가져가기
//			int valtmp=node.vals.get(node.keys.size()/2);
//			history.remove(cur);
//			//분할
//			for(int i =node.keys.size()/2; i < node.keys.size() ;i++) {
////					System.out.println("Key Value : "+keys.get(fanout/2));
//				Nonleaf.keys.add(node.keys.get(node.keys.size()/2+1)); //중간 Key값 이상만 넣기
//				Nonleaf.vals.add(node.vals.get(node.keys.size()/2)); //Value값은 그대로 가져오기.
//				node.keys.remove(node.keys.size()/2);
//				node.vals.remove(node.vals.size()/2);
//			}
//			node.vals.add(valtmp);
//			
//			parent.keys.add(keytmp);
//			parent.vals.add(Nonleaf.offset);
//			inserting(parent);
//			
//			PrintNodeTest(node,parent,Nonleaf);
//			UpdateNode(node,parent,Nonleaf);
////			history.remove(cur);
//		}


}




