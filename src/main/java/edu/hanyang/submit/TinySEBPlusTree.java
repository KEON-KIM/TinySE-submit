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
	static String filepath ="./tmp/node.meta";
	static String Nfilepath = "./tmp/node.data";
	static int blocksize=32;
	int nblocks;
	List<Integer> history = new ArrayList<Integer>();
	
	RandomAccess node_File;
	RandomAccess meta_File;
	static Node node = new Node(32,0,3); //Cusor Nodes
	
	int offset;
	int node_cur;
	
	
	public static void make_node(String filepath, int blocksize, int nblocks) {
		
	}
	public static void main(String[] args) throws IOException {

		TinySEBPlusTree Tree = new TinySEBPlusTree();
		Tree.insert(5, 10);
		Tree.insert(6, 15);
		Tree.insert(4, 20);
		
		node = searchRoot();
		System.out.println("Root Keys : "+node.keys);
		System.out.println("Root Vals : "+node.vals);
		System.out.println("Complete");
		
		
		
		
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
	private static int[] readFromMFile(String filePath, int position, int size)
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
		if(node.status != 0) {
			//node초기화
		}
		init_insert(node, key, val);
		if(isFull(node)) {
			if(isRootNode(node)) {
				System.out.println("Node is Full");
				splitLeafNode(node);
			}
			else {
				System.out.println("Node is Full");
				splitNonLeafNode(node);
			}
				//아닐 때
		}
		//node 변경하기
//		int[] Array = new int[1];
//		node = new Node(Array, blocksize, 2,0);
		
	}
	//node = origin, root = new, leaf = new
	public void UpdateNode(Node node, Node parent, Node child) throws IOException{
		//root일경우, root노드leaf노드(init경우) 같음
		writeToFile(Nfilepath, BufferedIntegerArray(node.keys, node.vals),node.offset*blocksize);
		writeToFile(Nfilepath, BufferedIntegerArray(parent.keys, parent.vals),parent.offset*blocksize);
		writeToFile(Nfilepath, BufferedIntegerArray(child.keys, child.vals),child.offset*blocksize);
	}
	//node = Origin, newnode = new
	public void UpdateMeta(Node node, Node parent, Node child) throws IOException {
	 //init시  모두 leaf혹은 root이므로 모두 meta파일에 저장해야함.
		writeToFile(filepath, BufferedMetaArray(node.offset, node.status),node.offset*8);
		writeToFile(filepath, BufferedMetaArray(parent.offset, parent.status),parent.offset*8);
		writeToFile(filepath, BufferedMetaArray(child.offset, child.status),child.offset*8);
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
		if(node.status == 2||node.status==3)
			return true;
		return false;
	}
	public boolean isRootNode(Node node) {
		if(node.status == 0||node.status==3)
			return true;
		return false;
	}
	public boolean isFull(Node node) {
		if(node.keys.size() == (blocksize-8)/(Integer.BYTES*2)) return true;
		return false;
	}
	//Node파일에 저장시킬 Integer Array생성
	public int[] BufferedIntegerArray(List<Integer> keys, List<Integer> vals) {
		int[] Array= new int[(blocksize-Integer.BYTES) / Integer.BYTES];
		int i = 0;
		System.out.println("Keys : "+keys);
		System.out.println("Vals : "+vals);
		for( i = 0; i < keys.size(); i++) {
			Array[i*2] = vals.get(i);
			Array[i*2+1] = keys.get(i);
		}
		//Error 때문에 임시방편 ㅠ
		if(i+1==vals.size()) {
			Array[i*2] = vals.get(i);
		}
		//padding
		for(int j = keys.size() * 2+1  ; j < Array.length; j++) {
			Array[j] = -1;
		}
		
		System.out.print("Array : [");
		for(int k = 0; k < Array.length; k++) {
			System.out.print(" "+Array[k]+",");
		}
		System.out.println("]");
		
		return Array;
	}
	//Mata파일 저장시킬 Integer Array 생성
	public int[] BufferedMetaArray(int offset, int status) {
		int[] Array= new int[2];
		Array[0] = status;
		Array[1] = offset;
		
		return Array;
	}
	public void init_insert(Node node, int key, int val) throws IOException{
		if (node.keys.size() == 0) {
			node.keys.add(key);
			node.vals.add(val);
		}
		//if (keys.size() == this.fanout) throw new Exception();
		else {
			Iterator<Integer> it = node.keys.iterator();
			
			while(it.hasNext()) {
				int n = it.next();
				if(n > key) {
					node.keys.add(node.keys.indexOf(n), key);
					node.vals.add(node.keys.indexOf(n)-1, val);
					writeToFile(Nfilepath, BufferedIntegerArray(node.keys, node.vals),node.offset*blocksize);
					return ;
				}
			}
			node.keys.add(key);
			node.vals.add(val);
		}
		writeToFile(Nfilepath, BufferedIntegerArray(node.keys, node.vals),node.offset*blocksize);
	}
	
	public void splitLeafNode(Node node) throws IOException {
		if(isLeafNode(node)) { //root이면서 leaf인경우 무조건 leaf생성
			Node root = new Node(blocksize, ++offset,0); //root로 생성
			Node leaf = new Node(blocksize, ++offset,2); //leaf로 생성
			int keytmp=node.keys.get(node.keys.size()/2);
			int valtmp=node.vals.get(node.keys.size()/2);
			
			//분할
			for(int i =node.keys.size()/2; i <= node.keys.size() ;i++) {
//					System.out.println("Key Value : "+keys.get(fanout/2));
				leaf.keys.add(node.keys.get(node.keys.size()/2));
				leaf.vals.add(node.vals.get(node.keys.size()/2));
				node.keys.remove(node.keys.size()/2);
				node.vals.remove(node.vals.size()/2);
			}
			node.vals.add(valtmp);
			//중간 값 root 노드에 저장
			root.keys.add(keytmp);
			root.vals.add(node.offset);
			root.vals.add(leaf.offset);
			node.status = 2; // leaf로 변경
			//노드 파일 업데이트. UPdateNode, UpdateMeta 둘다 순서 (origin, parent, child)
			UpdateNode(node,root,leaf);
			writeToFile(filepath, BufferedMetaArray(node.offset, node.status),8);
			writeToFile(filepath, BufferedMetaArray(root.offset, root.status),0);
			writeToFile(filepath, BufferedMetaArray(leaf.offset, leaf.status),leaf.offset*8);
		}
		else {//root일 경우 무조건 Non-leaf생성 
			Node root = new Node(blocksize, ++offset,0); //root로 생성
			Node Nonleaf = new Node(blocksize, ++offset,1); //Nonleaf로 생성
			int keytmp=node.keys.get(node.keys.size()/2);
			int valtmp=node.vals.get(node.keys.size()/2);
			
			//분할
			for(int i =node.keys.size()/2; i <= node.keys.size() ;i++) {
//					System.out.println("Key Value : "+keys.get(fanout/2));
				Nonleaf.keys.add(node.keys.get(node.keys.size()/2));
				Nonleaf.vals.add(node.vals.get(node.keys.size()/2));
				node.keys.remove(node.keys.size()/2);
				node.vals.remove(node.vals.size()/2);
			}
			node.vals.add(valtmp);
			//중간 값 root 노드에 저장
			root.keys.add(keytmp);
			root.vals.add(node.offset);
			root.vals.add(Nonleaf.offset);
			node.status = 1; // Nonleaf로 변경
			//노드 파일 업데이트. 
			writeToFile(Nfilepath, BufferedIntegerArray(node.keys, node.vals),node.offset*blocksize);
			writeToFile(Nfilepath, BufferedIntegerArray(root.keys, root.vals),root.offset*blocksize);
			writeToFile(Nfilepath, BufferedIntegerArray(Nonleaf.keys, Nonleaf.vals),Nonleaf.offset*blocksize);
			//루트는 항상 맨위에 8byte만큼 저장
			writeToFile(filepath, BufferedMetaArray(root.offset, root.status),0);
			
		}
	}
	
	public void splitNonLeafNode(Node node) throws IOException{
		if(isLeafNode(node)) { //leaf일 경우, 무조건 leaf만 생성
//			Node parent = new Node(int[] array,blocksize,offset, status);
			Node leaf = new Node(blocksize, ++offset,2); //leaf로 생성
			int keytmp=node.keys.get(node.keys.size()/2); // 7/8/9 -> 8가져가기
			int valtmp=node.vals.get(node.keys.size()/2);
			
			//분할
			for(int i =node.keys.size()/2; i <= node.keys.size() ;i++) {
//				System.out.println("Key Value : "+keys.get(fanout/2));
				leaf.keys.add(node.keys.get(node.keys.size()/2)); 
				leaf.vals.add(node.vals.get(node.keys.size()/2)); 
				node.keys.remove(node.keys.size()/2);
				node.vals.remove(node.vals.size()/2);
			}
			node.vals.add(valtmp);
			writeToFile(Nfilepath, BufferedIntegerArray(node.keys, node.vals),node.offset*blocksize);
			//parent에 Tree.insert(Key, Value)
			//중간 값 root 노드에 저장
//			parent.keys.add(keytmp);
//			parent.vals.add(node.offset);
//			parent.vals.add(leaf.offset);
//			node.status = 2; // 애초에 leaf노드이기에 변경해줄 필요없음
			//노드 파일 업데이트. UPdateNode, UpdateMeta 둘다 순서 (origin, parent, child)
//			UpdateNode(node,parent,leaf);
//			UpdateMeta(node,parent,leaf); //leaf정보 추가해야함
			
//			writeToFile(Nfilepath, BufferedIntegerArray(parent.keys, parent.vals),parent.offset*blocksize);
			writeToFile(Nfilepath, BufferedIntegerArray(leaf.keys, leaf.vals),leaf.offset*blocksize);
			writeToFile(filepath, BufferedMetaArray(leaf.offset, leaf.status),leaf.offset*8);
		}
		else { //leaf도, root도 아닐 경우(Nonleaf) 무조건 Nonleaf만 생성
//			Node root = new Node() //상위 Node를 찾아야 한다.
//			Node parent = new Node(int[] array,blocksize,offset, status);
			Node Nonleaf = new Node(blocksize, ++offset,1); //Nonleaf로 생성
			int keytmp=node.keys.get(node.keys.size()/2); // 7/8/9 -> 8가져가기
			int valtmp=node.vals.get(node.keys.size()/2);
			
			//분할
			for(int i =node.keys.size()/2; i <= node.keys.size() ;i++) {
//					System.out.println("Key Value : "+keys.get(fanout/2));
				Nonleaf.keys.add(node.keys.get(node.keys.size()/2+1)); //중간 Key값 이상만 넣기
				Nonleaf.vals.add(node.vals.get(node.keys.size()/2)); //Value값은 그대로 가져오기.
				node.keys.remove(node.keys.size()/2);
				node.vals.remove(node.vals.size()/2);
			}
			node.vals.add(valtmp);
			//중간 값 root 노드에 저장
//			parent.keys.add(keytmp);
//			parent.vals.add(node.offset);
//			parent.vals.add(Nonleaf.offset);
			node.status = 1; // Nonleaf로 변경
			//노드 파일 업데이트. UPdateNode, UpdateMeta 둘다 순서 (origin, parent, child)
//			UpdateNode(node,parent,Nonleaf);
//			UpdateMeta(node,root,leaf); //Meta파일 수정할 필요가 없음, 
		}
	}
	
	//status, offset 순으로 저장함
	public static Node searchRoot() throws IOException {
		int[] offsets = readFromMFile(filepath, 0, 8);
		int cur_status = offsets[0];
		int cur_offset = offsets[1];
		int[] Integers = readFromFile(Nfilepath, cur_offset*blocksize,blocksize);
		Node node = new Node(Integers, blocksize, cur_offset, cur_status);
		return node;
	}
	//leaf 찾기
	public Node searchLeaf( int key, int val)throws IOException {
		int find_offset = 0;
		for(int i=0;i<node.keys.size();i++) { //현재 Cur_node
			if(key<node.keys.get(i)) {
				find_offset = node.vals.get(i);
			}
			else {
				find_offset = node.vals.get(node.vals.size()-1);
			}
		}
		int[] integers = readFromFile(Nfilepath, find_offset*blocksize, blocksize);
		Node node = new Node(integers, blocksize, find_offset, 1);
		history.add(find_offset);
		
		return node;
		
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
	
	//커서 만들기.
	Node(int[] integers, int blocksize, int offset, int status) {
//		blocksize -= 8; // blocksize에서 한쌍 덜 읽어오게 8을 빼
		
		int max_keys = (blocksize - 2*Integer.BYTES) / (Integer.BYTES * 2);
		int max_vals = max_keys + 1;
		
		this.max_keys = max_keys;
		keys = new ArrayList<>(max_keys);
		vals = new ArrayList<>(max_vals);
		
		this.offset = offset;
		this.status = status;
		int i;
		/**/
		for(i = 0; i < integers.length / 2; i ++) {
			if(integers[i*2]==-1||integers[i*2+1]==-1) {
			}
			else {
				vals.add(integers[i*2]); //0, 2, 4, 8, 16, ... 번째 숫자들어감
				keys.add(integers[i*2+1]); //1, 3, 5, 7, 9 ... 번째 숫자 들어감
			}
		}
		vals.add(integers[i/2]);
		
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
	
}





