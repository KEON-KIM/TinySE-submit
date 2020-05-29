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
	static List<Integer> history = new ArrayList<Integer>();
	
	RandomAccess node_File;
	RandomAccess meta_File;
	static Node node = new Node(32,1,3); //Cusor Nodes
	
	static int offset=1;
	int node_cur;
	
	/*이부분은 tmp 파일 비우는 용도*/
	public static void clean(String dir) {
		File file = new File(dir);
		File[] tmpFiles = file.listFiles();
		if (tmpFiles != null) {
			for (int i = 0; i < tmpFiles.length; i++) {
				if (tmpFiles[i].isFile()) {
					tmpFiles[i].delete();
				} else {
					clean(tmpFiles[i].getAbsolutePath());
				}
				tmpFiles[i].delete();
			}
			file.delete();
		}
	}
	public static void init() {
		clean("./tmp");
		File treefile = new File("./tree.data");
		File metafile = new File("./meta.data");
		if(treefile.exists()) {
			treefile.delete();
		}
		if(metafile.exists()) {
			metafile.delete();
		}
	}
	
	
	/*
	 * 1. input stream open
	 * 2. 첫 blocksize 만큼의 data get*/
	public static void make_tree(String filepath, String treepath, String metapath, int blocksize, int nblocks) throws IOException {
//		blocksize -= 8;
		DataInputStream is = new DataInputStream(
								new BufferedInputStream(
									new FileInputStream(filepath), blocksize)
									);
		
		Node node = new Node(blocksize, 1, 3);
		/*
		 * ex) blocksize = 1024 ==> 총 256개의 숫자 받을 수 있음
		 *     그중 숫자 255개 받기
		 *     blocksize - Integer.BYTES*/
		
		/*한 노드에 들어갈 Integer 갯수*/
		int num_integer = (blocksize / Integer.BYTES) - 1;
		
		
		
		/* 첫 노드
		 * key : num_integer
		 * val : num_integer
		 *   | k1 | k2 | k3 | ... | kn |
		 * | p1 | p2 | p3 | ...| pn | -1 |
		 * 마지막 val에 -1 들어감*/
		
		for(int i = 0; i < num_integer / 2; i++) {
			int key = is.readInt();
			int val = is.readInt();
			node.insert(key, val);
			
		}
		node.vals.add(-1);
		
		
		/* node의 keys, vals를 buffer에 담아서 meta.data, tree.data에 write*/
		int[] tree_buffer = node.to_buffer();
		int[] meta_buffer = new int[2];
		int offset = 1;
		int status = 3;
		meta_buffer[0] = offset;
		meta_buffer[1] = status;
		
		writeToFile(treepath, tree_buffer, offset); // tree.data
		writeToFile(metapath, meta_buffer, offset); // meta.data
		
		int cur = 1; // init node의 offset = 1
		while(is.available() != 0) {
			int key = is.readInt();
			int val = is.readInt();
			
			
			
			/* case 1. 들어갈 leafnode가 full인 경우
			 *    - splitleafnode(cur)
			 *        - cur node [curnode.vals.size()/2] 부터 끝까지 복사해서 buffer에 넣고 그부분 제거
			 *        - writetofile(cur, tree.data)
			 *        - cur node <= 새로만든 leaf node의 offset
			 *        - cur node에 buffer넣기
			 *        - writetofile(cur, tree.data)
			 *        - writetofile(cur, meta.data)
			 *        - piv = 부모노드에 올라갈 (key, curnode offset)
			 *        - cur <= 부모노드
			 *        case1-1 부모노드가 full인 경우
			 *        	- splitnonleafnode(cur)
			 *        	- cur node [curnode.vals.size()/2] 부터 끝까지 복사해서 buffer에 넣고 그 부분 제거
			 *        	- writetofile(cur, tree)
			 *        	- writetofile(cur, meta)
			 *        	- cur node <= 새로만든 leaf node의 offset
			 *        	- cur node에 buffer 넣기
			 *        	- writetofile(cur, tree)
			 *        	- writetofile(cur, meta)
			 *        	- piv = 부모노드에 올라갈 (key, curnode offset)
			 *        	- cur <= 부모노드
			 *        	재귀들어가는부분()
			 *        case1-2 부모노드가 full이 아닌 경우
			 *            - cur node에 insert piv
			 *            - write
			 * case 2. 들어갈 leafnode가 full이 아닐 경우
			 *     - cur node에 insert(key, val)
			 *     - writetoFile(cur, tree)
			 *     */
				
		}
		
		
	}
	public static void main(String[] args) throws IOException {
		init();
		TinySEBPlusTree tree = new TinySEBPlusTree();
//		tree.insert(5, 10);
//		tree.insert(6, 15);
//		tree.insert(4, 20);
//		
//		node = searchRoot();
//		System.out.println("Root Keys : "+node.keys);
//		System.out.println("Root Vals : "+node.vals);
//		System.out.println("Complete");
		
		TinySEBPlusTree Tree = new TinySEBPlusTree();
		System.out.println("Starting");
		System.out.println("Root Keys : "+node.keys);
		System.out.println("Root Vals : "+node.vals);
		Tree.insert(5, 10);
		Tree.insert(6, 15);
		Tree.insert(4, 20);
		Tree.insert(7, 1);
		
//		node = searchRoot();
//		System.out.println("Root Keys : "+node.keys);
//		System.out.println("Root Vals : "+node.vals);
//		System.out.println("Root Search Complete");
//		node = searchLeaf(6,15);
//		System.out.println("leaf Keys : "+node.keys);
//		System.out.println("leaf Vals : "+node.vals);
//		System.out.println("history : "+history);
//		System.out.println("Complete");
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

		System.out.print("Integers : [ ");
		for(int i = 0; i < integers.length; i++) {
			integers[i] = file.readInt();
			System.out.print(integers[i]+",");
		}
		System.out.println("]");
		file.close();
		return integers;
	}
	
	private static int[] readFromMeta(String filePath, int position, int size)
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
		if(!isRootNode(node)) {
			node = searchRoot();
			if(!isLeafNode(node)) {
				node = searchLeaf(key,val);
			}
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
		}//아닐 때
		
		//node 변경하기
//		int[] Array = new int[1];
//		node = new Node(Array, blocksize, 2,0);
	}
	//node = origin, root = new, leaf = new
	public static void UpdateNode(Node node, Node parent, Node child) throws IOException{
		//root일경우, root노드leaf노드(init경우) 같음
		writeToFile(Nfilepath, BufferedIntegerArray(node.keys, node.vals),node.offset*blocksize);
		writeToFile(Nfilepath, BufferedIntegerArray(parent.keys, parent.vals),parent.offset*blocksize);
		writeToFile(Nfilepath, BufferedIntegerArray(child.keys, child.vals),child.offset*blocksize);
	}
	//node = Origin, newnode = new
	public static void UpdateMeta(Node node, Node parent, Node child) throws IOException {
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
	
	public static boolean isLeafNode(Node node) {
		if(node.status == 2||node.status==3)
			return true;
		return false;
	}
	public static boolean isRootNode(Node node) {
		if(node.status == 0||node.status==3)
			return true;
		return false;
	}
	public static boolean isFull(Node node) {
		if(node.keys.size() == (blocksize-8)/(Integer.BYTES*2)) return true;
		return false;
	}
	//Node파일에 저장시킬 Integer Array생성
	public static int[] BufferedIntegerArray(List<Integer> keys, List<Integer> vals) {
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
	public static int[] BufferedMetaArray(int offset, int status) {
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
	
	public static void splitLeafNode(Node node) throws IOException {
	
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
	
	public static void splitNonLeafNode(Node node) throws IOException{
		System.out.println("Parent! Nonleaf!");
		if(isLeafNode(node)) { //leaf일 경우, 무조건 leaf만 생성
			int[] integers = readFromFile(Nfilepath, history.get(history.size()-1)*blocksize, blocksize);
			Node parent = new Node(integers,blocksize,history.get(history.size()-1), 1); //parent node생성 leaf parent = non leaf
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
			parent.keys.add(keytmp);
			parent.vals.add(leaf.offset);
			writeToFile(Nfilepath, BufferedIntegerArray(node.keys, node.vals),node.offset*blocksize);
			writeToFile(Nfilepath, BufferedIntegerArray(parent.keys, parent.vals),parent.offset*blocksize);
			writeToFile(Nfilepath, BufferedIntegerArray(leaf.keys, leaf.vals),leaf.offset*blocksize);
			writeToFile(filepath, BufferedMetaArray(leaf.offset, leaf.status),leaf.offset*8);
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
		int[] offsets = readFromMeta(filepath, 0, 8);
		int cur_status = offsets[0];
		int cur_offset = offsets[1];
		int[] Integers = readFromFile(Nfilepath, cur_offset*blocksize,blocksize);
		Node node = new Node(Integers, blocksize, cur_offset, cur_status);
		return node;
	}
	//leaf 찾기
	public static Node searchLeaf(int key, int val)throws IOException {
		history.add(node.offset);
		int find_offset = 0;
		for(int i=0;i<node.keys.size();i++) { //현재 Cur_node
			if(key<node.keys.get(i)){find_offset = node.vals.get(i);break;}
			
		}
		if(find_offset == 0) {//keys를 돌고 작은 값을 찾지 못하면 find_offset은 0 마지막 value값 참조해야함
			find_offset = node.vals.get(node.vals.size()-1); //Vals중 가장 우측 값
		}
		int[] Status = readFromMeta(filepath,find_offset*8,8);
		if(Status[0]==2) {
			int[] Nodes = readFromFile(Nfilepath, find_offset*blocksize, blocksize);
			Node node = new Node(Nodes, blocksize, find_offset, Status[0]);
			
			return node;
		}
		else {
			int[] Nodes = readFromFile(Nfilepath, find_offset*blocksize, blocksize);
			node = new Node(Nodes, blocksize, find_offset, Status[0]);
			searchLeaf(key, val);
		}
		
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
	Node(int[] buffer, int blocksize, int offset, int status) {
//		blocksize -= 8; // blocksize에서 한쌍 덜 읽어오게 8을 빼
		int max_keys = (blocksize / Integer.BYTES - 1) / 2;
		int max_vals = max_keys + 1;
		
		this.max_keys = max_keys;
		keys = new ArrayList<>(max_keys);
		vals = new ArrayList<>(max_vals);
		
		this.offset = offset;
		this.status = status;
		int i;
		for(i = 0; i < buffer.length / 2; i ++) {
			if(buffer[i*2+1]<=0||buffer[i*2]<=0) break;
			vals.add(buffer[i*2]); //0, 2, 4, 8, 16, ... 번째 숫자들어감
			keys.add(buffer[i*2+1]); //1, 3, 5, 7, 9 ... 번째 숫자 들어감
		}
		if(buffer[i*2]!=0)vals.add(buffer[i*2]);
		System.out.println("integers size : "+buffer.length);
	}
	
	Node(int blocksize, int offset, int status) {
		int max_keys = (blocksize / Integer.BYTES - 1) / 2;
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
		if (this.keys.size() == 0) {
			this.keys.add(key);
			this.vals.add(val);
			return ;
		}
		Iterator<Integer> it = this.keys.iterator();
		
			
		while(it.hasNext()) {
			int n = it.next();
			
			if(n > key) {
				
				this.keys.add(this.keys.indexOf(n), key);
				this.vals.add(this.keys.indexOf(n), val);
				return;
			}
		}
		this.keys.add(key);
		this.vals.add(val);
	}
	
	
	/*
	 * keys, vals를 int 배열 buffer로 만들기*/
	public int[] to_buffer() {
		int[] buffer = new int[max_keys * 2 + 1];
		for(int i = 0; i < this.keys.size(); i++) {
			buffer[i*2] = this.vals.get(i);
			buffer[i*2 + 1] = this.keys.get(i);
		}
		buffer[keys.size()*2] = this.vals.indexOf(keys.size());
		
		return buffer;
	}
	
	
}





